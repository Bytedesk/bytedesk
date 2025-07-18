/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 11:46:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.gitee;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gitee 聊天配置
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.gitee.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIGiteeChatConfig {

    @Value("${spring.ai.gitee.base-url:https://api.gitee.com}")
    private String baseUrl;

    @Value("${spring.ai.gitee.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.gitee.chat.options.model:gitee-chat}")
    private String model;

    @Value("${spring.ai.gitee.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("giteeApi")
    OpenAiApi giteeApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("giteeChatOptions")
    OpenAiChatOptions giteeChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("giteeChatModel")
    OpenAiChatModel giteeChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(giteeApi())
                .defaultOptions(giteeChatOptions())
                .build();
    }

    @Bean("giteeChatClient")
    ChatClient giteeChatClient() {
        return  ChatClient.builder(giteeChatModel())
                .defaultOptions(giteeChatOptions())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

} 