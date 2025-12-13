/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-17 14:24:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 18:58:11
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
import com.bytedesk.ai.service.ChatClientInfoService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ChatClient信息查询控制器
 * 提供查看所有ChatClient和Primary ChatClient的接口
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/chat-clients")
@RequiredArgsConstructor
@ConditionalOnBean(ChatClient.class)
public class ChatClientInfoController {

    private final ChatClientInfoService chatClientInfoService;
    
    private final BytedeskProperties bytedeskProperties;

    /**
     * 获取所有ChatClient信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-clients/info
     */
    // @GetMapping("/info")
    // public ResponseEntity<JsonResult<?>> getAllChatClientsInfo() {
    //     if (!bytedeskProperties.getDebug()) {
    //         return ResponseEntity.ok(JsonResult.error("Service is not available"));
    //     }
    //     log.info("Getting all ChatClient information");
    //     Map<String, Object> result = chatClientInfoService.getAllChatClientsInfo();
    //     return ResponseEntity.ok(JsonResult.success(result));
    // }

    /**
     * 获取Primary ChatClient信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-clients/primary
     */
    @GetMapping("/primary")
    public ResponseEntity<JsonResult<?>> getPrimaryChatClientInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting Primary ChatClient information");
        Map<String, Object> result = chatClientInfoService.getPrimaryChatClientInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 获取RAG使用的ChatClient信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-clients/rag
     */
    @GetMapping("/rag")
    public ResponseEntity<JsonResult<?>> getRagChatClientInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting RAG ChatClient information");
        Map<String, Object> result = chatClientInfoService.getRagChatClientInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 测试指定的ChatClient
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-clients/test/{provider}
     */
    @GetMapping("/test/{provider}")
    public ResponseEntity<JsonResult<?>> testChatClient(@PathVariable String provider) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Testing chat client for provider: {}", provider);
        
        Map<String, Object> result = chatClientInfoService.testChatClient(provider);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 测试所有可用的ChatClient
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-clients/test-all
     */
    @GetMapping("/test-all")
    public ResponseEntity<JsonResult<?>> testAllChatClients() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Testing all available chat clients");
        
        Map<String, Object> result = chatClientInfoService.testAllChatClients();
        return ResponseEntity.ok(JsonResult.success(result));
    }
} 