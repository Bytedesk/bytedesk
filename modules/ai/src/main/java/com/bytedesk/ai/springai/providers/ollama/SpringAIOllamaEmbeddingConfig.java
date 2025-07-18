/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 14:03:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * https://ollama.com/
 * https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
 * Ollama Embedding Configuration
 */
@Slf4j
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.ollama.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaEmbeddingConfig {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.embedding.options.model:bge-m3:latest}")
    private String ollamaEmbeddingOptionsModel;

    @Value("${spring.ai.ollama.service.auto-check:true}")
    private Boolean autoCheckService;

    @Bean("bytedeskOllamaEmbeddingApi")
    OllamaApi bytedeskOllamaEmbeddingApi() {
        return OllamaApi.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    @Bean("bytedeskOllamaEmbeddingOptions")
    OllamaOptions bytedeskOllamaEmbeddingOptions() {
        return OllamaOptions.builder()
                .model(ollamaEmbeddingOptionsModel)
                .build();
    }

    @Bean("OllamaEmbeddingModel")
    @ConditionalOnProperty(name = SpringAIModelProperties.EMBEDDING_MODEL, havingValue = SpringAIModels.OLLAMA, matchIfMissing = false)
    OllamaEmbeddingModel ollamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .ollamaApi(bytedeskOllamaEmbeddingApi())
                .defaultOptions(bytedeskOllamaEmbeddingOptions())
                .build();
    }

} 