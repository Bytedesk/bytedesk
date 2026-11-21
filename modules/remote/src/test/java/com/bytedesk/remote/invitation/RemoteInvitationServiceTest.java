package com.bytedesk.remote.invitation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.remote.session.RemoteSessionResponse;
import com.bytedesk.remote.session.RemoteSessionService;
import com.bytedesk.remote.session.RemoteSessionStatusEnum;

class RemoteInvitationServiceTest {
    private final RemoteInvitationRepository invitations = mock(RemoteInvitationRepository.class);
    private final RemoteSessionService sessions = mock(RemoteSessionService.class);
    private final AuthService auth = mock(AuthService.class);
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final RemoteInvitationService service = new RemoteInvitationService(invitations, sessions, auth, redis);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        ValueOperations<String, String> values = mock(ValueOperations.class);
        var reservations = new HashMap<String, String>();
        when(redis.opsForValue()).thenReturn(values);
        when(values.setIfAbsent(anyString(), anyString(), eq(Duration.ofMinutes(5))))
                .thenAnswer(call -> reservations.putIfAbsent(call.getArgument(0), call.getArgument(1)) == null);
        when(values.get(anyString())).thenAnswer(call -> reservations.get((String) call.getArgument(0)));
        when(redis.execute(any(RedisScript.class), anyList(), anyString())).thenReturn(1L);
        when(auth.getUser()).thenReturn(user("host", "host-org"));
        when(sessions.inviteDevice(any(), any())).thenReturn(new RemoteSessionResponse(
                "session", null, null, "viewer", RemoteSessionStatusEnum.INITIALIZED,
                false, false, 0, 0, 0, 0, java.util.List.of()));
    }

    @Test
    void invitationCanBeConsumedOnceByUserFromAnotherOrganization() {
        var issued = service.issue();
        var captured = ArgumentCaptor.forClass(RemoteInvitationEntity.class);
        verify(invitations).save(captured.capture());
        var invitation = captured.getValue();
        when(invitations.lockByUid(issued.uid())).thenReturn(Optional.of(invitation));
        assertTrue(issued.code().matches("[0-9]{6}"));
        assertTrue(issued.hostSecret().matches("[A-Za-z0-9_-]{43}"));
        when(auth.getUser()).thenReturn(user("viewer", "another-org"));
        assertEquals("session", service.connect(issued.code()).uid());
        assertEquals("session", invitation.getSessionUid());
        assertThrows(ResponseStatusException.class, () -> service.connect(issued.code()));
        verify(sessions, times(1)).inviteDevice(any(), eq(invitation));
        assertThrows(ResponseStatusException.class, () -> service.pending(issued.uid()));
        when(auth.getUser()).thenReturn(user("host", "host-org"));
        assertEquals("session", service.pending(issued.uid()).sessionUid());
        service.reject(issued.uid());
        verify(sessions).rejectDeviceInvitation("session", "host");
    }

    @Test
    void expiredOrRevokedCodesCannotCreateSessions() {
        var issued = service.issue();
        var captured = ArgumentCaptor.forClass(RemoteInvitationEntity.class);
        verify(invitations).save(captured.capture());
        var invitation = captured.getValue();
        when(invitations.lockByUid(issued.uid())).thenReturn(Optional.of(invitation));
        invitation.setExpiresAt(ZonedDateTime.now().minusSeconds(1));
        assertThrows(ResponseStatusException.class, () -> service.connect(issued.code()));
        invitation.setExpiresAt(ZonedDateTime.now().plusMinutes(5));
        service.close(issued.uid());
        assertThrows(ResponseStatusException.class, () -> service.connect(issued.code()));
        verify(sessions, never()).inviteDevice(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void invalidFormatAndRateLimitedViewerCannotLookUpAnInvitation() {
        assertEquals(HttpStatus.BAD_REQUEST,
                assertThrows(ResponseStatusException.class, () -> service.connect("12345")).getStatusCode());
        when(redis.execute(any(RedisScript.class), anyList(), anyString())).thenReturn(11L);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS,
                assertThrows(ResponseStatusException.class, () -> service.connect("012345")).getStatusCode());
        verifyNoInteractions(invitations, sessions);
    }

    private UserEntity user(String uid, String orgUid) {
        return UserEntity.builder().uid(uid)
                .currentOrganization(OrganizationEntity.builder().uid(orgUid).build()).build();
    }
}
