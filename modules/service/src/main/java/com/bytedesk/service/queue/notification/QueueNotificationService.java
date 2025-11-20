package com.bytedesk.service.queue.notification;

import java.util.function.BiFunction;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueNotificationService {

    private final QueueNotificationBuilder queueNotificationBuilder;
    private final QueueMemberRestService queueMemberRestService;
    private final ThreadRestService threadRestService;
    private final AgentRestService agentRestService;
    private final IMessageSendService messageSendService;

    public void publishQueueJoinNotice(AgentEntity agent, QueueMemberEntity queueMemberEntity) {
        if (agent == null || queueMemberEntity == null) {
            return;
        }
        try {
            ThreadEntity agentQueueThread = resolveAgentQueueThread(agent);
            QueueNotificationPayload payload = queueNotificationBuilder
                    .buildJoinNotice(queueMemberEntity, agent.getUid());
            dispatch(agentQueueThread, payload);
        } catch (Exception ex) {
            log.warn("Failed to publish queue notice for agent {} member {}: {}", agent.getUid(),
                    queueMemberEntity.getUid(), ex.getMessage(), ex);
        }
    }

    public void publishQueueAssignmentNotice(AgentEntity agent, QueueMemberEntity queueMemberEntity) {
        if (agent == null || queueMemberEntity == null) {
            return;
        }
        try {
            ThreadEntity agentQueueThread = resolveAgentQueueThread(agent);
            QueueNotificationPayload payload = queueNotificationBuilder
                    .buildAssignmentNotice(queueMemberEntity, agent.getUid());
            dispatch(agentQueueThread, payload);
        } catch (Exception ex) {
            log.warn("Failed to publish assignment notice for agent {} member {}: {}", agent.getUid(),
                    queueMemberEntity.getUid(), ex.getMessage(), ex);
        }
    }

    public void publishQueueLeaveNotice(QueueMemberEntity queueMemberEntity) {
        publishMemberDelta(queueMemberEntity, queueNotificationBuilder::buildLeaveNotice, "leave");
    }

    public void publishQueueTimeoutNotice(QueueMemberEntity queueMemberEntity) {
        publishMemberDelta(queueMemberEntity, queueNotificationBuilder::buildTimeoutNotice, "timeout");
    }

    public void publishAgentNotification(String agentUid, QueueNotificationRequest request) {
        Assert.hasText(agentUid, "agentUid must not be blank");
        AgentEntity agent = agentRestService.findByUid(agentUid)
                .orElseThrow(() -> new NotFoundException("Agent " + agentUid + " not found"));
        QueueNotificationPayload payload = request.toPayload(agentUid);
        ThreadEntity agentQueueThread = resolveAgentQueueThread(agent);
        dispatch(agentQueueThread, payload);
    }

    private ThreadEntity resolveAgentQueueThread(AgentEntity agent) {
        String threadUid = queueMemberRestService.ensureAgentQueueThreadUid(agent);
        return threadRestService.findByUid(threadUid)
                .orElseThrow(() -> new NotFoundException("Agent queue thread not found: " + threadUid));
    }

    private void dispatch(ThreadEntity thread, QueueNotificationPayload payload) {
        MessageProtobuf message = ThreadMessageUtil.getAgentQueueNoticeMessage(payload, thread);
        messageSendService.sendProtobufMessage(message);
    }

    private void publishMemberDelta(QueueMemberEntity member,
            BiFunction<QueueMemberEntity, String, QueueNotificationPayload> payloadBuilder,
            String context) {
        if (member == null) {
            return;
        }
        String agentUid = resolveAgentUidFromMember(member);
        if (!StringUtils.hasText(agentUid)) {
            log.debug("Skip queue {} notice for member {}: missing agent", context, member.getUid());
            return;
        }
        try {
            AgentEntity agent = agentRestService.findByUid(agentUid)
                    .orElseThrow(() -> new NotFoundException("Agent " + agentUid + " not found"));
            ThreadEntity agentQueueThread = resolveAgentQueueThread(agent);
            QueueNotificationPayload payload = payloadBuilder.apply(member, agentUid);
            dispatch(agentQueueThread, payload);
        } catch (Exception ex) {
            log.warn("Failed to publish queue {} notice for member {} (agent {}): {}", context,
                    member.getUid(), agentUid, ex.getMessage(), ex);
        }
    }

    private String resolveAgentUidFromMember(QueueMemberEntity member) {
        ThreadEntity thread = member.getThread();
        if (thread != null) {
            UserProtobuf agentProto = thread.getAgentProtobuf();
            if (agentProto != null && StringUtils.hasText(agentProto.getUid())) {
                return agentProto.getUid();
            }
            if (StringUtils.hasText(thread.getTopic()) && TopicUtils.isOrgAgentTopic(thread.getTopic())) {
                try {
                    return TopicUtils.getAgentUidFromThreadTopic(thread.getTopic());
                } catch (RuntimeException ignored) {
                    // fallback below
                }
            }
        }
        if (member.getAgentQueue() != null && StringUtils.hasText(member.getAgentQueue().getTopic())) {
            return extractUidFromQueueTopic(member.getAgentQueue().getTopic());
        }
        return null;
    }

    private String extractUidFromQueueTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return null;
        }
        int idx = topic.lastIndexOf('/');
        return idx >= 0 ? topic.substring(idx + 1) : topic;
    }
}
