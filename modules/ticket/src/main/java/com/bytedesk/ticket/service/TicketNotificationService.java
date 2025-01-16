/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 16:40:20
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

import com.bytedesk.ticket.model.TicketEntity;
import com.bytedesk.ticket.model.TicketComment;

@Service
public class TicketNotificationService {
    
    public void notifyNewTicket(TicketEntity ticket) {
        // 新工单通知
    }
    
    public void notifyTicketAssigned(TicketEntity ticket) {
        // 工单分配通知
    }
    
    public void notifyTicketComment(TicketComment comment) {
        // 工单评论通知
    }
    
    public void notifySLABreach(TicketEntity ticket) {
        // SLA违规通知
    }
    
    public void notifyTicketClosed(TicketEntity ticket) {
        // 发送工单关闭通知
        System.out.println("Ticket closed notification sent for ticket: " + ticket.getId());
    }
} 