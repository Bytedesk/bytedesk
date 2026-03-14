package com.bytedesk.meet.conference;

public record ConferenceRoomLockRequest(
        String roomId,
        String participantId,
        String displayName,
        String password) {
}