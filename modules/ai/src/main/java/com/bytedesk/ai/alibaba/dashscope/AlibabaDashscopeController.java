/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-18 07:05:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 07:05:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.dashscope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云 Dashscope 控制器 - 使用官方 spring-ai-alibaba-starter-dashscope
 */
@Slf4j
@RestController
@RequestMapping("/alibaba/dashscope")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class AlibabaDashscopeController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

    private final BytedeskProperties bytedeskProperties;
    private final AlibabaDashscopeService alibabaDashscopeService;

    @Autowired(required = false)
    @Qualifier("alibabaDashscopeChatModel")
    private ChatModel alibabaDashscopeChatModel;

    /**
     * 方式1：同步调用
     * http://127.0.0.1:9003/alibaba/dashscope/chat/sync?message=hello
     */
    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope service is not available"));
        }
        
        String response = alibabaDashscopeService.processPromptSync(message, null, "");
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式2：使用官方 DashScopeChatModel 直接调用
     * http://127.0.0.1:9003/alibaba/dashscope/chat/official?message=hello
     */
    @GetMapping("/chat/official")
    public ResponseEntity<JsonResult<?>> chatOfficial(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope service is not available"));
        }
        
        if (alibabaDashscopeChatModel == null) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope ChatModel is not available"));
        }

        try {
            ChatResponse response = alibabaDashscopeChatModel.call(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
                    .builder()
                    .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
                    .build()));
            
            String result = response.getResult().getOutput().getText();
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Alibaba Dashscope API error", e);
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 演示如何获取 LLM 的 token 信息
     * http://127.0.0.1:9003/alibaba/dashscope/tokens
     */
    @GetMapping("/tokens")
    public ResponseEntity<JsonResult<?>> tokens() {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope service is not available"));
        }
        
        if (alibabaDashscopeChatModel == null) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope ChatModel is not available"));
        }

        try {
            ChatResponse chatResponse = alibabaDashscopeChatModel.call(new Prompt(DEFAULT_PROMPT, DashScopeChatOptions
                    .builder()
                    .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
                    .build()));

            Map<String, Object> res = new HashMap<>();
            res.put("output", chatResponse.getResult().getOutput().getText());
            res.put("output_token", chatResponse.getMetadata().getUsage().getCompletionTokens());
            res.put("input_token", chatResponse.getMetadata().getUsage().getPromptTokens());
            res.put("total_token", chatResponse.getMetadata().getUsage().getTotalTokens());

            return ResponseEntity.ok(JsonResult.success(res));
        } catch (Exception e) {
            log.error("Alibaba Dashscope API error", e);
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 使用编程方式自定义 LLMs ChatOptions 参数
     * http://127.0.0.1:9003/alibaba/dashscope/chat/custom?message=hello
     */
    @GetMapping("/chat/custom")
    public ResponseEntity<JsonResult<?>> chatCustom(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope service is not available"));
        }
        
        if (alibabaDashscopeChatModel == null) {
            return ResponseEntity.ok(JsonResult.error("Alibaba Dashscope ChatModel is not available"));
        }

        try {
            DashScopeChatOptions customOptions = DashScopeChatOptions.builder()
                    .withTopP(0.7)
                    .withTopK(50)
                    .withTemperature(0.8)
                    .build();

            ChatResponse response = alibabaDashscopeChatModel.call(new Prompt(message, customOptions));
            String result = response.getResult().getOutput().getText();
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Alibaba Dashscope API error", e);
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 健康检查
     * http://127.0.0.1:9003/alibaba/dashscope/health
     */
    @GetMapping("/health")
    public ResponseEntity<JsonResult<?>> health() {
        Boolean isHealthy = alibabaDashscopeService.isServiceHealthy();
        return ResponseEntity.ok(JsonResult.success(Map.of("healthy", isHealthy)));
    }

} 