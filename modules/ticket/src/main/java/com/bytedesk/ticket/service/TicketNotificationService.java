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
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.email_provider.EmailProviderEntity;
import com.bytedesk.core.email_provider.EmailProviderRepository;
import com.bytedesk.core.email_push.EmailPushSendService;
import com.bytedesk.core.sms_push.SmsPushSendService;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.notification.NotificationRequest;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.notification.NotificationTypeEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.push.apns_push.ApnsPushService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorRepository;
import com.bytedesk.service.visitor.VisitorStatusEnum;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketStatusEnum;
import com.bytedesk.ticket.ticket.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TicketNotificationService {

    public static final String EVENT_TYPE_TICKET_CREATED = "TICKET_CREATED";

    public static final String EVENT_TYPE_TICKET_STATUS_CHANGED = "TICKET_STATUS_CHANGED";

    public static final String EVENT_TYPE_TICKET_AGENT_MESSAGE = "TICKET_AGENT_MESSAGE";

    private final NotificationService notificationService;

    private final WorkgroupRepository workgroupRepository;

    private final VisitorRepository visitorRepository;

    private final EmailProviderRepository emailProviderRepository;

    private final SmsPushSendService smsPushSendService;

    private final ApnsPushService apnsPushService;

    private final EmailPushSendService emailPushSendService;

    private final TicketSettingsRepository ticketSettingsRepository;
    
    public void notifyNewTicket(TicketEntity ticket) {
        notifyTicketStatusChanged(ticket, null, ticket != null ? ticket.getStatus() : null);
    }
    
    public void notifyTicketAssigned(TicketEntity ticket) {
        notifyTicketStatusChanged(ticket, null, ticket != null ? ticket.getStatus() : null);
    }
    
    // public void notifyTicketComment(TicketCommentEntity comment) {
    //     // 工单评论通知
    // }
    
    public void notifySLABreach(TicketEntity ticket) {
        // SLA违规通知
    }
    
    public void notifyTicketClosed(TicketEntity ticket) {
        notifyTicketStatusChanged(ticket, null, ticket != null ? ticket.getStatus() : null);
    }

    public void notifyTicketStatusChanged(TicketEntity ticket, String previousStatus, String currentStatus) {
        if (ticket == null || !StringUtils.hasText(ticket.getUid()) || !StringUtils.hasText(currentStatus)) {
            return;
        }

        String eventType = StringUtils.hasText(previousStatus) ? EVENT_TYPE_TICKET_STATUS_CHANGED : EVENT_TYPE_TICKET_CREATED;
        Set<String> recipients = resolveRecipientUids(ticket, EVENT_TYPE_TICKET_CREATED.equals(eventType));
        if (recipients.isEmpty()) {
            return;
        }

        String reporterUid = ticket.getReporter() != null ? ticket.getReporter().getUid() : null;
        String title = buildTitle(ticket, eventType);
        String content = buildContent(ticket, previousStatus, currentStatus, eventType);
        String extra = buildExtra(ticket, previousStatus, currentStatus, eventType);

        for (String recipientUid : recipients) {
            try {
                boolean isReporter = StringUtils.hasText(reporterUid) && reporterUid.equals(recipientUid);
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

        // Push to agent iOS devices via APNs
        pushToAgentDevices(ticket, eventType, reporterUid, recipients);

        notifyExternalChannels(ticket, previousStatus, currentStatus, eventType, title, content, extra);
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
        log.info("SLA违规通知 - 工单: {}, 类型: {}, 详情: {}", ticketId, type, details);
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
        String recipientEmail = resolveReporterEmail(ticket);
        if (!StringUtils.hasText(recipientEmail)) {
            log.debug("ticket agent message email skipped: no reporter email, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isReporterOffline(ticket)) {
            TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
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
                ticket.getOrgUid());
    }

    private void sendTicketAgentMessageSmsNotification(TicketEntity ticket, String agentName) {
        // 复用现有的短信通知流程，使用 "replied" 事件类型
        sendTicketSmsNotification(ticket, null, null, EVENT_TYPE_TICKET_AGENT_MESSAGE,
                "工单新回复", null, null);
    }

    private Set<String> resolveRecipientUids(TicketEntity ticket, boolean includeWorkgroupUsers) {
        Set<String> recipients = new LinkedHashSet<>();

        UserProtobuf reporter = ticket.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            recipients.add(reporter.getUid());
        }

        UserProtobuf assignee = ticket.getAssignee();
        if (assignee != null && StringUtils.hasText(assignee.getUid())) {
            recipients.add(assignee.getUid());
        }

        if (includeWorkgroupUsers && StringUtils.hasText(ticket.getWorkgroupUid())) {
            workgroupRepository.findByUid(ticket.getWorkgroupUid())
                    .filter(workgroup -> !workgroup.isDeleted())
                    .ifPresent(workgroup -> workgroup.getAgents().stream()
                            .filter(agent -> agent != null && !agent.isDeleted())
                            .map(AgentEntity::getMember)
                            .filter(member -> member != null && !member.isDeleted())
                            .map(MemberEntity::getUser)
                            .filter(user -> user != null && !user.isDeleted())
                            .map(user -> user.getUid())
                            .filter(StringUtils::hasText)
                            .forEach(recipients::add));
        }

        return recipients;
    }

    /**
     * Push ticket notification to agent iOS devices via APNs.
     * Only pushes to agents (assignee + workgroup members), not to the reporter/visitor.
     * Android push is reserved as a TODO placeholder for future implementation.
     */
    private void pushToAgentDevices(TicketEntity ticket, String eventType, String reporterUid, Set<String> recipients) {
        String agentPushTitle = buildAgentPushTitle(ticket, eventType);
        String agentPushBody = buildAgentPushBody(ticket, eventType);

        for (String recipientUid : recipients) {
            // Skip reporter/visitor - only push to agents
            if (StringUtils.hasText(reporterUid) && reporterUid.equals(recipientUid)) {
                continue;
            }
            try {
                apnsPushService.pushNotificationToUser(recipientUid, agentPushTitle, agentPushBody, ticket.getUid());
            } catch (Exception ex) {
                log.warn("APNs ticket push failed: ticketUid={}, agentUid={}, error={}",
                        ticket.getUid(), recipientUid, ex.getMessage());
            }
            // TODO: Android push - implement when Android push service is available
            // androidPushService.pushToUser(recipientUid, agentPushTitle, agentPushBody, ticket.getUid());
        }
    }

    /**
     * Build push title for agent devices (shorter than the in-app notification title).
     */
    private String buildAgentPushTitle(TicketEntity ticket, String eventType) {
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "新工单";
        }
        return "工单状态更新";
    }

    /**
     * Build push body for agent devices.
     */
    private String buildAgentPushBody(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid();
        String summary = StringUtils.hasText(ticket.getTitle()) ? ticket.getTitle() : "";
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return StringUtils.hasText(summary)
                    ? "#" + ticketNumber + " " + summary
                    : "收到新工单 #" + ticketNumber;
        }
        String statusLabel = toStatusLabel(ticket.getStatus());
        return StringUtils.hasText(summary)
                ? "#" + ticketNumber + " " + statusLabel + " " + summary
                : "工单 #" + ticketNumber + " 状态：" + statusLabel;
    }

    private String buildTitle(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "新工单：#" + ticketNumber;
        }
        return "工单状态更新：#" + ticketNumber;
    }

    private String buildReporterTitle(TicketEntity ticket, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "工单创建成功";
        }
        return "工单状态更新：#" + ticketNumber;
    }

    private String buildContent(TicketEntity ticket, String previousStatus, String currentStatus, String eventType) {
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid();
        String summary = StringUtils.hasText(ticket.getTitle()) ? ticket.getTitle() : ticket.getDescription();
        StringBuilder builder = new StringBuilder();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            builder.append("收到新工单 #").append(ticketNumber);
        } else {
            builder.append("工单 #").append(ticketNumber).append(" 状态更新为 ").append(toStatusLabel(currentStatus));
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
        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber()) ? ticket.getTicketNumber() : ticket.getUid();
        StringBuilder builder = new StringBuilder();
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            builder.append("您的工单 #").append(ticketNumber).append(" 已创建成功，请耐心等待客服处理");
        } else {
            builder.append("工单 #").append(ticketNumber).append(" 状态已更新");
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
        String recipientEmail = resolveReporterEmail(ticket);
        if (!StringUtils.hasText(recipientEmail)) {
            log.debug("ticket email notification skipped: no reporter email found, ticketUid={}", ticket.getUid());
            return;
        }
        if (!isReporterOffline(ticket)) {
            TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
            boolean notifyWhenOnline = notifSettings != null && Boolean.TRUE.equals(notifSettings.getEmailNotifyWhenOnline());
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
            if (specific.isPresent() && !specific.get().isDeleted() && Boolean.TRUE.equals(specific.get().getEnabled())) {
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
        // 获取访客手机号：优先取 ticket.phone 字段，其次从 reporter VisitorEntity 中获取
        String recipientMobile = resolveReporterMobile(ticket);
        if (!StringUtils.hasText(recipientMobile)) {
            log.debug("ticket sms notification skipped: no reporter mobile found, ticketUid={}", ticket.getUid());
            return;
        }
        // 检查访客是否离线
        if (!isReporterOffline(ticket)) {
            TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
            boolean notifyWhenOnline = notifSettings != null && Boolean.TRUE.equals(notifSettings.getSmsNotifyWhenOnline());
            if (!notifyWhenOnline) {
                log.debug("ticket sms notification skipped: reporter is online, ticketUid={}", ticket.getUid());
                return;
            }
        }
        // 获取 ticketSettings 中的短信模板配置（smsTemplateIds: event → {"tc":"...","sn":"..."})
        TicketNotificationSettingsEntity notifSettings = resolveNotificationSettings(ticket);
        Map<String, String> smsTemplateIds = null;
        if (notifSettings != null && notifSettings.getSmsTemplateIds() != null
                && !notifSettings.getSmsTemplateIds().isEmpty()) {
            smsTemplateIds = notifSettings.getSmsTemplateIds();
        }
        if (smsTemplateIds == null || smsTemplateIds.isEmpty()) {
            log.debug("ticket sms notification: no smsTemplateIds configured, skipping for ticketUid={}", ticket.getUid());
            return;
        }
        // 按事件类型查找对应的模板配置 JSON
        String smsEventKey = resolveSmsEventKey(eventType, currentStatus, ticket);
        String templateJson = smsTemplateIds.get(smsEventKey);
        if (!StringUtils.hasText(templateJson)) {
            log.debug("ticket sms notification: no template configured for eventKey={}, skipping ticketUid={}",
                    smsEventKey, ticket.getUid());
            return;
        }
        // 解析 {"tc":"SMS_xxx","sn":"微语","vars":"[\"name\",\"ticketNumber\",\"status\"]"}
        String smsTemplateCode = null;
        String smsSignName = null;
        List<String> smsVariableNames = List.of("name", "ticketNumber", "status"); // default
        try {
            JSONObject tpl = JSON.parseObject(templateJson);
            smsTemplateCode = tpl.getString("tc");
            smsSignName = tpl.getString("sn");
            String varsRaw = tpl.getString("vars");
            if (StringUtils.hasText(varsRaw)) {
                smsVariableNames = JSON.parseArray(varsRaw, String.class);
            }
        } catch (Exception e) {
            log.warn("ticket sms notification: failed to parse templateJson={} for eventKey={}, ticketUid={}",
                    templateJson, smsEventKey, ticket.getUid(), e);
            return;
        }
        if (!StringUtils.hasText(smsTemplateCode) || !StringUtils.hasText(smsSignName)) {
            log.warn("ticket sms notification: incomplete template config (tc={}, sn={}) for eventKey={}, ticketUid={}",
                    smsTemplateCode, smsSignName, smsEventKey, ticket.getUid());
            return;
        }

        String ticketNumber = StringUtils.hasText(ticket.getTicketNumber())
                ? ticket.getTicketNumber() : ticket.getUid();
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
     * 将后端 eventType + currentStatus 映射为前端 smsTemplateIds 中的事件 key。
     * <ul>
     * <li>TICKET_CREATED → "created"</li>
     * <li>TICKET_STATUS_CHANGED → 根据 currentStatus 映射为 "assigned" / "resolved" / "closed" /
     * "status_changed" 等</li>
     * </ul>
     */
    private String resolveSmsEventKey(String eventType, String currentStatus, TicketEntity ticket) {
        if (EVENT_TYPE_TICKET_CREATED.equals(eventType)) {
            return "created";
        }
        if (EVENT_TYPE_TICKET_AGENT_MESSAGE.equals(eventType)) {
            return "replied";
        }
        // TICKET_STATUS_CHANGED: 推导具体事件
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
            Optional<TicketSettingsEntity> settingsOpt = ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid());
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