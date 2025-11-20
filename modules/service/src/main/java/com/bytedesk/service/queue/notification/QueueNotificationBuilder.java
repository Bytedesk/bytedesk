package com.bytedesk.service.queue.notification;

import static com.bytedesk.service.queue.notification.QueueNotificationDelta.JOINED;
import static com.bytedesk.service.queue.notification.QueueNotificationType.QUEUE_NOTICE;

import java.time.Clock;
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

    public QueueNotificationPayload buildJoinNotice(QueueMemberEntity queueMember) {
        ThreadEntity thread = queueMember.getThread();
        return QueueNotificationPayload.builder()
                .messageType(QUEUE_NOTICE)
                .delta(JOINED)
                .queueMemberUid(queueMember.getUid())
                .threadUid(thread != null ? thread.getUid() : null)
                .agentUid(resolveAgentUid(thread))
                .position(queueMember.getQueueNumber())
                .queueSize(queueMember.getAgentQueue() != null ? queueMember.getAgentQueue().getQueuingCount() : null)
                .estimatedWaitMs(estimateWaitMillis(queueMember))
                .serverTimestamp(clock.millis())
                .build();
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
