package com.bytedesk.remote.invitation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.bytedesk.remote.config.RemoteProperties;

/**
 * remote.properties 服务端开关（bytedesk.remote.enabled=false）行为：
 * 邀请码相关接口必须拒绝，防止绕过前端门控直接调用。
 */
class RemoteInvitationControllerTest {

    private final RemoteInvitationService invitations = mock(RemoteInvitationService.class);
    private final RemoteProperties properties = new RemoteProperties();

    @Test
    void disabledRemoteRejectsInvitationEndpoints() {
        properties.setEnabled(false);
        var controller = new RemoteInvitationController(invitations, properties);

        var disabled = assertThrows(ResponseStatusException.class,
                () -> controller.issue());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, disabled.getStatusCode());

        disabled = assertThrows(ResponseStatusException.class,
                () -> controller.connect(new RemoteInvitationController.Connect("123456")));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, disabled.getStatusCode());

        disabled = assertThrows(ResponseStatusException.class,
                () -> controller.pending("invitation-uid"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, disabled.getStatusCode());

        verifyNoInteractions(invitations);
    }

    @Test
    void enabledRemoteDelegatesToService() {
        properties.setEnabled(true);
        var controller = new RemoteInvitationController(invitations, properties);

        assertNotNull(controller.issue());
        verify(invitations).issue();
    }
}
