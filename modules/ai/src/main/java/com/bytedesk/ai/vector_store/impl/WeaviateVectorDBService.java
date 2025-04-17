package com.bytedesk.ai.vector_store.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.vector_store.VectorDBService;

/**
 * Weaviate向量数据库服务实现
 * 使用Spring AI的WeaviateVectorStore实现向量存储和检索
 */
@Service
@ConditionalOnProperty(name = "spring.ai.vectorstore.weaviate.enabled", havingValue = "true")
public class WeaviateVectorDBService implements VectorDBService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeaviateVectorDBService.class);
    
    @Autowired
    private WeaviateVectorStore weaviateVectorStore;
    
    @Override
    public int addDocuments(List<Document> documents, List<String> metadataFields) {
        try {
            logger.debug("Adding {} documents to Weaviate vector store", documents.size());
            weaviateVectorStore.add(documents);
            return documents.size();
        } catch (Exception e) {
            logger.error("Error adding documents to Weaviate vector store", e);
            return 0;
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k) {
        try {
            logger.debug("Performing similarity search in Weaviate for: {}", query);
            return weaviateVectorStore.similaritySearch(query, k);
        } catch (Exception e) {
            logger.error("Error searching in Weaviate vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        try {
            logger.debug("Performing filtered similarity search in Weaviate for: {} with filter: {}", query, filter);
            
            // 使用FilterExpressionBuilder创建过滤表达式
            FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op combinedOp = null;
            
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                FilterExpressionBuilder.Op currentOp = expressionBuilder.eq(entry.getKey(), entry.getValue().toString());
                
                if (combinedOp == null) {
                    combinedOp = currentOp;
                } else {
                    combinedOp = expressionBuilder.and(combinedOp, currentOp);
                }
            }
            
            // 构建最终表达式
            Expression filterExpression = combinedOp != null ? combinedOp.build() : null;
            
            // 创建搜索请求
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(k)
                    .filterExpression(filterExpression)
                    .build();
                    
            return weaviateVectorStore.search(searchRequest);
        } catch (Exception e) {
            logger.error("Error searching with filter in Weaviate vector store", e);
            return List.of();
        }
    }
    
    @Override
    public List<Document> search(SearchRequest searchRequest) {
        try {
            logger.debug("Performing advanced search in Weaviate: {}", searchRequest);
            return weaviateVectorStore.search(searchRequest);
        } catch (Exception e) {
            logger.error("Error performing advanced search in Weaviate vector store", e);
            return List.of();
        }
    }
    
    @Override
    public int delete(List<String> ids) {
        try {
            logger.debug("Deleting {} documents from Weaviate vector store", ids.size());
            weaviateVectorStore.delete(ids);
            return ids.size();
        } catch (Exception e) {
            logger.error("Error deleting documents from Weaviate vector store", e);
            return 0;
        }
    }
    
    @Override
    public boolean clear() {
        try {
            logger.debug("Clearing Weaviate vector store");
            weaviateVectorStore.deleteAll();
            return true;
        } catch (Exception e) {
            logger.error("Error clearing Weaviate vector store", e);
            return false;
        }
    }
    
    @Override
    public long count() {
        // Would require custom implementation using Weaviate client
        return -1;
    }
    
    @Override
    public String getType() {
        return "Weaviate";
    }
}
