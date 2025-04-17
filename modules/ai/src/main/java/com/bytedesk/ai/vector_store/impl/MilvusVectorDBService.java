package com.bytedesk.ai.vector_store.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.milvus.vectorstore.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.vector_store.VectorDBService;

/**
 * Milvus向量数据库服务实现
 * 使用Spring AI的MilvusVectorStore实现向量存储和检索
 */
@Service
@ConditionalOnProperty(name = "spring.ai.vectorstore.milvus.enabled", havingValue = "true")
public class MilvusVectorDBService implements VectorDBService {
    
    private static final Logger logger = LoggerFactory.getLogger(MilvusVectorDBService.class);
    
    @Autowired
    private MilvusVectorStore milvusVectorStore;
    
    @Override
    public int addDocuments(List<Document> documents, List<String> metadataFields) {
        try {
            logger.debug("Adding {} documents to Milvus vector store", documents.size());
            milvusVectorStore.add(documents);
            return documents.size();
        } catch (Exception e) {
            logger.error("Error adding documents to Milvus vector store", e);
            return 0;
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k) {
        try {
            logger.debug("Performing similarity search in Milvus for: {}", query);
            return milvusVectorStore.similaritySearch(query, k);
        } catch (Exception e) {
            logger.error("Error searching in Milvus vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        try {
            logger.debug("Performing filtered similarity search in Milvus for: {} with filter: {}", query, filter);
            return milvusVectorStore.similaritySearch(query, k, filter);
        } catch (Exception e) {
            logger.error("Error searching with filter in Milvus vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> search(SearchRequest searchRequest) {
        try {
            logger.debug("Performing advanced search in Milvus: {}", searchRequest);
            return milvusVectorStore.search(searchRequest);
        } catch (Exception e) {
            logger.error("Error performing advanced search in Milvus vector store", e);
            return List.of();
        }
    }
    
    @Override
    public int delete(List<String> ids) {
        try {
            logger.debug("Deleting {} documents from Milvus vector store", ids.size());
            milvusVectorStore.delete(ids);
            return ids.size();
        } catch (Exception e) {
            logger.error("Error deleting documents from Milvus vector store", e);
            return 0;
        }
    }
    
    @Override
    public boolean clear() {
        try {
            logger.debug("Clearing Milvus vector store");
            milvusVectorStore.deleteAll();
            return true;
        } catch (Exception e) {
            logger.error("Error clearing Milvus vector store", e);
            return false;
        }
    }
    
    @Override
    public long count() {
        // Would require custom implementation using Milvus client
        return -1;
    }
    
    @Override
    public String getType() {
        return "Milvus";
    }
}
