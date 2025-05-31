package com.bytedesk.ai.springai.rag;

import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 向量存储配置类
 * 为不同的向量存储服务提供配置
 */
@Slf4j
@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.vectorstore.elasticsearch.index-name}")
    private String elasticsearchIndexName;

    @Value("${spring.ai.vectorstore.elasticsearch.dimensions}")
    private Integer elasticsearchDimensions;

    // @Autowired
    // private JedisProperties jedisProperties;

    // @Autowired
    // private ApplicationContext applicationContext;

    /**
     * Elasticsearch向量存储配置
     * @Qualifier("bytedeskOllamaEmbeddingModel") 
     */
    @Bean("elasticsearchVectorStore")
//     @Primary
    @ConditionalOnProperty(name = "spring.ai.vectorstore.elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
    public ElasticsearchVectorStore elasticsearchVectorStore(RestClient restClient, EmbeddingModel embeddingModel) {
        
        log.info("Configuring ElasticsearchVectorStore with index: {} and dimensions: {}", 
                elasticsearchIndexName, elasticsearchDimensions);
        
        // 创建MetadataField对象，用于定义可搜索的元数据字段
        // var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
        // var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
        // var enabled = MetadataField.text("enabled");
        // var startDate = MetadataField.text("startDate"); 
        // var endDate = MetadataField.text("endDate");
        
        // 创建选项对象
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        options.setIndexName(elasticsearchIndexName);
        options.setDimensions(elasticsearchDimensions);
        // 智谱embedding-v2模型，固定维度为1024
        // ollama bgm-m3模型，固定维度为1024
        // options.setDimensions(1024); // 固定维度为1536
        
        // 使用正确的builder方法调用，提供必需的RestClient和EmbeddingModel参数
        ElasticsearchVectorStore vectorStore = ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(options)
                // .metadataFields(kbUid, fileUid, enabled, startDate, endDate)
                .initializeSchema(true)
                .build();
        
        return vectorStore;
    }

    /**
     * Zhipuai专用的RedisVectorStore配置
     */
    // @Bean("bytedeskZhipuaiRedisVectorStore")
    // @ConditionalOnProperty(name = "spring.ai.vectorstore.redis.initialize-schema",  havingValue = "true", matchIfMissing = false)
    // public RedisVectorStore bytedeskZhipuaiRedisVectorStore(RedisVectorStoreProperties properties) {
    //     try {
    //         var kbUid = MetadataField.text(KbaseConst.KBASE_KB_UID);
    //         var fileUid = MetadataField.text(KbaseConst.KBASE_FILE_UID);
            
    //         var jedisPooled = new JedisPooled(jedisProperties.getHost(),
    //                 jedisProperties.getPort(),
    //                 null,
    //                 jedisProperties.getPassword());
            
    //         var embeddingModel = applicationContext.getBean("bytedeskZhipuaiEmbeddingModel", EmbeddingModel.class);
    //         // 初始化向量库, 创建索引
    //         RedisVectorStore vectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
    //                 .indexName(properties.getIndex())
    //                 .prefix(properties.getPrefix())
    //                 .metadataFields(kbUid, fileUid)
    //                 .initializeSchema(true)
    //                 .build();
                    
    //         try {
    //             vectorStore.afterPropertiesSet();
    //             log.info("Successfully initialized RedisVectorStore with Zhipuai embedding model");
    //             return vectorStore;
    //         } catch (Exception e) {
    //             log.error("Error initializing RedisVectorStore with Zhipuai: {}", e.getMessage());
    //         }
    //     } catch (Exception e) {
    //         log.error("Failed to create RedisVectorStore with Zhipuai: {}", e.getMessage());
    //     }

    //     return null;
    // }

}