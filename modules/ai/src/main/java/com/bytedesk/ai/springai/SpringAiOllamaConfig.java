/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 12:17:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Data;

/**
 * https://ollama.com/
 * https://www.promptingguide.ai/
 * https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
 */
@Data
@Configuration
public class SpringAiOllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen2.5:1.5b}")
    private String ollamaChatModel;

    @Value("${spring.ai.ollama.chat.options.numa:false}")
    private boolean ollamaChatNuma;

    @Value("${spring.ai.ollama.embedding.options.model:qwen2.5:1.5b}")
    private String ollamaEmbeddingModel;

    @Bean("ollamaApi")
    OllamaApi ollamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    @Bean("ollamaChatOptions")
    OllamaOptions ollamaChatOptions() {
        return OllamaOptions.builder()
        .model(ollamaChatModel)
        .build();
    }

    @Bean("ollamaEmbeddingOptions")
    OllamaOptions ollamaEmbeddingOptions() {
        return OllamaOptions.builder()
        .model(ollamaEmbeddingModel)
        .build();
    }

    @Primary
    @Bean("ollamaChatModel")
    OllamaChatModel ollamaChatModel(OllamaApi ollamaApi, OllamaOptions ollamaChatOptions) {
        return OllamaChatModel.builder()
        .ollamaApi(ollamaApi)
        .defaultOptions(ollamaChatOptions)
        .build();
    }

    @Primary
    @Bean("ollamaEmbeddingModel")
    OllamaEmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi, OllamaOptions ollamaEmbeddingOptions) {
        return OllamaEmbeddingModel.builder()
        .ollamaApi(ollamaApi)
        .defaultOptions(ollamaEmbeddingOptions)
        .build();
    }

    @Bean("ollamaChatClientBuilder")
    ChatClient.Builder ollamaChatClientBuilder(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel);
    }

    @Primary
    @Bean("ollamaChatClient")
    ChatClient ollamaChatClient(ChatClient.Builder ollamaChatClientBuilder, OllamaOptions ollamaChatOptions) {
        return ollamaChatClientBuilder
                .defaultOptions(ollamaChatOptions)
                .build();
    }

}
