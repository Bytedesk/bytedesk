package com.bytedesk.remote.session;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class DesktopIceServiceTest {
    @Test
    void noExternalServersAreUsedUnlessExplicitlyConfigured() {
        var session = session(DesktopSessionStatusEnum.CONNECTING, Instant.now().plusSeconds(7200).toEpochMilli());
        assertTrue(configured("", "").servers(session).isEmpty());
        var stun = configured("stun:turn.example.com:3478", "").servers(session).getFirst();
        assertNull(stun.username());
        assertNull(stun.credential());
    }

    @Test
    void turnCredentialsCoverTheSessionLifetimeWithoutDisclosingTheSharedSecret() throws Exception {
        var session = session(DesktopSessionStatusEnum.CONNECTED, Instant.now().plusSeconds(7200).toEpochMilli());
        var server = configured("turns:turn.example.com:5349", "turn-test-secret").servers(session).getFirst();
        assertEquals((session.expiresAt() / 1000 + 300) + ":session", server.username());
        var verifier = Mac.getInstance("HmacSHA1");
        verifier.init(new SecretKeySpec("turn-test-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        assertEquals(Base64.getEncoder().encodeToString(verifier.doFinal(
                server.username().getBytes(StandardCharsets.UTF_8))), server.credential());
        assertNotEquals("turn-test-secret", server.credential());
    }

    @Test
    void endedOrExpiredSessionsCannotContinueMintingTurnCredentials() {
        var service = configured("turn:turn.example.com:3478", "turn-test-secret");
        var ended = session(DesktopSessionStatusEnum.TERMINATED, Instant.now().plusSeconds(7200).toEpochMilli());
        assertEquals(410, assertThrows(ResponseStatusException.class, () -> service.servers(ended))
                .getStatusCode().value());
        var expired = session(DesktopSessionStatusEnum.CONNECTED, Instant.now().minusSeconds(1).toEpochMilli());
        assertEquals(410, assertThrows(ResponseStatusException.class, () -> service.servers(expired))
                .getStatusCode().value());
    }

    @Test
    void invalidConfigurationIsAnExplicitError() {
        var session = session(DesktopSessionStatusEnum.CONNECTING, Instant.now().plusSeconds(7200).toEpochMilli());
        assertThrows(IllegalStateException.class, () -> configured("turn:turn.example.com", "").servers(session));
        assertThrows(IllegalStateException.class, () -> configured("https://turn.example.com", "").servers(session));
    }

    private DesktopSessionResponse session(DesktopSessionStatusEnum status, long expiresAt) {
        return new DesktopSessionResponse("session", "thread", "room", "Agent", status,
                false, false, Instant.now().toEpochMilli(), expiresAt, 0, 0, List.of());
    }

    private DesktopIceService configured(String urls, String secret) {
        var service = new DesktopIceService();
        ReflectionTestUtils.setField(service, "urls", urls);
        ReflectionTestUtils.setField(service, "secret", secret);
        return service;
    }
}
