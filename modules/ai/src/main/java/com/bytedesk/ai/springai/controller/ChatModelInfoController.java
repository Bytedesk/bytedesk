/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-17 14:24:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 16:20:41
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
import com.bytedesk.ai.springai.service.ChatModelInfoService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ChatModel信息查询控制器
 * 提供查看所有ChatModel和Primary ChatModel的接口
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/chat-models")
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class ChatModelInfoController {

    private final ChatModelInfoService chatModelInfoService;
    private final BytedeskProperties bytedeskProperties;

    /**
     * 获取所有ChatModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-models/info
     */
    @GetMapping("/info")
    public ResponseEntity<JsonResult<?>> getAllChatModelsInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting all ChatModel information");
        Map<String, Object> result = chatModelInfoService.getAllChatModelsInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 获取Primary ChatModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-models/primary
     */
    @GetMapping("/primary")
    public ResponseEntity<JsonResult<?>> getPrimaryChatModelInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting Primary ChatModel information");
        Map<String, Object> result = chatModelInfoService.getPrimaryChatModelInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 获取RAG使用的ChatModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-models/rag
     */
    @GetMapping("/rag")
    public ResponseEntity<JsonResult<?>> getRagChatModelInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting RAG ChatModel information");
        Map<String, Object> result = chatModelInfoService.getRagChatModelInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 测试指定的ChatModel
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-models/test/{provider}
     */
    @GetMapping("/test/{provider}")
    public ResponseEntity<JsonResult<?>> testChatModel(@PathVariable String provider) {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Testing chat model for provider: {}", provider);
        
        Map<String, Object> result = chatModelInfoService.testChatModel(provider);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 测试所有可用的ChatModel
     * GET http://127.0.0.1:9003/spring/ai/api/v1/chat-models/test-all
     */
    @GetMapping("/test-all")
    public ResponseEntity<JsonResult<?>> testAllChatModels() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Testing all available chat models");
        
        Map<String, Object> result = chatModelInfoService.testAllChatModels();
        return ResponseEntity.ok(JsonResult.success(result));
    }
} 