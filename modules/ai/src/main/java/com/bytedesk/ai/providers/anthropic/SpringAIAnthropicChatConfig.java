/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-02 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-08-02 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.providers.anthropic;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Anthropic 聊天配置
 * https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html
 * https://github.com/anthropics/anthropic-sdk-java
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.anthropic.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIAnthropicChatConfig {

    @Value("${spring.ai.anthropic.base-url:https://api.anthropic.com}")
    private String baseUrl;

    @Value("${spring.ai.anthropic.api-key:}")
    private String apiKey;

    @Value("${spring.ai.anthropic.chat.options.model:claude-sonnet-4-5}")
    private String model;

    @Value("${spring.ai.anthropic.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.anthropic.chat.options.max-tokens:4096}")
    private Integer maxTokens;

    @Bean("anthropicChatOptions")
    AnthropicChatOptions anthropicChatOptions() {
        return AnthropicChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }

    @Bean("anthropicChatModel")
    AnthropicChatModel anthropicChatModel() {
        // 通过 options 配置连接信息（apiKey/baseUrl），由 AnthropicChatModel 内部构建 client
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
        return AnthropicChatModel.builder()
                .options(options)
                .build();
    }

    @Bean("anthropicChatClient")
    ChatClient anthropicChatClient(ChatClientBuilderFactory chatClientBuilderFactory) {
        return chatClientBuilderFactory.builder(anthropicChatModel())
                .defaultOptions(anthropicChatOptions().mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

}
