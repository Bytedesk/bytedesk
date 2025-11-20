package com.bytedesk.service.queue.notification;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QueueNotificationPayload {
    QueueNotificationType messageType;
    QueueNotificationDelta delta;
    String queueMemberUid;
    String threadUid;
    String agentUid;
    Integer position;
    Integer queueSize;
    Long estimatedWaitMs;
    List<QueueNotificationSnapshot> snapshot;
    long serverTimestamp;

    @Value
    @Builder
    public static class QueueNotificationSnapshot {
        String queueMemberUid;
        String displayName;
        Integer position;
    }
}
