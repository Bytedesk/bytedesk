/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-04-23 14:25:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-04-23 14:25:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.moonshot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryTemplate;

import com.bytedesk.ai.providers.moonshot.api.MoonshotApi;
import com.bytedesk.ai.providers.moonshot.api.MoonshotChatModel;
import com.bytedesk.ai.providers.moonshot.api.MoonshotChatOptions;
import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;

import io.micrometer.observation.ObservationRegistry;

@Configuration
@ConditionalOnProperty(prefix = "spring.ai.moonshot.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMoonshotChatConfig {

    private final ObjectProvider<ToolCallingManager> toolCallingManagerProvider;

    private final ObjectProvider<RetryTemplate> retryTemplateProvider;

    private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

    public SpringAIMoonshotChatConfig(
            ObjectProvider<ToolCallingManager> toolCallingManagerProvider,
            ObjectProvider<RetryTemplate> retryTemplateProvider,
            ObjectProvider<ObservationRegistry> observationRegistryProvider) {
        this.toolCallingManagerProvider = toolCallingManagerProvider;
        this.retryTemplateProvider = retryTemplateProvider;
        this.observationRegistryProvider = observationRegistryProvider;
    }

    @Value("${spring.ai.moonshot.base-url:https://api.moonshot.cn}")
    private String baseUrl;

    @Value("${spring.ai.moonshot.api-key:sk-xxx}")
    private String apiKey;

    @Value("${spring.ai.moonshot.chat.options.model:kimi-k2.6}")
    private String model;

    @Value("${spring.ai.moonshot.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("moonshotApi")
    MoonshotApi moonshotApi() {
        return MoonshotApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

    @Bean("moonshotChatOptions")
    MoonshotChatOptions moonshotChatOptions() {
        return MoonshotChatOptions.builder()
                .model(model)
                .temperature(SpringAIMoonshotService.normalizeTemperature(model, temperature))
                .build();
    }

    @Bean("moonshotChatModel")
    MoonshotChatModel moonshotChatModel() {
        MoonshotChatModel.Builder builder = MoonshotChatModel.builder()
                .moonshotApi(moonshotApi())
                .defaultOptions(moonshotChatOptions());

        ToolCallingManager toolCallingManager = toolCallingManagerProvider.getIfAvailable();
        if (toolCallingManager != null) {
            builder.toolCallingManager(toolCallingManager);
        }
        RetryTemplate retryTemplate = retryTemplateProvider.getIfAvailable();
        if (retryTemplate != null) {
            builder.retryTemplate(retryTemplate);
        }
        ObservationRegistry observationRegistry = observationRegistryProvider.getIfAvailable();
        if (observationRegistry != null) {
            builder.observationRegistry(observationRegistry);
        }

        return builder.build();
    }

    @Bean("moonshotChatClient")
    ChatClient moonshotChatClient(ChatClientBuilderFactory chatClientBuilderFactory) {
        return chatClientBuilderFactory.builder(moonshotChatModel())
                .defaultOptions(moonshotChatOptions().mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}