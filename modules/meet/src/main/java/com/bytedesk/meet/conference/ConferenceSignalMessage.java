package com.bytedesk.meet.conference;

public record ConferenceSignalMessage(
        String roomId,
        String type,
        String fromParticipantId,
        String toParticipantId,
        String displayName,
        Object payload,
        long timestamp) {

    public static ConferenceSignalMessage roomState(String roomId, ConferenceRoomSnapshot snapshot) {
        return new ConferenceSignalMessage(roomId, "ROOM_STATE", "system", null, "system", snapshot,
                System.currentTimeMillis());
    }

    public ConferenceSignalMessage normalizeRoom(String normalizedRoomId) {
        return new ConferenceSignalMessage(
                normalizedRoomId,
                type,
                fromParticipantId,
                toParticipantId,
                displayName,
                payload,
                System.currentTimeMillis());
    }

    public boolean isChatMessage() {
        return "CHAT".equalsIgnoreCase(type);
    }
}