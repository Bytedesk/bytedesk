package com.bytedesk.remote.invitation;

import static org.springframework.http.HttpStatus.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.remote.session.RemoteSessionResponse;
import com.bytedesk.remote.session.RemoteSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RemoteInvitationService {
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Pattern CODE_FORMAT = Pattern.compile("[0-9]{6}");
    private static final long MAX_CONNECT_ATTEMPTS = 10;
    private static final DefaultRedisScript<Long> COUNT_ATTEMPT = new DefaultRedisScript<>("""
            local attempts = redis.call('INCR', KEYS[1])
            if attempts == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
            return attempts
            """, Long.class);
    private final RemoteInvitationRepository invitations;
    private final RemoteSessionService sessions;
    private final AuthService auth;
    private final StringRedisTemplate redis;
    private final SecureRandom random = new SecureRandom();

    public record Issued(String uid, String code, String hostSecret, long expiresAt) {
        @Override public String toString() { return "Issued[credentials=REDACTED]"; }
    }
    public record Pending(String sessionUid) {}

    @Transactional
    public Issued issue() {
        var user = user();
        var uid = UUID.randomUUID().toString();
        var hostSecret = secret();
        for (int attempt = 0; attempt < 20; attempt++) {
            var code = String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
            var codeHash = hash(code);
            var expires = ZonedDateTime.now().plus(CODE_TTL);
            if (Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(codeKey(codeHash), uid, CODE_TTL))) {
                invitations.save(RemoteInvitationEntity.builder().uid(uid).userUid(user.getUid())
                        .orgUid(user.getOrgUid()).codeHash(codeHash).hostSecretHash(hash(hostSecret))
                        .expiresAt(expires).build());
                return new Issued(uid, code, hostSecret, expires.toInstant().toEpochMilli());
            }
        }
        throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Could not allocate an invitation code");
    }

    @Transactional
    public RemoteSessionResponse connect(String code) {
        var viewer = user();
        if (code == null || !CODE_FORMAT.matcher(code).matches()) {
            throw new ResponseStatusException(BAD_REQUEST, "Invitation code must contain six digits");
        }
        var attempts = redis.execute(COUNT_ATTEMPT, List.of("remote:invitation:attempts:" + viewer.getUid()),
                Long.toString(CODE_TTL.toSeconds()));
        if (attempts == null) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Invitation validation unavailable");
        }
        if (attempts > MAX_CONNECT_ATTEMPTS) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many connection attempts; try again later");
        }
        var codeHash = hash(code);
        var uid = redis.opsForValue().get(codeKey(codeHash));
        if (uid == null) {
            throw new ResponseStatusException(FORBIDDEN, "Invitation expired or invalid");
        }
        var invitation = invitations.lockByUid(uid)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Invitation expired or invalid"));
        if (invitation.getSessionUid() != null || !invitation.getExpiresAt().isAfter(ZonedDateTime.now())
                || !MessageDigest.isEqual(invitation.getCodeHash().getBytes(StandardCharsets.US_ASCII),
                        codeHash.getBytes(StandardCharsets.US_ASCII))) {
            throw new ResponseStatusException(FORBIDDEN, "Invitation expired or invalid");
        }
        var session = sessions.inviteDevice(viewer, invitation);
        invitation.setSessionUid(session.uid());
        return session;
    }

    @Transactional
    public Pending pending(String uid) {
        var invitation = owned(uid);
        return new Pending(invitation.getSessionUid());
    }

    @Transactional
    public void close(String uid) {
        var invitation = owned(uid);
        if (invitation.getSessionUid() == null) invitation.setExpiresAt(ZonedDateTime.now());
    }

    @Transactional
    public void reject(String uid) {
        var invitation = owned(uid);
        if (invitation.getSessionUid() == null) {
            throw new ResponseStatusException(CONFLICT, "No pending connection");
        }
        sessions.rejectDeviceInvitation(invitation.getSessionUid(), invitation.getUserUid());
    }

    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void removeExpired() {
        invitations.deleteByExpiresAtBefore(ZonedDateTime.now().minusDays(1));
    }

    private RemoteInvitationEntity owned(String uid) {
        var user = user();
        var invitation = invitations.lockByUid(uid)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invitation not found"));
        if (!user.getUid().equals(invitation.getUserUid()) || !user.getOrgUid().equals(invitation.getOrgUid())) {
            throw new ResponseStatusException(FORBIDDEN, "Not the invitation owner");
        }
        return invitation;
    }

    private com.bytedesk.core.rbac.user.UserEntity user() {
        var user = auth.getUser();
        if (user == null || user.getUid() == null || user.getOrgUid() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Login required");
        }
        return user;
    }

    private String secret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String codeKey(String codeHash) {
        return "remote:invitation:code:" + codeHash;
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
