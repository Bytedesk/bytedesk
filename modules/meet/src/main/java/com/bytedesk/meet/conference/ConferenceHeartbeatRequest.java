package com.bytedesk.meet.conference;

public record ConferenceHeartbeatRequest(
        String roomId,
        String participantId) {
}