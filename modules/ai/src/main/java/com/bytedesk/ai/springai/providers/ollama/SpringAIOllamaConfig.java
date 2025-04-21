/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 17:23:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import java.util.Collections;

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
        // 使用安全的方式创建 OllamaOptions，确保所有必要的属性都有值
        return new OllamaOptions.Builder()
                .withModel(ollamaChatOptionsModel)
                .withNumPredict(100)
                .withTemperature(0.7f)
                .withTopK(40)
                .withTopP(0.9f)
                .withSystemPrompt("You are a helpful assistant.")  
                // 确保设置为空列表而不是null
                .withFunctionCallbacks(Collections.emptyList())
                .build();
    }

    @Bean("bytedeskOllamaEmbeddingOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    OllamaOptions bytedeskOllamaEmbeddingOptions() {
        return new OllamaOptions.Builder()
                .withModel(ollamaEmbeddingOptionsModel)
                .withFunctionCallbacks(Collections.emptyList())
                .build();
    }

    @Bean("bytedeskOllamaChatModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaChatModel bytedeskOllamaChatModel() {
        try {
            return new OllamaChatModel(bytedeskOllamaApi(), bytedeskOllamaChatOptions());
        } catch (Exception e) {
            log.error("Failed to create OllamaChatModel: {}", e.getMessage());
            return null;
        }
    }

    @Primary
    @Bean("bytedeskOllamaEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    EmbeddingModel bytedeskOllamaEmbeddingModel() {
        return new OllamaEmbeddingModel(bytedeskOllamaApi(), bytedeskOllamaEmbeddingOptions());
    }

    // 注意：RedisVectorStore相关配置已移至VectorStoreConfig类中统一管理

        /**
     * 检查 Ollama 服务是否可用
     * @return true if Ollama service is available
     */
    // private boolean isOllamaServiceAvailable() {
    //     if (!autoCheckService) {
    //         log.info("Ollama service auto-check is disabled");
    //         return true;
    //     }
        
    //     try {
    //         var restClient = org.springframework.web.client.RestClient.builder()
    //             .baseUrl(ollamaBaseUrl)
    //             .build();
            
    //         restClient.get()
    //             .uri("/api/tags")
    //             .retrieve()
    //             .toBodilessEntity();
            
    //         log.info("Ollama service is available at {}", ollamaBaseUrl);
    //         return true;
    //     } catch (Exception e) {
    //         log.warn("Ollama service is not available at {}: {}", ollamaBaseUrl, e.getMessage());
    //         return false;
    //     }
    // }
}
