package com.bytedesk.core.socket.connection;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ConnectionPresenceTests {

    @Autowired
    private ConnectionRestService connectionRestService;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    void testOnlineAfterConnect() {
        String userUid = "test-user-1";
        String clientId = "test-user-1/client/deviceA";
        connectionRestService.markConnected(userUid, null, clientId, "deviceA", "MQTT", null, null, null, 90);
        PresenceResponse presence = connectionRestService.getPresence(userUid);
        assertTrue(presence.isOnline(), "User should be online after connect");
        assertEquals(1, presence.getActiveCount());
    }

    @Test
    void testOfflineAfterForcedExpiry() {
        String userUid = "test-user-2";
        String clientId = "test-user-2/client/deviceA";
        connectionRestService.markConnected(userUid, null, clientId, "deviceA", "MQTT", null, null, null, 90);
        // Force entity to be expired by manipulating heartbeat + ttlSeconds
        Optional<ConnectionEntity> opt = connectionRepository.findByClientId(clientId);
        assertTrue(opt.isPresent());
        ConnectionEntity entity = opt.get();
        entity.setTtlSeconds(60); // within allowed range
        entity.setLastHeartbeatAt(System.currentTimeMillis() - 120_000L); // 120s ago
        connectionRepository.save(entity);
        // Run expiry process
        connectionRestService.expireStaleSessions();
        PresenceResponse presence = connectionRestService.getPresence(userUid);
        assertFalse(presence.isOnline(), "User should be offline after forced expiry");
        assertEquals(0, presence.getActiveCount());
    }
}
