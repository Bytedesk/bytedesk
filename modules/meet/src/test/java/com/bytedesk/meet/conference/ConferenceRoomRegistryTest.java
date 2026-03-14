package com.bytedesk.meet.conference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ConferenceRoomRegistryTest {

    private final ConferenceRoomRegistry registry = new ConferenceRoomRegistry();

    @Test
    void shouldJoinAndLeaveRoomWhileKeepingParticipantState() {
        ConferenceRoomSnapshot firstJoin = registry.join(new ConferenceJoinCommand("room-alpha", "u-1", "Alice", true, true, null));

        assertEquals("room-alpha", firstJoin.roomId());
        assertEquals(1, firstJoin.participants().size());
        assertEquals("u-1", firstJoin.participants().get(0).participantId());
        assertTrue(firstJoin.participants().get(0).audioEnabled());
        assertTrue(firstJoin.participants().get(0).videoEnabled());

        registry.join(new ConferenceJoinCommand("room-alpha", "u-2", "Bob", true, true, null));
        ConferenceRoomSnapshot updated = registry.updateMediaState("room-alpha", "u-2", false, true);

        assertEquals(2, updated.participants().size());
        assertFalse(updated.participants().stream()
                .filter(participant -> participant.participantId().equals("u-2"))
                .findFirst()
                .orElseThrow()
                .audioEnabled());

        ConferenceRoomSnapshot afterLeave = registry.leave("room-alpha", "u-1");

        assertEquals(1, afterLeave.participants().size());
        assertEquals("u-2", afterLeave.participants().get(0).participantId());
    }

    @Test
    void shouldReturnEmptySnapshotWhenRoomDoesNotExist() {
        ConferenceRoomSnapshot snapshot = registry.getSnapshot("missing-room");

        assertEquals("missing-room", snapshot.roomId());
        assertTrue(snapshot.participants().isEmpty());
    }

    @Test
    void shouldStoreRecentChatMessagesInRoomSnapshot() {
        registry.join(new ConferenceJoinCommand("room-chat", "u-1", "Alice", true, true, null));

        ConferenceRoomSnapshot snapshot = registry.appendChatMessage(
                "room-chat",
                "u-1",
                "Alice",
                "hello everyone",
                Instant.parse("2026-03-13T10:00:00Z"));

        assertEquals(1, snapshot.chatMessages().size());
        assertEquals("hello everyone", snapshot.chatMessages().get(0).content());
        assertEquals("Alice", snapshot.chatMessages().get(0).displayName());
    }

    @Test
    void shouldRemoveInactiveParticipantsDuringCleanup() {
        registry.join(new ConferenceJoinCommand("room-cleanup", "u-1", "Alice", true, true, null));
        registry.join(new ConferenceJoinCommand("room-cleanup", "u-2", "Bob", true, true, null));
        registry.touch("room-cleanup", "u-1", Instant.parse("2026-03-13T10:00:30Z"));
        registry.touch("room-cleanup", "u-2", Instant.parse("2026-03-13T10:00:00Z"));

        ConferenceCleanupResult result = registry.cleanupInactiveParticipants(
                Instant.parse("2026-03-13T10:01:05Z"),
                30);

        assertEquals(1, result.updatedRooms().size());
        ConferenceRoomSnapshot snapshot = result.updatedRooms().get(0);
        assertEquals(1, snapshot.participants().size());
        assertEquals("u-1", snapshot.participants().get(0).participantId());
    }

    @Test
    void shouldUpdateParticipantInteractionState() {
        registry.join(new ConferenceJoinCommand("room-state", "u-1", "Alice", true, true, null));

        ConferenceRoomSnapshot snapshot = registry.updateInteractionState("room-state", "u-1", true, true);

        ConferenceParticipant participant = snapshot.participants().get(0);
        assertTrue(participant.screenSharing());
        assertTrue(participant.handRaised());
    }

    @Test
    void shouldApplyInitialJoinMediaState() {
        ConferenceRoomSnapshot snapshot = registry.join(new ConferenceJoinCommand("room-join", "u-1", "Alice", false, false, null));

        ConferenceParticipant participant = snapshot.participants().get(0);
        assertFalse(participant.audioEnabled());
        assertFalse(participant.videoEnabled());
    }

    @Test
    void shouldLockAndUnlockRoomWithPassword() {
        registry.join(new ConferenceJoinCommand("room-lock", "u-1", "Alice", true, true, null));

        ConferenceRoomSnapshot locked = registry.lockRoom("room-lock", "u-1", "Alice", "123456");

        assertTrue(locked.locked());
        assertTrue(locked.passwordProtected());
        assertEquals("Alice", locked.lockedByDisplayName());

        ConferenceRoomSnapshot unlocked = registry.unlockRoom("room-lock", "u-1");

        assertFalse(unlocked.locked());
        assertFalse(unlocked.passwordProtected());
    }

    @Test
    void shouldRejectJoinWhenRoomIsLockedAndPasswordDoesNotMatch() {
        registry.join(new ConferenceJoinCommand("room-protected", "u-1", "Alice", true, true, null));
        registry.lockRoom("room-protected", "u-1", "Alice", "123456");

        try {
            registry.join(new ConferenceJoinCommand("room-protected", "u-2", "Bob", true, true, "bad"));
        } catch (ConferenceAccessDeniedException exception) {
            assertEquals("会议已锁定，请输入正确密码", exception.getMessage());
            return;
        }

        throw new AssertionError("expected ConferenceAccessDeniedException");
    }
}