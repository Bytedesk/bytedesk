package com.bytedesk.meet.conference;

public record ConferenceInteractionStateRequest(
        String roomId,
        String participantId,
        boolean screenSharing,
        boolean handRaised) {
}