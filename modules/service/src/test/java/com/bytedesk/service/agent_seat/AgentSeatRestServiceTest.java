package com.bytedesk.service.agent_seat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.constant.I18ServiceConsts;

@ExtendWith(MockitoExtension.class)
class AgentSeatRestServiceTest {

    @Mock
    private AgentSeatRepository agentSeatRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private AgentSeatDomainService agentSeatDomainService;

    private AgentSeatRestService agentSeatRestService;

    @BeforeEach
    void setUp() {
        agentSeatRestService = new AgentSeatRestService(
                agentSeatRepository,
                new ModelMapper(),
                uidUtils,
                authService,
                permissionService,
                agentSeatDomainService);
    }

    @Test
    void createShouldRejectWhenAssignedAgentAlreadyBoundToAnotherSeat() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");

        AgentSeatRequest request = AgentSeatRequest.builder()
                .orgUid("org-1")
                .seatNo("seat-2")
                .assignedAgentUid("agent-1")
                .build();

        AgentSeatEntity existingSeat = AgentSeatEntity.builder()
                .uid("seat-existing")
                .seatNo("seat-1")
                .assignedAgentUid("agent-1")
                .build();

        when(authService.getUser()).thenReturn(user);
        when(permissionService.canCreateAtLevel(AgentSeatPermissions.MODULE_NAME, "ORGANIZATION")).thenReturn(true);
        when(agentSeatRepository.findByAssignedAgentUidAndDeletedFalse("agent-1")).thenReturn(Optional.of(existingSeat));

        assertThatThrownBy(() -> agentSeatRestService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(I18Consts.withArgs(
                        I18ServiceConsts.I18N_AGENT_SEAT_ASSIGNED_AGENT_ALREADY_BOUND,
                        "seat-1"));

        verify(agentSeatRepository, never()).save(any(AgentSeatEntity.class));
    }

    @Test
    void updateShouldAllowKeepingSameAssignedAgentOnSameSeat() {
        AgentSeatEntity existingSeat = AgentSeatEntity.builder()
                .uid("seat-1")
                .orgUid("org-1")
                .seatNo("seat-1")
                .assignedAgentUid("agent-1")
                .build();

        AgentSeatRequest request = AgentSeatRequest.builder()
                .uid("seat-1")
                .assignedAgentUid("agent-1")
                .build();

        when(agentSeatRepository.findByUid("seat-1")).thenReturn(Optional.of(existingSeat));
        when(permissionService.hasEntityPermission(AgentSeatPermissions.MODULE_NAME, "UPDATE", existingSeat)).thenReturn(true);
        when(agentSeatRepository.findByAssignedAgentUidAndDeletedFalse("agent-1")).thenReturn(Optional.of(existingSeat));
        when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgentSeatResponse response = agentSeatRestService.update(request);

        assertThat(response).isNotNull();
        assertThat(response.getUid()).isEqualTo("seat-1");
        assertThat(response.getAssignedAgentUid()).isEqualTo("agent-1");
        verify(agentSeatRepository).save(existingSeat);
    }
}