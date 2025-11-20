package com.bytedesk.service.queue.notification;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.ObjectProvider;

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

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueNotificationService {

    private final QueueNotificationBuilder queueNotificationBuilder;
    private final ObjectProvider<QueueMemberRestService> queueMemberRestServiceProvider;
    private final ThreadRestService threadRestService;
    private final AgentRestService agentRestService;
    private final IMessageSendService messageSendService;

    private final Map<String, PendingBatch> pendingBatches = new ConcurrentHashMap<>();
    private final ScheduledExecutorService batchingExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "queue-notice-batcher");
        thread.setDaemon(true);
        return thread;
    });

    /** Enqueue a QUEUE_NOTICE event when a visitor joins the agent's waiting list. */
    public void publishQueueJoinNotice(AgentEntity agent, QueueMemberEntity queueMemberEntity) {
        if (agent == null || queueMemberEntity == null) {
            return;
        }
        try {
            QueueNotificationPayload payload = queueNotificationBuilder
                    .buildJoinNotice(queueMemberEntity, agent.getUid());
            QueueNotificationPayload.QueueNotificationSnapshot snapshot = queueNotificationBuilder
                    .buildSnapshot(queueMemberEntity);
            bufferAndSchedule(agent, payload, snapshot);
        } catch (Exception ex) {
            log.warn("Failed to publish queue notice for agent {} member {}: {}", agent.getUid(),
                    queueMemberEntity.getUid(), ex.getMessage(), ex);
        }
    }

    /** Send the assignment notification immediately once the visitor is matched to the agent. */
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

    /**
     * Publish a QUEUE_NOTICE/LEFT delta for members removed from the queue. Uses agent inferred from
     * the member or queue.
     */
    public void publishQueueLeaveNotice(QueueMemberEntity queueMemberEntity) {
        publishMemberDelta(queueMemberEntity, queueNotificationBuilder::buildLeaveNotice, "leave");
    }

    /** Publish a QUEUE_TIMEOUT delta when a member exceeded SLA wait time. */
    public void publishQueueTimeoutNotice(QueueMemberEntity queueMemberEntity) {
        publishMemberDelta(queueMemberEntity, queueNotificationBuilder::buildTimeoutNotice, "timeout");
    }

    /** Allow external callers to provide a fully built payload and deliver it to the agent topic. */
    public void publishAgentNotification(String agentUid, QueueNotificationRequest request) {
        Assert.hasText(agentUid, "agentUid must not be blank");
        AgentEntity agent = agentRestService.findByUid(agentUid)
                .orElseThrow(() -> new NotFoundException("Agent " + agentUid + " not found"));
        QueueNotificationPayload payload = request.toPayload(agentUid);
        ThreadEntity agentQueueThread = resolveAgentQueueThread(agent);
        dispatch(agentQueueThread, payload);
    }

    /** Resolve the backing agent queue thread, ensuring it exists before dispatching messages. */
    private ThreadEntity resolveAgentQueueThread(AgentEntity agent) {
        String threadUid = queueMemberRestService().ensureAgentQueueThreadUid(agent);
        return threadRestService.findByUid(threadUid)
                .orElseThrow(() -> new NotFoundException("Agent queue thread not found: " + threadUid));
    }

    /** Build a queue-notice protobuf for the thread and hand it off to the messaging layer. */
    private void dispatch(ThreadEntity thread, QueueNotificationPayload payload) {
        MessageProtobuf message = ThreadMessageUtil.getAgentQueueNoticeMessage(payload, thread);
        messageSendService.sendProtobufMessage(message);
    }

    /** Buffer single-member deltas so we can batch them later, unless they must be sent immediately. */
    private void bufferAndSchedule(AgentEntity agent, QueueNotificationPayload payload,
            QueueNotificationPayload.QueueNotificationSnapshot snapshot) {
        if (agent == null || payload == null) {
            return;
        }
        String agentUid = agent.getUid();
        PendingBatch batch = pendingBatches.computeIfAbsent(agentUid, PendingBatch::new);
        batch.addEvent(payload, snapshot);
        scheduleFlush(agentUid, agent.resolveQueueNoticeBatchWindowMs());
    }

    /** Schedule the batch flush task based on the agent-configured batching window. */
    private void scheduleFlush(String agentUid, int batchWindowMs) {
        PendingBatch batch = pendingBatches.get(agentUid);
        if (batch == null) {
            return;
        }
        if (batchWindowMs <= 0) {
            try {
                batchingExecutor.execute(() -> flushBatch(agentUid));
            } catch (RejectedExecutionException ex) {
                log.warn("Skip scheduling immediate queue batch for agent {}: {}", agentUid, ex.getMessage());
            }
            return;
        }
        synchronized (batch) {
            if (batch.hasScheduledTask()) {
                return;
            }
            try {
                ScheduledFuture<?> future = batchingExecutor.schedule(() -> flushBatch(agentUid), batchWindowMs,
                        TimeUnit.MILLISECONDS);
                batch.setScheduledTask(future);
            } catch (RejectedExecutionException ex) {
                log.warn("Skip scheduling queue batch for agent {}: {}", agentUid, ex.getMessage());
            }
        }
    }

    /** Drain buffered events for an agent and deliver either a single event or bulk update. */
    private void flushBatch(String agentUid) {
        PendingBatch batch = pendingBatches.remove(agentUid);
        if (batch == null) {
            return;
        }
        List<QueueNotificationPayload.QueueNotificationSnapshot> snapshots = batch.snapshotValues();
        List<QueueNotificationPayload> events = batch.drainEvents();
        if (events.isEmpty()) {
            return;
        }
        try {
            AgentEntity agent = agentRestService.findByUid(agentUid)
                    .orElseThrow(() -> new NotFoundException("Agent " + agentUid + " not found"));
            ThreadEntity agentQueueThread = resolveAgentQueueThread(agent);
            QueueNotificationPayload payloadToSend;
            if (events.size() == 1) {
                payloadToSend = events.get(0);
            } else {
                payloadToSend = queueNotificationBuilder.buildBatchUpdate(agentUid, events, snapshots);
            }
            dispatch(agentQueueThread, payloadToSend);
        } catch (Exception ex) {
            log.warn("Failed to flush queue notice batch for agent {}: {}", agentUid, ex.getMessage(), ex);
        }
    }

    /** Ensure we do not leak pending tasks when the bean is destroyed. */
    @PreDestroy
    public void shutdownBatchingExecutor() {
        batchingExecutor.shutdownNow();
    }

    /** Shared helper to emit queue deltas (leave/timeout) with snapshot batching. */
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
            QueueNotificationPayload payload = payloadBuilder.apply(member, agentUid);
            QueueNotificationPayload.QueueNotificationSnapshot snapshot = queueNotificationBuilder.buildSnapshot(member);
            bufferAndSchedule(agent, payload, snapshot);
        } catch (Exception ex) {
            log.warn("Failed to publish queue {} notice for member {} (agent {}): {}", context,
                    member.getUid(), agentUid, ex.getMessage(), ex);
        }
    }

    /** Find an agent uid associated with the member from thread, topic, or queue metadata. */
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

    /** Extract the trailing segment from a topic string which encodes agent uid. */
    private String extractUidFromQueueTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return null;
        }
        int idx = topic.lastIndexOf('/');
        return idx >= 0 ? topic.substring(idx + 1) : topic;
    }

    /** Lazily fetch the queue member rest service to avoid circular dependencies. */
    private com.bytedesk.service.queue_member.QueueMemberRestService queueMemberRestService() {
        return queueMemberRestServiceProvider.getObject();
    }

    private static final class PendingBatch {
        private final List<QueueNotificationPayload> events = new ArrayList<>();
        private final Map<String, QueueNotificationPayload.QueueNotificationSnapshot> snapshotByMember = new LinkedHashMap<>();
        private ScheduledFuture<?> scheduledTask;

        /** Remember the agent identity for potential future logging hooks. */
        private PendingBatch(String agentUid) {
            // agentUid reserved for logging/debug hooks if needed later
        }

        /** Add a queue event and latest snapshot override for the corresponding member. */
        private synchronized void addEvent(QueueNotificationPayload payload,
                QueueNotificationPayload.QueueNotificationSnapshot snapshot) {
            events.add(payload);
            if (snapshot != null && StringUtils.hasText(snapshot.getQueueMemberUid())) {
                snapshotByMember.put(snapshot.getQueueMemberUid(), snapshot);
            }
        }

        /** Return snapshot list preserving insertion order for consistent UI rendering. */
        private synchronized List<QueueNotificationPayload.QueueNotificationSnapshot> snapshotValues() {
            return new ArrayList<>(snapshotByMember.values());
        }

        /** Copy buffered events then clear internal state for the next batch cycle. */
        private synchronized List<QueueNotificationPayload> drainEvents() {
            List<QueueNotificationPayload> copy = new ArrayList<>(events);
            events.clear();
            snapshotByMember.clear();
            scheduledTask = null;
            return copy;
        }

        /** Indicates whether a flush runnable is already scheduled. */
        private synchronized boolean hasScheduledTask() {
            return scheduledTask != null && !scheduledTask.isDone();
        }

        /** Track scheduled flush so we do not enqueue duplicate timers. */
        private synchronized void setScheduledTask(ScheduledFuture<?> future) {
            this.scheduledTask = future;
        }
    }
}
