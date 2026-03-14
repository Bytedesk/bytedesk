package com.bytedesk.meet.conference;

import java.util.List;

public record ConferenceRoomSnapshot(
        String roomId,
        List<ConferenceParticipant> participants,
        List<ConferenceChatMessage> chatMessages,
        boolean locked,
        boolean passwordProtected,
        String lockedByDisplayName) {
}