/*
 * @Author: GitHub Copilot
 * @Date: 2026-02-07
 * @Description: Tests for routing pool accept handling
 */
package com.bytedesk.service.routing_pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponseSimple;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentRestService;

@ExtendWith(MockitoExtension.class)
class RoutingPoolRestServiceTest {

    @Mock
    private RoutingPoolRepository routingPoolRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private AgentRestService agentRestService;

    private RoutingPoolRestService routingPoolRestService;

    @BeforeEach
    void setUp() {
        routingPoolRestService = new RoutingPoolRestService(
                routingPoolRepository,
                modelMapper,
                uidUtils,
                authService,
                permissionService,
                agentRestService);
    }

    @Test
    void acceptManualThreadDeletesRoutingPoolWhenThreadAlreadyAccepted() {
        RoutingPoolEntity entity = new RoutingPoolEntity();
        entity.setUid("rp-1");
        entity.setType(RoutingPoolTypeEnum.MANUAL_THREAD.name());
        entity.setStatus(RoutingPoolStatusEnum.WAITING.name());
        entity.setThreadUid("thread-1");

        when(routingPoolRepository.findByUid("rp-1")).thenReturn(Optional.of(entity));
        when(agentRestService.acceptByAgent(any(ThreadRequest.class)))
                .thenThrow(new IllegalStateException("thread already accepted: CHATTING"));
        when(routingPoolRepository.save(any(RoutingPoolEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RoutingPoolRequest request = RoutingPoolRequest.builder().uid("rp-1").build();
        ThreadResponseSimple result = assertDoesNotThrow(() -> routingPoolRestService.acceptManualThread(request));

        assertThat(result).isNull();
        ArgumentCaptor<RoutingPoolEntity> captor = ArgumentCaptor.forClass(RoutingPoolEntity.class);
        verify(routingPoolRepository).save(captor.capture());
        RoutingPoolEntity saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(RoutingPoolStatusEnum.ACCEPTED.name());
        assertThat(saved.isDeleted()).isTrue();
    }
}
