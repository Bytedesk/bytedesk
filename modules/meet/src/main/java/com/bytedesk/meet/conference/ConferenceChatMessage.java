package com.bytedesk.meet.conference;

import java.time.Instant;

public record ConferenceChatMessage(
        String messageId,
        String participantId,
        String displayName,
        String content,
        Instant sentAt) {
}