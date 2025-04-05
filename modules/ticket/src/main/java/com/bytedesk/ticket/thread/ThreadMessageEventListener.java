/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-06 10:15:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-06 10:15:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.event.MessageCreateEvent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息事件监听器
 * 
 * 当新消息创建时，检查是否需要触发转人工请求
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadMessageEventListener {

    private final ThreadMessageService threadMessageService;
    private final ThreadRestService threadRestService;
    
    /**
     * 监听消息创建事件
     * 
     * @param event 消息创建事件
     */
    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        MessageEntity message = event.getMessage();
        if (message == null) {
            return;
        }
        
        log.debug("接收到新消息事件: messageUid={}, threadUid={}", 
                message.getUid(), message.getThreadUid());
        
        // 获取消息对应的会话线程
        ThreadEntity thread = null;
        try {
            thread = threadRestService.findByUid(message.getThreadUid()).orElse(null);
            if (thread == null) {
                log.warn("消息对应的会话不存在: messageUid={}, threadUid={}", 
                        message.getUid(), message.getThreadUid());
                return;
            }
            
            // 处理访客消息，检查转人工请求
            threadMessageService.processVisitorMessage(message, thread);
            
        } catch (Exception e) {
            log.error("处理消息事件时出错: {}", e.getMessage(), e);
        }
    }
}
