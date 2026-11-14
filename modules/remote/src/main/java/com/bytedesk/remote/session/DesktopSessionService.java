package com.bytedesk.remote.session;

import static org.springframework.http.HttpStatus.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.ZonedDateTime;
import java.util.*;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.remote.audit.DesktopAuditLogEntity;
import com.bytedesk.remote.audit.DesktopAuditLogRepository;
import com.bytedesk.remote.device.DesktopDeviceEntity;
import com.bytedesk.remote.device.DesktopDeviceRegisterRequest;
import com.bytedesk.remote.device.DesktopDeviceRepository;
import com.bytedesk.remote.session.DesktopActionRequest.Action;
import com.bytedesk.remote.session.DesktopSessionResponse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DesktopSessionService {
    private final DesktopSessionRepository sessions;
    private final DesktopDeviceRepository devices;
    private final DesktopAuditLogRepository audits;
    private final AuthService auth;
    private final ThreadRestService threads;
    private final DesktopSessionEvents events;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public Invitation invite(DesktopSessionCreateRequest request) {
        var thread = assignedThread(request.threadUid());
        if (sessions.findByActiveThreadUid(thread.getUid()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "This thread already has an active assistance session");
        }
        var visitor = UserProtobuf.fromJson(thread.getUser());
        if (visitor == null || visitor.getUid() == null) {
            throw new ResponseStatusException(CONFLICT, "Thread has no visitor");
        }
        var now = ZonedDateTime.now();
        var secret = secret();
        var session = DesktopSessionEntity.builder().uid(UUID.randomUUID().toString())
                .orgUid(thread.getOrgUid()).userUid(auth.getUser().getUid())
                .threadUid(thread.getUid()).activeThreadUid(thread.getUid())
                .viewerUid(auth.getUser().getUid()).viewerName(auth.getUser().getNickname())
                .hostUid(visitor.getUid()).invitationHash(hash(secret))
                .invitationExpiresAt(now.plusMinutes(2)).expiresAt(now.plusHours(2)).viewerSeenAt(now)
                .build();
        session.setRoomUid(events.open(session));
        sessions.save(session);
        audit(session, "INVITED", session.getViewerUid());
        events.notifyThread(session, "INVITED");
        return new Invitation(snapshot(session, false), secret);
    }

    @Transactional
    public Joined join(String uid, DesktopDeviceRegisterRequest request) {
        var session = lock(uid);
        if (session.getStatus() != DesktopSessionStatusEnum.INITIALIZED ||
                session.getInvitationExpiresAt().isBefore(ZonedDateTime.now()) ||
                !matches(session.getInvitationHash(), request.invitationToken())) {
            throw new ResponseStatusException(FORBIDDEN, "Invitation expired, used or invalid");
        }
        var secret = secret();
        var device = DesktopDeviceEntity.builder().uid(UUID.randomUUID().toString())
                .orgUid(session.getOrgUid()).userUid(session.getHostUid())
                .deviceName(request.deviceName()).osType(request.osType())
                .online(true).lastOnline(ZonedDateTime.now()).build();
        devices.save(device);
        session.setHostDeviceUid(device.getUid());
        session.setHostTokenHash(hash(secret));
        session.setInvitationHash(null);
        session.setHostSeenAt(ZonedDateTime.now());
        session.setStatus(DesktopSessionStatusEnum.CONNECTING);
        audit(session, "ACCEPTED", session.getHostUid());
        events.join(session);
        events.notifyThread(session, "ACCEPTED");
        return new Joined(snapshot(session, true), secret);
    }

    @Transactional
    public DesktopSessionResponse heartbeat(String uid, String hostToken, long inputCount) {
        var session = authorized(uid, hostToken);
        boolean host = hostToken != null;
        expire(session);
        if (!terminal(session)) {
            if (host) {
                if (inputCount < session.getInputCount() || inputCount - session.getInputCount() > 10000) {
                    throw new ResponseStatusException(BAD_REQUEST, "Invalid input counter");
                }
                session.setInputCount(inputCount);
                session.setHostSeenAt(ZonedDateTime.now());
            } else {
                session.setViewerSeenAt(ZonedDateTime.now());
            }
        }
        return snapshot(session, host);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public DesktopSessionResponse action(String uid, String hostToken, Action action) {
        var session = authorized(uid, hostToken);
        boolean host = hostToken != null;
        expire(session);
        if (terminal(session)) {
            if (action == Action.TERMINATE) return snapshot(session, host);
            throw new ResponseStatusException(CONFLICT, "Session has ended");
        }
        String actorUid = host ? session.getHostUid() : session.getViewerUid();
        switch (action) {
            case CONNECTED -> {
                if (session.getHostTokenHash() == null) throw new ResponseStatusException(CONFLICT, "Host has not joined");
                if (session.getStartedAt() == null) {
                    session.setStartedAt(ZonedDateTime.now());
                    session.setStatus(DesktopSessionStatusEnum.CONNECTED);
                    audit(session, "CONNECTED", actorUid);
                    events.notifyThread(session, "CONNECTED");
                }
            }
            case REQUEST_CONTROL -> {
                if (host) throw new ResponseStatusException(FORBIDDEN, "Only the viewer may request control");
                requireConnected(session);
                if (!session.isControlRequested()) {
                    session.setControlRequested(true);
                    audit(session, "CONTROL_REQUESTED", actorUid);
                }
            }
            case GRANT_CONTROL -> {
                if (!host) throw new ResponseStatusException(FORBIDDEN, "Local host consent is required");
                requireConnected(session);
                if (!session.isControlRequested()) throw new ResponseStatusException(CONFLICT, "No control request");
                if (!session.isControlGranted()) {
                    session.setControlGranted(true);
                    audit(session, "CONTROL_GRANTED", actorUid);
                    events.notifyThread(session, "CONTROL_GRANTED");
                }
            }
            case REVOKE_CONTROL -> {
                if (!host) throw new ResponseStatusException(FORBIDDEN, "Only the host may revoke control");
                session.setControlGranted(false);
                session.setControlRequested(false);
                audit(session, "CONTROL_REVOKED", actorUid);
                events.notifyThread(session, "CONTROL_REVOKED");
            }
            case REJECT -> {
                if (!host) throw new ResponseStatusException(FORBIDDEN, "Only the host may reject");
                finish(session, "REJECTED", actorUid);
            }
            case TERMINATE -> finish(session, host ? "HOST_ENDED" : "VIEWER_ENDED", actorUid);
        }
        return snapshot(session, host);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void signal(String uid, String hostToken, DesktopSignalRequest request) {
        var session = authorized(uid, hostToken);
        expire(session);
        if (terminal(session) || session.getHostTokenHash() == null) {
            throw new ResponseStatusException(CONFLICT, "Session cannot accept signals");
        }
        boolean host = hostToken != null;
        if (("offer".equals(request.type()) && !host) || ("answer".equals(request.type()) && host)) {
            throw new ResponseStatusException(FORBIDDEN, "Invalid signaling role");
        }
        // SDP/ICE are transient session data, never chat messages or audit payloads.
        var list = new ArrayList<>(signals(host ? session.getHostSignals() : session.getViewerSignals()));
        if (list.stream().anyMatch(s -> s.id().equals(request.id()))) return;
        if (list.size() >= 128 || (!"candidate".equals(request.type()) &&
                list.stream().anyMatch(s -> s.type().equals(request.type())))) {
            throw new ResponseStatusException(CONFLICT, "Signal limit reached; start a new session");
        }
        list.add(new Signal(request.id(), request.type(), request.payload()));
        String json = JSON.toJSONString(list);
        if (json.length() > 196608) throw new ResponseStatusException(CONTENT_TOO_LARGE, "Signal queue full");
        if (host) session.setHostSignals(json); else session.setViewerSignals(json);
    }

    @Transactional
    public List<DesktopAuditLogEntity> auditHistory(String uid) {
        authorized(uid, null);
        return audits.findBySessionUidOrderByCreatedAtAsc(uid);
    }

    @Transactional
    public void expireByUid(String uid) {
        expire(lock(uid));
    }

    private DesktopSessionEntity authorized(String uid, String token) {
        var session = lock(uid);
        if (token != null) {
            if (!matches(session.getHostTokenHash(), token)) {
                throw new ResponseStatusException(FORBIDDEN, "Invalid host token");
            }
        } else {
            var user = auth.getUser();
            if (user == null || !session.getOrgUid().equals(user.getOrgUid()) ||
                    !session.getViewerUid().equals(user.getUid())) {
                throw new ResponseStatusException(FORBIDDEN, "Not a session participant");
            }
            if (!terminal(session)) assignedThread(session.getThreadUid());
        }
        return session;
    }

    private ThreadEntity assignedThread(String uid) {
        var user = auth.getUser();
        if (user == null || user.getOrgUid() == null || user.getOrgUid().isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Login required");
        }
        var thread = threads.findByUid(uid).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Thread not found"));
        // Agent profile UIDs are not login-user UIDs. The owner is the assigned agent's user.
        if (!user.getOrgUid().equals(thread.getOrgUid()) || thread.getOwner() == null ||
                !user.getUid().equals(thread.getOwner().getUid())) {
            throw new ResponseStatusException(FORBIDDEN, "Only the assigned agent may invite");
        }
        if (thread.isDeleted() || !"CHATTING".equals(thread.getStatus())) {
            throw new ResponseStatusException(CONFLICT, "Thread is not chatting");
        }
        return thread;
    }

    private DesktopSessionEntity lock(String uid) {
        return sessions.lockByUid(uid).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Session not found"));
    }

    private void requireConnected(DesktopSessionEntity session) {
        if (session.getStatus() != DesktopSessionStatusEnum.CONNECTED) {
            throw new ResponseStatusException(CONFLICT, "Session is not connected");
        }
    }

    private void expire(DesktopSessionEntity s) {
        if (terminal(s)) return;
        var now = ZonedDateTime.now();
        if (s.getExpiresAt().isBefore(now) ||
                (s.getStatus() == DesktopSessionStatusEnum.INITIALIZED && s.getInvitationExpiresAt().isBefore(now)) ||
                (s.getHostSeenAt() != null && s.getHostSeenAt().plusSeconds(20).isBefore(now)) ||
                s.getViewerSeenAt().plusSeconds(s.getHostSeenAt() == null ? 120 : 20).isBefore(now)) {
            finish(s, "EXPIRED", null);
        }
    }

    private void finish(DesktopSessionEntity s, String reason, String actorUid) {
        if (terminal(s)) return;
        s.setStatus(DesktopSessionStatusEnum.TERMINATED);
        s.setEndedAt(ZonedDateTime.now());
        s.setControlGranted(false);
        s.setControlRequested(false);
        s.setActiveThreadUid(null);
        s.setInvitationHash(null);
        s.setHostSignals(null);
        s.setViewerSignals(null);
        s.setTerminationReason(reason);
        if (s.getHostDeviceUid() != null) devices.findByUid(s.getHostDeviceUid()).ifPresent(d -> {
            d.setOnline(false);
            d.setLastOnline(ZonedDateTime.now());
        });
        audit(s, reason, actorUid);
        events.close(s);
        events.notifyThread(s, reason);
    }

    private boolean terminal(DesktopSessionEntity s) {
        return s.getStatus() == DesktopSessionStatusEnum.TERMINATED || s.getStatus() == DesktopSessionStatusEnum.FAILED;
    }

    private DesktopSessionResponse snapshot(DesktopSessionEntity s, boolean host) {
        long lease = 0;
        if (s.isControlGranted() && !terminal(s) && s.getHostSeenAt() != null) {
            lease = Math.min(s.getViewerSeenAt().toInstant().toEpochMilli(),
                    s.getHostSeenAt().toInstant().toEpochMilli()) + 8000;
        }
        return new DesktopSessionResponse(s.getUid(), s.getThreadUid(), s.getRoomUid(), s.getViewerName(),
                s.getStatus(), s.isControlRequested(), s.isControlGranted(), System.currentTimeMillis(),
                s.getExpiresAt().toInstant().toEpochMilli(), lease, s.getInputCount(),
                signals(host ? s.getViewerSignals() : s.getHostSignals()));
    }

    private List<Signal> signals(String json) {
        return json == null ? List.of() : JSON.parseArray(json, Signal.class);
    }

    private void audit(DesktopSessionEntity s, String event, String actorUid) {
        audits.save(DesktopAuditLogEntity.builder().uid(UUID.randomUUID().toString()).orgUid(s.getOrgUid())
                .userUid(actorUid).sessionUid(s.getUid()).eventType(event)
                .details("inputCount=" + s.getInputCount()).build());
    }

    private String secret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String secret) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private boolean matches(String expected, String candidate) {
        return expected != null && candidate != null && candidate.length() <= 64 &&
                MessageDigest.isEqual(expected.getBytes(StandardCharsets.US_ASCII),
                        hash(candidate).getBytes(StandardCharsets.US_ASCII));
    }
}
