/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 15:55:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.tencent;

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
 * 腾讯混元大模型聊天配置
 * https://console.cloud.tencent.com/hunyuan/start#
 * https://cloud.tencent.com/document/product/1729/111007
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.tencent.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAITencentChatConfig {

    @Value("${spring.ai.tencent.base-url:https://api.hunyuan.cloud.tencent.com}")
    private String baseUrl;

    @Value("${spring.ai.tencent.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.tencent.chat.options.model:hunyuan-t1-latest}")
    private String model;

    @Value("${spring.ai.tencent.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("tencentApi")
    OpenAiApi tencentApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("tencentChatOptions")
    OpenAiChatOptions tencentChatOptions() {
        return OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("tencentChatModel")
    OpenAiChatModel tencentChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(tencentApi())
                .defaultOptions(tencentChatOptions())
                .build();
    }

    @Bean("tencentChatClient")
    ChatClient tencentChatClient() {
        return  ChatClient.builder(tencentChatModel())
                .defaultOptions(tencentChatOptions())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

} 