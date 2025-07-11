/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 11:21:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openrouter;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenRouter 聊天配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.openrouter.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOpenrouterChatConfig {

    @Value("${spring.ai.openrouter.base-url:https://api.openrouter.com}")
    private String baseUrl;

    @Value("${spring.ai.openrouter.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.openrouter.chat.options.model:gpt-4o}")
    private String model;

    @Value("${spring.ai.openrouter.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("openrouterApi")
    OpenAiApi openrouterApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("openrouterChatOptions")
    OpenAiChatOptions openrouterChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("openrouterChatModel")
    OpenAiChatModel openrouterChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(openrouterApi())
                .defaultOptions(openrouterChatOptions())
                .build();
    }

    @Bean("openrouterChatClient")
    ChatClient openrouterChatClient() {
        return  ChatClient.builder(openrouterChatModel())
                .defaultOptions(openrouterChatOptions())
                .build();
    }

} 