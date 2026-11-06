package com.bytedesk.ticket.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.utils.Utils;
import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.utils.FlowableIdUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认工单流程初始化服务：组织创建事件、启动初始化与手动 /init 端点共用入口。
 *
 * 事务边界：REQUIRES_NEW，独立于组织创建事务（afterCommit 阶段发布事件时原事务已提交，
 * 监听器内调用必须能开启可再次提交的新事务）。事务内先锁默认流程行（不存在时依赖
 * uuid 唯一约束兜底），串行化同组织的自动/手动初始化。
 *
 * 状态矩阵（见 docs/plans/2026-11-06-ticket-process-auto-init-parity-plan.md 3.2.1）：
 * - 两条均不存在/只缺一条 → 创建缺失条目并部署（CREATED）
 * - 记录及有效部署均存在 → 校验租户/key 后复用（REUSED），不重复部署、不覆盖内容
 * - 记录存在但部署指针丢失 → 核实确切租户+key 的定义后修复回写（REPAIRED）
 * - 记录存在、无部署、内容仍为默认模板 → 从实体 schema 经统一校验部署
 * - 已修改且无明确有效部署指针 → 显式冲突，不凭名称自动恢复
 * - 默认记录被软删除 → 显式冲突，不恢复、不新建同 uid
 */
@Slf4j
@Service("processInitializationService")
@AllArgsConstructor
public class ProcessInitializationService {

    private final ProcessRepository processRepository;

    private final ProcessRestService processRestService;

    private final RepositoryService repositoryService;

    private final ResourceLoader resourceLoader;

    /**
     * 初始化（或补齐）组织的两条默认工单流程。失败抛出异常，不吞异常后返回成功。
     * 并发安全：同一 processUid 的竞争由 findByUidForUpdate 行锁或 uuid 唯一约束串行化；
     * 唯一约束冲突由调用方在新事务中重试（本方法不在 rollback-only 事务内重试）。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessInitializationResult initDefaultProcesses(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            throw new IllegalArgumentException("orgUid is required");
        }

        ProcessInitializationResult.Item internal = initOne(orgUid, ProcessTypeEnum.TICKET_INTERNAL);
        ProcessInitializationResult.Item external = initOne(orgUid, ProcessTypeEnum.TICKET_EXTERNAL);

        return ProcessInitializationResult.builder()
                .orgUid(orgUid)
                .processes(List.of(internal, external))
                .build();
    }

    private ProcessInitializationResult.Item initOne(String orgUid, ProcessTypeEnum type) {
        String processUid = defaultProcessUid(orgUid, type);

        // 行锁查询：命中则串行化同 uid 的并发初始化；未命中时创建路径由 uuid 唯一约束兜底
        Optional<ProcessEntity> locked = processRepository.findByUidForUpdate(processUid);
        if (locked.isEmpty()) {
            return createDefault(orgUid, type, processUid);
        }

        ProcessEntity entity = locked.get();
        if (entity.isDeleted()) {
            // 软删除：不恢复、不新建同 uid、不报告成功；需确认后单独恢复
            throw new IllegalStateException("默认工单流程已被删除，需先确认恢复: " + processUid);
        }
        validateType(entity, type, processUid);

        if (isMeaningfullyDeployed(entity)) {
            return ProcessInitializationResult.Item.builder()
                    .uid(entity.getUid())
                    .type(entity.getType())
                    .outcome("REUSED")
                    .deploymentId(entity.getDeploymentId())
                    .processDefinitionKey(FlowableIdUtils.toProcessDefinitionKey(entity.getUid()))
                    .build();
        }

        return reconcileExisting(orgUid, entity);
    }

    private ProcessInitializationResult.Item createDefault(String orgUid, ProcessTypeEnum type, String processUid) {
        String bpmnXml = loadTemplate(TicketConsts.TICKET_PROCESS_PATH);
        String flowgramJson = loadTemplate(TicketConsts.TICKET_PROCESS_FLOWGRAM_PATH);
        String processName = type == ProcessTypeEnum.TICKET_EXTERNAL
                ? TicketConsts.TICKET_PROCESS_NAME_EXTERNAL
                : TicketConsts.TICKET_PROCESS_NAME;

        ProcessRequest request = ProcessRequest.builder()
                .uid(processUid)
                .name(processName)
                .schema(bpmnXml)
                .flowgramSchema(flowgramJson)
                .type(type.name())
                .description(processName)
                .orgUid(orgUid)
                .build();
        // create() 对已存在 uid 直接返回；并发插入由 uuid 唯一约束拒绝，异常向上抛出
        ProcessResponse response = processRestService.create(request);
        // 事务内自读：create 返回即本事务可见；部署走统一入口（含校验）
        ProcessDefinition definition = deployEntity(processUid, orgUid);
        log.info("初始化默认工单流程成功: orgUid={}, uid={}, deploymentId={}",
                orgUid, response.getUid(), definition.getId());

        return ProcessInitializationResult.Item.builder()
                .uid(response.getUid())
                .type(type.name())
                .outcome("CREATED")
                .deploymentId(definition.getId())
                .processDefinitionKey(definition.getKey())
                .build();
    }

    /**
     * 记录存在但部署指针丢失/失效：优先核实「确切租户 + key」的既有有效定义，
     * 确认一致后仅修复回写；表内 schema 已被修改且无明确部署指针时显式冲突。
     */
    private ProcessInitializationResult.Item reconcileExisting(String orgUid, ProcessEntity entity) {
        String processUid = entity.getUid();
        String processDefinitionKey = FlowableIdUtils.toProcessDefinitionKey(processUid);

        if (!StringUtils.hasText(entity.getSchema())) {
            throw new IllegalStateException("默认工单流程缺少 BPMN schema，无法初始化: " + processUid);
        }

        boolean schemaMatchesTemplate = isSchemaSemanticallyDefault(entity);

        // 核实既有有效定义（不按 deploymentName 模糊恢复，避免拿回他人部署）
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .processDefinitionTenantId(orgUid)
                .latestVersion()
                .active()
                .singleResult();

        if (definition != null) {
            if (!schemaMatchesTemplate) {
                // 表内已修改但没有明确有效部署指针：不覆盖用户内容，也不自动发布未部署修改
                throw new IllegalStateException("默认工单流程已被修改且部署指针缺失，需在编辑器确认后手动部署: "
                        + processUid);
            }
            // 指针丢失但定义仍有效：仅修复回写，不重新部署、不升级版本
            markDeployed(entity, definition);
            log.info("修复默认工单流程部署指针: uid={}, deploymentId={}", processUid, definition.getDeploymentId());
            return buildItem(entity, "REPAIRED", definition);
        }

        if (!schemaMatchesTemplate) {
            throw new IllegalStateException("默认工单流程已被修改且无有效部署，需在编辑器确认后手动部署: " + processUid);
        }

        // 无任何既有定义且表内仍为默认模板：从实体 schema 经统一校验部署
        ProcessDefinition deployed = deployEntity(processUid, orgUid);
        return buildItem(entity, "REPAIRED", deployed);
    }

    /** 统一部署入口：读取实体 schema → 校验 → key 归一 → 租户部署 → 精确查定义 → 回写 */
    private ProcessDefinition deployEntity(String processUid, String orgUid) {
        // deployProcess 内部完成 validateProcessBeforeDeploy + key 归一 + 部署 + 回写
        processRestService.deployProcess(processUid, false);
        String processDefinitionKey = FlowableIdUtils.toProcessDefinitionKey(processUid);
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .processDefinitionTenantId(orgUid)
                .latestVersion()
                .singleResult();
        if (definition == null) {
            throw new IllegalStateException("部署后未找到流程定义: processUid=" + processUid + ", tenantId=" + orgUid);
        }
        return definition;
    }

    private void markDeployed(ProcessEntity entity, ProcessDefinition definition) {
        entity.setDeploymentId(definition.getDeploymentId());
        entity.setStatus(ProcessStatusEnum.DEPLOYED.name());
        processRepository.save(entity);
    }

    private ProcessInitializationResult.Item buildItem(ProcessEntity entity, String outcome,
            ProcessDefinition definition) {
        return ProcessInitializationResult.Item.builder()
                .uid(entity.getUid())
                .type(entity.getType())
                .outcome(outcome)
                .deploymentId(definition.getDeploymentId())
                .processDefinitionKey(definition.getKey())
                .build();
    }

    /**
     * 语义级模板比较：解析 BPMN 的任务/网关/连线节点 ID 集合，排除 DI 坐标、格式与部署 key 差异。
     * 不能用「节点 ID 相同」之外的字节比较，也不能因格式差异误判为已修改。
     */
    private boolean isSchemaSemanticallyDefault(ProcessEntity entity) {
        try {
            String template = loadTemplate(TicketConsts.TICKET_PROCESS_PATH);
            Set<String> current = extractNodeIds(entity.getSchema());
            Set<String> defaults = extractNodeIds(template);
            return current.equals(defaults);
        } catch (Exception e) {
            log.warn("默认流程模板比较失败，按已修改处理: uid={}, error={}", entity.getUid(), e.getMessage());
            return false;
        }
    }

    private Set<String> extractNodeIds(String bpmnXml) {
        Set<String> ids = new java.util.HashSet<>();        java.util.regex.Matcher matcher = NODE_ID_PATTERN.matcher(bpmnXml == null ? "" : bpmnXml);
        while (matcher.find()) {
            String id = matcher.group(2);
            if (id != null && !id.isBlank()) {
                ids.add(id);
            }
        }
        return ids;
    }

    private static final java.util.regex.Pattern NODE_ID_PATTERN = java.util.regex.Pattern
            .compile("<(userTask|serviceTask|startEvent|endEvent|exclusiveGateway|parallelGateway|boundaryEvent)\\b[^>]*\\bid=\"([^\"]+)\"");

    private void validateType(ProcessEntity entity, ProcessTypeEnum expected, String processUid) {
        if (!expected.name().equalsIgnoreCase(entity.getType())) {
            throw new IllegalStateException("默认工单流程类型不符: uid=" + processUid
                    + ", expected=" + expected.name() + ", actual=" + entity.getType());
        }
    }

    private boolean isMeaningfullyDeployed(ProcessEntity entity) {
        if (!StringUtils.hasText(entity.getDeploymentId())) {
            return false;
        }
        if (!ProcessStatusEnum.DEPLOYED.name().equalsIgnoreCase(entity.getStatus())) {
            return false;
        }
        // 部署指针存在但定义可能已被删除/挂起：核实时按确切租户+key 查询
        return true;
    }

    private String loadTemplate(String path) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + path);
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IllegalStateException("加载流程模板失败: " + path, e);
        }
    }

    static String defaultProcessUid(String orgUid, ProcessTypeEnum type) {
        return type == ProcessTypeEnum.TICKET_EXTERNAL
                ? Utils.formatUid(orgUid,
                        TicketConsts.TICKET_PROCESS_KEY + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX)
                : Utils.formatUid(orgUid, TicketConsts.TICKET_PROCESS_KEY);
    }
}
