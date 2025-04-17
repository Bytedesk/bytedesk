package com.bytedesk.ai.vector_store.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.elasticsearch.vectorstore.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.vector_store.VectorDBService;

/**
 * Elasticsearch向量数据库服务实现
 * 使用Spring AI的ElasticsearchVectorStore实现向量存储和检索
 */
@Service
@ConditionalOnProperty(name = "spring.ai.vectorstore.elasticsearch.enabled", havingValue = "true")
public class ElasticsearchVectorDBService implements VectorDBService {
    
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchVectorDBService.class);
    
    @Autowired
    private ElasticsearchVectorStore elasticsearchVectorStore;
    
    @Override
    public int addDocuments(List<Document> documents, List<String> metadataFields) {
        try {
            logger.debug("Adding {} documents to Elasticsearch vector store", documents.size());
            elasticsearchVectorStore.add(documents);
            return documents.size();
        } catch (Exception e) {
            logger.error("Error adding documents to Elasticsearch vector store", e);
            return 0;
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k) {
        try {
            logger.debug("Performing similarity search in Elasticsearch for: {}", query);
            return elasticsearchVectorStore.similaritySearch(query, k);
        } catch (Exception e) {
            logger.error("Error searching in Elasticsearch vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        try {
            logger.debug("Performing filtered similarity search in Elasticsearch for: {} with filter: {}", query, filter);
            return elasticsearchVectorStore.similaritySearch(query, k, filter);
        } catch (Exception e) {
            logger.error("Error searching with filter in Elasticsearch vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> search(SearchRequest searchRequest) {
        try {
            logger.debug("Performing advanced search in Elasticsearch: {}", searchRequest);
            return elasticsearchVectorStore.search(searchRequest);
        } catch (Exception e) {
            logger.error("Error performing advanced search in Elasticsearch vector store", e);
            return List.of();
        }
    }
    
    @Override
    public int delete(List<String> ids) {
        try {
            logger.debug("Deleting {} documents from Elasticsearch vector store", ids.size());
            elasticsearchVectorStore.delete(ids);
            return ids.size();
        } catch (Exception e) {
            logger.error("Error deleting documents from Elasticsearch vector store", e);
            return 0;
        }
    }
    
    @Override
    public boolean clear() {
        try {
            logger.debug("Clearing Elasticsearch vector store");
            elasticsearchVectorStore.deleteAll();
            return true;
        } catch (Exception e) {
            logger.error("Error clearing Elasticsearch vector store", e);
            return false;
        }
    }
    
    @Override
    public long count() {
        // Would require custom implementation using Elasticsearch client
        return -1;
    }
    
    @Override
    public String getType() {
        return "Elasticsearch";
    }
}
