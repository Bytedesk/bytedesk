/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-17 14:24:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 18:49:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.config.properties.BytedeskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ChatClient测试控制器
 * 提供基本的聊天功能演示
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/chat-client")
@RequiredArgsConstructor
@ConditionalOnBean(ChatClient.class)
public class ChatClientTestController {

    private final ChatClient chatClient;
    
    private final BytedeskProperties bytedeskProperties;

    /**
     * 简单的聊天接口
     * POST http://127.0.0.1:9003/spring/ai/api/v1/chat-client/chat
     */
    @PostMapping("/chat")
    public ResponseEntity<JsonResult<?>> chat(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("Message is required"));
        }

        try {
            log.info("Chat request: {}", message);
            
            var response = chatClient.prompt()
                    .user(message)
                    .call()
                    .chatResponse();
            
            String responseText = response.getResult().getOutput().getText();
            
            Map<String, Object> result = Map.of(
                "message", message,
                "response", responseText,
                "provider", "Primary ChatClient",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(JsonResult.success(result));
            
        } catch (Exception e) {
            log.error("Error in chat: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Chat failed: " + e.getMessage()));
        }
    }

    /**
     * 带系统提示的聊天接口
     * POST http://127.0.0.1:9003/spring/ai/api/v1/chat-client/chat-with-system
     */
    @PostMapping("/chat-with-system")
    public ResponseEntity<JsonResult<?>> chatWithSystem(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        String message = request.get("message");
        String systemPrompt = request.get("systemPrompt");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("Message is required"));
        }

        try {
            log.info("Chat with system request: {}", message);
            
            var response = chatClient.prompt()
                    .system(systemPrompt != null ? systemPrompt : "You are a helpful assistant.")
                    .user(message)
                    .call()
                    .chatResponse();
            
            String responseText = response.getResult().getOutput().getText();
            
            Map<String, Object> result = Map.of(
                "message", message,
                "systemPrompt", systemPrompt != null ? systemPrompt : "You are a helpful assistant.",
                "response", responseText,
                "provider", "Primary ChatClient",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(JsonResult.success(result));
            
        } catch (Exception e) {
            log.error("Error in chat with system: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Chat failed: " + e.getMessage()));
        }
    }

    /**
     * 流式聊天接口（返回完整响应）
     * POST http://127.0.0.1:9003/spring/ai/api/v1/chat-client/stream-chat
     */
    @PostMapping("/stream-chat")
    public ResponseEntity<JsonResult<?>> streamChat(@RequestBody Map<String, String> request) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("Message is required"));
        }

        try {
            log.info("Stream chat request: {}", message);
            
            var response = chatClient.prompt()
                    .user(message)
                    .call()
                    .chatResponse();
            
            String responseText = response.getResult().getOutput().getText();
            
            Map<String, Object> result = Map.of(
                "message", message,
                "response", responseText,
                "responseLength", responseText.length(),
                "provider", "Primary ChatClient",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(JsonResult.success(result));
            
        } catch (Exception e) {
            log.error("Error in stream chat: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Stream chat failed: " + e.getMessage()));
        }
    }

    /**
     * 获取ChatClient信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-client/info
     */
    @GetMapping("/info")
    public ResponseEntity<JsonResult<?>> getChatClientInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        try {
            Map<String, Object> info = Map.of(
                "className", chatClient.getClass().getSimpleName(),
                "fullClassName", chatClient.getClass().getName(),
                "provider", "Primary ChatClient",
                "status", "Active",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(JsonResult.success(info));
            
        } catch (Exception e) {
            log.error("Error getting chat client info: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Failed to get info: " + e.getMessage()));
        }
    }
} 