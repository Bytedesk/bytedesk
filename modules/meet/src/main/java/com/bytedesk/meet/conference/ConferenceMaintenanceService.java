package com.bytedesk.meet.conference;

import java.time.Instant;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConferenceMaintenanceService {

    private final ConferenceRoomRegistry conferenceRoomRegistry;

    private final ConferenceRuntimeProperties conferenceRuntimeProperties;

    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedDelayString = "${bytedesk.meet.cleanup-interval-ms:15000}")
    public void cleanupInactiveParticipants() {
        ConferenceCleanupResult result = conferenceRoomRegistry.cleanupInactiveParticipants(
                Instant.now(),
                conferenceRuntimeProperties.participantTimeoutSeconds());

        result.updatedRooms().forEach(snapshot -> messagingTemplate.convertAndSend(
                "/topic/meet/rooms/" + snapshot.roomId(),
                ConferenceSignalMessage.roomState(snapshot.roomId(), snapshot)));
    }
}