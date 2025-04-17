/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-17 14:47:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.ollama;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Value("${spring.ai.ollama.chat.options.model:qwen2.5:1.5b}")
    private String ollamaChatOptionsModel;

    @Value("${spring.ai.ollama.chat.options.numa:false}")
    private boolean ollamaChatOptionsNuma;

    @Value("${spring.ai.ollama.embedding.options.model:qwen2.5:1.5b}")
    private String ollamaEmbeddingOptionsModel;

    @Value("${spring.ai.ollama.service.auto-check:true}")
    private boolean autoCheckService;

    /**
     * 检查 Ollama 服务是否可用
     * @return true if Ollama service is available
     */
    private boolean isOllamaServiceAvailable() {
        if (!autoCheckService) {
            log.info("Ollama service auto-check is disabled");
            return true;
        }
        
        try {
            var restClient = org.springframework.web.client.RestClient.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
            
            restClient.get()
                .uri("/api/tags")
                .retrieve()
                .toBodilessEntity();
            
            log.info("Ollama service is available at {}", ollamaBaseUrl);
            return true;
        } catch (Exception e) {
            log.warn("Ollama service is not available at {}: {}", ollamaBaseUrl, e.getMessage());
            return false;
        }
    }

    @Bean("bytedeskOllamaApi")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaApi bytedeskOllamaApi() {
        if (!isOllamaServiceAvailable()) {
            log.warn("Ollama service is not available, some features may not work properly");
            return null;
        }
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
        if (!isOllamaServiceAvailable()) {
            log.warn("Ollama service is not available, chat model will not be created");
            return null;
        }
        return OllamaChatModel.builder()
                .ollamaApi(bytedeskOllamaApi())
                .defaultOptions(bytedeskOllamaChatOptions())
                .build();
    }

    @Bean("bytedeskOllamaEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    EmbeddingModel bytedeskOllamaEmbeddingModel() {
        if (!isOllamaServiceAvailable()) {
            log.warn("Creating fallback embedding model");
            return createFallbackEmbeddingModel();
        }
        return OllamaEmbeddingModel.builder()
                .ollamaApi(bytedeskOllamaApi())
                .defaultOptions(bytedeskOllamaEmbeddingOptions())
                .build();
    }

    private EmbeddingModel createFallbackEmbeddingModel() {
        return new EmbeddingModel() {
            private static final int VECTOR_DIMENSIONS = 1536;

            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                log.debug("Using fallback embedding model");
                List<Embedding> embeddings = IntStream.range(0, request.getInstructions().size())
                    .mapToObj(i -> new Embedding(new float[VECTOR_DIMENSIONS], i))
                    .collect(Collectors.toList());
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public float[] embed(Document document) {
                log.debug("Using fallback embedding for document: {}", document.getId());
                float[] vector = new float[VECTOR_DIMENSIONS];
                Arrays.fill(vector, 0.0f);
                return vector;
            }
        };
    }

    // 注意：RedisVectorStore相关配置已移至VectorStoreConfig类中统一管理
}
