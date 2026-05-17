package com.bytedesk.core.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
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

        when(workflowRepository.findByUid(BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID)).thenReturn(Optional.empty());
        when(workflowRepository.findByUid("df_org_uid_df_workflow_builder")).thenReturn(Optional.empty());
        when(workflowSettingsRestService.getOrCreateDefault(BytedeskConsts.DEFAULT_ORGANIZATION_UID))
                .thenReturn(new WorkflowSettingsEntity());
        when(workflowRepository.save(any(WorkflowEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(WorkflowEntity.class), org.mockito.ArgumentMatchers.eq(WorkflowResponse.class)))
                .thenAnswer(invocation -> new WorkflowResponse());

        workflowRestService.initDefaultWorkflow(BytedeskConsts.DEFAULT_ORGANIZATION_UID);

        ArgumentCaptor<WorkflowEntity> entityCaptor = ArgumentCaptor.forClass(WorkflowEntity.class);
        org.mockito.Mockito.verify(workflowRepository, org.mockito.Mockito.atLeastOnce()).save(entityCaptor.capture());

        List<WorkflowEntity> savedEntities = entityCaptor.getAllValues();
        WorkflowEntity ivrEntity = savedEntities.stream()
                .filter(entity -> BytedeskConsts.DEFAULT_IVR_WORKFLOW_UID.equals(entity.getUid()))
                .findFirst()
                .orElse(null);

        assertThat(ivrEntity).isNotNull();
        assertThat(ivrEntity.getType()).isEqualTo(WorkflowTypeEnum.IVR.name());
        assertThat(ivrEntity.getCurrentNodeId()).isEqualTo(WorkflowInitData.DEFAULT_IVR_START_NODE_ID);
        assertThat(ivrEntity.getSchema()).contains("积分/余额查询");
        assertThat(ivrEntity.getSchema()).contains("订单取消");
        assertThat(ivrEntity.getSchema()).contains("服务政策播报");
        assertThat(ivrEntity.getSchema()).contains("keyboard");
    }
}