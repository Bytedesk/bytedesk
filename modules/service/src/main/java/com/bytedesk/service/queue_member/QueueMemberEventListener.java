/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:51:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 16:58:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.QueueContent;
import com.bytedesk.core.message.content.QueueNotification;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.enums.ThreadCloseTypeEnum;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.agent.AgentCapacityService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.queue_settings.QueueTipTemplateUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMemberEventListener {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;

    private final QueueMemberRestService queueMemberRestService;

    private final ThreadRestService threadRestService;

    private final IMessageSendService messageSendService;

    private final WorkgroupRestService workgroupRestService;

    private final PresenceFacadeService presenceFacadeService;

    private final QueueService queueService;

    private final AgentCapacityService agentCapacityService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final MessageRestService messageRestService;

    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue member onThreadAcceptEvent: {}", thread.getUid());
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        if (memberOptional.isPresent()) {
            QueueMemberEntity member = memberOptional.get();
            member.manualAcceptThread();
            queueMemberRestService.saveAsyncBestEffort(member);
            // 
            handleQueueAcceptBroadcast(thread, member);
        } else {
            log.error("queue member onThreadAcceptEvent: member not found: {}", thread.getUid());
        }
    }

    /**
     * 当某个会话关闭，则检查当前客服队列或所在工作组队列中是否有排队成员，如果有，则自动接入最前面的排队成员
     */
    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        if (thread == null || thread.getTopic() == null) {
            return;
        }

        // 只在会话确实已关闭时触发（避免未来事件重复/误触发）
        if (!ThreadProcessStatusEnum.CLOSED.name().equals(thread.getStatus())) {
            return;
        }

        // 可配置：仅自动关闭时才触发自动接入（避免人工关闭也触发）
        if (!ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(thread.getCloseType())) {
            log.debug("queue member auto accept skipped, closeType not AUTO: threadUid={} closeType={} type={}",
                    thread.getUid(), thread.getCloseType(), thread.getType());
            return;
        }

        // 关闭会话必须能解析到接待客服，否则无法确定"谁来自动接入"
        // Try to resolve agent from thread (works for agent threads)
        UserProtobuf agentProto = resolveAgentProtobuf(thread);

        // If no agent in thread and it's a workgroup thread, route via workgroup
        if (agentProto == null && isWorkgroupThread(thread)) {
            log.debug("Thread is workgroup type, attempting workgroup routing - threadUid: {}", thread.getUid());
            agentProto = resolveWorkgroupAgent(thread);
        }

        // Validate we have an agent
        if (agentProto == null || !StringUtils.hasText(agentProto.getUid())) {
            log.debug("queue member auto accept skipped, agent missing: threadUid={} topic={} status={} closeType={}",
                    thread.getUid(), thread.getTopic(), thread.getStatus(), thread.getCloseType());
            return;
        }

        QueueMemberEntity closingQueueMember = queueMemberRestService.findByThreadUid(thread.getUid()).orElse(null);

        boolean assigned = false;
        try {
            if (isWorkgroupThread(thread)) {
                // 工作组类型：只从工作组队列接入（避免误接入到客服一对一队列）
                assigned = tryAssignFromWorkgroupQueue(agentProto, thread, closingQueueMember);
            } else {
                // 一对一客服：优先从“客服个人队列”接入；若无，再从“工作组队列”兜底接入
                assigned = tryAssignFromAgentQueue(agentProto, thread, closingQueueMember);
                if (!assigned) {
                    assigned = tryAssignFromWorkgroupQueue(agentProto, thread, closingQueueMember);
                }
            }
        } catch (Exception e) {
            log.warn("queue member auto accept failed: agentUid={} closedThreadUid={} error={}",
                    agentProto.getUid(), thread.getUid(), e.getMessage(), e);
            return;
        }

        if (assigned) {
            log.info("queue member auto accept completed: agentUid={} closedThreadUid={} closeType={}",
                    agentProto.getUid(), thread.getUid(), thread.getCloseType());
        } else {
            log.debug("queue member auto accept skipped, no queued members: agentUid={} closedThreadUid={} closeType={}",
                    agentProto.getUid(), thread.getUid(), thread.getCloseType());
        }
    }

    /**
     * 1. 通知工作组内其他成员该排队会话已被接受 QUEUE_ACCEPT, 并在desktop端从队列中删除此排队会话
     * 2. 刷新同队列其余排队成员位置 QUEUE_UPDATE，并在visitor端更新排队位置显示
     */
    private void handleQueueAcceptBroadcast(ThreadEntity thread, QueueMemberEntity acceptedMember) {
        if (thread == null || acceptedMember == null) {
            return;
        }
        if (acceptedMember.getWorkgroupQueue() == null) {
            log.debug("queue accept broadcast skipped, no workgroup queue bound: threadUid={}", thread.getUid());
            return;
        }

        List<QueueMemberEntity> queueMembers = queueMemberRestService.findQueuingMembersByWorkgroupQueueUid(acceptedMember.getWorkgroupQueue().getUid());
        queueMembers.removeIf(member -> member.getThread() == null);
        queueMembers.removeIf(member -> thread.getUid().equals(member.getThread().getUid()));

        notifyAgentsQueueAccepted(thread, acceptedMember, queueMembers.size());
        if (queueMembers.isEmpty()) {
            return;
        }
        broadcastQueueUpdates(queueMembers);
    }

    public UserProtobuf resolveAgentProtobuf(ThreadEntity thread) {
        if (thread == null || !StringUtils.hasText(thread.getAgent())) {
            return null;
        }
        return UserProtobuf.fromJson(thread.getAgent());
    }

    /**
     * Check if thread is a workgroup thread
     */
    private boolean isWorkgroupThread(ThreadEntity thread) {
        if (thread == null) {
            return false;
        }
        // 主逻辑：以 thread.type 为准（createWorkgroupThread 会写入 WORKGROUP）
        if (ThreadTypeEnum.WORKGROUP.name().equalsIgnoreCase(thread.getType())) {
            return true;
        }

        // 兼容历史数据：部分旧 thread 可能未写 type，但 workgroup 字段存在
        return !StringUtils.hasText(thread.getType()) && StringUtils.hasText(thread.getWorkgroup());
    }

    /**
     * Resolve agent for workgroup thread using routing strategy
     */
    private UserProtobuf resolveWorkgroupAgent(ThreadEntity thread) {
        Optional<WorkgroupEntity> workgroupOpt = resolveWorkgroupEntity(thread);
        if (workgroupOpt.isEmpty()) {
            log.debug("Workgroup not found for thread - threadUid: {}", thread.getUid());
            return null;
        }

        WorkgroupEntity workgroup = workgroupOpt.get();

        // Get available agents (online + available status)
        List<AgentEntity> availableAgents = presenceFacadeService.getAvailableAgents(workgroup);
        if (availableAgents.isEmpty()) {
            log.debug("No available agents for workgroup routing - workgroupUid: {}", workgroup.getUid());
            return null;
        }

        // Use routing strategy to find agent with capacity
        AgentEntity selectedAgent = agentCapacityService.findAvailableAgentWithCapacity(workgroup, thread, availableAgents);
        if (selectedAgent == null) {
            log.debug("No agent with capacity found - workgroupUid: {}", workgroup.getUid());
            return null;
        }

        log.info("Workgroup routing selected agent - workgroupUid: {}, agentUid: {}, routingMode: {}",
                workgroup.getUid(), selectedAgent.getUid(), workgroup.getRoutingMode());

        return selectedAgent.toUserProtobuf();
    }

    public boolean tryAssignFromAgentQueue(UserProtobuf agentProto, ThreadEntity closedThread,
            QueueMemberEntity closingQueueMember) {
        if (agentProto == null || !StringUtils.hasText(agentProto.getUid())) {
            return false;
        }
        Optional<QueueEntity> agentQueueOpt = Optional.empty();
        if (closingQueueMember != null && closingQueueMember.getAgentQueue() != null) {
            agentQueueOpt = Optional.of(closingQueueMember.getAgentQueue());
        } else {
            agentQueueOpt = resolveAgentQueueEntity(agentProto.getUid());
        }
        if (!agentQueueOpt.isPresent()) {
            return false;
        }
        Optional<QueueMemberEntity> nextMemberOpt = queueMemberRestService
                .findEarliestAgentQueueMemberForUpdate(agentQueueOpt.get().getUid());
        if (!nextMemberOpt.isPresent()) {
            return false;
        }
        QueueMemberEntity nextMember = nextMemberOpt.get();
        if (isSameThread(nextMember, closedThread)) {
            return false;
        }
        return autoAcceptQueueMember(nextMember, closedThread, agentProto, agentQueueOpt.get());
    }

    public boolean tryAssignFromWorkgroupQueue(UserProtobuf agentProto, ThreadEntity closedThread,
            QueueMemberEntity closingQueueMember) {
        Optional<QueueEntity> workgroupQueueOpt = resolveWorkgroupQueueEntity(closedThread, closingQueueMember);
        if (!workgroupQueueOpt.isPresent()) {
            return false;
        }
        Optional<QueueMemberEntity> nextMemberOpt = queueMemberRestService
            .findEarliestWorkgroupQueueMemberForUpdate(workgroupQueueOpt.get().getUid());
        if (!nextMemberOpt.isPresent()) {
            return false;
        }
        QueueMemberEntity nextMember = nextMemberOpt.get();
        if (isSameThread(nextMember, closedThread)) {
            return false;
        }
        QueueEntity agentQueue = resolveAgentQueueEntity(agentProto.getUid()).orElse(null);
        return autoAcceptQueueMember(nextMember, closedThread, agentProto, agentQueue);
    }

    private Optional<QueueEntity> resolveAgentQueueEntity(String agentUid) {
        if (!StringUtils.hasText(agentUid)) {
            return Optional.empty();
        }
        String today = LocalDate.now().format(ISO_DATE);
        String topic = TopicUtils.getQueueTopicFromUid(agentUid);
        return queueService.findByTopicAndDay(topic, today);
    }

    private Optional<QueueEntity> resolveWorkgroupQueueEntity(ThreadEntity thread,
            QueueMemberEntity closingQueueMember) {
        if (closingQueueMember != null && closingQueueMember.getWorkgroupQueue() != null) {
            return Optional.of(closingQueueMember.getWorkgroupQueue());
        }
        Optional<WorkgroupEntity> workgroupOpt = resolveWorkgroupEntity(thread);
        if (!workgroupOpt.isPresent()) {
            return Optional.empty();
        }
        String today = LocalDate.now().format(ISO_DATE);
        String topic = TopicUtils.getQueueTopicFromUid(workgroupOpt.get().getUid());
        return queueService.findByTopicAndDay(topic, today);
    }

    private boolean autoAcceptQueueMember(QueueMemberEntity queueMember, ThreadEntity referenceThread,
            UserProtobuf agentProto, QueueEntity agentQueue) {
        if (queueMember == null || agentProto == null) {
            return false;
        }
        ThreadEntity targetThread = queueMember.getThread();
        if (targetThread == null) {
            return false;
        }
        if (!ThreadProcessStatusEnum.QUEUING.name().equals(targetThread.getStatus())) {
            return false;
        }

        targetThread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        targetThread.setAgent(agentProto.toJson());
        targetThread.setUserUid(agentProto.getUid());
        if (referenceThread != null && referenceThread.getOwner() != null) {
            targetThread.setOwner(referenceThread.getOwner());
        }

        ThreadEntity savedThread = threadRestService.save(targetThread);
        if (savedThread == null) {
            log.warn("Auto accept skipped, thread persistence failed: threadUid={}", targetThread.getUid());
            return false;
        }

        if (agentQueue != null) {
            queueMember.setAgentQueue(agentQueue);
        }
        queueMember.agentAutoAcceptThread();
        queueMemberRestService.saveAsyncBestEffort(queueMember);

        publishThreadAcceptedEvents(savedThread);
        return true;
    }

    private void publishThreadAcceptedEvents(ThreadEntity thread) {
        if (thread == null) {
            return;
        }
        try {
            bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, thread));
        } catch (Exception e) {
            log.warn("Failed to publish ThreadAddTopicEvent for thread {}: {}", thread.getUid(), e.getMessage());
        }
        try {
            bytedeskEventPublisher.publishEvent(new ThreadAcceptEvent(this, thread));
        } catch (Exception e) {
            log.warn("Failed to publish ThreadAcceptEvent for thread {}: {}", thread.getUid(), e.getMessage());
        }
    }

    private boolean isSameThread(QueueMemberEntity queueMember, ThreadEntity thread) {
        if (queueMember == null || queueMember.getThread() == null || thread == null) {
            return false;
        }
        return thread.getUid().equals(queueMember.getThread().getUid());
    }

    private void notifyAgentsQueueAccepted(ThreadEntity thread, QueueMemberEntity acceptedMember, int remainingQueueSize) {
        Optional<WorkgroupEntity> workgroupOpt = resolveWorkgroupEntity(thread);
        if (!workgroupOpt.isPresent()) {
            log.debug("queue accept broadcast skipped, workgroup missing: threadUid={}", thread != null ? thread.getUid() : null);
            return;
        }

        List<AgentEntity> availableAgents = presenceFacadeService.getAvailableAgents(workgroupOpt.get());
        if (availableAgents == null || availableAgents.isEmpty()) {
            log.debug("queue accept broadcast skipped, no available agents: workgroupUid={}", workgroupOpt.get().getUid());
            return;
        }

        String acceptedAgentUid = resolveAgentUid(thread);
        List<AgentEntity> targetAgents = availableAgents.stream()
                .filter(agent -> !StringUtils.hasText(acceptedAgentUid) || !acceptedAgentUid.equals(agent.getUid()))
                .toList();

        if (targetAgents.isEmpty()) {
            log.debug("queue accept broadcast skipped, only accepting agent online: threadUid={} workgroupUid={}",
                    thread.getUid(), workgroupOpt.get().getUid());
            return;
        }

        QueueNotification payload = buildQueueNotification(thread, acceptedMember, null, remainingQueueSize, null);
        for (AgentEntity agent : targetAgents) {
            try {
                ThreadEntity agentQueueThread = queueMemberRestService.createAgentQueueThread(agent);
                MessageProtobuf message = ThreadMessageUtil.getAgentQueueAcceptMessage(payload, agentQueueThread);
                messageSendService.sendProtobufMessage(message);
            } catch (Exception e) {
                log.error("Failed to send QUEUE_ACCEPT notice: workgroupUid={}, agentUid={}, queueMemberUid={}, error={}",
                        workgroupOpt.get().getUid(), agent.getUid(), acceptedMember.getUid(), e.getMessage(), e);
            }
        }
    }

    private void broadcastQueueUpdates(List<QueueMemberEntity> queueMembers) {
        int totalCount = queueMembers.size();
        for (int i = 0; i < queueMembers.size(); i++) {
            QueueMemberEntity member = queueMembers.get(i);
            int position = i;
            sendVisitorQueueUpdate(member, position, totalCount);
        }
    }

    private void sendVisitorQueueUpdate(QueueMemberEntity queueMember, int position, int totalCount) {
        log.debug("sendVisitorQueueUpdate: queueMemberUid={}, position={}, totalCount={}",
                queueMember.getUid(), position, totalCount);
        ThreadEntity targetThread = queueMember.getThread();
        if (targetThread == null) {
            return;
        }
        QueueContent queueContent = buildQueueContent(queueMember, position, totalCount);
        log.debug("sendVisitorQueueUpdate: queueMemberUid={}, content={}",
                queueMember.getUid(), queueContent.getContent());
        try {
            targetThread.setContent(ThreadContent.of(MessageTypeEnum.QUEUE_UPDATE, queueContent.getContent(), queueContent.toJson()).toJson());
            threadRestService.save(targetThread);
        } catch (Exception e) {
            log.debug("queue update content persistence skipped: threadUid={}, error={}",
                    targetThread.getUid(), e.getMessage());
        }
        try {
            MessageProtobuf message = ThreadMessageUtil.getThreadQueueUpdateMessage(queueContent, targetThread);
            messageSendService.sendProtobufMessage(message);
        } catch (Exception e) {
            log.error("Failed to send visitor QUEUE_UPDATE: threadUid={}, queueMemberUid={}, error={}",
                    targetThread.getUid(), queueMember.getUid(), e.getMessage(), e);
        }
    }

    private QueueNotification buildQueueNotification(ThreadEntity thread, QueueMemberEntity queueMember,
            Integer position, int queueSize, Integer waitSeconds) {
        Long estimatedWaitMs = waitSeconds != null ? waitSeconds.longValue() * 1000L : null;
        return QueueNotification.builder()
                .queueMemberUid(queueMember.getUid())
                .threadUid(thread.getUid())
                .threadTopic(thread.getTopic())
                .position(position)
                .queueSize(queueSize)
                .estimatedWaitMs(estimatedWaitMs)
                .serverTimestamp(System.currentTimeMillis())
                .user(thread.getUser())
                .build();
    }

    private QueueContent buildQueueContent(QueueMemberEntity queueMember, int position, int totalCount) {
        int safePosition = Math.max(position, 1);
        int safeQueueSize = Math.max(totalCount, safePosition);

        QueueSettingsEntity queueSettings = resolveQueueSettings(queueMember);
        int avgWaitTimePerPerson = queueSettings != null && queueSettings.getAvgWaitTimePerPerson() != null
                ? queueSettings.getAvgWaitTimePerPerson()
                : QueueTipTemplateUtils.DEFAULT_AVG_WAIT_TIME_PER_PERSON;

        String displayText = QueueTipTemplateUtils.resolveTemplate(queueSettings, safePosition, safeQueueSize,
                avgWaitTimePerPerson);

        int waitSeconds = safePosition * avgWaitTimePerPerson;
        String estimatedWaitTime = QueueTipTemplateUtils.formatWaitTime(waitSeconds);

        return QueueContent.builder()
                .content(displayText)
                .position(safePosition)
                .queueSize(safeQueueSize)
                .waitSeconds(waitSeconds)
                .estimatedWaitTime(estimatedWaitTime)
                .serverTimestamp(System.currentTimeMillis())
                .build();
    }

    private QueueSettingsEntity resolveQueueSettings(QueueMemberEntity queueMember) {
        if (queueMember == null) {
            return null;
        }
        ThreadEntity thread = queueMember.getThread();
        if (thread == null) {
            return null;
        }

        Optional<WorkgroupEntity> workgroupOpt = resolveWorkgroupEntity(thread);
        return workgroupOpt.map(this::extractQueueSettings).orElse(null);
    }

    private QueueSettingsEntity extractQueueSettings(WorkgroupEntity workgroup) {
        if (workgroup == null || workgroup.getSettings() == null) {
            return null;
        }
        QueueSettingsEntity settings = workgroup.getSettings().getQueueSettings();
        if (settings == null) {
            settings = workgroup.getSettings().getDraftQueueSettings();
        }
        return settings;
    }

    private Optional<WorkgroupEntity> resolveWorkgroupEntity(ThreadEntity thread) {
        if (thread == null) {
            return Optional.empty();
        }

        UserProtobuf workgroupProtobuf = UserProtobuf.fromJson(thread.getWorkgroup());
        if (workgroupProtobuf == null || !StringUtils.hasText(workgroupProtobuf.getUid())) {
            return Optional.empty();
        }

        try {
            Optional<WorkgroupEntity> workgroupOpt = workgroupRestService.findByUid(workgroupProtobuf.getUid());
            if (!workgroupOpt.isPresent()) {
                log.debug("workgroup not found when resolving thread workgroup: {}", workgroupProtobuf.getUid());
            }
            return workgroupOpt;
        } catch (Exception e) {
            log.warn("Failed to resolve workgroup {}: {}", workgroupProtobuf.getUid(), e.getMessage());
            return Optional.empty();
        }
    }

    private String resolveAgentUid(ThreadEntity thread) {
        if (thread == null || !StringUtils.hasText(thread.getAgent())) {
            return null;
        }
        UserProtobuf agentProto = UserProtobuf.fromJson(thread.getAgent());
        return agentProto != null ? agentProto.getUid() : null;
    }


    /**
     * 
     */
    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message == null) {
            return;
        }
        // log.debug("QueueMemberEventListener 接收到新消息事件: messageUid={}, threadUid={},
        // content={}",
        // message.getUid(), message.getThread().getUid(), message.getContent());

        // 获取消息对应的会话线程
        ThreadEntity thread = null;
        try {
            thread = threadRestService.findByUid(message.getThread().getUid()).orElse(null);
            if (thread == null) {
                log.warn("消息对应的会话不存在: messageUid={}, threadUid={}",
                        message.getUid(), message.getThread().getUid());
                return;
            }

            if (message.isFromVisitor()) {
                // 访客消息：标记为“待客服回复”
                ensureVisitorMessageUnreplied(message);
                // 更新访客消息统计
                updateVisitorMessageStats(message, thread);
            } else if (message.isFromAgent()) {
                // 客服消息：先抓取“上一次客服回复时间”作为窗口起点（避免扫描整段历史）
                ZonedDateTime previousAgentLastResponseAt = null;
                ZonedDateTime visitorFirstMessageAt = null;
                Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
                if (queueMemberOpt.isPresent()) {
                    previousAgentLastResponseAt = queueMemberOpt.get().getAgentLastResponseAt();
                    visitorFirstMessageAt = queueMemberOpt.get().getVisitorFirstMessageAt();
                }

                // 客服消息本身不属于“待回复”消息
                ensureNonVisitorMessageReplied(message);
                // 更新客服消息统计
                updateAgentMessageStats(message, thread);

                // 批量标记：将该会话中“上次客服回复之后”的未回复访客消息全部置为已回复
                markVisitorMessagesRepliedByAgent(thread, message, previousAgentLastResponseAt, visitorFirstMessageAt);
            } else if (message.isFromRobot()) {
                // 机器人消息本身不属于“待回复”消息（但不清理访客待回复状态）
                ensureNonVisitorMessageReplied(message);
                // 处理机器人消息
                updateRobotMessageStats(message, thread);
            } else if (message.isFromSystem()) {
                // 系统消息本身不属于“待回复”消息
                ensureNonVisitorMessageReplied(message);
                // 处理系统消息
                updateSystemMessageStats(message, thread);
            }
        } catch (Exception e) {
            log.error("处理消息事件时出错: {}", e.getMessage(), e);
        }
    }

    private void ensureVisitorMessageUnreplied(MessageEntity message) {
        if (message == null || !message.isFromVisitor()) {
            return;
        }

        boolean changed = false;
        if (message.getAgentReplied() == null || Boolean.TRUE.equals(message.getAgentReplied())) {
            message.setAgentReplied(false);
            changed = true;
        }
        if (message.getAgentRepliedAt() != null) {
            message.setAgentRepliedAt(null);
            changed = true;
        }
        if (StringUtils.hasText(message.getAgentRepliedByUid())) {
            message.setAgentRepliedByUid(null);
            changed = true;
        }

        if (changed) {
            messageRestService.save(message);
        }
    }

    private void ensureNonVisitorMessageReplied(MessageEntity message) {
        if (message == null || message.isFromVisitor()) {
            return;
        }

        if (!Boolean.TRUE.equals(message.getAgentReplied())) {
            message.setAgentReplied(true);
            messageRestService.save(message);
        }
    }

    private void markVisitorMessagesRepliedByAgent(ThreadEntity thread, MessageEntity agentMessage,
            ZonedDateTime previousAgentLastResponseAt, ZonedDateTime visitorFirstMessageAt) {
        if (thread == null || agentMessage == null || !agentMessage.isFromAgent()) {
            return;
        }

        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
        if (queueMemberOpt.isEmpty()) {
            // QueueMember 缺失时，暂不做批量标记（避免无窗口起点导致扫描大量历史）
            return;
        }

        ZonedDateTime now = BdDateUtils.now();
        ZonedDateTime windowStart = previousAgentLastResponseAt;
        if (windowStart == null) {
            windowStart = visitorFirstMessageAt;
        }
        if (windowStart == null) {
            windowStart = thread.getCreatedAt();
        }
        if (windowStart == null) {
            // 极端兜底
            windowStart = now.minusDays(1);
        }

        String agentUid = agentMessage.getUserProtobuf().getUid();
        List<MessageEntity> candidates = messageRestService.findByThreadUidBetweenCreatedAt(thread.getUid(), windowStart,
                now);
        if (candidates == null || candidates.isEmpty()) {
            return;
        }

        int updated = 0;
        for (MessageEntity m : candidates) {
            if (m == null) {
                continue;
            }
            if (agentMessage.getUid() != null && agentMessage.getUid().equals(m.getUid())) {
                continue;
            }
            if (!m.isFromVisitor()) {
                continue;
            }
            if (Boolean.TRUE.equals(m.getAgentReplied())) {
                continue;
            }

            m.setAgentReplied(true);
            m.setAgentRepliedAt(now);
            m.setAgentRepliedByUid(agentUid);
            messageRestService.save(m);
            updated++;
        }

        if (updated > 0) {
            log.debug("已批量标记访客消息为已回复: threadUid={}, count={}, agentUid={}", thread.getUid(), updated,
                    agentUid);
        }
    }

    /**
     * 更新访客消息统计
     * 
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateVisitorMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromVisitor()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新首次消息时间（如果尚未设置）
            if (queueMember.getVisitorFirstMessageAt() == null) {
                queueMember.setVisitorFirstMessageAt(now);
            }

            // 更新最后一次访客消息时间
            queueMember.setVisitorLastMessageAt(now);

            // 更新访客消息计数
            queueMember.setVisitorMessageCount(queueMember.getVisitorMessageCount() + 1);

            // 保存更新 - 支持重试机制
            queueMemberRestService.saveAsyncBestEffort(queueMember);
            log.debug("已更新队列成员访客消息统计: threadUid={}, visitorMsgCount={}",
                    thread.getUid(), queueMember.getVisitorMessageCount());
        } catch (Exception e) {
            log.error("更新访客消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新客服消息统计
     * 
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateAgentMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromAgent()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新客服消息计数
            queueMember.setAgentMessageCount(queueMember.getAgentMessageCount() + 1);

            // 如果是首次响应，记录首次响应时间
            if (!queueMember.getAgentFirstResponse() && queueMember.getVisitorLastMessageAt() != null) {
                queueMember.setAgentFirstResponse(true);
                queueMember.setAgentFirstResponseAt(now);

                // 计算首次响应时间（秒）
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();
                queueMember.setAgentMaxResponseLength((int) responseTimeInSeconds);
                queueMember.setAgentAvgResponseLength((int) responseTimeInSeconds);
            } else if (queueMember.getVisitorLastMessageAt() != null) {
                // 非首次响应，更新平均和最大响应时间
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();

                // 更新最大响应时间
                if (responseTimeInSeconds > queueMember.getAgentMaxResponseLength()) {
                    queueMember.setAgentMaxResponseLength((int) responseTimeInSeconds);
                }

                // 更新平均响应时间 - 使用累计平均计算方法
                // (currentAvg * (messageCount-1) + newValue) / messageCount
                int messageCount = queueMember.getAgentMessageCount();
                if (messageCount > 1) { // 避免除以零
                    int currentTotal = queueMember.getAgentAvgResponseLength() * (messageCount - 1);
                    queueMember.setAgentAvgResponseLength((currentTotal + (int) responseTimeInSeconds) / messageCount);
                }
            }

            // 更新最后响应时间
            queueMember.setAgentLastResponseAt(now);

            // 保存更新
            queueMemberRestService.saveAsyncBestEffort(queueMember);
            log.debug("已更新队列成员客服消息统计: threadUid={}, agentMsgCount={}, avgResponseTime={}s, maxResponseTime={}s",
                    thread.getUid(), queueMember.getAgentMessageCount(),
                    queueMember.getAgentAvgResponseLength(), queueMember.getAgentMaxResponseLength());
        } catch (Exception e) {
            log.error("更新客服消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新机器人消息统计
     *
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateRobotMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromRobot()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新首次机器人消息时间（如果尚未设置）
            if (queueMember.getRobotFirstResponseAt() == null) {
                queueMember.setRobotFirstResponseAt(now);
            }

            // 更新最后一次机器人消息时间
            queueMember.setRobotLastResponseAt(now);

            // 更新机器人消息计数
            queueMember.setRobotMessageCount(queueMember.getRobotMessageCount() + 1);

            // 如果是访客提问后的机器人回复，计算响应时间
            if (queueMember.getVisitorLastMessageAt() != null) {
                long responseTimeInSeconds = Duration.between(queueMember.getVisitorLastMessageAt(), now).getSeconds();

                // 更新最大响应时间
                if (queueMember.getRobotMaxResponseLength() == 0 ||
                        responseTimeInSeconds > queueMember.getRobotMaxResponseLength()) {
                    queueMember.setRobotMaxResponseLength((int) responseTimeInSeconds);
                }

                // 更新平均响应时间
                int messageCount = queueMember.getRobotMessageCount();
                if (messageCount > 1) {
                    int currentTotal = queueMember.getRobotAvgResponseLength() * (messageCount - 1);
                    queueMember.setRobotAvgResponseLength((currentTotal + (int) responseTimeInSeconds) / messageCount);
                } else {
                    queueMember.setRobotAvgResponseLength((int) responseTimeInSeconds);
                }
            }

            // 保存更新
            queueMemberRestService.saveAsyncBestEffort(queueMember);
            log.debug("已更新队列成员机器人消息统计: threadUid={}, robotMsgCount={}, avgResponseTime={}s, maxResponseTime={}s",
                    thread.getUid(), queueMember.getRobotMessageCount(),
                    queueMember.getRobotAvgResponseLength(), queueMember.getRobotMaxResponseLength());
        } catch (Exception e) {
            log.error("更新机器人消息统计时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新系统消息统计
     *
     * @param message 消息对象
     * @param thread  会话对象
     */
    private void updateSystemMessageStats(MessageEntity message, ThreadEntity thread) {
        if (thread == null || message == null) {
            return;
        }
        if (!message.isFromSystem()) {
            return;
        }

        try {
            // 查找关联的队列成员记录
            Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
            if (queueMemberOpt.isEmpty()) {
                log.warn("未找到与会话关联的队列成员: threadUid={}", thread.getUid());
                return;
            }

            QueueMemberEntity queueMember = queueMemberOpt.get();
            ZonedDateTime now = BdDateUtils.now();

            // 更新系统消息计数
            queueMember.setSystemMessageCount(queueMember.getSystemMessageCount() + 1);

            // 记录首次系统消息时间（如果尚未设置）
            if (queueMember.getSystemFirstResponseAt() == null) {
                queueMember.setSystemFirstResponseAt(now);
            }

            // 更新最后一次系统消息时间
            queueMember.setSystemLastResponseAt(now);

            // 保存更新
            queueMemberRestService.saveAsyncBestEffort(queueMember);
            log.debug("已更新队列成员系统消息统计: threadUid={}, systemMsgCount={}",
                    thread.getUid(), queueMember.getSystemMessageCount());
        } catch (Exception e) {
            log.error("更新系统消息统计时出错: {}", e.getMessage(), e);
        }
    }


}
