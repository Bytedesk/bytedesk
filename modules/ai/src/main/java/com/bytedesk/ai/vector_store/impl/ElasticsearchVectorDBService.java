package com.bytedesk.ai.vector_store.impl;

// import java.util.List;
// import java.util.Map;

// import org.springframework.ai.document.Document;
// import org.springframework.ai.vectorstore.SearchRequest;
// import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
// import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
// import org.springframework.ai.vectorstore.filter.Filter.Expression;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.stereotype.Service;

// import com.bytedesk.ai.vector_store.VectorDBService;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * Elasticsearch向量数据库服务实现
//  * 使用Spring AI的ElasticsearchVectorStore实现向量存储和检索
//  * 
//  * https://java2ai.com/blog/spring-ai-alibaba-rag-ollama/?spm=0.29160081.0.0.5ecc25fb30LArO
//  */
// @Slf4j
// @Service
// @ConditionalOnProperty(name = "spring.ai.vectorstore.elasticsearch.enabled", havingValue = "true")
// public class ElasticsearchVectorDBService implements VectorDBService {
    
//     @Autowired
//     private ElasticsearchVectorStore elasticsearchVectorStore;
    
//     @Override
//     public int addDocuments(List<Document> documents, List<String> metadataFields) {
//         try {
//             log.debug("Adding {} documents to Elasticsearch vector store", documents.size());
//             elasticsearchVectorStore.add(documents);
//             return documents.size();
//         } catch (Exception e) {
//             log.error("Error adding documents to Elasticsearch vector store: {}", e.getMessage());
//             return 0;
//         }
//     }
    
//     @Override
//     public List<Document> similaritySearch(String query, int k) {
//         try {
//             log.debug("Performing similarity search in Elasticsearch for query with k={}", k);
//             // 修复：使用SearchRequest.builder()构建请求对象，指定topK参数
//             SearchRequest searchRequest = SearchRequest.builder()
//                     .query(query)
//                     .topK(k)
//                     .build();
//             return elasticsearchVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error searching in Elasticsearch vector store: {}", e.getMessage());
//             return List.of();
//         }
//     }
    
//     @Override
//     public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
//         try {
//             log.debug("Performing filtered similarity search in Elasticsearch with k={}", k);
            
//             // 使用FilterExpressionBuilder创建过滤表达式
//             FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
//             FilterExpressionBuilder.Op combinedOp = null;
            
//             for (Map.Entry<String, Object> entry : filter.entrySet()) {
//                 FilterExpressionBuilder.Op currentOp;
                
//                 // 根据值的类型选择适当的过滤方法
//                 Object value = entry.getValue();
//                 if (value == null) {
//                     currentOp = expressionBuilder.eq(entry.getKey(), null);
//                 } else if (value instanceof Number) {
//                     currentOp = expressionBuilder.eq(entry.getKey(), value);
//                 } else if (value instanceof Boolean) {
//                     currentOp = expressionBuilder.eq(entry.getKey(), value);
//                 } else {
//                     // 默认当作字符串处理
//                     currentOp = expressionBuilder.eq(entry.getKey(), value.toString());
//                 }
                
//                 if (combinedOp == null) {
//                     combinedOp = currentOp;
//                 } else {
//                     combinedOp = expressionBuilder.and(combinedOp, currentOp);
//                 }
//             }
            
//             // 构建最终表达式
//             Expression filterExpression = combinedOp != null ? combinedOp.build() : null;
            
//             // 创建搜索请求
//             SearchRequest searchRequest = SearchRequest.builder()
//                     .query(query)
//                     .topK(k)
//                     .filterExpression(filterExpression)
//                     .build();
                    
//             return elasticsearchVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error searching with filter in Elasticsearch vector store: {}", e.getMessage());
//             return List.of();
//         }
//     }
    
//     @Override
//     public List<Document> search(SearchRequest searchRequest) {
//         try {
//             log.debug("Performing advanced search in Elasticsearch with topK={}", searchRequest.getTopK());
//             return elasticsearchVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error performing advanced search in Elasticsearch vector store: {}", e.getMessage());
//             return List.of();
//         }
//     }
    
//     @Override
//     public int delete(List<String> ids) {
//         try {
//             log.debug("Deleting {} documents from Elasticsearch vector store", ids.size());
//             elasticsearchVectorStore.delete(ids);
//             return ids.size();
//         } catch (Exception e) {
//             log.error("Error deleting documents from Elasticsearch vector store", e);
//             return 0;
//         }
//     }
    
//     @Override
//     public boolean clear() {
//         try {
//             log.debug("Clearing Elasticsearch vector store");
//             // elasticsearchVectorStore.deleteAll();
//             return true;
//         } catch (Exception e) {
//             log.error("Error clearing Elasticsearch vector store", e);
//             return false;
//         }
//     }
    
//     @Override
//     public long count() {
//         // Would require custom implementation using Elasticsearch client
//         log.debug("Count operation is not supported in ElasticsearchVectorStore");
//         return -1;
//     }
    
//     @Override
//     public String getType() {
//         return "Elasticsearch";
//     }
// }
