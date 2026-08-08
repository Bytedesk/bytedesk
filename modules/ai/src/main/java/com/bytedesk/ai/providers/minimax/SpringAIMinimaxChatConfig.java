/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 08:34:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.minimax;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;

/**
 * Minimax 聊天配置
 * https://docs.spring.io/spring-ai/reference/api/chat/minimax-chat.html
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.minimax.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMinimaxChatConfig {

    @Value("${spring.ai.minimax.base-url:${spring.ai.anthropic.base-url:https://api.minimax.io/anthropic}}")
    private String baseUrl;

    @Value("${spring.ai.minimax.api-key:${spring.ai.anthropic.api-key:}}")
    private String apiKey;

    @Value("${spring.ai.minimax.chat.options.model:${spring.ai.minimax.chat.model:${spring.ai.anthropic.chat.model:${spring.ai.anthropic.chat.options.model:MiniMax-M3}}}}")
    private String model;

    @Value("${spring.ai.minimax.chat.options.temperature:${spring.ai.anthropic.chat.options.temperature:0.7}}")
    private Double temperature;

    @Value("${spring.ai.minimax.chat.options.max-tokens:${spring.ai.anthropic.chat.options.max-tokens:4096}}")
    private Integer maxTokens;

    @Bean("minimaxChatOptions")
    AnthropicChatOptions minimaxChatOptions() {
        return AnthropicChatOptions.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }

    @Bean("minimaxChatModel")
    AnthropicChatModel minimaxChatModel() {
        return AnthropicChatModel.builder()
                .options(minimaxChatOptions())
                .build();
    }

    @Bean("minimaxChatClient")
    ChatClient minimaxChatClient(ChatClientBuilderFactory chatClientBuilderFactory) {
        return  chatClientBuilderFactory.builder(minimaxChatModel())
                .defaultOptions(minimaxChatOptions().mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

} 