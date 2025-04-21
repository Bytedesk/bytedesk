package com.bytedesk.ai.vector_store.impl;

// import java.util.List;
// import java.util.Map;

// import org.springframework.ai.document.Document;
// import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
// import org.springframework.ai.vectorstore.filter.Filter.Expression;
// import org.springframework.ai.vectorstore.redis.RedisVectorStore;
// import org.springframework.ai.vectorstore.SearchRequest;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.stereotype.Service;

// import com.bytedesk.ai.vector_store.VectorDBService;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * Redis向量数据库服务实现
//  * 使用Spring AI的RedisVectorStore实现向量存储和检索
//  */
// @Slf4j
// @Service
// @ConditionalOnProperty(name = "spring.ai.vectorstore.redis.enabled", havingValue = "true")
// public class RedisVectorDBService implements VectorDBService {
    
//     @Autowired
//     private RedisVectorStore redisVectorStore;
    
//     @Override
//     public int addDocuments(List<Document> documents, List<String> metadataFields) {
//         try {
//             log.debug("Adding {} documents to Redis vector store", documents.size());
//             redisVectorStore.add(documents);
//             return documents.size();
//         } catch (Exception e) {
//             log.error("Error adding documents to Redis vector store", e);
//             return 0;
//         }
//     }
    
//     @Override
//     public List<Document> similaritySearch(String query, int k) {
//         try {
//             log.debug("Performing similarity search in Redis for: {}", query);
//             // Create a SearchRequest with the query and top-k
//             SearchRequest searchRequest = SearchRequest.builder()
//                     .query(query)
//                     .topK(k)
//                     .build();
//             return redisVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error searching in Redis vector store", e);
//             return List.of();
//         }
//     }
    
//     @Override
//     public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
//         try {
//             log.debug("Performing filtered similarity search in Redis for: {} with filter: {}", query, filter);
//             // Create metadata filters for each entry in the filter map
//             FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            
//             // 首先创建基础表达式
//             FilterExpressionBuilder.Op combinedOp = null;
            
//             for (Map.Entry<String, Object> entry : filter.entrySet()) {
//                 FilterExpressionBuilder.Op currentOp = expressionBuilder.eq(entry.getKey(), entry.getValue().toString());
                
//                 if (combinedOp == null) {
//                     combinedOp = currentOp;
//                 } else {
//                     combinedOp = expressionBuilder.and(combinedOp, currentOp);
//                 }
//             }
            
//             // 构建最终表达式
//             Expression filterExpression = combinedOp != null ? combinedOp.build() : null;
            
//             // Create search request with filter
//             SearchRequest searchRequest = SearchRequest.builder()
//                     .query(query)
//                     .topK(k)
//                     .filterExpression(filterExpression)
//                     .build();
                    
//             return redisVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error searching with filter in Redis vector store", e);
//             return List.of();
//         }
//     }
    
//     @Override
//     public List<Document> search(SearchRequest searchRequest) {
//         try {
//             log.debug("Performing advanced search in Redis: {}", searchRequest);
//             return redisVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error performing advanced search in Redis vector store", e);
//             return List.of();
//         }
//     }
    
//     @Override
//     public int delete(List<String> ids) {
//         try {
//             log.debug("Deleting {} documents from Redis vector store", ids.size());
//             redisVectorStore.delete(ids);
//             return ids.size();
//         } catch (Exception e) {
//             log.error("Error deleting documents from Redis vector store", e);
//             return 0;
//         }
//     }
    
//     @Override
//     public boolean clear() {
//         try {
//             log.debug("Clearing Redis vector store");
//             // No direct method to clear all documents, so we use a workaround
//             // We could consider implementing a solution using Redis commands directly
//             // For now, this is a limitation
//             log.warn("Clear operation is not fully supported for Redis vector store");
//             return true;
//         } catch (Exception e) {
//             log.error("Error clearing Redis vector store", e);
//             return false;
//         }
//     }
    
//     @Override
//     public long count() {
//         // Redis vector store doesn't have a direct count method
//         // This would require custom implementation using Redis commands
//         return -1;
//     }
    
//     @Override
//     public String getType() {
//         return "Redis";
//     }
// }
