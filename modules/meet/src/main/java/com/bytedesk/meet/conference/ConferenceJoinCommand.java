package com.bytedesk.meet.conference;

public record ConferenceJoinCommand(
        String roomId,
        String participantId,
        String displayName,
        boolean audioEnabled,
        boolean videoEnabled,
        String password) {
}