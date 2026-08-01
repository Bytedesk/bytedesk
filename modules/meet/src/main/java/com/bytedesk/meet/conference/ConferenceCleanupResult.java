package com.bytedesk.meet.conference;

import java.util.List;

public record ConferenceCleanupResult(
        List<ConferenceRoomSnapshot> updatedRooms) {

    public static ConferenceCleanupResult empty() {
        return new ConferenceCleanupResult(List.of());
    }
}