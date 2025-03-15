/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-15 17:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-15 17:00:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.bytedesk.core.exception.TabooException;
import com.bytedesk.core.utils.JsonResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket异常处理器
 * 专门处理WebSocket通信中的异常
 */
@Slf4j
public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.error("处理客户端消息时出错: {}", ex.getMessage());

        // 提取根本原因
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        // 处理TabooException
        if (cause instanceof TabooException) {
            return handleTabooException(clientMessage, (TabooException) cause);
        }

        // 默认处理
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleTabooException(Message<byte[]> clientMessage, TabooException ex) {
        log.warn("敏感词异常: {}", ex.getMessage());
        
        // 创建错误信息
        JsonResult<?> errorResponse = JsonResult.error("消息含有敏感内容，已被过滤");
        byte[] payload;
        
        try {
            payload = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("序列化错误响应失败", e);
            payload = "消息含有敏感内容，已被过滤".getBytes();
        }
        
        // 创建新的StompHeaderAccessor
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(ex.getMessage());
        accessor.setLeaveMutable(true);
        
        // 从原消息中复制必要的头信息
        MessageHeaderAccessor originalAccessor = MessageHeaderAccessor.getAccessor(clientMessage, MessageHeaderAccessor.class);
        if (originalAccessor != null) {
            // 正确获取sessionId和subscriptionId
            if (originalAccessor instanceof StompHeaderAccessor) {
                StompHeaderAccessor originalStompAccessor = (StompHeaderAccessor) originalAccessor;
                accessor.setSessionId(originalStompAccessor.getSessionId());
                accessor.setSubscriptionId(originalStompAccessor.getSubscriptionId());
            } else {
                // 通过通用方式获取
                accessor.setHeader("simpSessionId", originalAccessor.getHeader("simpSessionId"));
                accessor.setHeader("simpSubscriptionId", originalAccessor.getHeader("simpSubscriptionId"));
            }
        }
        
        return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
    }
}
