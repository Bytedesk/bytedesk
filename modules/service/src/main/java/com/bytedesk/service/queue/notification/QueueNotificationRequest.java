package com.bytedesk.service.queue.notification;

import java.util.List;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueNotificationRequest {

    @NotNull
    private QueueNotificationType type;

    // private QueueNotificationDelta delta;

    @NotBlank
    private String queueMemberUid;

    private String threadUid;

    private String agentUid;

    private Integer position;

    private Integer queueSize;

    private Long estimatedWaitMs;

    @NotNull
    private Long serverTimestamp;

    private List<Snapshot> snapshot;

    public QueueNotificationPayload toPayload(String fallbackAgentUid) {
        QueueNotificationPayload.QueueNotificationPayloadBuilder builder = QueueNotificationPayload.builder()
                .type(type)
                // .delta(delta)
                .queueMemberUid(queueMemberUid)
                .threadUid(threadUid)
                .agentUid(StringUtils.hasText(agentUid) ? agentUid : fallbackAgentUid)
                .position(position)
                .queueSize(queueSize)
                .estimatedWaitMs(estimatedWaitMs)
                .serverTimestamp(serverTimestamp);

        // if (!CollectionUtils.isEmpty(snapshot)) {
        //     builder.snapshot(snapshot.stream()
        //             .map(item -> QueueNotificationPayload.QueueNotificationSnapshot.builder()
        //                     .queueMemberUid(item.getQueueMemberUid())
        //                     .displayName(item.getDisplayName())
        //                     .position(item.getPosition())
        //                     .build())
        //             .toList());
        // }

        return builder.build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Snapshot {
        @NotBlank
        private String queueMemberUid;
        private String displayName;
        private Integer position;
    }
}
