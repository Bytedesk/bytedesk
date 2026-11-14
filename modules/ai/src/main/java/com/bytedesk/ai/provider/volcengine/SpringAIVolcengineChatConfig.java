/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-11-10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.volcengine;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;
import com.volcengine.ark.runtime.service.ArkApi;

import lombok.extern.slf4j.Slf4j;

/**
 * 火山引擎方舟聊天配置。
 *
 * <p>2026-11 迁移至方舟官方 SDK（com.volcengine:ark-runtime），替换原
 * Spring AI OpenAI 兼容客户端（OpenAiChatModel + ark /api/v3 兼容端点）。</p>
 *
 * <p>Bean 层次：</p>
 * <ol>
 * <li><b>arkApi</b> — 方舟官方 SDK API 客户端（apiKey + baseUrl，
 *     经 {@link ArkServiceFactory} 构建，见该类说明）</li>
 * <li><b>volcengineChatModel</b> — {@link ArkChatModel}（Spring AI ChatModel 适配）</li>
 * <li><b>volcengineChatClient</b> — ChatClient（Advisor 链：记忆/工具/日志）</li>
 * </ol>
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.volcengine.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIVolcengineChatConfig {

    @Value("${spring.ai.volcengine.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${spring.ai.volcengine.api-key:}")
    private String apiKey;

    @Value("${spring.ai.volcengine.chat.options.model:doubao-1-5-pro-32k-250115}")
    private String model;

    @Value("${spring.ai.volcengine.chat.options.temperature:0.7}")
    private Double temperature;

    /**
     * 方舟官方 SDK API 客户端。
     * baseUrl 归一化：SDK 内部按 baseUrl + /api/v3/chat/completions 拼接，
     * 兼容历史配置中带 /api/v3 后缀的写法（去后缀后再交给 SDK）。
     */
    @Bean("arkApi")
    ArkApi arkApi() {
        String resolvedBaseUrl = normalizeBaseUrl(baseUrl);
        log.info("Initializing Volcengine ArkApi with baseUrl: {}, model: {}", resolvedBaseUrl, model);
        return ArkServiceFactory.createApi(apiKey, resolvedBaseUrl);
    }

    @Bean("volcengineChatModel")
    ChatModel volcengineChatModel() {
        return new ArkChatModel(arkApi(), ArkChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build());
    }

    @Bean("volcengineChatClient")
    ChatClient volcengineChatClient(ChatClientBuilderFactory chatClientBuilderFactory) {
        return chatClientBuilderFactory.builder(volcengineChatModel())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    /**
     * 归一化 baseUrl：SDK BASE_URL 为 https://ark.cn-beijing.volces.com（不带 /api/v3），
     * 历史 OpenAI 兼容配置带 /api/v3 后缀，这里统一去掉，避免 /api/v3/api/v3 重复拼接。
     */
    static String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return "https://ark.cn-beijing.volces.com";
        }
        String trimmed = url.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.endsWith("/api/v3")) {
            trimmed = trimmed.substring(0, trimmed.length() - "/api/v3".length());
        }
        return trimmed;
    }

}
