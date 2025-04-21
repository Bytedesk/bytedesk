/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 17:53:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import io.micrometer.observation.ObservationRegistry;

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
    
    @Value("${spring.ai.ollama.model-management.pull-model-strategy:IF_NOT_PRESENT}")
    private String pullModelStrategy;

    @Bean("bytedeskOllamaApi")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaApi bytedeskOllamaApi() {
        return new OllamaApi(ollamaBaseUrl);
    }

    @Bean("bytedeskOllamaChatOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaOptions bytedeskOllamaChatOptions() {
        OllamaOptions options = new OllamaOptions();
        options.setModel(ollamaChatOptionsModel);
        options.setNumPredict(100);
        options.setTemperature(0.0d);
        options.setTopK(40);
        options.setTopP(0.9d);
        
        try {
            if (OllamaOptions.class.getMethod("getToolCallbacks") != null) {
                java.lang.reflect.Method method = OllamaOptions.class.getMethod("setToolCallbacks", java.util.List.class);
                if (method != null) {
                    method.invoke(options, Collections.emptyList());
                }
            }
        } catch (Exception e) {
            log.debug("ToolCallbacks not available in this version of OllamaOptions: {}", e.getMessage());
        }
        return options;
    }

    @Bean("bytedeskOllamaEmbeddingOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    OllamaOptions bytedeskOllamaEmbeddingOptions() {
        OllamaOptions options = new OllamaOptions();
        options.setModel(ollamaEmbeddingOptionsModel);
        try {
            if (OllamaOptions.class.getMethod("getToolCallbacks") != null) {
                java.lang.reflect.Method method = OllamaOptions.class.getMethod("setToolCallbacks", java.util.List.class);
                if (method != null) {
                    method.invoke(options, Collections.emptyList());
                }
            }
        } catch (Exception e) {
            log.debug("ToolCallbacks not available in this version of OllamaOptions: {}", e.getMessage());
        }
        return options;
    }
    
    @Bean
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    ModelManagementOptions bytedeskOllamaModelManagementOptions() {
        PullModelStrategy strategy;
        try {
            strategy = PullModelStrategy.valueOf(pullModelStrategy);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid pull model strategy: {}. Using default WHEN_MISSING", pullModelStrategy);
            strategy = PullModelStrategy.WHEN_MISSING;
        }
        
        // 使用Builder模式创建ModelManagementOptions，提供所有必要的参数
        return ModelManagementOptions.builder()
                .pullModelStrategy(strategy)
                .additionalModels(List.of()) // 空列表
                .timeout(Duration.ofMinutes(5)) // 默认超时时间
                .maxRetries(3) // 设置重试次数
                .build();
    }
    
    @Bean
    ObservationRegistry observationRegistry() {
        return ObservationRegistry.NOOP;
    }

    @Bean("bytedeskOllamaChatModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
    OllamaChatModel bytedeskOllamaChatModel() {
        try {
            OllamaApi api = bytedeskOllamaApi();
            OllamaOptions options = bytedeskOllamaChatOptions();
            ObservationRegistry registry = observationRegistry();
            ModelManagementOptions managementOptions = bytedeskOllamaModelManagementOptions();
            
            // 使用Builder模式创建OllamaChatModel
            return OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(options)
                .observationRegistry(registry)
                .modelManagementOptions(managementOptions)
                .build();
        } catch (Exception e) {
            log.error("Failed to create OllamaChatModel: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Bean
    ToolCallingManager toolCallingManager() {
        return ToolCallingManager.builder().build();
    }

    @Primary
    @Bean("bytedeskOllamaEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = false)
    EmbeddingModel bytedeskOllamaEmbeddingModel() {
        try {
            OllamaApi api = bytedeskOllamaApi();
            OllamaOptions options = bytedeskOllamaEmbeddingOptions();
            ObservationRegistry registry = observationRegistry();
            ModelManagementOptions managementOptions = bytedeskOllamaModelManagementOptions();
            
            return new OllamaEmbeddingModel(api, options, registry, managementOptions);
        } catch (Exception e) {
            log.error("Failed to create OllamaEmbeddingModel: {}", e.getMessage(), e);
            return null;
        }
    }

    // 注意：RedisVectorStore相关配置已移至VectorStoreConfig类中统一管理
}
