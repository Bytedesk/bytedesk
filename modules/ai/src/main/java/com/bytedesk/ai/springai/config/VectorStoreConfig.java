package com.bytedesk.ai.springai.config;

import java.net.URI;
import java.util.Arrays;

import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import lombok.extern.slf4j.Slf4j;

/**
 * 向量存储配置类
 * 为不同的向量存储服务提供配置
 */
@Slf4j
@Configuration
public class VectorStoreConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;

    @Value("${spring.elasticsearch.username:}")
    private String elasticsearchUsername;

    @Value("${spring.elasticsearch.password:}")
    private String elasticsearchPassword;

    @Value("${spring.ai.vectorstore.elasticsearch.index-name}")
    private String elasticsearchIndexName;

    @Value("${spring.ai.vectorstore.elasticsearch.dimensions}")
    private Integer elasticsearchDimensions;

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnMissingBean(RestClient.class)
    public RestClient bytedeskElasticsearchRestClient() {
        log.info("Creating fallback RestClient for Elasticsearch at: {}", elasticsearchUris);
        org.apache.http.HttpHost[] hosts = Arrays.stream(parseUris(elasticsearchUris))
                .map(uri -> new org.apache.http.HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()))
                .toArray(org.apache.http.HttpHost[]::new);
        org.elasticsearch.client.RestClientBuilder builder = RestClient.builder(hosts);
        if (hasCredentials()) {
            org.apache.http.impl.client.BasicCredentialsProvider credentialsProvider = new org.apache.http.impl.client.BasicCredentialsProvider();
            credentialsProvider.setCredentials(org.apache.http.auth.AuthScope.ANY,
                    new org.apache.http.auth.UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return builder.build();
    }

    /**
     * Elasticsearch向量存储配置
     * 只有当 embedding 模型可用且 elasticsearch 启用时才创建
     */
    @Bean("elasticsearchVectorStore")
    @ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnBean(EmbeddingModel.class)
    public ElasticsearchVectorStore elasticsearchVectorStore(EmbeddingModel embeddingModel) {
        
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
        
        Rest5Client rest5Client = createRest5Client();

        ElasticsearchVectorStore vectorStore = ElasticsearchVectorStore.builder(rest5Client, embeddingModel)
                .options(options)
                // .metadataFields(kbUid, fileUid, enabled, startDate, endDate)
                .initializeSchema(true)
                .build();
        
        return vectorStore;
    }

    private Rest5Client createRest5Client() {
        var builder = Rest5Client.builder(parseUris(elasticsearchUris));
        if (hasCredentials()) {
            org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider credentialsProvider = new org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider();
            credentialsProvider.setCredentials(new org.apache.hc.client5.http.auth.AuthScope((String) null, -1),
                    new org.apache.hc.client5.http.auth.UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword.toCharArray()));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return builder.build();
    }

    private URI[] parseUris(String uris) {
        return Arrays.stream(StringUtils.commaDelimitedListToStringArray(uris))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(URI::create)
                .toArray(URI[]::new);
    }

    private boolean hasCredentials() {
        return StringUtils.hasText(elasticsearchUsername) && StringUtils.hasText(elasticsearchPassword);
    }



}