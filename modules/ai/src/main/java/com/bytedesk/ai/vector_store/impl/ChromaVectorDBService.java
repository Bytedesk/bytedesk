package com.bytedesk.ai.vector_store.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.vector_store.VectorDBService;

/**
 * Chroma向量数据库服务实现
 * 使用Spring AI的ChromaVectorStore实现向量存储和检索
 */
@Service
@ConditionalOnProperty(name = "spring.ai.vectorstore.chroma.enabled", havingValue = "true")
public class ChromaVectorDBService implements VectorDBService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChromaVectorDBService.class);
    
    @Autowired
    private ChromaVectorStore chromaVectorStore;
    
    @Override
    public int addDocuments(List<Document> documents, List<String> metadataFields) {
        try {
            logger.debug("Adding {} documents to Chroma vector store", documents.size());
            chromaVectorStore.add(documents);
            return documents.size();
        } catch (Exception e) {
            logger.error("Error adding documents to Chroma vector store", e);
            return 0;
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k) {
        try {
            logger.debug("Performing similarity search in Chroma for: {}", query);
            return chromaVectorStore.similaritySearch(query, k);
        } catch (Exception e) {
            logger.error("Error searching in Chroma vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        try {
            logger.debug("Performing filtered similarity search in Chroma for: {} with filter: {}", query, filter);
            return chromaVectorStore.similaritySearch(query, k, filter);
        } catch (Exception e) {
            logger.error("Error searching with filter in Chroma vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> search(SearchRequest searchRequest) {
        try {
            logger.debug("Performing advanced search in Chroma: {}", searchRequest);
            return chromaVectorStore.search(searchRequest);
        } catch (Exception e) {
            logger.error("Error performing advanced search in Chroma vector store", e);
            return List.of();
        }
    }
    
    @Override
    public int delete(List<String> ids) {
        try {
            logger.debug("Deleting {} documents from Chroma vector store", ids.size());
            chromaVectorStore.delete(ids);
            return ids.size();
        } catch (Exception e) {
            logger.error("Error deleting documents from Chroma vector store", e);
            return 0;
        }
    }
    
    @Override
    public boolean clear() {
        try {
            logger.debug("Clearing Chroma vector store");
            chromaVectorStore.deleteAll();
            return true;
        } catch (Exception e) {
            logger.error("Error clearing Chroma vector store", e);
            return false;
        }
    }
    
    @Override
    public long count() {
        // Chroma doesn't provide a direct count method
        return -1;
    }
    
    @Override
    public String getType() {
        return "Chroma";
    }
}
