/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 12:26:42
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://deepseek.com/
 * 复用 openai 的配置
 */
@Configuration
public class SpringAiDeepseekConfig {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

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
                .model("deepseek-chat")
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
