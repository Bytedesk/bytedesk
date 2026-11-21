package com.bytedesk.core.connection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class ConnectionRestServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PresenceTtlResolver presenceTtlResolver;

    @Mock
    private ConnectionMetrics connectionMetrics;

    private ConnectionRestService connectionRestService;

    @BeforeEach
    void setUp() {
        connectionRestService = new ConnectionRestService(
                connectionRepository,
                modelMapper,
                uidUtils,
                authService,
                userRepository,
                presenceTtlResolver,
                connectionMetrics,
                null,
                null);
    }

    @Test
    void markConnectedLocksExistingConnectionBeforeUpdatingIt() {
        String clientId = "user/client/device";
        ConnectionEntity existing = ConnectionEntity.builder()
                .uid("connection-uid")
                .clientId(clientId)
                .userUid("user")
                .status(ConnectionStatusEnum.DISCONNECTED.name())
                .build();
        when(connectionRepository.findByClientIdForUpdate(clientId)).thenReturn(Optional.of(existing));
        when(presenceTtlResolver.resolve(anyString(), any())).thenReturn(90);
        when(connectionRepository.saveAndFlush(any(ConnectionEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByUidWithOrganizations("user")).thenReturn(Optional.empty());

        connectionRestService.markConnected(
                "user", null, clientId, "device", "MQTT", null, null, null, 90);

        assertEquals(ConnectionStatusEnum.CONNECTED.name(), existing.getStatus());
        verify(connectionRepository).findByClientIdForUpdate(clientId);
        verify(connectionRepository, never()).findByClientId(clientId);
    }
}
