package com.bytedesk.remote.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.bytedesk.remote.device.RemoteDeviceRepository;
import com.bytedesk.remote.config.RemoteActionRequest.Action;
import com.bytedesk.remote.controller.RemoteHostController;
import com.bytedesk.remote.device.RemoteDeviceRegisterRequest;
import com.bytedesk.remote.invitation.RemoteInvitationEntity;
import com.bytedesk.remote.session.event.RemoteSessionAuditEvent;
import org.mockito.ArgumentCaptor;
import java.time.ZonedDateTime;

class RemoteSessionServiceTest {
    
    private final RemoteSessionRepository sessions = mock(RemoteSessionRepository.class);
    private final RemoteDeviceRepository devices = mock(RemoteDeviceRepository.class);
    private final java.util.List<Object> published = new java.util.ArrayList<>();
    private final AuthService auth = mock(AuthService.class);
    private final ThreadRestService threads = mock(ThreadRestService.class);
    private final RemoteSessionEvents events = mock(RemoteSessionEvents.class);
    private RemoteSessionService service;

    @BeforeEach
    void setUp() {
        service = new RemoteSessionService(sessions, devices, auth, threads, events, published::add);
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
                () -> service.invite(new RemoteSessionCreateRequest("thread")));
        assertEquals(403, error.getStatusCode().value());
        verify(sessions, never()).save(any());
    }

    @Test
    void deviceInvitationRequiresSeparateHostSecretAndStillRequiresControlConsent() throws Exception {
        var hostSecret = "h".repeat(43);
        var hash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
                .digest(hostSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        var invitation = RemoteInvitationEntity.builder().userUid("host").hostSecretHash(hash).build();
        var response = service.inviteDevice(user("agent", "org"), invitation);
        var captured = ArgumentCaptor.forClass(RemoteSessionEntity.class);
        verify(sessions).save(captured.capture());
        var entity = captured.getValue();
        when(sessions.lockByUid(response.uid())).thenReturn(Optional.of(entity));
        assertThrows(ResponseStatusException.class, () -> service.join(response.uid(),
                new RemoteDeviceRegisterRequest("x".repeat(43), "Desktop", "darwin")));
        var joined = service.join(response.uid(),
                new RemoteDeviceRegisterRequest(hostSecret, "Desktop", "darwin"));
        assertFalse(joined.session().controlGranted());
        assertThrows(ResponseStatusException.class,
                () -> service.action(response.uid(), null, Action.GRANT_CONTROL));
        service.action(response.uid(), joined.hostToken(), Action.CONNECTED);
        service.action(response.uid(), null, Action.REQUEST_CONTROL);
        service.action(response.uid(), joined.hostToken(), Action.GRANT_CONTROL);
        assertFalse(service.action(response.uid(), joined.hostToken(), Action.REVOKE_CONTROL).controlGranted());
        verify(events, never()).notifyThread(any(), anyString());
    }

    @Test
    void rejectedDeviceInvitationEndsTheWaitingSession() {
        var invitation = RemoteInvitationEntity.builder().userUid("host").hostSecretHash("hash").build();
        var response = service.inviteDevice(user("agent", "org"), invitation);
        var captured = ArgumentCaptor.forClass(RemoteSessionEntity.class);
        verify(sessions).save(captured.capture());
        var entity = captured.getValue();
        when(sessions.lockByUid(response.uid())).thenReturn(Optional.of(entity));
        assertThrows(ResponseStatusException.class, () -> service.rejectDeviceInvitation(response.uid(), "other"));
        service.rejectDeviceInvitation(response.uid(), "host");
        assertEquals(RemoteSessionStatusEnum.TERMINATED, entity.getStatus());
        assertEquals("REJECTED", entity.getTerminationReason());
        assertNull(entity.getInvitationHash());
    }

    @Test
    void invitationIsSingleUseAndControlRequiresHostApproval() {
        var invitation = service.invite(new RemoteSessionCreateRequest("thread"));
        var entity = new java.util.concurrent.atomic.AtomicReference<RemoteSessionEntity>();
        verify(sessions).save(argThat(saved -> {
            entity.set(saved);
            return true;
        }));
        when(sessions.lockByUid(invitation.session().uid())).thenAnswer(i -> Optional.of(entity.get()));
        var joined = service.join(invitation.session().uid(),
                new RemoteDeviceRegisterRequest(invitation.invitationToken(), "Test desktop", "darwin"));
        assertNotNull(joined.hostToken());
        assertThrows(ResponseStatusException.class, () -> service.join(invitation.session().uid(),
                new RemoteDeviceRegisterRequest(invitation.invitationToken(), "Other desktop", "darwin")));
        assertFalse(joined.session().controlGranted());
    }

    @Test
    void hostCanGrantCombinedConsentAfterConnectingWithoutASeparateViewerRequest() throws Exception {
        var joined = joinedSession();
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(new RemoteHostController(service)).build();
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.action(joined.uid(), joined.token(), Action.GRANT_CONTROL)).getStatusCode().value());
        service.action(joined.uid(), joined.token(), Action.CONNECTED);
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.action(joined.uid(), null, Action.GRANT_CONTROL)).getStatusCode().value());
        mvc.perform(post("/visitor/api/v1/remote/sessions/" + joined.uid() + "/action")
                .header("X-Remote-Token", joined.token()).contentType("application/json")
                .content("{\"action\":\"GRANT_CONTROL\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.controlGranted").value(true))
                .andExpect(jsonPath("$.data.hostToken").doesNotExist());
        var granted = service.heartbeat(joined.uid(), joined.token(), 0);
        assertTrue(granted.controlGranted());
        assertTrue(granted.leaseUntil() > granted.serverTime());
        var revoked = service.action(joined.uid(), joined.token(), Action.REVOKE_CONTROL);
        assertFalse(revoked.controlGranted());
        assertEquals(0, revoked.leaseUntil());
        assertFalse(service.heartbeat(joined.uid(), joined.token(), 0).controlGranted());
        service.action(joined.uid(), joined.token(), Action.TERMINATE);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.action(joined.uid(), joined.token(), Action.GRANT_CONTROL)).getStatusCode().value());
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
        assertFalse(service.heartbeat(joined.uid(), joined.token(), 0).controlGranted());
    }

    @Test
    void tenantIsolationAndTerminalStateAreEnforced() {
        var joined = joinedSession();
        when(auth.getUser()).thenReturn(user("agent", "different-org"));
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.heartbeat(joined.uid(), null, 0)).getStatusCode().value());
        var ended = service.action(joined.uid(), joined.token(), Action.TERMINATE);
        assertEquals(RemoteSessionStatusEnum.TERMINATED, ended.status());
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
        assertEquals(RemoteSessionStatusEnum.TERMINATED, ended.status());
        assertNull(joined.entity().getActiveThreadUid());
        assertNull(joined.entity().getHostSignals());
    }

    @Test
    void auditIdentifiesTheParticipantWhoRequestsAndRejectsControl() {
        var joined = joinedSession();
        service.action(joined.uid(), joined.token(), Action.CONNECTED);
        service.action(joined.uid(), null, Action.REQUEST_CONTROL);
        service.action(joined.uid(), joined.token(), Action.REJECT);
        var history = published.stream().filter(RemoteSessionAuditEvent.class::isInstance)
                .map(RemoteSessionAuditEvent.class::cast).toList();
        assertTrue(history.stream().anyMatch(a -> "CONTROL_REQUESTED".equals(a.eventType())
                && "agent".equals(a.actorUid())));
        assertTrue(history.stream().anyMatch(a -> "REJECTED".equals(a.eventType())
                && "visitor".equals(a.actorUid())));
    }

    private record Joined(String uid, String token, RemoteSessionEntity entity) {}

    private Joined joinedSession() {
        var invitation = service.invite(new RemoteSessionCreateRequest("thread"));
        var captured = ArgumentCaptor.forClass(RemoteSessionEntity.class);
        verify(sessions).save(captured.capture());
        var entity = captured.getValue();
        when(sessions.lockByUid(entity.getUid())).thenReturn(Optional.of(entity));
        var joined = service.join(entity.getUid(),
                new RemoteDeviceRegisterRequest(invitation.invitationToken(), "Desktop", "darwin"));
        return new Joined(entity.getUid(), joined.hostToken(), entity);
    }

    private UserEntity user(String uid, String orgUid) {
        return UserEntity.builder().uid(uid)
                .currentOrganization(OrganizationEntity.builder().uid(orgUid).build()).build();
    }
}
