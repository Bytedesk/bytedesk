/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 11:46:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;

/**
 * OpenAI 聊天配置
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.openai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOpenaiChatConfig {

    @Value("${spring.ai.openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model:gpt-4o}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("openaiChatOptions")
    OpenAiChatOptions openaiChatOptions() {
        return OpenAiChatOptions.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("openaiChatModel")
    OpenAiChatModel openaiChatModel() {
        return OpenAiChatModel.builder()
                .options(openaiChatOptions())
                .build();
    }

    @Bean("openaiChatClient")
    ChatClient openaiChatClient(ChatClientBuilderFactory chatClientBuilderFactory) {
        return  chatClientBuilderFactory.builder(openaiChatModel())
                .defaultOptions(openaiChatOptions().mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

} 