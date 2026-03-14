package com.bytedesk.meet.conference;

public record ConferenceLeaveRequest(
        String roomId,
        String participantId) {
}