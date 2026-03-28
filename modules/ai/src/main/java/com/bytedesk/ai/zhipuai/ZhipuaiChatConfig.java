/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 12:22:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.zhipuai;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.z.openapi.ZhipuAiClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 智谱AI聊天配置类
 * 使用 zai-sdk 的 ZhipuAiClient
 * 统一使用 spring.ai.zhipuai 配置
 * https://github.com/zai-org/z-ai-sdk-java
 */
@Slf4j
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ZhipuaiChatConfig {

    @Value("${spring.ai.zhipuai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4.5-flash}")
    private String model;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    private double temperature;

    @Value("${spring.ai.zhipuai.chat.options.top-p:0.9}")
    private double topP;

    @Value("${spring.ai.zhipuai.chat.options.max-tokens:4096}")
    private int maxTokens;

    @Value("${spring.ai.zhipuai.request-timeout:0}")
    private int requestTimeout;

    @Value("${spring.ai.zhipuai.connection-timeout:30}")
    private int connectionTimeout;

    @Value("${spring.ai.zhipuai.read-timeout:10}")
    private int readTimeout;

    @Value("${spring.ai.zhipuai.write-timeout:10}")
    private int writeTimeout;

    @Value("${spring.ai.zhipuai.max-idle-connections:8}")
    private int maxIdleConnections;

    @Value("${spring.ai.zhipuai.keep-alive-duration:1}")
    private int keepAliveDuration;

    @Bean("zhipuAiClient")
    public ZhipuAiClient zhipuAiClient() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Zhipuai API key is not configured");
            return null;
        }

        log.info("Initializing ZhipuAiClient with model: {}", model);

        try {
            int effectiveRequestTimeout = requestTimeout > 0
                    ? requestTimeout
                    : connectionTimeout + readTimeout + writeTimeout + 5;

            return ZhipuAiClient.builder()
                .ofZHIPU()
                    .apiKey(apiKey)
                .enableTokenCache()
                .networkConfig(
                    effectiveRequestTimeout,
                    connectionTimeout,
                    readTimeout,
                    writeTimeout,
                    TimeUnit.SECONDS)
                .connectionPool(
                    maxIdleConnections,
                    keepAliveDuration,
                    TimeUnit.SECONDS)
                    .build();
        } catch (Exception e) {
            // zai-sdk 会在 setApiKey 时做格式校验；无效 key 不应阻塞应用启动
            log.warn("Failed to initialize ZhipuAiClient due to invalid api key, ZhipuAI features will be disabled: {}",
                    e.getMessage());
            return null;
        } catch (LinkageError e) {
            log.warn(
                    "Failed to initialize ZhipuAiClient due to SDK/runtime incompatibility, ZhipuAI features will be disabled: {}",
                    e.getMessage());
            return null;
        }
    }
} 