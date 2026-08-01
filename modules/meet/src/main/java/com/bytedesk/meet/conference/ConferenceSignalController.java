package com.bytedesk.meet.conference;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ConferenceSignalController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ConferenceRoomRegistry conferenceRoomRegistry;

    @MessageMapping("/meet/rooms/{roomId}/signal")
    public void signal(@DestinationVariable String roomId, ConferenceSignalMessage message) {
        ConferenceSignalMessage normalized = message.normalizeRoom(roomId);

        if (normalized.isChatMessage()) {
            String toParticipantId = extractChatRecipientId(normalized.payload());
            if (!toParticipantId.isBlank()) {
                messagingTemplate.convertAndSend(
                        "/topic/meet/rooms/" + roomId + "/private/" + toParticipantId,
                        normalized);
                return;
            }

            String content = extractChatContent(normalized.payload());
            ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.appendChatMessage(
                    roomId,
                    normalized.fromParticipantId(),
                    normalized.displayName(),
                    content);
            messagingTemplate.convertAndSend("/topic/meet/rooms/" + roomId,
                    ConferenceSignalMessage.roomState(roomId, snapshot));
            return;
        }

        messagingTemplate.convertAndSend("/topic/meet/rooms/" + roomId, normalized);
    }

    private String extractChatRecipientId(Object payload) {
        if (payload instanceof Map<?, ?> mapPayload) {
            Object participantId = mapPayload.get("toParticipantId");
            return participantId == null ? "" : String.valueOf(participantId).trim();
        }
        return "";
    }

    private String extractChatContent(Object payload) {
        if (payload instanceof Map<?, ?> mapPayload) {
            Object content = mapPayload.get("content");
            return content == null ? "" : String.valueOf(content);
        }
        return payload == null ? "" : String.valueOf(payload);
    }
}