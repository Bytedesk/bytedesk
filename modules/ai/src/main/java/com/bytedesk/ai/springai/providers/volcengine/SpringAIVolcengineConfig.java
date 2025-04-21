/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 15:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.volcengine;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "spring.ai.volcengine.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIVolcengineConfig {

    @Value("${spring.ai.volcengine.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${spring.ai.volcengine.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.volcengine.chat.options.model:volcengine-chat}")
    private String model;

    @Value("${spring.ai.volcengine.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("volcengineApi")
    OpenAiApi volcengineApi() {
        // 使用VolcengineApi工厂方法创建API实例，自动配置正确的路径
        return VolcengineApi.create(baseUrl, apiKey);
    }

    @Bean("volcengineChatOptions")
    OpenAiChatOptions volcengineChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("volcengineChatModel")
    OpenAiChatModel volcengineChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(volcengineApi())
                .defaultOptions(volcengineChatOptions())
                .build();
    }

    @Bean("volcengineChatClient")
    ChatClient volcengineChatClient() {
        return  ChatClient.builder(volcengineChatModel())
                .defaultOptions(volcengineChatOptions())
                .build();
    }

}
