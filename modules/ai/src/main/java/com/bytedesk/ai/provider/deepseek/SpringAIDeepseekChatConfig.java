/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-17 11:17:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 11:45:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.deepseek;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;

import io.micrometer.observation.ObservationRegistry;

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

    @Value("${spring.ai.deepseek.chat.options.model:deepseek-v4-flash}")
    private String model;

    @Value("${spring.ai.deepseek.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("deepseekApi")
    DeepSeekApi deepseekApi() {
        return DeepSeekApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("deepseekChatOptions")
    DeepSeekChatOptions deepseekChatOptions() {
        return DeepSeekChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
    }

    /**
     * 注入 Boot 自动配置的 ObservationRegistry，使 deepseek 调用产生
     * gen_ai_client_operation_seconds / gen_ai_client_token_usage_total 指标。
     * 否则 ChatModel 层观测缺失，Grafana Provider 分布 / Token 用量面板无数据。
     */
    @Bean("deepseekChatModel")
    DeepSeekChatModel deepseekChatModel(ObservationRegistry observationRegistry) {
        return DeepSeekChatModel.builder()
                .deepSeekApi(deepseekApi())
                .options(deepseekChatOptions())
                .observationRegistry(observationRegistry)
                .build();
    }

    @Bean("deepseekChatClient")
    ChatClient deepseekChatClient(ChatClientBuilderFactory chatClientBuilderFactory,
            ObservationRegistry observationRegistry) {
        return  chatClientBuilderFactory.builder(deepseekChatModel(observationRegistry))
                .defaultOptions(deepseekChatOptions().mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

} 