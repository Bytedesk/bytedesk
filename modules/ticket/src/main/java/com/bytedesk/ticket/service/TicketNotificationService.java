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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.ticket.TicketEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class TicketNotificationService {
    
    public void notifyNewTicket(TicketEntity ticket) {
        // 新工单通知
    }
    
    public void notifyTicketAssigned(TicketEntity ticket) {
        // 工单分配通知
    }
    
    // public void notifyTicketComment(TicketCommentEntity comment) {
    //     // 工单评论通知
    // }
    
    public void notifySLABreach(TicketEntity ticket) {
        // SLA违规通知
    }
    
    public void notifyTicketClosed(TicketEntity ticket) {
        // 发送工单关闭通知
        System.out.println("Ticket closed notification sent for ticket: " + ticket.getId());
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
} 