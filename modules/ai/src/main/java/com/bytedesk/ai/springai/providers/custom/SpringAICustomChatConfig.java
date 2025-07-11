/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 10:15:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.custom;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom AI 聊天配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.custom.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAICustomChatConfig {

    @Value("${spring.ai.custom.base-url:https://api.custom.com}")
    private String baseUrl;

    @Value("${spring.ai.custom.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.custom.chat.options.model:custom-chat}")
    private String model;

    @Value("${spring.ai.custom.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("customApi")
    OpenAiApi customApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("customChatOptions")
    OpenAiChatOptions customChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("customChatModel")
    OpenAiChatModel customChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(customApi())
                .defaultOptions(customChatOptions())
                .build();
    }

    @Bean("customChatClient")
    ChatClient customChatClient() {
        return  ChatClient.builder(customChatModel())
                .defaultOptions(customChatOptions())
                .build();
    }

} 