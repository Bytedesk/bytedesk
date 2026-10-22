/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-20 21:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-20 21:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.aliyun.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;

/**
 * 阿里云百炼 OpenAI 兼容模式聊天配置
 *
 * <p>阿里云百炼（DashScope）的 OpenAI 兼容端点：
 * https://dashscope.aliyuncs.com/compatible-mode/v1，qwen3.8-max / qwen3.8-flash
 * 等最新模型仅支持该协议。本配置与原生 DashScope 协议（DashscopeChatConfig）相互独立。</p>
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.aliyun_openai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIAliyunOpenaiChatConfig {

    @Value("${spring.ai.aliyun_openai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${spring.ai.aliyun_openai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.aliyun_openai.chat.options.model:qwen3.8-flash}")
    private String model;

    @Value("${spring.ai.aliyun_openai.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean("aliyunOpenaiChatOptions")
    OpenAiChatOptions aliyunOpenaiChatOptions() {
        return OpenAiChatOptions.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .model(model)
                .temperature(temperature)
                .build();
    }

    @Bean("aliyunOpenaiChatModel")
    OpenAiChatModel aliyunOpenaiChatModel() {
        return OpenAiChatModel.builder()
                .options(aliyunOpenaiChatOptions())
                .build();
    }

    @Bean("aliyunOpenaiChatClient")
    ChatClient aliyunOpenaiChatClient(ChatClientBuilderFactory chatClientBuilderFactory) {
        return chatClientBuilderFactory.builder(aliyunOpenaiChatModel())
                .defaultOptions(aliyunOpenaiChatOptions().mutate())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

}
