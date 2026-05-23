package com.bytedesk.core.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.workflow_settings.WorkflowSettingsEntity;
import com.bytedesk.core.workflow_settings.WorkflowSettingsRestService;

@ExtendWith(MockitoExtension.class)
class WorkflowRestServiceTest {

        @Mock
        private WorkflowRepository workflowRepository;

        @Mock
        private ModelMapper modelMapper;

        @Mock
        private UidUtils uidUtils;

        @Mock
        private WorkflowSettingsRestService workflowSettingsRestService;

        @Test
        void initDefaultWorkflowShouldAlsoSeedDefaultIvrWorkflowForDefaultOrganization() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID))
                                .thenReturn(Optional.empty());
                when(workflowRepository.findByUid("df_org_uid_df_workflow_builder")).thenReturn(Optional.empty());
                when(workflowSettingsRestService.getOrCreateDefault(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                                .thenReturn(new WorkflowSettingsEntity());
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(modelMapper.map(any(WorkflowEntity.class),
                                org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                                .thenAnswer(invocation -> new WorkflowResponse());

                workflowRestService.initDefaultWorkflow(BytedeskConsts.DEFAULT_ORGANIZATION_UID);

                ArgumentCaptor<WorkflowEntity> entityCaptor = ArgumentCaptor.forClass(WorkflowEntity.class);
                org.mockito.Mockito.verify(workflowRepository, org.mockito.Mockito.atLeastOnce())
                                .save(entityCaptor.capture());

                List<WorkflowEntity> savedEntities = entityCaptor.getAllValues();
                Map<String, WorkflowEntity> workflowByUid = savedEntities.stream()
                                .collect(java.util.stream.Collectors.toMap(WorkflowEntity::getUid, Function.identity(),
                                                (left, right) -> right));

                WorkflowEntity ivrEntity = workflowByUid.get(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID);
                assertThat(ivrEntity).isNotNull();
                assertThat(ivrEntity.getType()).isEqualTo(WorkflowTypeEnum.IVR.name());
                assertThat(ivrEntity.getCurrentNodeId()).isEqualTo(WorkflowInitData.DEFAULT_IVR_START_NODE_ID);
                assertThat(ivrEntity.getSchema()).contains("积分/余额查询");
                assertThat(ivrEntity.getSchema()).contains("订单信息查询");
                assertThat(ivrEntity.getSchema()).contains("服务政策播报");
                assertThat(ivrEntity.getSchema()).contains("机器人对话");
                assertThat(ivrEntity.getSchema()).contains("9201");
                assertThat(ivrEntity.getSchema()).contains("9203");
                assertThat(ivrEntity.getSchema()).contains("\"type\": \"bot\"");
                assertThat(ivrEntity.getSchema()).contains("keyboard");

                WorkflowEntity satisfactionEntity = workflowByUid
                                .get(BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID);
                assertThat(satisfactionEntity).isNotNull();
                assertThat(satisfactionEntity.getType()).isEqualTo(WorkflowTypeEnum.IVR.name());
                assertThat(satisfactionEntity.getCurrentNodeId())
                                .isEqualTo(WorkflowInitData.DEFAULT_IVR_SATISFACTION_START_NODE_ID);
                assertThat(satisfactionEntity.getSchema()).contains("满意度评分");
                assertThat(satisfactionEntity.getSchema()).contains("非常满意");
                assertThat(satisfactionEntity.getSchema()).contains("留下您的建议");

                WorkflowEntity passwordEntity = workflowByUid
                                .get(BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID);
                assertThat(passwordEntity).isNotNull();
                assertThat(passwordEntity.getType()).isEqualTo(WorkflowTypeEnum.IVR.name());
                assertThat(passwordEntity.getCurrentNodeId())
                                .isEqualTo(WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID);
                assertThat(passwordEntity.getSchema()).contains("6 位服务密码");
                assertThat(passwordEntity.getSchema()).contains("验证成功");
                assertThat(passwordEntity.getSchema()).contains("验证失败转人工");

                WorkflowEntity botEntity = workflowByUid.get(BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID);
                assertThat(botEntity).isNotNull();
                assertThat(botEntity.getType()).isEqualTo(WorkflowTypeEnum.IVR.name());
                assertThat(botEntity.getCurrentNodeId()).isEqualTo(WorkflowInitData.DEFAULT_IVR_BOT_START_NODE_ID);
                assertThat(botEntity.getSchema()).contains("机器人语音服务演示");
                assertThat(botEntity.getSchema()).contains("9201");
                assertThat(botEntity.getSchema()).contains("9203");
        }

        @Test
        void resetShouldUseServerSideDefaultBotSchemaForDefaultBotWorkflow() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity botWorkflow = WorkflowEntity.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID)
                                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .schema("old")
                                .currentNodeId("old-start")
                                .build();

                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID))
                                .thenReturn(Optional.of(botWorkflow));
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(modelMapper.map(any(WorkflowEntity.class),
                                org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                                .thenAnswer(invocation -> new WorkflowResponse());

                WorkflowResponse response = workflowRestService.reset(WorkflowRequest.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID)
                                .build());

                assertThat(response).isNotNull();
                assertThat(botWorkflow.getCurrentNodeId()).isEqualTo(WorkflowInitData.DEFAULT_IVR_BOT_START_NODE_ID);
                assertThat(botWorkflow.getSchema()).contains("机器人语音服务演示");
                assertThat(botWorkflow.getSchema()).contains("ivr-bot-keyboard-main");
                assertThat(botWorkflow.getSchema()).contains("9201");
                assertThat(botWorkflow.getSchema()).contains("9203");
                assertThat(botWorkflow.getSchema()).contains("\"type\": \"bot\"");
        }

        @Test
        void resetShouldUseOwnSchemaForDefaultIvrWorkflow() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity ivrWorkflow = WorkflowEntity.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID)
                                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .schema("old")
                                .currentNodeId("old-start")
                                .build();

                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID))
                                .thenReturn(Optional.of(ivrWorkflow));
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(modelMapper.map(any(WorkflowEntity.class),
                                org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                                .thenAnswer(invocation -> new WorkflowResponse());

                WorkflowResponse response = workflowRestService.reset(WorkflowRequest.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID)
                                .build());

                assertThat(response).isNotNull();
                assertThat(ivrWorkflow.getCurrentNodeId()).isEqualTo(WorkflowInitData.DEFAULT_IVR_START_NODE_ID);
                assertThat(ivrWorkflow.getSchema()).contains("积分/余额查询");
                assertThat(ivrWorkflow.getSchema()).contains("机器人模式说明");
                assertThat(ivrWorkflow.getSchema()).contains("ivr-keyboard-bot-menu");
        }

        @Test
        void resetShouldUseOwnSchemaForDefaultSatisfactionWorkflow() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity satisfactionWorkflow = WorkflowEntity.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID)
                                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .schema("old")
                                .currentNodeId("old-start")
                                .build();

                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID))
                                .thenReturn(Optional.of(satisfactionWorkflow));
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(modelMapper.map(any(WorkflowEntity.class),
                                org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                                .thenAnswer(invocation -> new WorkflowResponse());

                WorkflowResponse response = workflowRestService.reset(WorkflowRequest.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID)
                                .build());

                assertThat(response).isNotNull();
                assertThat(satisfactionWorkflow.getCurrentNodeId())
                                .isEqualTo(WorkflowInitData.DEFAULT_IVR_SATISFACTION_START_NODE_ID);
                assertThat(satisfactionWorkflow.getSchema()).contains("满意度评分");
                assertThat(satisfactionWorkflow.getSchema()).contains("非常满意");
                assertThat(satisfactionWorkflow.getSchema()).doesNotContain("机器人模式说明");
        }

        @Test
        void resetShouldUseOwnSchemaForDefaultPasswordVerificationWorkflow() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity passwordWorkflow = WorkflowEntity.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID)
                                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .schema("old")
                                .currentNodeId("old-start")
                                .build();

                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID))
                                .thenReturn(Optional.of(passwordWorkflow));
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(modelMapper.map(any(WorkflowEntity.class),
                                org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                                .thenAnswer(invocation -> new WorkflowResponse());

                WorkflowResponse response = workflowRestService.reset(WorkflowRequest.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID)
                                .build());

                assertThat(response).isNotNull();
                assertThat(passwordWorkflow.getCurrentNodeId())
                                .isEqualTo(WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID);
                assertThat(passwordWorkflow.getSchema()).contains("6 位服务密码");
                assertThat(passwordWorkflow.getSchema()).contains("验证失败转人工");
                assertThat(passwordWorkflow.getSchema()).doesNotContain("满意度评分");
        }

        @Test
        void resetShouldRejectUnsupportedWorkflow() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity customWorkflow = WorkflowEntity.builder()
                                .uid("custom_uid")
                                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .build();

                when(workflowRepository.findByUid("custom_uid")).thenReturn(Optional.of(customWorkflow));

                assertThatThrownBy(() -> workflowRestService.reset(WorkflowRequest.builder().uid("custom_uid").build()))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("does not support demo reset");
        }

        @Test
        void queryIvrDemoTemplateOptionsShouldUseWorkflowInitDataMetadata() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                List<WorkflowTemplateOptionResponse> options = workflowRestService.queryIvrDemoTemplateOptions();

                assertThat(options).hasSize(4);
                assertThat(options).extracting(WorkflowTemplateOptionResponse::getValue)
                                .containsExactly(
                                                "demo-default",
                                                "demo-satisfaction",
                                                "demo-password-verification",
                                                "demo-bot");
                assertThat(options.get(0).getLabel()).isEqualTo(WorkflowInitData.DEFAULT_IVR_WORKFLOW_NAME);
                assertThat(options.get(0).getDescription()).isEqualTo(WorkflowInitData.DEFAULT_IVR_WORKFLOW_DESCRIPTION);
                assertThat(options.get(0).getSchema()).contains("积分/余额查询");
                assertThat(options.get(1).getLabel()).isEqualTo(WorkflowInitData.DEFAULT_IVR_SATISFACTION_WORKFLOW_NAME);
                assertThat(options.get(1).getSchema()).contains("满意度评分");
                assertThat(options.get(2).getLabel()).isEqualTo(WorkflowInitData.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_NAME);
                assertThat(options.get(2).getSchema()).contains("6 位服务密码");
                assertThat(options.get(3).getLabel()).isEqualTo(WorkflowInitData.DEFAULT_IVR_BOT_WORKFLOW_NAME);
                assertThat(options.get(3).getSchema()).contains("机器人语音服务演示");
        }

        @Test
        void initDefaultIvrWorkflowShouldRefreshExistingDefaultSchemaWhenOutdated() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity existingDefaultIvr = WorkflowEntity.builder()
                                .uid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID)
                                .orgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID)
                                .nickname(WorkflowInitData.DEFAULT_IVR_WORKFLOW_NAME)
                                .description(WorkflowInitData.DEFAULT_IVR_WORKFLOW_DESCRIPTION)
                                .schema("{\"nodes\":[],\"edges\":[]}")
                                .currentNodeId(WorkflowInitData.DEFAULT_IVR_START_NODE_ID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .build();

                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID))
                                .thenReturn(Optional.of(existingDefaultIvr));
                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_SATISFACTION_WORKFLOW_UID))
                                .thenReturn(Optional.empty());
                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID))
                                .thenReturn(Optional.empty());
                when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_BOT_WORKFLOW_UID))
                                .thenReturn(Optional.empty());
                when(workflowSettingsRestService.getOrCreateDefault(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                                .thenReturn(new WorkflowSettingsEntity());
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(modelMapper.map(any(WorkflowEntity.class),
                                org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                                .thenAnswer(invocation -> new WorkflowResponse());

                workflowRestService.initDefaultIvrWorkflow(BytedeskConsts.DEFAULT_ORGANIZATION_UID);

                assertThat(existingDefaultIvr.getSchema()).contains("按 4 体验机器人对话");
                assertThat(existingDefaultIvr.getSchema()).contains("ivr-keyboard-bot-menu");
                assertThat(existingDefaultIvr.getSchema()).contains("9201");
                assertThat(existingDefaultIvr.getSchema()).contains("9203");
        }

        @Test
        void findByUidShouldAutoRefreshStaleOrgScopedDefaultIvrWorkflow() {
                WorkflowRestService workflowRestService = new WorkflowRestService(
                                workflowRepository,
                                modelMapper,
                                uidUtils,
                                workflowSettingsRestService);

                WorkflowEntity existingOrgDefaultIvr = WorkflowEntity.builder()
                                .uid("1916671518311462_" + WorkflowInitData.DEFAULT_IVR_WORKFLOW_UID_SUFFIX)
                                .orgUid("1916671518311462")
                                .nickname(WorkflowInitData.DEFAULT_IVR_WORKFLOW_NAME)
                                .description(WorkflowInitData.DEFAULT_IVR_WORKFLOW_DESCRIPTION)
                                .schema("{\"nodes\":[],\"edges\":[]}")
                                .currentNodeId(WorkflowInitData.DEFAULT_IVR_START_NODE_ID)
                                .type(WorkflowTypeEnum.IVR.name())
                                .build();

                when(workflowRepository.findByUid(existingOrgDefaultIvr.getUid()))
                                .thenReturn(Optional.of(existingOrgDefaultIvr));
                when(workflowSettingsRestService.getOrCreateDefault("1916671518311462"))
                                .thenReturn(new WorkflowSettingsEntity());
                when(workflowRepository.save(any(WorkflowEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                WorkflowEntity workflow = workflowRestService.findByUid(existingOrgDefaultIvr.getUid())
                                .orElseThrow();

                assertThat(workflow.getSchema()).contains("按 4 体验机器人对话");
                assertThat(workflow.getSchema()).contains("ivr-keyboard-bot-menu");
                assertThat(workflow.getSchema()).contains("9201");
                assertThat(workflow.getSchema()).contains("9203");
        }
}