/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 11:37:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.deepseek;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Deepseek 聊天配置
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.deepseek.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDeepseekChatConfig {

    @Value("${spring.ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${spring.ai.deepseek.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.deepseek.chat.options.model:deepseek-chat}")
    private String model;

    @Value("${spring.ai.deepseek.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("deepseekApi")
    OpenAiApi deepseekApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("deepseekChatOptions")
    OpenAiChatOptions deepseekChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("deepseekChatModel")
    OpenAiChatModel deepseekChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(deepseekApi())
                .defaultOptions(deepseekChatOptions())
                .build();
    }

    @Bean("deepseekChatClient")
    ChatClient deepseekChatClient() {
        return  ChatClient.builder(deepseekChatModel())
                .defaultOptions(deepseekChatOptions())
                .build();
    }

} 