/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-18 17:01:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 17:03:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.message.event.TicketMessageEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketMessageHandler {
    
    // private final EmailService emailService;
    // private final WebSocketService webSocketService;
    // private final WechatService wechatService;
    
    @Async
    @EventListener
    public void handleTicketMessage(TicketMessageEvent event) {
        log.info("处理工单消息: {}", event);
        
        try {
            switch (event.getType()) {
                case ASSIGNED -> handleAssignedMessage(event);
                case UNCLAIMED -> handleUnclaimedMessage(event);
                case COMPLETED -> handleCompletedMessage(event);
                case ESCALATED -> handleEscalatedMessage(event);
            }
        } catch (Exception e) {
            log.error("消息处理失败: {}", e.getMessage());
            // TODO: 消息重试机制
        }
    }
    
    private void handleAssignedMessage(TicketMessageEvent event) {
        UserProtobuf assignee = event.getAssignee();
        log.info("处理工单消息: {}", assignee);
        
        // 1. 发送站内信
        // webSocketService.sendMessage(assignee.getUid(), 
        //     String.format("工单 %s 已分配给您", event.getTicketUid()));
            
        // 2. 发送邮件
        // if (StringUtils.hasText(assignee.getEmail())) {
        //     emailService.sendAssignmentEmail(
        //         assignee.getEmail(), 
        //         event.getTicketUid(),
        //         event.getDescription()
        //     );
        // }
        
        // 3. 发送微信通知
        // if (StringUtils.hasText(assignee.getWechatOpenid())) {
        //     wechatService.sendTemplateMessage(
        //         assignee.getWechatOpenid(),
        //         "工单分配通知",
        //         event.getDescription()
        //     );
        // }
    }

    private void handleUnclaimedMessage(TicketMessageEvent event) {
        // TODO: 实现未认领消息处理
    }

    private void handleCompletedMessage(TicketMessageEvent event) {
        // TODO: 实现完成消息处理
    }

    private void handleEscalatedMessage(TicketMessageEvent event) {
        // TODO: 实现升级消息处理
    }
    
} 