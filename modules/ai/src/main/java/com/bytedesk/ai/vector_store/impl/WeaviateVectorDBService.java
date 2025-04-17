package com.bytedesk.ai.vector_store.impl;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.vector_store.VectorDBService;

import lombok.extern.slf4j.Slf4j;

/**
 * Weaviate向量数据库服务实现
 * 使用Spring AI的WeaviateVectorStore实现向量存储和检索
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.vectorstore.weaviate.enabled", havingValue = "true")
public class WeaviateVectorDBService implements VectorDBService {
    
    @Autowired
    private WeaviateVectorStore weaviateVectorStore;
    
    @Override
    public int addDocuments(List<Document> documents, List<String> metadataFields) {
        try {
            log.debug("Adding {} documents to Weaviate vector store", documents.size());
            weaviateVectorStore.add(documents);
            return documents.size();
        } catch (Exception e) {
            log.error("Error adding documents to Weaviate vector store: {}", e.getMessage());
            return 0;
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k) {
        try {
            log.debug("Performing similarity search in Weaviate for query with k={}", k);
            // 修复：使用SearchRequest.builder()构建请求对象，指定topK参数
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(k)
                    .build();
            return weaviateVectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            log.error("Error searching in Weaviate vector store: {}", e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        try {
            log.debug("Performing filtered similarity search in Weaviate with k={}", k);
            
            // 使用FilterExpressionBuilder创建过滤表达式
            FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op combinedOp = null;
            
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                FilterExpressionBuilder.Op currentOp;
                
                // 根据值的类型选择适当的过滤方法
                Object value = entry.getValue();
                if (value == null) {
                    currentOp = expressionBuilder.eq(entry.getKey(), null);
                } else if (value instanceof Number) {
                    currentOp = expressionBuilder.eq(entry.getKey(), value);
                } else if (value instanceof Boolean) {
                    currentOp = expressionBuilder.eq(entry.getKey(), value);
                } else {
                    // 默认当作字符串处理
                    currentOp = expressionBuilder.eq(entry.getKey(), value.toString());
                }
                
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
                    
            return weaviateVectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            log.error("Error searching with filter in Weaviate vector store: {}", e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public List<Document> search(SearchRequest searchRequest) {
        try {
            log.debug("Performing advanced search in Weaviate with topK={}", searchRequest.getTopK());
            return weaviateVectorStore.similaritySearch(searchRequest);
        } catch (Exception e) {
            log.error("Error performing advanced search in Weaviate vector store: {}", e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public int delete(List<String> ids) {
        try {
            log.debug("Deleting {} documents from Weaviate vector store", ids.size());
            weaviateVectorStore.delete(ids);
            return ids.size();
        } catch (Exception e) {
            log.error("Error deleting documents from Weaviate vector store", e);
            return 0;
        }
    }
    
    @Override
    public boolean clear() {
        try {
            log.debug("Clearing Weaviate vector store");
            // weaviateVectorStore.deleteAll();
            return true;
        } catch (Exception e) {
            log.error("Error clearing Weaviate vector store", e);
            return false;
        }
    }
    
    @Override
    public long count() {
        // Would require custom implementation using Weaviate client
        log.debug("Count operation is not supported in WeaviateVectorStore");
        return -1;
    }
    
    @Override
    public String getType() {
        return "Weaviate";
    }
}
