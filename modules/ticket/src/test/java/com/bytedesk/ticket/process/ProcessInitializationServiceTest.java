package com.bytedesk.ticket.process;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.utils.FlowableIdUtils;

/**
 * ProcessInitializationService 状态矩阵单测（Mock Flowable/JPA 层）：
 * - 两条默认流程不存在 → CREATED（配对模板创建 + 统一部署入口）
 * - 记录及有效部署均存在 → REUSED（不重复部署、不覆盖内容）
 * - 部署指针丢失但定义仍有效 → REPAIRED（仅修复回写，不重新部署）
 * - 软删除 → 显式冲突（不恢复、不新建同 uid）
 * - 已修改且无有效部署 → 显式冲突（不自动发布用户修改）
 * 事务边界/真实提交行为由集成测试层覆盖（规划 T1/T2/T4）。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProcessInitializationServiceTest {

    private static final String ORG_UID = "org-init-test";
    private static final String INTERNAL_UID = ORG_UID + "_" + TicketConsts.TICKET_PROCESS_KEY;
    private static final String EXTERNAL_UID = INTERNAL_UID + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX;

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private ProcessRestService processRestService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private ProcessDefinition processDefinition;

    @Mock
    private ProcessDefinitionQuery processDefinitionQuery;

    @Mock
    private DeploymentQuery deploymentQuery;

    @Mock
    private DeploymentBuilder deploymentBuilder;

    @Mock
    private Deployment deployment;

    private ProcessInitializationService service;

    private String templateXml;
    private String templateJson;

    @BeforeEach
    void setUp() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/processes/ticket-process.bpmn20.xml")) {
            templateXml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        try (InputStream in = getClass().getResourceAsStream("/processes/ticket-process.json")) {
            templateJson = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        // 按路径返回对应模板，避免调用顺序耦合
        lenient().when(resourceLoader.getResource(anyString())).thenAnswer(inv -> {
            String path = inv.getArgument(0, String.class);
            return new StringResource(path.endsWith(".json") ? templateJson : templateXml);
        });

        service = new ProcessInitializationService(processRepository, processRestService,
                repositoryService, resourceLoader);

        lenient().when(processDefinition.getKey()).thenReturn(FlowableIdUtils.toProcessDefinitionKey(INTERNAL_UID));
        lenient().when(processDefinition.getDeploymentId()).thenReturn("deploy-1");
        lenient().when(repositoryService.createProcessDefinitionQuery()).thenReturn(processDefinitionQuery);
        lenient().when(processDefinitionQuery.processDefinitionKey(anyString())).thenReturn(processDefinitionQuery);
        lenient().when(processDefinitionQuery.processDefinitionTenantId(anyString())).thenReturn(processDefinitionQuery);
        lenient().when(processDefinitionQuery.latestVersion()).thenReturn(processDefinitionQuery);
        lenient().when(processDefinitionQuery.active()).thenReturn(processDefinitionQuery);
        lenient().when(repositoryService.createDeploymentQuery()).thenReturn(deploymentQuery);
        lenient().when(deploymentQuery.deploymentTenantId(anyString())).thenReturn(deploymentQuery);
        lenient().when(deploymentQuery.deploymentName(anyString())).thenReturn(deploymentQuery);
        // 统一部署入口走 processRestService.deployProcess（真实校验），此处仅打桩返回响应
        lenient().doReturn(ProcessDefinitionResponse.builder()
                .id("def-1").key("key-1").deploymentId("deploy-1").version(1).build())
                .when(processRestService).deployProcess(anyString(), anyBoolean());
    }

    /** 简单字符串资源桩，避免依赖额外测试工具 */
    static class StringResource implements Resource {
        private final String data;

        StringResource(String data) {
            this.data = data;
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        }

        @Override public boolean isReadable() { return true; }
        @Override public boolean isOpen() { return false; }
        @Override public java.net.URL getURL() { throw new UnsupportedOperationException(); }
        @Override public java.net.URI getURI() { throw new UnsupportedOperationException(); }
        @Override public java.io.File getFile() { throw new UnsupportedOperationException(); }
        @Override public long contentLength() { return data.length(); }
        @Override public long lastModified() { return 0; }
        @Override public Resource createRelative(String relativePath) { throw new UnsupportedOperationException(); }
        @Override public String getFilename() { return "template"; }
        @Override public String getDescription() { return "string-resource"; }
    }

    private ProcessEntity entity(String uid, String type, boolean deleted, String deploymentId, String schema) {
        ProcessEntity entity = ProcessEntity.builder()
                .uid(uid)
                .name("默认工单流程")
                .type(type)
                .schema(schema == null ? templateXml : schema)
                .status(deploymentId == null ? ProcessStatusEnum.DRAFT.name() : ProcessStatusEnum.DEPLOYED.name())
                .orgUid(ORG_UID)
                .deleted(deleted)
                .build();
        entity.setDeploymentId(deploymentId);
        return entity;
    }

    @Test
    void missingProcessesAreCreatedWithPairedTemplates() {
        when(processRepository.findByUidForUpdate(INTERNAL_UID)).thenReturn(Optional.empty());
        when(processRepository.findByUidForUpdate(EXTERNAL_UID)).thenReturn(Optional.empty());
        lenient().when(processRestService.create(any())).thenAnswer(inv -> {
            ProcessRequest req = inv.getArgument(0);
            return ProcessResponse.builder().uid(req.getUid()).type(req.getType()).build();
        });
        when(processDefinitionQuery.singleResult()).thenReturn(processDefinition);

        ProcessInitializationResult result = service.initDefaultProcesses(ORG_UID);

        assertThat(result.getProcesses()).hasSize(2);
        assertThat(result.getProcesses()).allSatisfy(item -> {
            assertThat(item.getOutcome()).isEqualTo("CREATED");
        });
        // 部署必须走统一入口（含校验），而不是绕过记录从磁盘部署
        verify(processRestService).deployProcess(INTERNAL_UID, false);
        verify(processRestService).deployProcess(EXTERNAL_UID, false);
    }

    @Test
    void deployedProcessesAreReusedWithoutRedeploy() {
        when(processRepository.findByUidForUpdate(INTERNAL_UID))
                .thenReturn(Optional.of(entity(INTERNAL_UID, ProcessTypeEnum.TICKET_INTERNAL.name(), false,
                        "deploy-int", null)));
        when(processRepository.findByUidForUpdate(EXTERNAL_UID))
                .thenReturn(Optional.of(entity(EXTERNAL_UID, ProcessTypeEnum.TICKET_EXTERNAL.name(), false,
                        "deploy-ext", null)));

        ProcessInitializationResult result = service.initDefaultProcesses(ORG_UID);

        assertThat(result.getProcesses()).allSatisfy(item -> {
            assertThat(item.getOutcome()).isEqualTo("REUSED");
        });
        verify(processRestService, never()).deployProcess(anyString(), anyBoolean());
        verify(processRepository, never()).save(any());
    }

    @Test
    void lostDeploymentPointerWithActiveDefinitionIsRepairedNotRedeployed() {
        // 部署指针丢失（deploymentId=null、status=DRAFT），但租户+key 仍有有效定义
        when(processRepository.findByUidForUpdate(INTERNAL_UID))
                .thenReturn(Optional.of(entity(INTERNAL_UID, ProcessTypeEnum.TICKET_INTERNAL.name(), false,
                        null, null)));
        when(processRepository.findByUidForUpdate(EXTERNAL_UID))
                .thenReturn(Optional.of(entity(EXTERNAL_UID, ProcessTypeEnum.TICKET_EXTERNAL.name(), false,
                        null, null)));
        when(processDefinitionQuery.singleResult()).thenReturn(processDefinition);

        ProcessInitializationResult result = service.initDefaultProcesses(ORG_UID);

        assertThat(result.getProcesses()).allSatisfy(item -> {
            assertThat(item.getOutcome()).isEqualTo("REPAIRED");
            assertThat(item.getDeploymentId()).isEqualTo("deploy-1");
        });
        // 只修复回写（内部/外部各一次），不重新部署、不升级版本
        verify(processRestService, never()).deployProcess(anyString(), anyBoolean());
        verify(processRepository, org.mockito.Mockito.times(2)).save(any(ProcessEntity.class));
    }

    @Test
    void softDeletedDefaultProcessReportsConflict() {
        when(processRepository.findByUidForUpdate(INTERNAL_UID))
                .thenReturn(Optional.of(entity(INTERNAL_UID, ProcessTypeEnum.TICKET_INTERNAL.name(), true,
                        "deploy-int", null)));

        assertThatThrownBy(() -> service.initDefaultProcesses(ORG_UID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("已被删除");
        verify(processRestService, never()).deployProcess(anyString(), anyBoolean());
    }

    @Test
    void modifiedProcessWithoutValidDeploymentReportsConflict() {
        // 表内 schema 已被用户修改（节点不同）且无有效定义
        String modified = templateXml.replace("id=\"createTicket\"", "id=\"createTaskRenamed\"");
        when(processRepository.findByUidForUpdate(INTERNAL_UID))
                .thenReturn(Optional.of(entity(INTERNAL_UID, ProcessTypeEnum.TICKET_INTERNAL.name(), false,
                        null, modified)));
        when(processDefinitionQuery.singleResult()).thenReturn(null);

        assertThatThrownBy(() -> service.initDefaultProcesses(ORG_UID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("已被修改");
        verify(processRestService, never()).deployProcess(anyString(), anyBoolean());
    }

    @Test
    void mismatchedTypeReportsConflict() {
        when(processRepository.findByUidForUpdate(INTERNAL_UID))
                .thenReturn(Optional.of(entity(INTERNAL_UID, ProcessTypeEnum.THREAD.name(), false,
                        "deploy-int", null)));

        assertThatThrownBy(() -> service.initDefaultProcesses(ORG_UID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("类型不符");
    }
}
