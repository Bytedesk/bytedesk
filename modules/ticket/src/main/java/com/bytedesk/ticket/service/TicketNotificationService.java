/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 13:48:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 13:49:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.email_provider.EmailProviderEntity;
import com.bytedesk.core.email_provider.EmailProviderRepository;
import com.bytedesk.core.email_push.EmailPushSendService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.sms_push.SmsPushSendService;
import com.bytedesk.core.sms_template.SmsTemplateEntity;
import com.bytedesk.core.sms_template.SmsTemplateRepository;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.notification.NotificationRequest;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.notification.NotificationTypeEnum;
import com.bytedesk.core.push.service.AgentPushGateService;
import com.bytedesk.core.push.service.PushMobileService;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorRepository;
import com.bytedesk.service.visitor.VisitorStatusEnum;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsEntity;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityService;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TicketNotificationService {

    public static final String EVENT_TYPE_TICKET_CREATED = "TICKET_CREATED";

    public static final String EVENT_TYPE_TICKET_STATUS_CHANGED = "TICKET_STATUS_CHANGED";

    public static final String EVENT_TYPE_TICKET_TRANSFERRED = "TICKET_TRANSFERRED";

    public static final String EVENT_TYPE_TICKET_AGENT_MESSAGE = "TICKET_AGENT_MESSAGE";

    public static final String EVENT_TYPE_TICKET_SLA_WARNING = "TICKET_SLA_WARNING";

    public static final String EVENT_TYPE_TICKET_SLA_BREACH = "TICKET_SLA_BREACH";

    /**
     * 外部工单状态变更时允许向访客会话投递系统消息的状态白名单。
     * 仅 RESOLVED 附带验证按钮，其余状态为纯文案回执（形成验证闭环）。
     */
    private static final Set<String> VISITOR_NOTIFIABLE_STATUSES = Set.of(
            TicketStatusEnum.RESOLVED.name(),
            TicketStatusEnum.REOPENED.name(),
            TicketStatusEnum.VERIFIED_OK.name(),
            TicketStatusEnum.CLOSED.name());

    private final NotificationService notificationService;

    private final MemberRestService memberRestService;

    private final WorkgroupRepository workgroupRepository;

    private final VisitorRepository visitorRepository;

    private final EmailProviderRepository emailProviderRepository;

    private final SmsPushSendService smsPushSendService;

    private final SmsTemplateRepository smsTemplateRepository;

    private final ObjectProvider<PushMobileService> mobilePushProvider;

    private final AgentPushGateService agentPushGateService;

    private final EmailPushSendService emailPushSendService;

    private final TicketSettingsRepository ticketSettingsRepository;

    private final TicketRepository ticketRepository;

    private final ThreadRestService threadRestService;

    private final MessageRestService messageRestService;

    private final IMessageSendService messageSendService;

    private final TicketVisibilityService ticketVisibilityService;

    public void notifyNewTicket(TicketEntity ticket) {
        notifyTicketStatusChanged(ticket, null, ticket != null ? ticket.getStatus() : null);
    }

    public void notifyTicketAssigned(TicketEntity ticket) {
        notifyTicketStatusChanged(ticket, null, ticket != null ? ticket.getStatus() : null);
    }

    public void notifyTicketTransferred(TicketEntity ticket) {
        if (ticket == null || ticket.getAssignee() == null || !StringUtils.hasText(ticket.getAssignee().getUid())) {
            return;
        }

        String eventType = EVENT_TYPE_TICKET_TRANSFERRED;
        String title = buildTitle(ticket, eventType);
        String content = buildContent(ticket, null, ticket.getStatus(), eventType);
        String extra = buildExtra(ticket, null, ticket.getStatus(), eventType);
        String assigneeUid = resolveNotificationUserUid(ticket.getAssignee(), ticket, "transferred-assignee");
        if (!StringUtils.hasText(assigneeUid)) {
            return;
        }

        try {
            NotificationRequest request = NotificationRequest.builder()
                    .title(title)
                    .content(content)
                    .type(NotificationTypeEnum.TICKET.name())
                    .level(LevelEnum.USER.name())
                    .orgUid(ticket.getOrgUid())
                    .userUid(assigneeUid)
                    .extra(extra)
                    .build();
            notificationService.dispatchSystemNotificationToUser(request);
        } catch (Exception ex) {
            log.warn("dispatch transferred ticket notification failed: ticketUid={}, recipientUid={}, error={}",
                    ticket.getUid(), assigneeUid, ex.getMessage());
        }

        pushToAgentDevices(ticket, eventType, ticket.getReporter() != null ? ticket.getReporter().getUid() : null,
                Set.of(assigneeUid));
    }

    // public void notifyTicketComment(TicketCommentEntity comment) {
    // // 工单评论通知
    // }

    public void notifySLABreach(TicketEntity ticket) {
        // SLA违规通知
    }

    public void notifyTicketClosed(TicketEntity ticket) {
        notifyTicketStatusChanged(ticket, null, ticket != null ? ticket.getStatus() : null);
    }

    public void notifyTicketStatusChanged(TicketEntity ticket, String previousStatus, String currentStatus) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid()) || !StringUtils.hasText(currentStatus)) {
            log.info("[NOTICE-DIAG] notifyTicketStatusChanged SKIPPED: null/empty params");
            return;
        }

        String eventType = StringUtils.hasText(previousStatus) ? EVENT_TYPE_TICKET_STATUS_CHANGED
                : EVENT_TYPE_TICKET_CREATED;
        log.info("[NOTICE-DIAG] notifyTicketStatusChanged: ticketUid={} eventType={} previous={} current={}",
                ticket.getUid(), eventType, previousStatus, currentStatus);
        String title = buildTitle(ticket, eventType);
        String content = buildContent(ticket, previousStatus, currentStatus, eventType);
        String extra = buildExtra(ticket, previousStatus, currentStatus, eventType);

        // 外部工单白名单状态：由增强版投递接管（带验证按钮 payload），避免同会话重复两条系统消息
        if (!emitVisitorTicketStatusMessage(ticket, previousStatus, currentStatus, eventType, content)) {
            emitTicketThreadStatusMessage(ticket, previousStatus, currentStatus, eventType, content);
        }

        // 报告人判定与 APNs 排除统一使用解析后的最终 userUid（member uid 需归一，避免误投/漏推）
        RecipientSnapshot snapshot = resolveRecipientSnapshot(ticket,
                EVENT_TYPE_TICKET_CREATED.equals(eventType));
        Set<String> recipients = snapshot.recipients();
        String reporterUid = snapshot.reporterUid();
        log.info("[NOTICE-DIAG] notifyTicketStatusChanged recipients: size={} uids={}",
                recipients.size(), recipients);
        if (!recipients.isEmpty()) {
            dispatchTicketNotifications(ticket, eventType, title, content, extra, reporterUid, recipients);
            // Push to agent iOS devices via APNs
            pushToAgentDevices(ticket, eventType, reporterUid, recipients);
        }

        // 独立联系渠道（邮件/短信）与客服站内投递解耦：客服名单为空不短路报告人既有通知
        notifyExternalChannels(ticket, previousStatus, currentStatus, eventType, title, content, extra);
    }

    private void dispatchTicketNotifications(TicketEntity ticket, String eventType, String title, String content,
            String extra, String reporterUid, Set<String> recipients) {
        for (String recipientUid : recipients) {
            try {
                boolean isReporter = reporterUid != null && StringUtils.hasText(reporterUid)
                        && reporterUid.equals(recipientUid);
                String recipientTitle = isReporter ? buildReporterTitle(ticket, eventType) : title;
                String recipientContent = isReporter ? buildReporterContent(ticket, eventType) : content;
                NotificationRequest request = NotificationRequest.builder()
                        .title(recipientTitle)
                        .content(recipientContent)
                        .type(NotificationTypeEnum.TICKET.name())
                        .level(LevelEnum.USER.name())
                        .orgUid(ticket.getOrgUid())
                        .userUid(recipientUid)
                        .extra(extra)
                        .build();
                notificationService.dispatchSystemNotificationToUser(request);
            } catch (Exception ex) {
                log.warn("dispatch ticket notification failed: ticketUid={}, recipientUid={}, error={}",
                        ticket.getUid(), recipientUid, ex.getMessage());
            }
        }
    }

    private void emitTicketThreadStatusMessage(TicketEntity ticket, String previousStatus, String currentStatus,
            String eventType, String content) {
        if (!EVENT_TYPE_TICKET_STATUS_CHANGED.equals(eventType)
                || !StringUtils.hasText(content)
                || Objects.equals(previousStatus, currentStatus)
                || !StringUtils.hasText(ticket.getThreadUid())) {
            return;
        }
        try {
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(ticket.getThreadUid());
            if (threadOptional.isEmpty()) {
                log.debug("ticket thread system message skipped: thread not found, ticketUid={}, threadUid={}",
                        ticket.getUid(), ticket.getThreadUid());
                return;
            }
            ThreadEntity thread = threadOptional.get();
            MessageEntity message = ThreadMessageUtil.getThreadSystemMessage(content, thread);
            messageRestService.save(message);
            MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
            messageSendService.sendProtobufMessage(messageProtobuf);
        } catch (Exception ex) {
            log.warn("ticket thread system message failed: ticketUid={}, threadUid={}, error={}",
                    ticket.getUid(), ticket.getThreadUid(), ex.getMessage());
        }
    }

    /**
     * 外部工单状态变更时，向访客可感知的会话投递系统消息（增强版，携带验证按钮 payload）：
     * - 工单自有会话(threadUid)：投递增强版，供访客在工单会话内直接验证（替代原纯文案版）；
     * - 访客原会话(visitorThreadUid)：与工单会话不同时补发，访客在聊天界面即可看到状态更新并验证。
     *
     * @return true 表示本方法已接管投递，调用方无需再调用 {@link #emitTicketThreadStatusMessage}
     */
    private boolean emitVisitorTicketStatusMessage(TicketEntity ticket, String previousStatus, String currentStatus,
            String eventType, String content) {
        if (!EVENT_TYPE_TICKET_STATUS_CHANGED.equals(eventType)
                || !TicketTypeEnum.EXTERNAL.name().equals(ticket.getType())
                || !VISITOR_NOTIFIABLE_STATUSES.contains(currentStatus)
                || Objects.equals(previousStatus, currentStatus)
                || !StringUtils.hasText(content)) {
            return false;
        }
        String reporterUid = ticket.getReporter() != null ? ticket.getReporter().getUid() : null;
        String systemExtra = buildTicketVerifyExtra(ticket, previousStatus, currentStatus, reporterUid);
        if (StringUtils.hasText(ticket.getThreadUid())) {
            sendTicketSystemMessage(ticket, ticket.getThreadUid(), content, systemExtra);
        }
        if (StringUtils.hasText(ticket.getVisitorThreadUid())
                && !Objects.equals(ticket.getVisitorThreadUid(), ticket.getThreadUid())) {
            sendTicketSystemMessage(ticket, ticket.getVisitorThreadUid(), content, systemExtra);
        }
        return true;
    }

    private void sendTicketSystemMessage(TicketEntity ticket, String threadUid, String content, String systemExtra) {
        try {
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadUid);
            if (threadOptional.isEmpty()) {
                log.debug("ticket visitor system message skipped: thread not found, ticketUid={}, threadUid={}",
                        ticket.getUid(), threadUid);
                return;
            }
            ThreadEntity thread = threadOptional.get();
            MessageEntity message = ThreadMessageUtil.getThreadSystemMessage(content, systemExtra, thread);
            messageRestService.save(message);
            MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
            messageSendService.sendProtobufMessage(messageProtobuf);
        } catch (Exception ex) {
            log.warn("ticket visitor system message failed: ticketUid={}, threadUid={}, error={}",
                    ticket.getUid(), threadUid, ex.getMessage());
        }
    }

    /**
     * 构造 SystemContent.extra：ticketStatusNotice 供前端 i18n 组装正文（P1），
     * ticketVerify 驱动消息内验证按钮（仅 RESOLVED 且报告人存在时 show=true）
     */
    private String buildTicketVerifyExtra(TicketEntity ticket, String previousStatus, String currentStatus,
            String reporterUid) {
        JSONObject ticketStatusNotice = new JSONObject();
        ticketStatusNotice.put("ticketUid", ticket.getUid());
        ticketStatusNotice.put("ticketNumber", ticket.getTicketNumber());
        ticketStatusNotice.put("orgUid", ticket.getOrgUid());
        ticketStatusNotice.put("previousStatus", previousStatus);
        ticketStatusNotice.put("currentStatus", currentStatus);
        ticketStatusNotice.put("title", ticket.getTitle());

        boolean showVerify = TicketStatusEnum.RESOLVED.name().equals(currentStatus)
                && StringUtils.hasText(reporterUid);
        JSONObject ticketVerify = new JSONObject();
        ticketVerify.put("show", showVerify);
        ticketVerify.put("ticketUid", ticket.getUid());
        ticketVerify.put("ticketNumber", ticket.getTicketNumber());
        ticketVerify.put("orgUid", ticket.getOrgUid());
        ticketVerify.put("operatorUid", reporterUid);
        ticketVerify.put("submitted", false);

        JSONObject extra = new JSONObject();
        extra.put("ticketStatusNotice", ticketStatusNotice);
        extra.put("ticketVerify", ticketVerify);
        return JSON.toJSONString(extra);
    }

    /**
     * 通知管理员
     */
    public void notifyManager(String assignee, String message) {
        // TODO: 实现实际的通知逻辑，如发送邮件、短信等
        log.info("通知管理员 - 处理人: {}, 消息: {}", assignee, message);
    }

    /**
     * 通知技术团队
     */
    public void notifyTechnicalTeam(String caseId, String message) {
        log.info("通知技术团队 - 案例ID: {}, 消息: {}", caseId, message);
    }

    /**
     * 通知客户
     */
    public void notifyCustomer(String reporter, String message) {
        log.info("通知客户 - 报告人: {}, 消息: {}", reporter, message);
    }

    /**
     * 发送 SLA 违规通知
     */
    public void sendSLABreachNotification(String ticketId, String type, String details) {
        notifySla(ticketId, type, details, true);
    }

    /**
     * 发送 SLA 预警通知
     */
    public void sendSLAWarningNotification(String ticketId, String type, String details) {
        notifySla(ticketId, type, details, false);
    }

    private void notifySla(String ticketUid, String slaType, String details, boolean breached) {
        if (!StringUtils.hasText(ticketUid)) {
            return;
        }
        Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(ticketUid);
        if (ticketOptional.isEmpty()) {
            log.warn("SLA notification skipped: ticket not found, ticketUid={}, slaType={}, breached={}",
                    ticketUid, slaType, breached);
            return;
        }

        TicketEntity ticket = ticketOptional.get();
        String eventType = breached ? EVENT_TYPE_TICKET_SLA_BREACH : EVENT_TYPE_TICKET_SLA_WARNING;
        String title = buildSlaTitle(ticket, slaType, breached);
        String content = buildSlaContent(ticket, slaType, details, breached);
        String extra = buildSlaExtra(ticket, slaType, details, eventType);

        emitSlaThreadSystemMessage(ticket, content);
        // SLA 站内与 APNs 复用同一收件人快照（单次解析，避免两轮结果不一致）
        RecipientSnapshot slaSnapshot = resolveRecipientSnapshot(ticket, true);
        dispatchSlaNotifications(ticket, eventType, title, content, extra, slaSnapshot.recipients());
        pushToAgentDevices(ticket, eventType, slaSnapshot.reporterUid(), slaSnapshot.recipients());
        notifyExternalChannels(ticket, null, breached ? "BREACHED" : "WARNED", eventType, title, content, extra);

        log.info("SLA notification sent: ticketUid={}, slaType={}, eventType={}, details={}",
                ticketUid, slaType, eventType, details);
    }

    private void dispatchSlaNotifications(TicketEntity ticket, String eventType, String title, String content,
            String extra, Set<String> recipients) {
        if (recipients.isEmpty()) {
            return;
        }
        for (String recipientUid : recipients) {
            try {
                NotificationRequest request = NotificationRequest.builder()
                        .title(title)
                        .content(content)
                        .type(NotificationTypeEnum.TICKET.name())
                        .level(LevelEnum.USER.name())
                        .orgUid(ticket.getOrgUid())
                        .userUid(recipientUid)
                        .extra(extra)
                        .build();
                notificationService.dispatchSystemNotificationToUser(request);
            } catch (Exception ex) {
                log.warn(
                        "dispatch SLA ticket notification failed: ticketUid={}, eventType={}, recipientUid={}, error={}",
                        ticket.getUid(), eventType, recipientUid, ex.getMessage());
            }
        }
    }

    private void emitSlaThreadSystemMessage(TicketEntity ticket, String content) {
        if (ticket == null || !StringUtils.hasText(ticket.getThreadUid())) {
            return;
        }
        try {
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(ticket.getThreadUid());
            if (threadOptional.isEmpty()) {
                log.debug("SLA thread system message skipped: thread not found, ticketUid={}, threadUid={}",
                        ticket.getUid(), ticket.getThreadUid());
                return;
            }
            ThreadEntity thread = threadOptional.get();
            MessageEntity message = ThreadMessageUtil.getThreadSystemMessage(content, thread);
            messageRestService.save(message);
            MessageProtobuf messageProtobuf = ServiceConvertUtils.convertToMessageProtobuf(message, thread);
            messageSendService.sendProtobufMessage(messageProtobuf);
        } catch (Exception ex) {
            log.warn("SLA thread system message failed: ticketUid={}, threadUid={}, error={}",
                    ticket.getUid(), ticket.getThreadUid(), ex.getMessage());
        }
    }

    private String buildSlaTitle(TicketEntity ticket, String slaType, boolean breached) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        return (breached ? "SLA已超时：" : "SLA即将超时：") + ticketNumber + " " + toSlaTypeLabel(slaType);
    }

    private String buildSlaContent(TicketEntity ticket, String slaType, String details, boolean breached) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        StringBuilder builder = new StringBuilder();
        builder.append("工单 ").append(ticketNumber).append(" 的 ").append(toSlaTypeLabel(slaType));
        builder.append(breached ? " SLA 已超时" : " SLA 即将超时");
        if (StringUtils.hasText(details)) {
            builder.append("，").append(details);
        }
        return builder.toString();
    }

    private String buildSlaExtra(TicketEntity ticket, String slaType, String details, String eventType) {
        JSONObject extra = new JSONObject();
        extra.put("eventType", eventType);
        extra.put("ticketUid", ticket.getUid());
        extra.put("uid", ticket.getUid());
        extra.put("ticketNumber", ticket.getTicketNumber());
        extra.put("title", ticket.getTitle());
        extra.put("slaType", slaType);
        extra.put("details", details);
        extra.put("priority", ticket.getPriority());
        extra.put("type", ticket.getType());
        extra.put("workgroupUid", ticket.getWorkgroupUid());
        UserProtobuf reporter = ticket.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            extra.put("reporterUid", reporter.getUid());
        }
        UserProtobuf assignee = ticket.getAssignee();
        if (assignee != null && StringUtils.hasText(assignee.getUid())) {
            extra.put("assigneeUid", assignee.getUid());
        }
        return JSON.toJSONString(extra);
    }

    // ============ 工单客服回复消息通知 ============

    /**
     * 工单会话中客服发送消息时，通过邮件/短信通知访客。
     * 由 TicketEventListener.onMessageJsonEvent 调用。
     *
     * @param ticket    工单实体
     * @param agentName 回复客服昵称
     */
    public void notifyVisitorOfAgentMessage(TicketEntity ticket, String agentName) {
        if (ticket == null) {
            return;
        }
        sendTicketAgentMessageEmailNotification(ticket, agentName);
        sendTicketAgentMessageSmsNotification(ticket, agentName);
    }

    private void sendTicketAgentMessageEmailNotification(TicketEntity ticket, String agentName) {
        TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
        if (!isEmailEnabled(notifSettings)) {
            log.debug("ticket agent message email skipped: email disabled, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isEventInList(notifSettings.getEmailEvents(), EVENT_TYPE_TICKET_AGENT_MESSAGE)) {
            log.debug("ticket agent message email skipped: event not in emailEvents, ticketUid={}", ticket.getUid());
            return;
        }
        String recipientEmail = resolveReporterEmail(ticket);
        if (!StringUtils.hasText(recipientEmail)) {
            log.debug("ticket agent message email skipped: no reporter email, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isReporterOffline(ticket)) {
            boolean notifyWhenOnline = notifSettings != null
                    && Boolean.TRUE.equals(notifSettings.getEmailNotifyWhenOnline());
            if (!notifyWhenOnline) {
                log.debug("ticket agent message email skipped: reporter is online, ticketUid={}", ticket.getUid());
                return;
            }
        }
        var emailConfigOpt = resolveEmailProvider(ticket);
        if (emailConfigOpt.isEmpty()) {
            log.debug("ticket agent message email skipped: no email provider config, orgUid={}", ticket.getOrgUid());
            return;
        }
        emailPushSendService.sendTicketMessageEmail(
                emailConfigOpt.get(),
                recipientEmail,
                StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid(),
                ticket.getTitle(),
                ticket.getContactName(),
                agentName,
                ticket.getUid(),
                ticket.getWorkgroupUid(),
                ticket.getOrgUid());
    }

    private void sendTicketAgentMessageSmsNotification(TicketEntity ticket, String agentName) {
        // 复用现有的短信通知流程，使用 "replied" 事件类型
        sendTicketSmsNotification(ticket, null, null, EVENT_TYPE_TICKET_AGENT_MESSAGE,
                "工单新回复", null, null);
    }

    /**
     * 工单通知收件人快照：一次解析得到的去重最终 userUid 集合，站内写入与 APNs 共用。
     */
    private record RecipientSnapshot(Set<String> recipients, String reporterUid, String assigneeUid) {
    }

    private Set<String> resolveRecipientUids(TicketEntity ticket, boolean includeWorkgroupUsers) {
        return resolveRecipientSnapshot(ticket, includeWorkgroupUsers).recipients();
    }

    /**
     * 单次解析本轮全部收件人（报告人/处理人豁免 + 工作组候选部门过滤），供站内与 APNs 复用同一快照。
     */
    private RecipientSnapshot resolveRecipientSnapshot(TicketEntity ticket, boolean includeWorkgroupUsers) {
        Set<String> recipients = new LinkedHashSet<>();
        if (ticket == null) {
            return new RecipientSnapshot(recipients, null, null);
        }

        // 1. 报告人/处理人豁免：始终保留（与详情可见性豁免一致）
        UserProtobuf reporter = ticket.getReporter();
        String reporterUid = resolveNotificationUserUid(reporter, ticket, "reporter");
        if (StringUtils.hasText(reporterUid)) {
            recipients.add(reporterUid);
        }

        UserProtobuf assignee = ticket.getAssignee();
        String assigneeUid = resolveNotificationUserUid(assignee, ticket, "assignee");
        if (StringUtils.hasText(assigneeUid)) {
            recipients.add(assigneeUid);
        }

        // 2. 工作组候选：按已发布可见性设置过滤普通坐席（仅可见者收到通知）
        if (includeWorkgroupUsers && StringUtils.hasText(ticket.getWorkgroupUid())) {
            TicketVisibilitySettingsData visibility;
            try {
                visibility = ticketVisibilityService.resolve(ticket);
            } catch (Exception ex) {
                // 决策 D4/5.4：解析失败不得伪装成 ORG_WIDE 公开广播；本轮放弃工作组候选并记录
                log.warn("ticket notification visibility resolve failed, skip workgroup candidates: "
                        + "ticketUid={}, orgUid={}, workgroupUid={}, error={}",
                        ticket.getUid(), ticket.getOrgUid(), ticket.getWorkgroupUid(), ex.getMessage());
                return new RecipientSnapshot(recipients, reporterUid, assigneeUid);
            }
            TicketVisibilitySettingsData visibilitySnapshot = visibility;
            workgroupRepository.findByUid(ticket.getWorkgroupUid())
                    .filter(workgroup -> !workgroup.isDeleted()
                            && ticket.getOrgUid() != null && ticket.getOrgUid().equals(workgroup.getOrgUid()))
                    .ifPresent(workgroup -> workgroup.getAgents().stream()
                            .filter(agent -> agent != null && !agent.isDeleted())
                            .map(agent -> agent.getMember())
                            .filter(member -> member != null && !member.isDeleted()
                                    && ticket.getOrgUid() != null && ticket.getOrgUid().equals(member.getOrgUid()))
                            .filter(member -> visibilitySnapshot.canViewByDepartment(
                                    ticket.getCategoryUid(), ticket.getDepartmentUid(), member.getDeptUid()))
                            .map(member -> member.getUser())
                            .filter(user -> user != null && !user.isDeleted()
                                    && StringUtils.hasText(user.getUid()))
                            .map(user -> user.getUid())
                            .forEach(recipients::add));
        }

        return new RecipientSnapshot(recipients, reporterUid, assigneeUid);
    }

    private String resolveNotificationUserUid(UserProtobuf user) {
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return null;
        }

        if (UserTypeEnum.MEMBER.name().equals(user.getType())) {
            return memberRestService.findByUid(user.getUid())
                    .map(member -> member.getUser())
                    .filter(Objects::nonNull)
                    .map(userEntity -> userEntity.getUid())
                    .filter(StringUtils::hasText)
                    .orElse(user.getUid());
        }

        return user.getUid();
    }

    /**
     * 带上下文的身份解析：MEMBER 解析失败（无有效成员/关联用户）时不再回退 member uid 冒充投递，
     * 显式记录并跳过（决策 D5）。VISITOR/USER 身份沿用原 uid 语义。
     */
    private String resolveNotificationUserUid(UserProtobuf user, TicketEntity ticket, String role) {
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return null;
        }
        if (!UserTypeEnum.MEMBER.name().equals(user.getType())) {
            return user.getUid();
        }
        Optional<MemberEntity> memberOptional;
        try {
            memberOptional = memberRestService.findByUid(user.getUid());
        } catch (Exception ex) {
            log.warn("ticket notification member resolve failed, skip recipient: ticketUid={}, role={}, "
                    + "memberUid={}, error={}", ticket.getUid(), role, user.getUid(), ex.getMessage());
            return null;
        }
        if (memberOptional.isEmpty()) {
            log.warn("ticket notification member not found, skip recipient: ticketUid={}, role={}, memberUid={}",
                    ticket.getUid(), role, user.getUid());
            return null;
        }
        MemberEntity member = memberOptional.get();
        if (member.isDeleted()) {
            log.warn("ticket notification member deleted, skip recipient: ticketUid={}, role={}, memberUid={}",
                    ticket.getUid(), role, user.getUid());
            return null;
        }
        if (member.getUser() == null || !StringUtils.hasText(member.getUser().getUid())) {
            log.warn("ticket notification member has no valid user, skip recipient: ticketUid={}, role={}, "
                    + "memberUid={}", ticket.getUid(), role, user.getUid());
            return null;
        }
        return member.getUser().getUid();
    }

    /**
     * Push ticket notification to agent iOS devices via APNs.
     * Only pushes to agents (assignee + workgroup members), not to the
     * reporter/visitor.
     * Android push is reserved as a TODO placeholder for future implementation.
     */
    private void pushToAgentDevices(TicketEntity ticket, String eventType, String reporterUid, Set<String> recipients) {
        TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
        if (notifSettings == null || !Boolean.TRUE.equals(notifSettings.getApnsEnabled())) {
            return;
        }
        String agentPushTitle = buildAgentPushTitle(ticket, eventType);
        String agentPushBody = buildAgentPushBody(ticket, eventType);

        for (String recipientUid : recipients) {
            // Skip reporter/visitor - only push to agents
            if (StringUtils.hasText(reporterUid) && reporterUid.equals(recipientUid)) {
                continue;
            }
            try {
                // 离线才推（D7）：在线坐席已通过站内通知感知，推送只会双提醒；工单为强通知不节流
                if (agentPushGateService.isUserOnline(recipientUid)) {
                    log.debug("Skip ticket mobile push because recipient online, ticketUid={}, recipientUid={}",
                            ticket.getUid(), recipientUid);
                    continue;
                }
                PushMobileService mobilePush = mobilePushProvider.getIfAvailable();
                if (mobilePush == null) {
                    log.debug("Skip ticket mobile push: no AgentMobilePushService bean (community edition)");
                    return;
                }
                java.util.Map<String, String> extras = new java.util.HashMap<>();
                extras.put("type", "ticket");
                extras.put("ticketUid", ticket.getUid());
                extras.put("orgUid", ticket.getOrgUid());
                mobilePush.pushNotificationToUser(recipientUid, agentPushTitle, agentPushBody, ticket.getUid(),
                        "ticket", extras);
            } catch (Exception ex) {
                log.warn("APNs ticket push failed: ticketUid={}, agentUid={}, error={}",
                        ticket.getUid(), recipientUid, ex.getMessage());
            }
            // TODO: Android push - implement when Android push service is available
            // androidPushService.pushToUser(recipientUid, agentPushTitle, agentPushBody,
            // ticket.getUid());
        }
    }

    /**
     * Build push title for agent devices (shorter than the in-app notification
     * title).
     */
    private String buildAgentPushTitle(TicketEntity ticket, String eventType) {
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "新工单";
        }
        if (EVENT_TYPE_TICKET_TRANSFERRED.equals(eventType)) {
            return "工单转派";
        }
        if (EVENT_TYPE_TICKET_SLA_WARNING.equals(eventType)) {
            return "工单SLA即将超时";
        }
        if (EVENT_TYPE_TICKET_SLA_BREACH.equals(eventType)) {
            return "工单SLA已超时";
        }
        return "工单状态更新";
    }

    /**
     * Build push body for agent devices.
     */
    private String buildAgentPushBody(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        String summary = StringUtils.hasText(ticket.getTitle()) ? ticket.getTitle() : "";
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return StringUtils.hasText(summary)
                    ? ticketNumber + " " + summary
                    : "收到新工单 " + ticketNumber;
        }
        if (EVENT_TYPE_TICKET_TRANSFERRED.equals(eventType)) {
            return StringUtils.hasText(summary)
                ? "工单 " + ticketNumber + " 已转派给您。内容：" + summary
                : "工单 " + ticketNumber + " 已转派给您";
        }
        if (EVENT_TYPE_TICKET_SLA_WARNING.equals(eventType)) {
            return StringUtils.hasText(summary)
                    ? ticketNumber + " 即将超时 " + summary
                    : "工单 " + ticketNumber + " SLA 即将超时";
        }
        if (EVENT_TYPE_TICKET_SLA_BREACH.equals(eventType)) {
            return StringUtils.hasText(summary)
                    ? ticketNumber + " 已超时 " + summary
                    : "工单 " + ticketNumber + " SLA 已超时";
        }
        String statusLabel = toStatusLabel(ticket.getStatus());
        return StringUtils.hasText(summary)
                ? ticketNumber + " " + statusLabel + " " + summary
                : "工单 " + ticketNumber + " 状态：" + statusLabel;
    }

    private String buildTitle(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "新工单：" + ticketNumber;
        }
        if (EVENT_TYPE_TICKET_TRANSFERRED.equals(eventType)) {
            return "工单转派：" + ticketNumber;
        }
        return "工单状态更新：" + ticketNumber;
    }

    private String buildReporterTitle(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "工单创建成功";
        }
        return "工单状态更新：" + ticketNumber;
    }

    private String buildContent(TicketEntity ticket, String previousStatus, String currentStatus, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        String summary = StringUtils.hasText(ticket.getTitle()) ? ticket.getTitle() : ticket.getDescription();
        StringBuilder builder = new StringBuilder();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            builder.append("收到新工单 ").append(ticketNumber);
        } else if (EVENT_TYPE_TICKET_TRANSFERRED.equals(eventType)) {
            builder.append("工单 ").append(ticketNumber).append(" 已转派给您");
        } else {
            builder.append("工单 ").append(ticketNumber).append(" 状态更新为 ").append(toStatusLabel(currentStatus));
        }
        if (StringUtils.hasText(previousStatus)) {
            builder.append("，原状态为 ").append(toStatusLabel(previousStatus));
        }
        if (StringUtils.hasText(summary)) {
            builder.append("。内容：").append(summary);
        }
        return builder.toString();
    }

    private String buildReporterContent(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber()
                : ticket.getUid();
        StringBuilder builder = new StringBuilder();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            builder.append("您的工单 ").append(ticketNumber).append(" 已创建成功，请耐心等待客服处理");
        } else {
            builder.append("工单 ").append(ticketNumber).append(" 状态已更新");
        }
        return builder.toString();
    }

    private String buildExtra(TicketEntity ticket, String previousStatus, String currentStatus, String eventType) {
        JSONObject extra = new JSONObject();
        extra.put("eventType", eventType);
        extra.put("ticketUid", ticket.getUid());
        extra.put("uid", ticket.getUid());
        extra.put("title", ticket.getTitle());
        extra.put("ticketNumber", ticket.getTicketNumber());
        extra.put("ticketStatus", currentStatus);
        extra.put("status", currentStatus);
        extra.put("previousStatus", previousStatus);
        extra.put("priority", ticket.getPriority());
        extra.put("type", ticket.getType());
        extra.put("workgroupUid", ticket.getWorkgroupUid());
        UserProtobuf reporter = ticket.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            extra.put("reporterUid", reporter.getUid());
        }
        UserProtobuf assignee = ticket.getAssignee();
        if (assignee != null && StringUtils.hasText(assignee.getUid())) {
            extra.put("assigneeUid", assignee.getUid());
        }
        return JSON.toJSONString(extra);
    }

    private void notifyExternalChannels(TicketEntity ticket, String previousStatus, String currentStatus,
            String eventType, String title, String content, String extra) {
        sendTicketEmailNotification(ticket, previousStatus, currentStatus, eventType, title, content, extra);
        sendTicketSmsNotification(ticket, previousStatus, currentStatus, eventType, title, content, extra);
    }

    public void sendTicketEmailNotification(TicketEntity ticket, String previousStatus, String currentStatus,
            String eventType, String title, String content, String extra) {
        if (ticket == null) {
            return;
        }
        TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
        if (!isEmailEnabled(notifSettings)) {
            log.debug("ticket email notification skipped: email disabled, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isEventInList(notifSettings.getEmailEvents(), resolveEventKey(eventType, currentStatus, ticket))) {
            log.debug("ticket email notification skipped: event not in emailEvents, ticketUid={}, eventType={}",
                    ticket.getUid(), eventType);
            return;
        }
        String recipientEmail = resolveReporterEmail(ticket);
        if (!StringUtils.hasText(recipientEmail)) {
            log.debug("ticket email notification skipped: no reporter email found, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isReporterOffline(ticket)) {
            boolean notifyWhenOnline = notifSettings != null
                    && Boolean.TRUE.equals(notifSettings.getEmailNotifyWhenOnline());
            if (!notifyWhenOnline) {
                log.debug("ticket email notification skipped: reporter is online, ticketUid={}", ticket.getUid());
                return;
            }
        }
        var emailConfigOpt = resolveEmailProvider(ticket);
        if (emailConfigOpt.isEmpty()) {
            log.debug("ticket email notification skipped: no email provider config, orgUid={}", ticket.getOrgUid());
            return;
        }
        emailPushSendService.sendTicketEmail(
                emailConfigOpt.get(),
                recipientEmail,
                StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid(),
                ticket.getTitle(),
                ticket.getContactName(),
                eventType,
                toStatusLabel(currentStatus),
                StringUtils.hasText(previousStatus) ? toStatusLabel(previousStatus) : null,
                ticket.getUid(),
                ticket.getWorkgroupUid(),
                ticket.getOrgUid());
    }

    /**
     * 获取访客/报告人的邮箱地址
     */
    private String resolveReporterEmail(TicketEntity ticket) {
        // 1. 优先取 ticket.email 字段
        if (StringUtils.hasText(ticket.getEmail())) {
            return ticket.getEmail().trim();
        }
        // 2. 从 reporter UserProtobuf 的 uid 查找 VisitorEntity 的 email
        UserProtobuf reporter = ticket.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            Optional<VisitorEntity> visitorOpt = visitorRepository.findByUidAndDeleted(reporter.getUid(), false);
            if (visitorOpt.isPresent() && StringUtils.hasText(visitorOpt.get().getEmail())) {
                return visitorOpt.get().getEmail().trim();
            }
        }
        return null;
    }

    /**
     * 检查报告人（访客）是否处于离线状态
     */
    private boolean isReporterOffline(TicketEntity ticket) {
        UserProtobuf reporter = ticket.getReporter();
        if (reporter == null || !StringUtils.hasText(reporter.getUid())) {
            // 无 reporter 信息，默认视为离线以通过邮件通知
            return true;
        }
        Optional<VisitorEntity> visitorOpt = visitorRepository.findByUidAndDeleted(reporter.getUid(), false);
        return visitorOpt.map(v -> VisitorStatusEnum.OFFLINE.name().equals(v.getStatus())).orElse(true);
    }

    /**
     * 解析邮件供应商：优先使用通知设置中指定的供应商，否则自动匹配 org 下首个同类型启用供应商。
     */
    private Optional<EmailProviderEntity> resolveEmailProvider(TicketEntity ticket) {
        // 1. 尝试从通知设置中获取指定的邮件供应商
        TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
        if (notifSettings != null && StringUtils.hasText(notifSettings.getEmailProviderUid())) {
            Optional<EmailProviderEntity> specific = emailProviderRepository
                    .findByUid(notifSettings.getEmailProviderUid());
            if (specific.isPresent() && !specific.get().isDeleted()
                    && Boolean.TRUE.equals(specific.get().getEnabled())) {
                return specific;
            }
            log.debug("ticket email notification: specified provider not found or disabled, uid={}",
                    notifSettings.getEmailProviderUid());
        }
        // 2. 回退：自动匹配 org 下首个同 ticket type 的启用供应商
        String ticketType = StringUtils.hasText(ticket.getType()) ? ticket.getType() : TicketTypeEnum.EXTERNAL.name();
        return emailProviderRepository
                .findFirstByOrgUidAndTypeAndEnabledTrueAndDeletedFalse(ticket.getOrgUid(), ticketType);
    }

    public void sendTicketSmsNotification(TicketEntity ticket, String previousStatus, String currentStatus,
            String eventType, String title, String content, String extra) {
        if (ticket == null) {
            return;
        }
        TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
        if (!isSmsEnabled(notifSettings)) {
            log.debug("ticket sms notification skipped: sms disabled, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isEventInList(notifSettings.getSmsEvents(), resolveEventKey(eventType, currentStatus, ticket))) {
            log.debug("ticket sms notification skipped: event not in smsEvents, ticketUid={}, eventType={}",
                    ticket.getUid(), eventType);
            return;
        }
        // 获取访客手机号：优先取 ticket.phone 字段，其次从 reporter VisitorEntity 中获取
        String recipientMobile = resolveReporterMobile(ticket);
        if (!StringUtils.hasText(recipientMobile)) {
            log.debug("ticket sms notification skipped: no reporter mobile found, ticketUid={}", ticket.getUid());
            return;
        }
        // 检查访客是否离线
        if (!isReporterOffline(ticket)) {
            boolean notifyWhenOnline = notifSettings != null
                    && Boolean.TRUE.equals(notifSettings.getSmsNotifyWhenOnline());
            if (!notifyWhenOnline) {
                log.debug("ticket sms notification skipped: reporter is online, ticketUid={}", ticket.getUid());
                return;
            }
        }
        // 获取 ticketSettings 中配置的统一短信模板（所有工单通知共用同一个 SmsTemplateEntity）
        String smsTemplateUid = notifSettings != null ? notifSettings.getSmsTemplateUid() : null;
        if (!StringUtils.hasText(smsTemplateUid)) {
            log.debug("ticket sms notification: no smsTemplateUid configured, skipping for ticketUid={}",
                    ticket.getUid());
            return;
        }
        Optional<SmsTemplateEntity> smsTemplateOpt = smsTemplateRepository.findByUid(smsTemplateUid);
        if (smsTemplateOpt.isEmpty() || smsTemplateOpt.get().isDeleted()) {
            log.warn("ticket sms notification: sms template not found or deleted, smsTemplateUid={}, ticketUid={}",
                    smsTemplateUid, ticket.getUid());
            return;
        }
        SmsTemplateEntity smsTemplate = smsTemplateOpt.get();
        String smsTemplateCode = smsTemplate.getTemplateCode();
        String smsSignName = smsTemplate.getSignName();
        List<String> smsVariableNames = smsTemplate.getVariableNames() != null
                ? smsTemplate.getVariableNames()
                : List.of("name", "ticketNumber", "status");
        if (!StringUtils.hasText(smsTemplateCode) || !StringUtils.hasText(smsSignName)) {
            log.warn("ticket sms notification: incomplete sms template (templateCode={}, signName={}), smsTemplateUid={}, ticketUid={}",
                    smsTemplateCode, smsSignName, smsTemplateUid, ticket.getUid());
            return;
        }

        String smsEventKey = resolveEventKey(eventType, currentStatus, ticket);
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber())
                ? ticket.getTicketNumber()
                : ticket.getUid();
        Map<String, String> templateParams = SmsPushSendService.buildTicketSmsVariables(
                smsVariableNames, ticketNumber, toStatusLabel(currentStatus), eventType,
                ticket.getContactName());
        try {
            smsPushSendService.sendSmsWithTemplate(recipientMobile, "86", smsSignName, smsTemplateCode,
                    templateParams, ticket.getOrgUid());
            log.info("ticket sms sent: ticketUid={}, eventType={}, eventKey={}, mobile={}",
                    ticket.getUid(), eventType, smsEventKey, recipientMobile);
        } catch (Exception e) {
            log.warn("ticket sms send failed: ticketUid={}, eventType={}, mobile={}, error={}",
                    ticket.getUid(), eventType, recipientMobile, e.getMessage());
        }
    }

    /**
     * 将后端 eventType + currentStatus 映射为前端 smsEvents 中的事件 key。
     * <ul>
     * <li>TICKET_CREATED → "created"</li>
     * <li>TICKET_STATUS_CHANGED → 根据 currentStatus 映射为 "assigned" / "resolved" /
     * "closed" /
     * "status_changed" 等</li>
     * </ul>
     */
    private String resolveEventKey(String eventType, String currentStatus, TicketEntity ticket) {
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "created";
        }
        if (EVENT_TYPE_TICKET_AGENT_MESSAGE.equals(eventType)) {
            return "replied";
        }
        if (EVENT_TYPE_TICKET_SLA_WARNING.equals(eventType)) {
            return "sla_warning";
        }
        if (EVENT_TYPE_TICKET_SLA_BREACH.equals(eventType)) {
            return "sla_breach";
        }
        // TICKET_STATUS_CHANGED: derive specific event
        if (StringUtils.hasText(currentStatus)) {
            try {
                TicketStatusEnum status = TicketStatusEnum.valueOf(currentStatus);
                return switch (status) {
                    case ASSIGNED, CLAIMED -> "assigned";
                    case RESOLVED, VERIFIED_OK -> "resolved";
                    case CLOSED -> "closed";
                    case PROCESSING -> "status_changed";
                    case PENDING, HOLDING -> "status_changed";
                    case REOPENED -> "status_changed";
                    case CANCELLED -> "closed";
                    default -> "status_changed";
                };
            } catch (IllegalArgumentException e) {
                // fallback
            }
        }
        return "status_changed";
    }

    private boolean isEmailEnabled(TicketNotificationSettingsEntity settings) {
        return settings != null && Boolean.TRUE.equals(settings.getEmailEnabled());
    }

    private boolean isSmsEnabled(TicketNotificationSettingsEntity settings) {
        return settings != null && Boolean.TRUE.equals(settings.getSmsEnabled());
    }

    private boolean isEventInList(java.util.List<String> events, String eventKey) {
        return events != null && !events.isEmpty() && events.contains(eventKey);
    }

    /**
     * 获取访客/报告人的手机号
     */
    private String resolveReporterMobile(TicketEntity ticket) {
        // 1. 优先取 ticket.phone 字段
        if (StringUtils.hasText(ticket.getPhone())) {
            return ticket.getPhone().trim();
        }
        // 2. 从 reporter UserProtobuf 的 uid 查找 VisitorEntity 的 mobile
        UserProtobuf reporter = ticket.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            Optional<VisitorEntity> visitorOpt = visitorRepository.findByUidAndDeleted(reporter.getUid(), false);
            if (visitorOpt.isPresent() && StringUtils.hasText(visitorOpt.get().getMobile())) {
                return visitorOpt.get().getMobile().trim();
            }
        }
        return null;
    }

    private String toStatusLabel(String status) {
        if (!StringUtils.hasText(status)) {
            return "UNKNOWN";
        }
        try {
            return switch (TicketStatusEnum.valueOf(status)) {
                case NEW -> "新建";
                case ASSIGNED -> "已分配";
                case CLAIMED -> "已认领";
                case UNCLAIMED -> "已退回";
                case PROCESSING -> "处理中";
                case PENDING -> "待回应";
                case HOLDING -> "挂起中";
                case RESUMED -> "已恢复";
                case REOPENED -> "重新打开";
                case RESOLVED -> "已解决";
                case VERIFIED_OK -> "验证通过";
                case VERIFIED_FAIL -> "验证未通过";
                case CLOSED -> "已关闭";
                case CANCELLED -> "已取消";
                case ESCALATED -> "已升级";
                case TRANSFERRED -> "已转派";
            };
        } catch (IllegalArgumentException ex) {
            return status;
        }
    }

    private String toSlaTypeLabel(String slaType) {
        if (!StringUtils.hasText(slaType)) {
            return "SLA";
        }
        return switch (slaType) {
            case "CLAIM" -> "认领";
            case "FIRST_RESPONSE" -> "首次响应";
            case "RESOLUTION" -> "解决";
            case "CUSTOMER_VERIFY" -> "客户确认";
            default -> slaType;
        };
    }

    /**
     * 获取工单对应的通知设置实体。
     * 优先通过 ticket.ticketSettingsUid 精确定位，回退到 org 默认设置。
     */
    private TicketNotificationSettingsEntity resolveNotificationSettings(TicketEntity ticket) {
        if (ticket == null || !StringUtils.hasText(ticket.getOrgUid())) {
            return null;
        }
        // 1. 优先通过 ticketSettingsUid 精确定位
        if (StringUtils.hasText(ticket.getTicketSettingsUid())) {
            Optional<TicketSettingsEntity> settingsOpt = ticketSettingsRepository
                    .findByUid(ticket.getTicketSettingsUid());
            if (settingsOpt.isPresent()) {
                TicketNotificationSettingsEntity notifSettings = settingsOpt.get().getNotificationSettings();
                if (notifSettings != null) {
                    return notifSettings;
                }
            }
        }
        // 2. 回退：查询 org 默认同类型设置
        String ticketType = StringUtils.hasText(ticket.getType()) ? ticket.getType() : TicketTypeEnum.EXTERNAL.name();
        List<TicketSettingsEntity> settingsList = ticketSettingsRepository
                .findByOrgUidAndTypeAndIsDefaultTrue(ticket.getOrgUid(), ticketType);
        if (settingsList.isEmpty()) {
            return null;
        }
        return settingsList.get(0).getNotificationSettings();
    }
}