package com.bytedesk.service.workgroup;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import com.bytedesk.ai.robot.settings.RobotRoutingSettingsService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.constant.I18ServiceConsts;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsRestService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkgroupRestServiceTest {

    @Mock
    private WorkgroupRepository workgroupRepository;

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private AgentRestService agentRestService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private WorkgroupSettingsRestService workgroupSettingsRestService;

    @Mock
    private OrganizationRestService organizationRestService;

    @Mock
    private RobotRoutingSettingsService robotRoutingSettingsService;

    private WorkgroupRestService workgroupRestService;

    @BeforeEach
    void setUp() {
        workgroupRestService = spy(new WorkgroupRestService(
                workgroupRepository,
                threadRepository,
                threadRestService,
                agentRestService,
                modelMapper,
                uidUtils,
                authService,
                workgroupSettingsRestService,
                organizationRestService,
                robotRoutingSettingsService));
    }

    @Test
    void createShouldRejectWhenOrganizationWorkgroupCapacityIsExhausted() {
        WorkgroupRequest request = WorkgroupRequest.builder()
                .uid("wg-1")
                .orgUid("org-1")
                .nickname("Workgroup A")
                .build();

        OrganizationEntity organization = OrganizationEntity.builder()
                .uid("org-1")
                .maxWorkgroups(1)
                .build();

        when(authService.getUser()).thenReturn(null);
        when(organizationRestService.findByUid("org-1")).thenReturn(Optional.of(organization));
        when(workgroupRepository.countByOrgUidAndDeletedFalse("org-1")).thenReturn(1L);

        assertThatThrownBy(() -> workgroupRestService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(I18ServiceConsts.I18N_ORGANIZATION_WORKGROUP_LIMIT_EXCEEDED);

        verify(workgroupRepository, never()).save(org.mockito.ArgumentMatchers.any(WorkgroupEntity.class));
    }
}