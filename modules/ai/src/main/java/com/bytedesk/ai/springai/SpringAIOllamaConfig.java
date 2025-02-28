/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 10:24:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 10:29:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bytedesk.core.redis.JedisProperties;
import com.bytedesk.kbase.config.KbaseConst;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;
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
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = true)
public class SpringAIOllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen2.5:1.5b}")
    private String ollamaChatModel;

    @Value("${spring.ai.ollama.chat.options.numa:false}")
    private boolean ollamaChatNuma;

    @Value("${spring.ai.ollama.embedding.options.model:qwen2.5:1.5b}")
    private String ollamaEmbeddingModel;

    @Autowired
    private JedisProperties jedisProperties;

    /**
     * 检查 Ollama 服务是否可用
     * @return true if Ollama service is available
     */
    private boolean isOllamaServiceAvailable() {
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

    @Primary
    @Bean("ollamaApi")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = true)
    OllamaApi ollamaApi() {
        if (!isOllamaServiceAvailable()) {
            log.warn("Ollama service is not available, some features may not work properly");
            return null;
        }
        return new OllamaApi(ollamaBaseUrl);
    }

    @Primary
    @Bean("ollamaChatModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = true)
    OllamaChatModel ollamaChatModel() {
        if (!isOllamaServiceAvailable()) {
            log.warn("Ollama service is not available, some features may not work properly");
            return null;
        }
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi())
                .defaultOptions(ollamaChatOptions())
                .build();
    }

    @Primary
    @Bean("ollamaEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = true)
    EmbeddingModel ollamaEmbeddingModel() {
        if (!isOllamaServiceAvailable()) {
            log.warn("Creating fallback embedding model");
            return createFallbackEmbeddingModel();
        }
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi())
                .defaultOptions(ollamaEmbeddingOptions())
                .build();
    }

    @Bean("ollamaChatOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = true)
    OllamaOptions ollamaChatOptions() {
        return OllamaOptions.builder()
                .model(ollamaChatModel)
                .build();
    }

    @Bean("ollamaEmbeddingOptions")
    @ConditionalOnProperty(name = "spring.ai.ollama.embedding.enabled", havingValue = "true", matchIfMissing = true)
    OllamaOptions ollamaEmbeddingOptions() {
        return OllamaOptions.builder()
                .model(ollamaEmbeddingModel)
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

    @Primary
    @Bean("ollamaRedisVectorStore")
    @ConditionalOnProperty(name = {"spring.ai.ollama.embedding.enabled", "spring.ai.vectorstore.redis.initialize-schema"}, 
        havingValue = "true", matchIfMissing = true)
    public RedisVectorStore ollamaRedisVectorStore(EmbeddingModel ollamaEmbeddingModel,
    RedisVectorStoreProperties properties) {
        
        try {
            var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
            var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
            
            var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword());

            RedisVectorStore vectorStore = RedisVectorStore.builder(jedisPooled, ollamaEmbeddingModel)
                    .indexName(properties.getIndex())
                    .prefix(properties.getPrefix())
                    .metadataFields(kbUid, fileUid)
                    .initializeSchema(true)
                    .build();

            try {
                vectorStore.afterPropertiesSet();
                log.info("Successfully initialized RedisVectorStore");
                return vectorStore;
            } catch (Exception e) {
                log.error("Error initializing RedisVectorStore: {}", e.getMessage());
                return createFallbackVectorStore(jedisPooled, properties);
            }

        } catch (Exception e) {
            log.error("Failed to create RedisVectorStore: {}", e.getMessage());
            return createFallbackVectorStore(new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword()), 
                    properties);
        }
    }

    /**
     * 创建一个降级的 VectorStore 实现
     */
    private RedisVectorStore createFallbackVectorStore(JedisPooled jedisPooled, 
            RedisVectorStoreProperties properties) {
        log.warn("Creating fallback RedisVectorStore without embedding model");
        
        // 创建一个简单的 EmbeddingModel 实现，用于降级情况
        EmbeddingModel fallbackModel = new EmbeddingModel() {
            private static final int VECTOR_DIMENSIONS = 1536; // 常用的向量维度

            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                log.warn("Using fallback embedding model for request: {}", request);
                final List<Embedding> embeddings = IntStream.range(0, request.getInstructions().size())
                    .mapToObj(i -> {
                        float[] vector = new float[VECTOR_DIMENSIONS];
                        Arrays.fill(vector, 0.0f);
                        return new Embedding(vector, i);
                    })
                    .collect(Collectors.toList());
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public float[] embed(Document document) {
                log.warn("Using fallback embedding model for document: {}", document.getId());
                // 创建固定维度的零向量
                float[] vector = new float[VECTOR_DIMENSIONS];
                Arrays.fill(vector, 0.0f);
                return vector;
            }

        };

        try {
            var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
            var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);

            RedisVectorStore fallbackStore = RedisVectorStore.builder(jedisPooled, fallbackModel)
                    .indexName(properties.getIndex())
                    .prefix(properties.getPrefix())
                    .metadataFields(kbUid, fileUid)
                    .initializeSchema(false) // 不初始化 schema，避免出错
                    .build();

            log.info("Successfully created fallback RedisVectorStore");
            return fallbackStore;

        } catch (Exception e) {
            log.error("Failed to create fallback RedisVectorStore: {}", e.getMessage());
            // 如果连降级方案都失败了，返回一个空的实现
            return new EmptyRedisVectorStore();
        }
    }

    /**
     * 空的 RedisVectorStore 实现，用于完全降级情况
     */
    private static class EmptyRedisVectorStore extends RedisVectorStore {
        public EmptyRedisVectorStore() {
            super(null);
        }

        @Override
        public void add(List<Document> documents) {
            log.warn("Add operation not supported in fallback mode");
        }

        @Override
        public void delete(List<String> ids) {
            log.warn("Delete operation not supported in fallback mode");
        }

        @Override
        public void afterPropertiesSet() {
            // Do nothing
        }
    }
}
