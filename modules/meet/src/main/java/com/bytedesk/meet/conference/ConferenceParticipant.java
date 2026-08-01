package com.bytedesk.meet.conference;

import java.time.Instant;

public record ConferenceParticipant(
        String participantId,
        String displayName,
        boolean audioEnabled,
        boolean videoEnabled,
        boolean screenSharing,
        boolean handRaised,
        Instant joinedAt,
        Instant lastSeenAt) {

    public ConferenceParticipant withMedia(boolean audioState, boolean videoState) {
        return new ConferenceParticipant(participantId, displayName, audioState, videoState, screenSharing, handRaised, joinedAt, lastSeenAt);
    }

    public ConferenceParticipant withInteractionState(boolean screenSharingState, boolean handRaisedState) {
        return new ConferenceParticipant(participantId, displayName, audioEnabled, videoEnabled, screenSharingState,
                handRaisedState, joinedAt, lastSeenAt);
    }

    public ConferenceParticipant heartbeatAt(Instant heartbeatAt) {
        return new ConferenceParticipant(participantId, displayName, audioEnabled, videoEnabled, screenSharing, handRaised, joinedAt, heartbeatAt);
    }
}