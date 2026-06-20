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
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.notification.NotificationRequest;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.notification.NotificationTypeEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TicketNotificationService {

    public static final String EVENT_TYPE_TICKET_CREATED = "TICKET_CREATED";

    public static final String EVENT_TYPE_TICKET_STATUS_CHANGED = "TICKET_STATUS_CHANGED";

    private final NotificationService notificationService;

    private final WorkgroupRepository workgroupRepository;
    
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
        log.debug("ticket email notification hook skipped: ticketUid={}, eventType={}",
                ticket != null ? ticket.getUid() : null, eventType);
    }

    public void sendTicketSmsNotification(TicketEntity ticket, String previousStatus, String currentStatus,
            String eventType, String title, String content, String extra) {
        log.debug("ticket sms notification hook skipped: ticketUid={}, eventType={}",
                ticket != null ? ticket.getUid() : null, eventType);
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
} 