/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-18 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-18 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.routing_strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.event.ThreadTransferToAgentEvent;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.content.SystemContent;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.routing_strategy.AbstractThreadRoutingStrategy;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup_routing.WorkgroupRoutingService;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsResponse;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings_basic.TicketAssignmentModeEnum;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工单线程路由策略
 * 
 * <p>功能特点：
 * - 根据传入的 ticketUid 直接加载现有会话
 * - 支持工单对话的查看和继续
 * - 不创建新会话，仅加载已存在的工单会话
 * 
 * <p>处理流程：
 * 1. 验证 ticketUid 参数
 * 2. 根据 ticketUid 查找会话
 * 3. 返回会话信息和最后一条消息
 * 
 * @author jackning 270580156@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component("ticketThreadStrategy")
@AllArgsConstructor
public class TicketThreadRoutingStrategy extends AbstractThreadRoutingStrategy {

    private static final String DEFAULT_ASSIGNMENT_MODE = TicketAssignmentModeEnum.DEFAULT.name();

    private final ThreadRestService threadRestService;
    private final MessageRestService messageRestService;
    private final TicketRepository ticketRepository;
    private final WorkgroupRestService workgroupRestService;
    private final WorkgroupRoutingService workgroupRoutingService;
    private final TicketSettingsRestService ticketSettingsRestService;
    private final IMessageSendService messageSendService;
    private final BytedeskEventPublisher bytedeskEventPublisher;
    private final TopicSubscriptionRestService topicSubscriptionRestService;

    @Override
    protected ThreadRestService getThreadRestService() {
        return threadRestService;
    }

    @Override
    public MessageProtobuf createThread(VisitorRequest visitorRequest) {
        return executeWithExceptionHandling("load ticket thread", visitorRequest.getSid(),
                () -> loadTicketThread(visitorRequest));
    }

    /**
     * 加载工单会话
     * 
     * <p>根据 VisitorRequest 中的 sid（作为 ticketUid）加载工单，并获取其 threadUid/workgroupUid
     * 然后为该工单会话按工作组路由规则分配客服（如尚未分配），并返回会话信息与最后一条消息
     * 
     * @param visitorRequest 访客请求，sid 字段作为 ticketUid 使用
     * @return 消息协议对象，包含会话信息
     * @throws IllegalArgumentException 如果 ticketUid 为空、工单不存在或会话不存在
     */
    public MessageProtobuf loadTicketThread(VisitorRequest visitorRequest) {
        // 1. 获取 ticketUid (使用 sid 字段)
        String ticketUid = visitorRequest.getSid();

        // 2. 验证 ticketUid
        if (!StringUtils.hasText(ticketUid)) {
            log.error("Ticket UID is required for ticket thread routing");
            throw new IllegalArgumentException("Ticket UID is required for ticket thread routing");
        }

        log.debug("Loading ticket thread by ticketUid: {}", ticketUid);

        // 3. 加载工单
        TicketEntity ticket = ticketRepository.findByUid(ticketUid)
                .orElseThrow(() -> new IllegalArgumentException("Ticket uid " + ticketUid + " not found"));

        if (!StringUtils.hasText(ticket.getThreadUid())) {
            log.error("Ticket {} does not have threadUid", ticketUid);
            throw new IllegalArgumentException("Ticket " + ticketUid + " does not have threadUid");
        }

        // 4. 查找会话
        ThreadEntity thread = getThreadByUid(ticket.getThreadUid());

        // 4.1 会话已关闭：仅允许查看聊天记录，不做分配/不返回 continue/open 类消息
        if (ThreadProcessStatusEnum.CLOSED.name().equalsIgnoreCase(thread.getStatus())) {
            TicketBasicSettingsResponse basicSettings = resolveBasicSettings(ticket);
            String closeTip = basicSettings != null ? basicSettings.getCloseTip() : null;
            return getTicketThreadMessageReadOnly(thread, closeTip);
        }

        // 5. 确保 thread.workgroup 写入（便于前端/路由侧展示与后续处理）
        if (StringUtils.hasText(ticket.getWorkgroupUid())) {
            ensureThreadWorkgroup(thread, ticket.getWorkgroupUid());
        }

        // 6. 若未分配客服，按工作组路由策略选择并写入 thread
        ThreadEntity updatedThread = ensureThreadAgentAssigned(thread, ticket);

        // 6.1 NEW -> CHATTING：发送接入提示语、分配客服、通知被分配客服
        ThreadEntity latestThread = getThreadByUid(updatedThread.getUid());
        if (ThreadProcessStatusEnum.NEW.name().equalsIgnoreCase(latestThread.getStatus())) {
            MessageProtobuf processedMessage = handleTicketThreadNew(latestThread, ticket, visitorRequest);
            if (processedMessage != null) {
                return processedMessage;
            }
        }

        // 7. 返回会话消息（CHATTING 等）
        return getTicketThreadMessage(updatedThread);
    }

    private TicketBasicSettingsResponse resolveBasicSettings(TicketEntity ticket) {
        if (ticket == null) {
            return null;
        }
        String orgUid = StringUtils.hasText(ticket.getOrgUid())
                ? ticket.getOrgUid()
                : BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String workgroupUid = ticket.getWorkgroupUid();
        try {
            TicketSettingsResponse settings = ticketSettingsRestService.getOrDefaultByWorkgroup(orgUid, workgroupUid);
            return settings != null ? settings.getBasicSettings() : null;
        } catch (Exception e) {
            log.debug("Failed to resolve ticket basic settings for ticket {}: {}", ticket.getUid(), e.getMessage());
            return null;
        }
    }

    /**
     * 从 TicketBasicSettingsEntity 读取工单分配模式。
     * 外部工单依赖此配置决定客服分配策略（ROUND_ROBIN / LEAST_ACTIVE / RANDOM / MANUAL 等）。
     * 内部工单如已直接指定处理人，则不会经过此路径。
     */
    private String getTicketAssignmentMode(TicketEntity ticket) {
        TicketBasicSettingsResponse basicSettings = resolveBasicSettings(ticket);
        if (basicSettings != null && StringUtils.hasText(basicSettings.getAssignmentMode())) {
            return TicketAssignmentModeEnum.normalize(basicSettings.getAssignmentMode());
        }
        return DEFAULT_ASSIGNMENT_MODE;
    }

    /**
     * 处理工单会话 NEW 状态：
     * - 发送工单接入提示语
     * - 切换线程状态为 CHATTING
     * - 发布转接事件通知被分配客服
     */
    private MessageProtobuf handleTicketThreadNew(ThreadEntity threadFromDb, TicketEntity ticket, VisitorRequest visitorRequest) {
        if (threadFromDb == null || ticket == null) {
            return null;
        }

        // 并发保护：再次读取确认状态仍为 NEW
        ThreadEntity latest = getThreadByUid(threadFromDb.getUid());
        if (!ThreadProcessStatusEnum.NEW.name().equalsIgnoreCase(latest.getStatus())) {
            return null;
        }

        // 若还未分配客服，尝试分配
        ThreadEntity assigned = ensureThreadAgentAssigned(latest, ticket);
        ThreadEntity latestAfterAssign = getThreadByUid(assigned.getUid());

        // 切换为 CHATTING
        TicketBasicSettingsResponse basicSettings = resolveBasicSettings(ticket);
        String accessTip = basicSettings != null ? basicSettings.getAccessTip() : null;
        if (!StringUtils.hasText(accessTip)) {
            // fallback：避免 NEW 状态无提示导致前端体验不一致
            accessTip = "您好，工单已接入，我们将尽快为您处理。";
        }

        WelcomeContent welcomeContent = WelcomeContent.builder().content(accessTip).build();
        String payload = welcomeContent.toJson();
        latestAfterAssign.setChatting().setContent(ThreadContent.of(MessageTypeEnum.WELCOME, accessTip, payload).toJson());
        ThreadEntity savedThread = saveThread(latestAfterAssign);

        // 同步订阅 topic（含 internal），放在发消息之前，避免首条消息因订阅延迟而丢失
        subscribeThreadTopics(savedThread, topicSubscriptionRestService);

        // 通知被分配客服：按 WorkgroupThreadRoutingStrategy 的模式发布事件
        if (hasAssignedAgent(savedThread)) {
            try {
                bytedeskEventPublisher.publishEvent(new ThreadTransferToAgentEvent(this, savedThread));
            } catch (Exception e) {
                log.debug("Failed to publish ThreadTransferToAgentEvent for ticket {}: {}", ticket.getUid(), e.getMessage());
            }
        }

        // 发送接入提示语（结构化 welcome 消息）
        if (hasAssignedAgent(savedThread)) {
            MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(welcomeContent, savedThread);
            try {
                messageSendService.sendProtobufMessage(messageProtobuf);
            } catch (Exception e) {
                log.debug("Failed to send ticket access welcome message for ticket {}: {}", ticket.getUid(), e.getMessage());
            }
            return messageProtobuf;
        }

        // 若未分配到客服，退化为继续/最后消息
        return getTicketThreadMessage(savedThread);
    }

    private void ensureThreadWorkgroup(ThreadEntity thread, String workgroupUid) {
        try {
            UserProtobuf existing = thread.getWorkgroupProtobuf();
            if (existing != null && StringUtils.hasText(existing.getUid())) {
                return;
            }
        } catch (Exception ignore) {
            // ignore parse errors and attempt to overwrite below
        }

        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);
        if (workgroupOptional.isEmpty()) {
            log.warn("Workgroup uid {} not found for ticket thread {}, skip setting workgroup", workgroupUid,
                    thread.getUid());
            return;
        }

        thread.setWorkgroup(ServiceConvertUtils.convertToUserProtobufJSONString(workgroupOptional.get()));
        saveThread(thread);
    }

    private ThreadEntity ensureThreadAgentAssigned(ThreadEntity thread, TicketEntity ticket) {
        // 已有客服则直接返回
        if (hasAssignedAgent(thread)) {
            return thread;
        }

        String workgroupUid = ticket.getWorkgroupUid();
        if (!StringUtils.hasText(workgroupUid)) {
            // 内部工单可能走 departmentUid，此处按需求仅对 workgroup 路由；没有 workgroup 则不做分配
            log.debug("Ticket {} has no workgroupUid, skip agent assignment", ticket.getUid());
            return thread;
        }

        WorkgroupEntity workgroup = workgroupRestService.findByUid(workgroupUid)
                .orElseThrow(() -> new IllegalArgumentException("Workgroup uid " + workgroupUid + " not found"));

        ThreadEntity latestThread = getThreadByUid(thread.getUid());
        if (hasAssignedAgent(latestThread)) {
            return latestThread;
        }

        AgentEntity assignedAgent = resolveAssignedAgentFromTicket(ticket, workgroup);
        if (assignedAgent != null) {
            return applyAgentToThread(latestThread, workgroup, assignedAgent);
        }
        if (hasTicketAssignee(ticket)) {
            log.warn("Ticket {} already has assignee {}, but no matching workgroup agent found, skip rerouting",
                    ticket.getUid(), ticket.getAssignee() != null ? ticket.getAssignee().getUid() : null);
            return latestThread;
        }

        // 按 TicketBasicSettingsEntity.assignmentMode 选择客服（MANUAL 时返回 null 即不分配）
        String assignmentMode = getTicketAssignmentMode(ticket);
        AgentEntity selectedAgent = workgroupRoutingService.selectAgent(workgroup, latestThread, assignmentMode);
        if (selectedAgent == null) {
            log.info("No available agent for ticket {}, workgroup {}, assignmentMode={}",
                    ticket.getUid(), workgroupUid, assignmentMode);
            return latestThread;
        }

        UserProtobuf agentProtobuf = selectedAgent.toUserProtobuf();
        if (agentProtobuf == null || !StringUtils.hasText(agentProtobuf.getUid())) {
            log.warn("Selected agent has no uid for ticket {}, skip assignment", ticket.getUid());
            return latestThread;
        }

        // 同步工单 assignee/status：自动分配成功后，工单从 NEW/UNCLAIMED 进入 ASSIGNED
        try {
            boolean ticketHasAssignee = false;
            try {
                UserProtobuf existingAssignee = ticket.getAssignee();
                ticketHasAssignee = existingAssignee != null && StringUtils.hasText(existingAssignee.getUid());
            } catch (Exception ignore) {
                ticketHasAssignee = false;
            }

            boolean wroteTicketAssignee = false;
            if (!ticketHasAssignee) {
                MemberEntity member = selectedAgent.getMember();
                if (member != null && StringUtils.hasText(member.getUid())) {
                    UserProtobuf assigneeMember = UserProtobuf.builder()
                            .uid(member.getUid())
                            .nickname(member.getNickname())
                            .avatar(member.getAvatar())
                            .type(UserTypeEnum.MEMBER.name())
                            .build();
                    ticket.setAssignee(assigneeMember.toJson());
                    wroteTicketAssignee = true;
                } else {
                    log.warn("Selected agent {} has no member, skip syncing ticket assignee for ticket {}",
                            selectedAgent.getUid(), ticket.getUid());
                }
            }

            if (wroteTicketAssignee) {
                String status = ticket.getStatus();
                if (!StringUtils.hasText(status)
                        || TicketStatusEnum.NEW.name().equalsIgnoreCase(status)
                        || TicketStatusEnum.UNCLAIMED.name().equalsIgnoreCase(status)) {
                    ticket.setStatus(TicketStatusEnum.ASSIGNED.name());
                }
                ticketRepository.save(ticket);
            }
        } catch (Exception e) {
            log.warn("Failed to sync ticket assignee/status for ticket {}: {}", ticket.getUid(), e.getMessage());
        }

        ThreadEntity savedThread = applyAgentToThread(latestThread, workgroup, selectedAgent);
        log.info("Assigned ticket thread {} to agent {} for ticket {}", savedThread.getUid(),
                selectedAgent.getUid(), ticket.getUid());
        return savedThread;
    }

    private boolean hasTicketAssignee(TicketEntity ticket) {
        if (ticket == null) {
            return false;
        }
        try {
            UserProtobuf assignee = ticket.getAssignee();
            return assignee != null && StringUtils.hasText(assignee.getUid());
        } catch (Exception ex) {
            return false;
        }
    }

    private AgentEntity resolveAssignedAgentFromTicket(TicketEntity ticket, WorkgroupEntity workgroup) {
        if (!hasTicketAssignee(ticket) || workgroup == null || workgroup.getAgents() == null) {
            return null;
        }
        String assigneeUid = ticket.getAssignee().getUid();
        return workgroup.getAgents().stream()
                .filter(agent -> agent != null
                        && agent.getMember() != null
                        && StringUtils.hasText(agent.getMember().getUid())
                        && assigneeUid.equals(agent.getMember().getUid()))
                .findFirst()
                .orElse(null);
    }

    private ThreadEntity applyAgentToThread(ThreadEntity thread, WorkgroupEntity workgroup, AgentEntity agent) {
        UserProtobuf agentProtobuf = agent.toUserProtobuf();
        thread.setAgent(agentProtobuf.toJson());

        try {
            if (agent.getMember() != null && agent.getMember().getUser() != null) {
                thread.setOwner(agent.getMember().getUser());
                thread.setUserUid(agent.getMember().getUser().getUid());
            }
        } catch (Exception e) {
            log.debug("Failed to set thread owner for thread {}: {}", thread.getUid(), e.getMessage());
        }

        try {
            UserProtobuf wg = thread.getWorkgroupProtobuf();
            if (wg == null || !StringUtils.hasText(wg.getUid())) {
                thread.setWorkgroup(ServiceConvertUtils.convertToUserProtobufJSONString(workgroup));
            }
        } catch (Exception e) {
            thread.setWorkgroup(ServiceConvertUtils.convertToUserProtobufJSONString(workgroup));
        }

        return saveThread(thread);
    }

    private boolean hasAssignedAgent(ThreadEntity thread) {
        if (thread == null) {
            return false;
        }
        String agentJson = thread.getAgent();
        if (!StringUtils.hasText(agentJson) || BytedeskConsts.EMPTY_JSON_STRING.equals(agentJson)) {
            return false;
        }
        try {
            UserProtobuf agent = JSON.parseObject(agentJson, UserProtobuf.class);
            return agent != null && StringUtils.hasText(agent.getUid());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 构建工单会话消息响应
     * 
     * @param thread 工单会话实体
     * @return 消息协议对象
     */
    private MessageProtobuf getTicketThreadMessage(ThreadEntity thread) {
        // 查找该会话的最后一条消息
        Optional<MessageEntity> lastMessageOptional = messageRestService.findLatestByThreadUid(thread.getUid());
        
        if (lastMessageOptional.isPresent()) {
            MessageEntity lastMessage = lastMessageOptional.get();
            log.debug("Found last message for ticket thread {}: {}", thread.getUid(), lastMessage.getUid());
            return ServiceConvertUtils.convertToMessageProtobuf(lastMessage, thread);
        }
        
        // 如果没有消息，返回一个空消息但包含会话信息
        log.debug("No messages found for ticket thread {}, returning thread info only", thread.getUid());
        return buildEmptyMessageProtobuf(thread);
    }

    private MessageProtobuf getTicketThreadMessageReadOnly(ThreadEntity thread, String closeTip) {
        Optional<MessageEntity> lastMessageOptional = messageRestService.findLatestByThreadUid(thread.getUid());
        if (lastMessageOptional.isPresent()) {
            return ServiceConvertUtils.convertToMessageProtobuf(lastMessageOptional.get(), thread);
        }
        return buildClosedMessageProtobuf(thread, closeTip);
    }

    /**
     * 构建空消息协议对象（仅包含会话信息）
     * 
     * @param thread 会话实体
     * @return 消息协议对象
     */
    private MessageProtobuf buildEmptyMessageProtobuf(ThreadEntity thread) {
        // 已关闭会话：不返回 CONTINUE（避免前端误认为可继续发送）
        if (ThreadProcessStatusEnum.CLOSED.name().equalsIgnoreCase(thread.getStatus())) {
            return buildClosedMessageProtobuf(thread, null);
        }

        // 对齐 WorkgroupThreadRoutingStrategy：没有历史消息时返回一条系统 CONTINUE 消息。
        // 这样 visitor 端不会拿到一堆 null 字段（uid/type/status/channel/createdAt/extra/user...）。
        UserProtobuf user = null;
        try {
            String agentJson = thread.getAgent();
            if (StringUtils.hasText(agentJson) && !BytedeskConsts.EMPTY_JSON_STRING.equals(agentJson)) {
                user = JSON.parseObject(agentJson, UserProtobuf.class);
            }
        } catch (Exception ignore) {
            // ignore parse errors and fallback below
        }
        if (user == null || !StringUtils.hasText(user.getUid())) {
            user = UserProtobuf.getSystemUser();
        }
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }

        return ThreadMessageUtil.getThreadContinueMessage(user, thread);
    }

    private MessageProtobuf buildClosedMessageProtobuf(ThreadEntity thread, String closeTip) {
        String tip = StringUtils.hasText(closeTip) ? closeTip : I18Consts.I18N_AGENT_CLOSE_TIP;
        if (!StringUtils.hasText(tip)) {
            tip = "本会话已关闭";
        }

        UserProtobuf system = UserProtobuf.getSystemUser();
        if (system.getExtra() == null) {
            system.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }

        MessageExtra extra = MessageExtra.fromOrgUid(thread.getOrgUid());
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
            .content(SystemContent.of(MessageTypeEnum.AGENT_CLOSED, tip).toJson())
                .type(MessageTypeEnum.AGENT_CLOSED.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(system.toJson())
                .orgUid(thread.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(thread)
                .extra(extra.toJson())
                .build();
        return ServiceConvertUtils.convertToMessageProtobuf(message, thread);
    }
}
