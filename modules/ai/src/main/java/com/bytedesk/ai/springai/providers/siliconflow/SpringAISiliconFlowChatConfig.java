/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 16:29:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.providers.siliconflow;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SiliconFlow 聊天配置
 * @author: https://github.com/fzj111
 * date: 2025-03-19
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.siliconflow.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowChatConfig {

    @Value("${spring.ai.siliconflow.base-url}")
    private String baseUrl;

    @Value("${spring.ai.siliconflow.api-key}")
    private String apiKey;

    @Value("${spring.ai.siliconflow.chat.options.model}")
    private String model;

    @Value("${spring.ai.siliconflow.chat.options.temperature}")
    private Double temperature;

    @Bean("siliconFlowApi")
    public OpenAiApi siliconFlowApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("siliconFlowChatOptions")
    OpenAiChatOptions siliconFlowChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    } 

    @Bean("siliconFlowChatModel")
    OpenAiChatModel siliconFlowChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(siliconFlowApi())
                .defaultOptions(siliconFlowChatOptions())
                .build();
    }

    @Bean("siliconFlowChatClient")
    ChatClient siliconFlowChatClient() {
        return  ChatClient.builder(siliconFlowChatModel())
                .defaultOptions(siliconFlowChatOptions())
                .build();
    }

} 