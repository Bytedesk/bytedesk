package com.bytedesk.ai.vector_store.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.vector_store.VectorDBService;

/**
 * Redis向量数据库服务实现
 * 使用Spring AI的RedisVectorStore实现向量存储和检索
 */
@Service
@ConditionalOnProperty(name = "spring.ai.vectorstore.redis.enabled", havingValue = "true")
public class RedisVectorDBService implements VectorDBService {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisVectorDBService.class);
    
    @Autowired
    private RedisVectorStore redisVectorStore;
    
    @Override
    public int addDocuments(List<Document> documents, List<String> metadataFields) {
        try {
            logger.debug("Adding {} documents to Redis vector store", documents.size());
            redisVectorStore.add(documents);
            return documents.size();
        } catch (Exception e) {
            logger.error("Error adding documents to Redis vector store", e);
            return 0;
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k) {
        try {
            logger.debug("Performing similarity search in Redis for: {}", query);
            return redisVectorStore.similaritySearch(SearchRequest.defaults().withQuery(query).withTopK(k));
        } catch (Exception e) {
            logger.error("Error searching in Redis vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        try {
            logger.debug("Performing filtered similarity search in Redis for: {} with filter: {}", query, filter);
            Filter metadataFilter = Filter.from(filter);
            return redisVectorStore.similaritySearch(
                SearchRequest.defaults().withQuery(query).withTopK(k).withSimilarityThreshold(0.0).withFilter(metadataFilter)
            );
        } catch (Exception e) {
            logger.error("Error searching with filter in Redis vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> search(SearchRequest searchRequest) {
        try {
            logger.debug("Performing advanced search in Redis: {}", searchRequest);
            return redisVectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            logger.error("Error performing advanced search in Redis vector store", e);
            return List.of();
        }
    }
    
    @Override
    public int delete(List<String> ids) {
        try {
            logger.debug("Deleting {} documents from Redis vector store", ids.size());
            redisVectorStore.delete(ids);
            return ids.size();
        } catch (Exception e) {
            logger.error("Error deleting documents from Redis vector store", e);
            return 0;
        }
    }
    
    @Override
    public boolean clear() {
        try {
            logger.debug("Clearing Redis vector store");
            redisVectorStore.reset();
            return true;
        } catch (Exception e) {
            logger.error("Error clearing Redis vector store", e);
            return false;
        }
    }
    
    @Override
    public long count() {
        // Redis vector store doesn't have a direct count method
        // This would require custom implementation using Redis commands
        return -1;
    }
    
    @Override
    public String getType() {
        return "Redis";
    }
}
