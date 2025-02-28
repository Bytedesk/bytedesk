/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 11:46:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "spring.ai.deepseek.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDeepseekConfig {

    @Value("${spring.ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${spring.ai.deepseek.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.deepseek.chat.options.model:deepseek-chat}")
    private String model;

    @Value("${spring.ai.deepseek.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("deepSeekApi")
    OpenAiApi deepSeekApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("deepSeekChatOptions")
    ChatOptions deepSeekChatOptions() {
        return ChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("deepSeekChatModel")
    OpenAiChatModel deepSeekChatModel(OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .build();
    }

    @Bean("deepSeekChatClientBuilder")
    ChatClient.Builder deepSeekChatClientBuilder(OpenAiChatModel deepSeekChatModel) {
        return ChatClient.builder(deepSeekChatModel);
    }

    @Bean("deepSeekChatClient")
    ChatClient deepSeekChatClient(ChatClient.Builder deepSeekChatClientBuilder, ChatOptions deepSeekChatOptions) {
        return deepSeekChatClientBuilder
                .defaultOptions(deepSeekChatOptions)
                .build();
    }

}
