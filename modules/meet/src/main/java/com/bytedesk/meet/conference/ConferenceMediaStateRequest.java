package com.bytedesk.meet.conference;

public record ConferenceMediaStateRequest(
        String roomId,
        String participantId,
        boolean audioEnabled,
        boolean videoEnabled) {
}