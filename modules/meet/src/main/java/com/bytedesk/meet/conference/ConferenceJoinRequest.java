package com.bytedesk.meet.conference;

public record ConferenceJoinRequest(
        String roomId,
        String participantId,
        String displayName,
        Boolean audioEnabled,
        Boolean videoEnabled,
        String password) {
}