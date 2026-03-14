package com.bytedesk.meet.conference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ConferenceRoomRegistry {

    private static final int MAX_CHAT_MESSAGES = 60;

    private final Map<String, RoomState> rooms = new ConcurrentHashMap<>();

    public ConferenceRoomSnapshot join(ConferenceJoinCommand command) {
        String roomId = normalize(command.roomId());
        String participantId = normalize(command.participantId());
        String displayName = normalizeDisplayName(command.displayName(), participantId);
        boolean audioEnabled = command.audioEnabled();
        boolean videoEnabled = command.videoEnabled();

        RoomState state = rooms.computeIfAbsent(roomId, ignored -> new RoomState(roomId));
        if (state.locked && !matchesPassword(state.password, command.password())) {
            throw new ConferenceAccessDeniedException("会议已锁定，请输入正确密码");
        }
        state.participants.compute(participantId, (ignored, existing) -> {
            if (existing != null) {
                return new ConferenceParticipant(
                        participantId,
                        displayName,
                        audioEnabled,
                        videoEnabled,
                        existing.screenSharing(),
                        existing.handRaised(),
                        existing.joinedAt(),
                        Instant.now());
            }
            Instant now = Instant.now();
            return new ConferenceParticipant(participantId, displayName, audioEnabled, videoEnabled, false, false, now, now);
        });
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot lockRoom(String roomId, String participantId, String displayName, String password) {
        String normalizedRoomId = normalize(roomId);
        RoomState state = rooms.get(normalizedRoomId);
        if (state == null) {
            return emptySnapshot(roomId);
        }
        String normalizedParticipantId = normalize(participantId);
        ConferenceParticipant participant = state.participants.get(normalizedParticipantId);
        if (participant == null) {
            return snapshotOf(state);
        }

        String normalizedPassword = normalize(password);
        if (normalizedPassword.isEmpty()) {
            throw new IllegalArgumentException("锁定会议需要设置密码");
        }

        state.locked = true;
        state.password = normalizedPassword;
        state.lockedByDisplayName = normalizeDisplayName(displayName, participant.displayName());
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot unlockRoom(String roomId, String participantId) {
        String normalizedRoomId = normalize(roomId);
        RoomState state = rooms.get(normalizedRoomId);
        if (state == null) {
            return emptySnapshot(roomId);
        }

        if (!state.participants.containsKey(normalize(participantId))) {
            return snapshotOf(state);
        }

        state.locked = false;
        state.password = null;
        state.lockedByDisplayName = null;
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot updateMediaState(String roomId, String participantId, boolean audioEnabled,
            boolean videoEnabled) {
        RoomState state = rooms.get(normalize(roomId));
        if (state == null) {
            return emptySnapshot(roomId);
        }
        state.participants.computeIfPresent(normalize(participantId),
                (ignored, existing) -> existing.withMedia(audioEnabled, videoEnabled).heartbeatAt(Instant.now()));
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot updateInteractionState(
            String roomId,
            String participantId,
            boolean screenSharing,
            boolean handRaised) {
        RoomState state = rooms.get(normalize(roomId));
        if (state == null) {
            return emptySnapshot(roomId);
        }
        state.participants.computeIfPresent(normalize(participantId),
                (ignored, existing) -> existing.withInteractionState(screenSharing, handRaised).heartbeatAt(Instant.now()));
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot touch(String roomId, String participantId) {
        return touch(roomId, participantId, Instant.now());
    }

    public ConferenceRoomSnapshot touch(String roomId, String participantId, Instant heartbeatAt) {
        RoomState state = rooms.get(normalize(roomId));
        if (state == null) {
            return emptySnapshot(roomId);
        }
        state.participants.computeIfPresent(normalize(participantId),
                (ignored, existing) -> existing.heartbeatAt(heartbeatAt));
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot appendChatMessage(String roomId, String participantId, String displayName, String content) {
        return appendChatMessage(roomId, participantId, displayName, content, Instant.now());
    }

    public ConferenceRoomSnapshot appendChatMessage(
            String roomId,
            String participantId,
            String displayName,
            String content,
            Instant sentAt) {
        String normalizedRoomId = normalize(roomId);
        RoomState state = rooms.get(normalizedRoomId);
        if (state == null) {
            return emptySnapshot(roomId);
        }

        String normalizedParticipantId = normalize(participantId);
        String normalizedDisplayName = normalizeDisplayName(displayName, normalizedParticipantId);
        String normalizedContent = normalize(content);
        if (normalizedContent.isEmpty()) {
            return snapshotOf(state);
        }

        state.chatMessages.add(new ConferenceChatMessage(
                UUID.randomUUID().toString(),
                normalizedParticipantId,
                normalizedDisplayName,
                normalizedContent,
                sentAt));
        if (state.chatMessages.size() > MAX_CHAT_MESSAGES) {
            state.chatMessages.remove(0);
        }
        touch(normalizedRoomId, normalizedParticipantId, sentAt);
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot leave(String roomId, String participantId) {
        String normalizedRoomId = normalize(roomId);
        RoomState state = rooms.get(normalizedRoomId);
        if (state == null) {
            return emptySnapshot(roomId);
        }

        state.participants.remove(normalize(participantId));
        if (state.participants.isEmpty()) {
            rooms.remove(normalizedRoomId);
            return emptySnapshot(normalizedRoomId);
        }
        return snapshotOf(state);
    }

    public ConferenceRoomSnapshot getSnapshot(String roomId) {
        RoomState state = rooms.get(normalize(roomId));
        if (state == null) {
            return emptySnapshot(roomId);
        }
        return snapshotOf(state);
    }

    public ConferenceCleanupResult cleanupInactiveParticipants(Instant now, long timeoutSeconds) {
        if (timeoutSeconds <= 0) {
            return ConferenceCleanupResult.empty();
        }

        List<ConferenceRoomSnapshot> updatedRooms = new ArrayList<>();
        for (RoomState state : rooms.values()) {
            state.participants.entrySet().removeIf(entry -> entry.getValue().lastSeenAt().plusSeconds(timeoutSeconds).isBefore(now));

            if (state.participants.isEmpty()) {
                rooms.remove(state.roomId);
                continue;
            }

            updatedRooms.add(snapshotOf(state));
        }

        return updatedRooms.isEmpty() ? ConferenceCleanupResult.empty() : new ConferenceCleanupResult(updatedRooms);
    }

    private ConferenceRoomSnapshot snapshotOf(RoomState state) {
        List<ConferenceParticipant> participants = new ArrayList<>(state.participants.values());
        participants.sort(Comparator.comparing(ConferenceParticipant::joinedAt)
                .thenComparing(ConferenceParticipant::participantId));
        return new ConferenceRoomSnapshot(
                state.roomId,
                participants,
                List.copyOf(state.chatMessages),
                state.locked,
                state.password != null,
                state.lockedByDisplayName);
    }

    private ConferenceRoomSnapshot emptySnapshot(String roomId) {
        return new ConferenceRoomSnapshot(normalize(roomId), List.of(), List.of(), false, false, null);
    }

    private boolean matchesPassword(String expectedPassword, String actualPassword) {
        return expectedPassword == null || expectedPassword.equals(normalize(actualPassword));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeDisplayName(String displayName, String participantId) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return participantId;
        }
        return displayName.trim();
    }

    private static final class RoomState {
        private final String roomId;
        private final Map<String, ConferenceParticipant> participants = new ConcurrentHashMap<>();
        private final List<ConferenceChatMessage> chatMessages = new ArrayList<>();
        private boolean locked;
        private String password;
        private String lockedByDisplayName;

        private RoomState(String roomId) {
            this.roomId = roomId;
        }
    }
}