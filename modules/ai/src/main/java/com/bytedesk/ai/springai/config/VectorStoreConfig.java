package com.bytedesk.ai.springai.config;

import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

    /**
     * Elasticsearch向量存储配置
     * 只有当 embedding 模型可用且 elasticsearch 启用时才创建
     */
    @Bean("elasticsearchVectorStore")
    @ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnBean(EmbeddingModel.class)
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



}