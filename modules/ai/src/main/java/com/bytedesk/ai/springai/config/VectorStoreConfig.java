package com.bytedesk.ai.springai.config;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
// import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
// import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore.MetadataField;
// 更新 ChromaVectorStore 的正确导入
// import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
// import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.elasticsearch.client.RestClient;

import com.bytedesk.core.redis.JedisProperties;
import com.bytedesk.kbase.config.KbaseConst;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;

/**
 * 向量存储配置类
 * 为不同的向量存储服务提供配置
 */
@Slf4j
@Configuration
public class VectorStoreConfig {

    @Autowired
    private JedisProperties jedisProperties;
    
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${spring.ai.vectorstore.elasticsearch.index-name}")
    private String elasticsearchIndexName;

    @Value("${spring.ai.vectorstore.elasticsearch.dimensions}")
    private int elasticsearchDimensions;

    @Value("${spring.ai.vectorstore.weaviate.index-name}")
    private String weaviateIndexName;

    @Value("${spring.ai.vectorstore.weaviate.text-field-name}")
    private String weaviateTextFieldName;

    @Value("${spring.ai.vectorstore.chroma.collection-name}")
    private String chromaCollectionName;

    @Value("${spring.ai.vectorstore.milvus.collection-name}")
    private String milvusCollectionName;

    @Value("${spring.ai.vectorstore.milvus.vector-field}")
    private String milvusVectorField;

    @Value("${spring.ai.vectorstore.milvus.text-field}")
    private String milvusTextField;

    @Value("${spring.ai.vectorstore.milvus.dimensions}")
    private int milvusDimensions;

    /**
     * Elasticsearch向量存储配置
     * 指定使用zhipuaiEmbeddingModel作为嵌入模型
     */
    @Bean
    @ConditionalOnProperty(name = "spring.ai.vectorstore.elasticsearch.enabled", havingValue = "true")
    public ElasticsearchVectorStore elasticsearchVectorStore(
            RestClient restClient,
            @Qualifier("bytedeskZhipuaiEmbeddingModel") EmbeddingModel embeddingModel) {
        
        log.info("Configuring ElasticsearchVectorStore with index: {} and dimensions: {}", 
                elasticsearchIndexName, elasticsearchDimensions);
        
        // 创建选项对象
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        options.setIndexName(elasticsearchIndexName);
        options.setDimensions(elasticsearchDimensions);
        
        // 使用正确的builder方法调用，提供必需的RestClient和EmbeddingModel参数
        ElasticsearchVectorStore vectorStore = ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(options)
                .initializeSchema(true)
                .build();
        
        return vectorStore;
    }
    
    /**
     * Weaviate向量存储配置
     * 指定使用zhipuaiEmbeddingModel作为嵌入模型
     */
    // @Bean
    // @ConditionalOnProperty(name = "spring.ai.vectorstore.weaviate.enabled", havingValue = "true")
    // public WeaviateVectorStore weaviateVectorStore(
    //         io.weaviate.client.WeaviateClient weaviateClient,
    //         @Qualifier("bytedeskZhipuaiEmbeddingModel") EmbeddingModel embeddingModel) {
        
    //     log.info("Configuring WeaviateVectorStore with class name: {} and textFieldName: {}", 
    //             weaviateIndexName, weaviateTextFieldName);
        
    //     // 使用builder模式创建WeaviateVectorStore
    //     return WeaviateVectorStore.builder(weaviateClient, embeddingModel)
    //             .objectClass(weaviateIndexName)
    //             .filterMetadataFields(List.of(
    //                 WeaviateVectorStore.MetadataField.text("kbUid"),
    //                 WeaviateVectorStore.MetadataField.text("fileUid")
    //             ))
    //             .build();
    // }
    
    /**
     * Chroma向量存储配置
     * 指定使用zhipuaiEmbeddingModel作为嵌入模型
     */
    // @Bean
    // @ConditionalOnProperty(name = "spring.ai.vectorstore.chroma.enabled", havingValue = "true")
    // public ChromaVectorStore chromaVectorStore(
    //         @Qualifier("bytedeskZhipuaiEmbeddingModel") EmbeddingModel embeddingModel,
    //         ChromaApi chromaApi) {
        
    //     log.info("Configuring ChromaVectorStore with collection name: {}", chromaCollectionName);
        
    //     // 使用构建器模式创建ChromaVectorStore
    //     return ChromaVectorStore.builder(chromaApi, embeddingModel)
    //             .collectionName(chromaCollectionName)
    //             .initializeSchema(true)
    //             .build();
    // }
    
    /**
     * Milvus向量存储配置
     * 指定使用zhipuaiEmbeddingModel作为嵌入模型
     */
    // @Bean
    // @ConditionalOnProperty(name = "spring.ai.vectorstore.milvus.enabled", havingValue = "true")
    // public MilvusVectorStore milvusVectorStore(
    //         @Qualifier("bytedeskZhipuaiEmbeddingModel") EmbeddingModel embeddingModel,
    //         io.milvus.client.MilvusServiceClient milvusClient) {
        
    //     log.info("Configuring MilvusVectorStore with collection: {}, vectorField: {}, textField: {}, dimensions: {}", 
    //             milvusCollectionName, milvusVectorField, milvusTextField, milvusDimensions);
        
    //     return MilvusVectorStore.builder(milvusClient, embeddingModel)
    //             .collectionName(milvusCollectionName)
    //             .embeddingFieldName(milvusVectorField)
    //             .contentFieldName(milvusTextField)
    //             .embeddingDimension(milvusDimensions)
    //             .initializeSchema(true)
    //             .build();
    // }

    /**
     * 检查Ollama服务是否可用
     * @param baseUrl Ollama服务基地址
     * @param autoCheck 是否自动检查服务
     * @return true if Ollama service is available
     */
    private boolean isOllamaServiceAvailable(String baseUrl, boolean autoCheck) {
        if (!autoCheck) {
            log.info("Ollama service auto-check is disabled");
            return true;
        }
        
        try {
            var restClient = org.springframework.web.client.RestClient.builder()
                .baseUrl(baseUrl)
                .build();
            
            restClient.get()
                .uri("/api/tags")
                .retrieve()
                .toBodilessEntity();
            
            log.info("Ollama service is available at {}", baseUrl);
            return true;
        } catch (Exception e) {
            log.warn("Ollama service is not available at {}: {}", baseUrl, e.getMessage());
            return false;
        }
    }

    /**
     * 创建主要的RedisVectorStore配置
     * 同时支持Ollama和Zhipuai作为嵌入模型
     */
    @Primary
    @Bean("redisVectorStore")
    @ConditionalOnProperty(name = "spring.ai.vectorstore.redis.enabled", havingValue = "true")
    public RedisVectorStore redisVectorStore(RedisVectorStoreProperties properties) {
        try {
            var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
            var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
            
            var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword());

            // 1. 尝试使用Ollama嵌入模型
            if (isOllamaServiceAvailable("http://host.docker.internal:11434", true)) {
                try {
                    var embeddingModel = applicationContext.getBean("bytedeskOllamaEmbeddingModel", EmbeddingModel.class);
                    RedisVectorStore vectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                            .indexName(properties.getIndex())
                            .prefix(properties.getPrefix())
                            .metadataFields(kbUid, fileUid)
                            .initializeSchema(true)
                            .build();
                    
                    vectorStore.afterPropertiesSet();
                    log.info("Successfully initialized RedisVectorStore with Ollama embedding model");
                    return vectorStore;
                } catch (Exception e) {
                    log.warn("Failed to create RedisVectorStore with Ollama embedding model: {}", e.getMessage());
                    // 继续尝试其他模型
                }
            }

            // 2. 尝试使用Zhipuai嵌入模型
            try {
                var embeddingModel = applicationContext.getBean("bytedeskZhipuaiEmbeddingModel", EmbeddingModel.class);
                RedisVectorStore vectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                        .indexName(properties.getIndex())
                        .prefix(properties.getPrefix())
                        .metadataFields(kbUid, fileUid)
                        .initializeSchema(true)
                        .build();
                
                vectorStore.afterPropertiesSet();
                log.info("Successfully initialized RedisVectorStore with Zhipuai embedding model");
                return vectorStore;
            } catch (Exception e) {
                log.warn("Failed to create RedisVectorStore with Zhipuai embedding model: {}", e.getMessage());
            }

            // 3. 使用降级模型
            log.warn("All embedding models failed, using fallback embedding model");
            return createFallbackVectorStore(jedisPooled, properties);

        } catch (Exception e) {
            log.error("Failed to create any RedisVectorStore: {}", e.getMessage());
            return createFallbackVectorStore(new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword()), 
                    properties);
        }
    }

    /**
     * Ollama专用的RedisVectorStore配置
     */
    @Bean("bytedeskOllamaRedisVectorStore")
    @ConditionalOnProperty(name = {"spring.ai.ollama.embedding.enabled", "spring.ai.vectorstore.redis.initialize-schema"}, 
        havingValue = "true", matchIfMissing = false)
    public RedisVectorStore bytedeskOllamaRedisVectorStore(RedisVectorStoreProperties properties) {
        try {
            if (!isOllamaServiceAvailable("http://host.docker.internal:11434", true)) {
                log.warn("Ollama service is not available, using fallback vector store");
                var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                        jedisProperties.getPort(),
                        null,
                        jedisProperties.getPassword());
                return createFallbackVectorStore(jedisPooled, properties);
            }
            
            var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
            var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
            
            var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword());

            var embeddingModel = applicationContext.getBean("bytedeskOllamaEmbeddingModel", EmbeddingModel.class);
            RedisVectorStore vectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                    .indexName(properties.getIndex())
                    .prefix(properties.getPrefix())
                    .metadataFields(kbUid, fileUid)
                    .initializeSchema(true)
                    .build();

            try {
                vectorStore.afterPropertiesSet();
                log.info("Successfully initialized RedisVectorStore with Ollama embedding model");
                return vectorStore;
            } catch (Exception e) {
                log.error("Error initializing RedisVectorStore with Ollama: {}", e.getMessage());
                return createFallbackVectorStore(jedisPooled, properties);
            }

        } catch (Exception e) {
            log.error("Failed to create RedisVectorStore with Ollama: {}", e.getMessage());
            return createFallbackVectorStore(new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword()), 
                    properties);
        }
    }

    /**
     * Zhipuai专用的RedisVectorStore配置
     */
    @Bean("bytedeskZhipuaiRedisVectorStore")
    @ConditionalOnProperty(name = {"spring.ai.zhipuai.embedding.enabled", "spring.ai.vectorstore.redis.initialize-schema"}, 
        havingValue = "true", matchIfMissing = false)
    public RedisVectorStore bytedeskZhipuaiRedisVectorStore(RedisVectorStoreProperties properties) {
        try {
            var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
            var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
            
            var jedisPooled = new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword());
            
            var embeddingModel = applicationContext.getBean("bytedeskZhipuaiEmbeddingModel", EmbeddingModel.class);
            // 初始化向量库, 创建索引
            RedisVectorStore vectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                    .indexName(properties.getIndex())
                    .prefix(properties.getPrefix())
                    .metadataFields(kbUid, fileUid)
                    .initializeSchema(true)
                    .build();
                    
            try {
                vectorStore.afterPropertiesSet();
                log.info("Successfully initialized RedisVectorStore with Zhipuai embedding model");
                return vectorStore;
            } catch (Exception e) {
                log.error("Error initializing RedisVectorStore with Zhipuai: {}", e.getMessage());
                return createFallbackVectorStore(jedisPooled, properties);
            }
        } catch (Exception e) {
            log.error("Failed to create RedisVectorStore with Zhipuai: {}", e.getMessage());
            return createFallbackVectorStore(new JedisPooled(jedisProperties.getHost(),
                    jedisProperties.getPort(),
                    null,
                    jedisProperties.getPassword()), 
                    properties);
        }
    }


}