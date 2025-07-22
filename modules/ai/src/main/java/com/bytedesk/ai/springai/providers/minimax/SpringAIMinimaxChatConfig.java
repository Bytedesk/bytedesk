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
package com.bytedesk.ai.springai.providers.minimax;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minimax 聊天配置
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.minimax.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMinimaxChatConfig {

    @Value("${spring.ai.minimax.base-url:https://api.minimax.chat}")
    private String baseUrl;

    @Value("${spring.ai.minimax.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.minimax.chat.options.model:minimax-chat}")
    private String model;

    @Value("${spring.ai.minimax.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("minimaxApi")
    MiniMaxApi minimaxApi() {
        return new MiniMaxApi(baseUrl, apiKey);
    }

    @Bean("minimaxChatOptions")
    MiniMaxChatOptions minimaxChatOptions() {
        return MiniMaxChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("minimaxChatModel")
    MiniMaxChatModel minimaxChatModel() {
        return new MiniMaxChatModel(minimaxApi(), minimaxChatOptions());
    }

    @Bean("minimaxChatClient")
    ChatClient minimaxChatClient() {
        return  ChatClient.builder(minimaxChatModel())
                .defaultOptions(minimaxChatOptions())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

} 