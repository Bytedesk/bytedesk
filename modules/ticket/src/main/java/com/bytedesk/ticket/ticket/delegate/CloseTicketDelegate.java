/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 15:03:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 15:22:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketNotificationService;

import java.time.LocalDateTime;

@Component
public class CloseTicketDelegate implements JavaDelegate {

    @Autowired
    private TicketNotificationService notificationService;

    @Override
    public void execute(DelegateExecution execution) {
        TicketEntity ticket = (TicketEntity) execution.getVariable("ticket");
        ticket.setStatus("已关闭");
        ticket.setUpdatedAt(LocalDateTime.now());
        
        // 发送通知
        notificationService.notifyTicketClosed(ticket);
    }
} 