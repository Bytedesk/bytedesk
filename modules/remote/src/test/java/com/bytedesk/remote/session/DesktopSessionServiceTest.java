package com.bytedesk.remote.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.remote.audit.DesktopAuditLogRepository;
import com.bytedesk.remote.audit.DesktopAuditLogEntity;
import com.bytedesk.remote.device.DesktopDeviceRepository;
import com.bytedesk.remote.device.DesktopDeviceRegisterRequest;
import com.bytedesk.remote.session.DesktopActionRequest.Action;
import org.mockito.ArgumentCaptor;
import java.time.ZonedDateTime;

class DesktopSessionServiceTest {
    
    private final DesktopSessionRepository sessions = mock(DesktopSessionRepository.class);
    private final DesktopDeviceRepository devices = mock(DesktopDeviceRepository.class);
    private final DesktopAuditLogRepository audits = mock(DesktopAuditLogRepository.class);
    private final AuthService auth = mock(AuthService.class);
    private final ThreadRestService threads = mock(ThreadRestService.class);
    private final DesktopSessionEvents events = mock(DesktopSessionEvents.class);
    private DesktopSessionService service;

    @BeforeEach
    void setUp() {
        service = new DesktopSessionService(sessions, devices, audits, auth, threads, events);
        when(auth.getUser()).thenReturn(user("agent", "org"));
        ThreadEntity thread = ThreadEntity.builder().uid("thread").orgUid("org").owner(user("agent", "org"))
                .status("CHATTING").topic("org/thread")
                .agent(UserProtobuf.builder().uid("agent-profile").build().toJson())
                .user(UserProtobuf.builder().uid("visitor").build().toJson()).build();
        when(threads.findByUid("thread")).thenReturn(Optional.of(thread));
        when(sessions.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void onlyAssignedAgentInTheSameTenantCanInvite() {
        when(auth.getUser()).thenReturn(user("other", "org"));
        var error = assertThrows(ResponseStatusException.class,
                () -> service.invite(new DesktopSessionCreateRequest("thread")));
        assertEquals(403, error.getStatusCode().value());
        verify(sessions, never()).save(any());
    }

    @Test
    void invitationIsSingleUseAndControlRequiresHostApproval() {
        var invitation = service.invite(new DesktopSessionCreateRequest("thread"));
        var entity = new java.util.concurrent.atomic.AtomicReference<DesktopSessionEntity>();
        verify(sessions).save(argThat(saved -> {
            entity.set(saved);
            return true;
        }));
        when(sessions.lockByUid(invitation.session().uid())).thenAnswer(i -> Optional.of(entity.get()));
        var joined = service.join(invitation.session().uid(),
                new DesktopDeviceRegisterRequest(invitation.invitationToken(), "Test desktop", "darwin"));
        assertNotNull(joined.hostToken());
        assertThrows(ResponseStatusException.class, () -> service.join(invitation.session().uid(),
                new DesktopDeviceRegisterRequest(invitation.invitationToken(), "Other desktop", "darwin")));
        assertFalse(joined.session().controlGranted());
    }

    @Test
    void guestTokenCannotCrossSessionsAndViewerCannotSelfGrant() {
        var joined = joinedSession();
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.heartbeat(joined.uid(), "wrong", 0)).getStatusCode().value());
        service.action(joined.uid(), joined.token(), Action.CONNECTED);
        service.action(joined.uid(), null, Action.REQUEST_CONTROL);
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.action(joined.uid(), null, Action.GRANT_CONTROL)).getStatusCode().value());
        var granted = service.action(joined.uid(), joined.token(), Action.GRANT_CONTROL);
        assertTrue(granted.controlGranted());
        assertTrue(granted.leaseUntil() > granted.serverTime());
        service.action(joined.uid(), joined.token(), Action.REVOKE_CONTROL);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.action(joined.uid(), joined.token(), Action.GRANT_CONTROL)).getStatusCode().value());
    }

    @Test
    void tenantIsolationAndTerminalStateAreEnforced() {
        var joined = joinedSession();
        when(auth.getUser()).thenReturn(user("agent", "different-org"));
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.heartbeat(joined.uid(), null, 0)).getStatusCode().value());
        var ended = service.action(joined.uid(), joined.token(), Action.TERMINATE);
        assertEquals(DesktopSessionStatusEnum.TERMINATED, ended.status());
        assertFalse(ended.controlGranted());
        assertEquals(0, ended.leaseUntil());
        assertTrue(ended.signals().isEmpty());
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.action(joined.uid(), joined.token(), Action.CONNECTED)).getStatusCode().value());
        verify(events).close(any());
    }

    @Test
    void heartbeatExpiryEndsSessionAndClearsTransientSignals() {
        var joined = joinedSession();
        joined.entity().setHostSeenAt(ZonedDateTime.now().minusMinutes(1));
        joined.entity().setHostSignals("[]");
        var ended = service.heartbeat(joined.uid(), joined.token(), 0);
        assertEquals(DesktopSessionStatusEnum.TERMINATED, ended.status());
        assertNull(joined.entity().getActiveThreadUid());
        assertNull(joined.entity().getHostSignals());
    }

    @Test
    void auditIdentifiesTheParticipantWhoRequestsAndRejectsControl() {
        var joined = joinedSession();
        service.action(joined.uid(), joined.token(), Action.CONNECTED);
        service.action(joined.uid(), null, Action.REQUEST_CONTROL);
        service.action(joined.uid(), joined.token(), Action.REJECT);
        var captured = ArgumentCaptor.forClass(DesktopAuditLogEntity.class);
        verify(audits, atLeastOnce()).save(captured.capture());
        var history = captured.getAllValues();
        assertTrue(history.stream().anyMatch(a -> "CONTROL_REQUESTED".equals(a.getEventType())
                && "agent".equals(a.getUserUid())));
        assertTrue(history.stream().anyMatch(a -> "REJECTED".equals(a.getEventType())
                && "visitor".equals(a.getUserUid())));
    }

    private record Joined(String uid, String token, DesktopSessionEntity entity) {}

    private Joined joinedSession() {
        var invitation = service.invite(new DesktopSessionCreateRequest("thread"));
        var captured = ArgumentCaptor.forClass(DesktopSessionEntity.class);
        verify(sessions).save(captured.capture());
        var entity = captured.getValue();
        when(sessions.lockByUid(entity.getUid())).thenReturn(Optional.of(entity));
        var joined = service.join(entity.getUid(),
                new DesktopDeviceRegisterRequest(invitation.invitationToken(), "Desktop", "darwin"));
        return new Joined(entity.getUid(), joined.hostToken(), entity);
    }

    private UserEntity user(String uid, String orgUid) {
        return UserEntity.builder().uid(uid)
                .currentOrganization(OrganizationEntity.builder().uid(orgUid).build()).build();
    }
}
