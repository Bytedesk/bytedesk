/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-11 09:27:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import org.springframework.ai.chat.client.ChatClient;
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
@ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen3:0.6b}")
    private String ollamaChatOptionsModel;

    @Value("${spring.ai.ollama.chat.options.numa:false}")
    private Boolean ollamaChatOptionsNuma;

    @Value("${spring.ai.ollama.embedding.options.model:bge-m3:latest}")
    private String ollamaEmbeddingOptionsModel;

    @Value("${spring.ai.ollama.service.auto-check:true}")
    private Boolean autoCheckService;

    @Bean("bytedeskOllamaApi")
    OllamaApi bytedeskOllamaApi() {
        return OllamaApi.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    @Bean("bytedeskOllamaChatOptions")
    OllamaOptions bytedeskOllamaChatOptions() {
        return OllamaOptions.builder()
                .model(ollamaChatOptionsModel)
                // 使用keepAlive而不是timeout来设置超时
                .keepAlive("30s") // 使用30秒的超时设置
                // .numKeep(0)  // 不保留历史对话
                .useNUMA(ollamaChatOptionsNuma) // 使用正确的方法名useNUMA而不是numa
                .build();
    }

    @Bean("bytedeskOllamaEmbeddingOptions")
    OllamaOptions bytedeskOllamaEmbeddingOptions() {
        return OllamaOptions.builder()
                .model(ollamaEmbeddingOptionsModel)
                .build();
    }

    @Primary
    @Bean("bytedeskOllamaChatModel")
    OllamaChatModel bytedeskOllamaChatModel() {
        return OllamaChatModel.builder()
                .ollamaApi(bytedeskOllamaApi())
                .defaultOptions(bytedeskOllamaChatOptions())
                .build();
    }

    @Bean("bytedeskOllamaEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    EmbeddingModel bytedeskOllamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(bytedeskOllamaApi())
                .defaultOptions(bytedeskOllamaEmbeddingOptions())
                .build();
    }

    // https://docs.spring.io/spring-ai/reference/api/chatclient.html
    @Primary
    @Bean("bytedeskOllamaChatClient")
    ChatClient bytedeskOllamaChatClient() {
        return ChatClient.builder(bytedeskOllamaChatModel())
                // .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                // qwen3: 在末尾添加 /no_think 禁用思考 or /think 开启思考，默认开启
                .defaultSystem("You are a helpful assistant. Answer questions concisely and clearly.")
                .build();
    }

}
