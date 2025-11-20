package com.bytedesk.service.queue.notification;

import static com.bytedesk.service.queue.notification.QueueNotificationDelta.ASSIGNED;
import static com.bytedesk.service.queue.notification.QueueNotificationDelta.BULK_CLEANUP;
import static com.bytedesk.service.queue.notification.QueueNotificationDelta.JOINED;
import static com.bytedesk.service.queue.notification.QueueNotificationDelta.LEFT;
import static com.bytedesk.service.queue.notification.QueueNotificationDelta.TIMEOUT;
import static com.bytedesk.service.queue.notification.QueueNotificationType.QUEUE_ACCEPT;
import static com.bytedesk.service.queue.notification.QueueNotificationType.QUEUE_NOTICE;
import static com.bytedesk.service.queue.notification.QueueNotificationType.QUEUE_TIMEOUT;
import static com.bytedesk.service.queue.notification.QueueNotificationType.QUEUE_UPDATE;

import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.queue_member.QueueMemberEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueNotificationBuilder {

    private final Clock clock;

    public QueueNotificationPayload buildJoinNotice(QueueMemberEntity queueMember, String fallbackAgentUid) {
        return basePayload(queueMember, fallbackAgentUid)
                .messageType(QUEUE_NOTICE)
                .delta(JOINED)
                .build();
    }

    public QueueNotificationPayload buildLeaveNotice(QueueMemberEntity queueMember, String fallbackAgentUid) {
        return basePayload(queueMember, fallbackAgentUid)
                .messageType(QUEUE_NOTICE)
                .delta(LEFT)
                .build();
    }

    public QueueNotificationPayload buildTimeoutNotice(QueueMemberEntity queueMember, String fallbackAgentUid) {
        return basePayload(queueMember, fallbackAgentUid)
                .messageType(QUEUE_TIMEOUT)
                .delta(TIMEOUT)
                .build();
    }

    public QueueNotificationPayload buildAssignmentNotice(QueueMemberEntity queueMember, String agentUid) {
        return basePayload(queueMember, agentUid)
                .messageType(QUEUE_ACCEPT)
                .delta(ASSIGNED)
                .estimatedWaitMs(0L)
                .position(null)
                .build();
    }

    public QueueNotificationPayload buildBatchUpdate(String agentUid,
            List<QueueNotificationPayload> events,
            List<QueueNotificationPayload.QueueNotificationSnapshot> snapshots) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("events must not be empty");
        }
        QueueNotificationPayload anchor = events.get(events.size() - 1);
        List<QueueNotificationPayload.QueueNotificationSnapshot> safeSnapshots =
                (snapshots == null || snapshots.isEmpty()) ? null : snapshots;
        return QueueNotificationPayload.builder()
                .messageType(QUEUE_UPDATE)
                .delta(BULK_CLEANUP)
                .queueMemberUid(anchor.getQueueMemberUid())
                .threadUid(anchor.getThreadUid())
                .agentUid(StringUtils.hasText(anchor.getAgentUid()) ? anchor.getAgentUid() : agentUid)
                .position(anchor.getPosition())
                .queueSize(anchor.getQueueSize())
                .estimatedWaitMs(anchor.getEstimatedWaitMs())
                .snapshot(safeSnapshots)
                .serverTimestamp(clock.millis())
                .build();
    }

    public QueueNotificationPayload.QueueNotificationSnapshot buildSnapshot(QueueMemberEntity queueMember) {
        if (queueMember == null) {
            return null;
        }
        ThreadEntity thread = queueMember.getThread();
        String displayName = null;
        if (thread != null && StringUtils.hasText(thread.getUser())) {
            UserProtobuf visitor = UserProtobuf.fromJson(thread.getUser());
            if (visitor != null) {
                displayName = visitor.getNickname();
            }
        }
        return QueueNotificationPayload.QueueNotificationSnapshot.builder()
                .queueMemberUid(queueMember.getUid())
                .displayName(displayName)
                .position(queueMember.getQueueNumber())
                .build();
    }

    private QueueNotificationPayload.QueueNotificationPayloadBuilder basePayload(QueueMemberEntity queueMember,
            String fallbackAgentUid) {
        ThreadEntity thread = queueMember.getThread();
        String agentUid = resolveAgentUid(thread);
        if (!StringUtils.hasText(agentUid)) {
            agentUid = fallbackAgentUid;
        }
        Integer queueSize = queueMember.getAgentQueue() != null ? queueMember.getAgentQueue().getQueuingCount() : null;
        return QueueNotificationPayload.builder()
                .queueMemberUid(queueMember.getUid())
                .threadUid(thread != null ? thread.getUid() : null)
                .agentUid(agentUid)
                .position(queueMember.getQueueNumber())
                .queueSize(queueSize)
                .estimatedWaitMs(estimateWaitMillis(queueMember))
                .serverTimestamp(clock.millis());
    }

    private String resolveAgentUid(ThreadEntity thread) {
        if (thread == null) {
            return null;
        }
        String agentJson = thread.getAgent();
        if (!StringUtils.hasText(agentJson)) {
            return null;
        }
        UserProtobuf agent = UserProtobuf.fromJson(agentJson);
        return agent != null ? agent.getUid() : null;
    }

    private long estimateWaitMillis(QueueMemberEntity member) {
        int queueSize = member.getAgentQueue() != null ? member.getAgentQueue().getQueuingCount() : 0;
        // 当前缺少 SLA 统计，先使用固定 2min/人估算，后续可替换为 rolling window 平均
        return queueSize * 120_000L;
    }
}
