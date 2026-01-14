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
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.event.ThreadTransferToAgentEvent;
import com.bytedesk.core.topic.TopicRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.content.WelcomeContent;
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
import com.bytedesk.ticket.ticket_settings.TicketSettingsResponse;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
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

    private final ThreadRestService threadRestService;
    private final MessageRestService messageRestService;
    private final TicketRepository ticketRepository;
    private final WorkgroupRestService workgroupRestService;
    private final WorkgroupRoutingService workgroupRoutingService;
    private final TicketSettingsRestService ticketSettingsRestService;
    private final IMessageSendService messageSendService;
    private final BytedeskEventPublisher bytedeskEventPublisher;
    private final TopicRestService topicRestService;

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
        subscribeThreadTopics(savedThread, topicRestService);

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

        AgentEntity selectedAgent = workgroupRoutingService.selectAgent(workgroup, thread);
        if (selectedAgent == null) {
            log.info("No available agent for ticket {}, workgroup {}", ticket.getUid(), workgroupUid);
            return thread;
        }

        // 读取最新线程再写入，避免并发覆盖
        ThreadEntity latestThread = getThreadByUid(thread.getUid());
        if (hasAssignedAgent(latestThread)) {
            return latestThread;
        }

        UserProtobuf agentProtobuf = selectedAgent.toUserProtobuf();
        if (agentProtobuf == null || !StringUtils.hasText(agentProtobuf.getUid())) {
            log.warn("Selected agent has no uid for ticket {}, skip assignment", ticket.getUid());
            return latestThread;
        }

        // 按 workgroup 路由策略写入 agent，并同步设置 userUid
        latestThread.setAgent(agentProtobuf.toJson());
        // owner: best-effort（避免 agent.member 缺失导致抛错）
        try {
            if (selectedAgent.getMember() != null && selectedAgent.getMember().getUser() != null) {
                latestThread.setOwner(selectedAgent.getMember().getUser());
                latestThread.setUserUid(selectedAgent.getMember().getUser().getUid());
            }
        } catch (Exception e) {
            log.debug("Failed to set thread owner for ticket {}: {}", ticket.getUid(), e.getMessage());
        }

        // workgroup 信息补写
        try {
            UserProtobuf wg = latestThread.getWorkgroupProtobuf();
            if (wg == null || !StringUtils.hasText(wg.getUid())) {
                latestThread.setWorkgroup(ServiceConvertUtils.convertToUserProtobufJSONString(workgroup));
            }
        } catch (Exception e) {
            latestThread.setWorkgroup(ServiceConvertUtils.convertToUserProtobufJSONString(workgroup));
        }

        return saveThread(latestThread);
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
                .content(tip)
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
