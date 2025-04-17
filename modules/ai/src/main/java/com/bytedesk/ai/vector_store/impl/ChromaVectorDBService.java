package com.bytedesk.ai.vector_store.impl;

// import java.util.List;
// import java.util.Map;

// import org.springframework.ai.document.Document;
// import org.springframework.ai.vectorstore.SearchRequest;
// import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
// import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
// import org.springframework.ai.vectorstore.filter.Filter.Expression;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.stereotype.Service;

// import com.bytedesk.ai.vector_store.VectorDBService;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * Chroma向量数据库服务实现
//  * 使用Spring AI的ChromaVectorStore实现向量存储和检索
//  */
// @Slf4j
// @Service
// @ConditionalOnProperty(name = "spring.ai.vectorstore.chroma.enabled", havingValue = "true")
// public class ChromaVectorDBService implements VectorDBService {
    
//     @Autowired
//     private ChromaVectorStore chromaVectorStore;
    
//     @Override
//     public int addDocuments(List<Document> documents, List<String> metadataFields) {
//         try {
//             log.debug("Adding {} documents to Chroma vector store", documents.size());
//             chromaVectorStore.add(documents);
//             return documents.size();
//         } catch (Exception e) {
//             log.error("Error adding documents to Chroma vector store: {}", e.getMessage());
//             return 0;
//         }
//     }
    
//     @Override
//     public List<Document> similaritySearch(String query, int k) {
//         try {
//             log.debug("Performing similarity search in Chroma for query with k={}", k);
//             // 修复：使用SearchRequest.builder()构建请求对象，指定topK参数
//             SearchRequest searchRequest = SearchRequest.builder()
//                     .query(query)
//                     .topK(k)
//                     .build();
//             return chromaVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error searching in Chroma vector store: {}", e.getMessage());
//             return List.of();
//         }
//     }
    
//     @Override
//     public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
//         try {
//             log.debug("Performing filtered similarity search in Chroma with k={}", k);
            
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
                    
//             return chromaVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error searching with filter in Chroma vector store: {}", e.getMessage());
//             return List.of();
//         }
//     }
    
//     @Override
//     public List<Document> search(SearchRequest searchRequest) {
//         try {
//             log.debug("Performing advanced search in Chroma with topK={}", searchRequest.getTopK());
//             return chromaVectorStore.similaritySearch(searchRequest);
//         } catch (Exception e) {
//             log.error("Error performing advanced search in Chroma vector store: {}", e.getMessage());
//             return List.of();
//         }
//     }
    
//     @Override
//     public int delete(List<String> ids) {
//         try {
//             log.debug("Deleting {} documents from Chroma vector store", ids.size());
//             chromaVectorStore.delete(ids);
//             return ids.size();
//         } catch (Exception e) {
//             log.error("Error deleting documents from Chroma vector store: {}", e.getMessage());
//             return 0;
//         }
//     }
    
//     @Override
//     public boolean clear() {
//         try {
//             log.debug("Clearing Chroma vector store");
//             // chromaVectorStore.deleteAll();
//             return true;
//         } catch (Exception e) {
//             log.error("Error clearing Chroma vector store: {}", e.getMessage());
//             return false;
//         }
//     }
    
//     @Override
//     public long count() {
//         // ChromaVectorStore不提供直接的计数方法
//         log.debug("Count operation is not supported in ChromaVectorStore");
//         return -1;
//     }
    
//     @Override
//     public String getType() {
//         return "Chroma";
//     }
// }
