/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 21:33:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * https://ollama.com/
 * https://www.promptingguide.ai/
 * https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
 */
@Slf4j
@Data
@Configuration
// @Conditional(OllamaAvailableCondition.class)
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen2.5:latest}")
    private String ollamaChatOptionsModel;

    @Value("${spring.ai.ollama.chat.options.numa:false}")
    private boolean ollamaChatOptionsNuma;

    @Value("${spring.ai.ollama.embedding.options.model:bge-m3:latest}")
    private String ollamaEmbeddingOptionsModel;

    @Value("${spring.ai.ollama.service.auto-check:true}")
    private boolean autoCheckService;

    @Bean("bytedeskOllamaApi")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaApi bytedeskOllamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    @Bean("bytedeskOllamaChatOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaOptions bytedeskOllamaChatOptions() {
        return OllamaOptions.builder()
                .model(ollamaChatOptionsModel)
                .build();
    }

    @Bean("bytedeskOllamaEmbeddingOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    OllamaOptions bytedeskOllamaEmbeddingOptions() {
        return OllamaOptions.builder()
                .model(ollamaEmbeddingOptionsModel)
                .build();
    }

    @Bean("bytedeskOllamaChatModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaChatModel bytedeskOllamaChatModel() {
        return OllamaChatModel.builder()
                .ollamaApi(bytedeskOllamaApi())
                .defaultOptions(bytedeskOllamaChatOptions())
                .build();
    }

    @Primary
    @Bean("bytedeskOllamaEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    EmbeddingModel bytedeskOllamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(bytedeskOllamaApi())
                .defaultOptions(bytedeskOllamaEmbeddingOptions())
                .build();
    }

}
