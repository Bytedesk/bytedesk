package com.bytedesk.meet.conference;

public record ConferenceRoomUnlockRequest(
        String roomId,
        String participantId,
        String displayName) {
}