/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-20 17:32:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeConfig {

    @Value("${spring.ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode}")
    private String baseUrl;

    @Value("${spring.ai.dashscope.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.dashscope.chat.options.model:deepseek-r1}")
    private String model;

    @Value("${spring.ai.dashscope.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("dashscopeApi")
    OpenAiApi dashscopeApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("dashscopeChatOptions")
    OpenAiChatOptions dashscopeChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("dashscopeChatModel")
    OpenAiChatModel dashscopeChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(dashscopeApi())
                .defaultOptions(dashscopeChatOptions())
                .build();
    }

    @Bean("dashscopeChatClient")
    ChatClient dashscopeChatClient() {
        return  ChatClient.builder(dashscopeChatModel())
                .defaultOptions(dashscopeChatOptions())
                .build();
    }

}
