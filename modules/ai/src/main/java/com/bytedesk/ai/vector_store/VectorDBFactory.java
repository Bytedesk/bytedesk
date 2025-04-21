package com.bytedesk.ai.vector_store;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.ApplicationContext;
// import org.springframework.stereotype.Component;

// // import com.bytedesk.ai.vector_store.impl.ChromaVectorDBService;
// import com.bytedesk.ai.vector_store.impl.ElasticsearchVectorDBService;
// // import com.bytedesk.ai.vector_store.impl.MilvusVectorDBService;
// import com.bytedesk.ai.vector_store.impl.RedisVectorDBService;
// // import com.bytedesk.ai.vector_store.impl.WeaviateVectorDBService;

// import jakarta.annotation.PostConstruct;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * 向量数据库工厂
//  * 用于根据配置选择和创建合适的向量数据库服务
//  */
// @Slf4j
// @Component
// public class VectorDBFactory {
        
//     @Autowired
//     private ApplicationContext context;
    
//     @Autowired
//     private RedisVectorDBService redisVectorDBService;
    
//     @Value("${spring.ai.vectorstore.chroma.enabled:false}")
//     private boolean chromaEnabled;
    
//     @Value("${spring.ai.vectorstore.elasticsearch.enabled:false}")
//     private boolean elasticsearchEnabled;
    
//     @Value("${spring.ai.vectorstore.weaviate.enabled:false}")
//     private boolean weaviateEnabled;
    
//     @Value("${spring.ai.vectorstore.milvus.enabled:false}")
//     private boolean milvusEnabled;
    
//     private VectorDBService primaryVectorDBService;
    
//     @PostConstruct
//     public void init() {
//         // 默认使用Redis向量存储
//         this.primaryVectorDBService = redisVectorDBService;
        
//         // 根据配置文件选择向量数据库
//         if (chromaEnabled) {
//             // try {
//             //     this.primaryVectorDBService = context.getBean(ChromaVectorDBService.class);
//             //     log.info("Using Chroma as the primary vector database");
//             // } catch (Exception e) {
//             //     log.warn("Failed to initialize Chroma vector database, falling back to Redis", e);
//             // }
//         } else if (elasticsearchEnabled) {
//             try {
//                 this.primaryVectorDBService = context.getBean(ElasticsearchVectorDBService.class);
//                 log.info("Using Elasticsearch as the primary vector database");
//             } catch (Exception e) {
//                 log.warn("Failed to initialize Elasticsearch vector database, falling back to Redis", e);
//             }
//         } else if (weaviateEnabled) {
//             // try {
//             //     this.primaryVectorDBService = context.getBean(WeaviateVectorDBService.class);
//             //     log.info("Using Weaviate as the primary vector database");
//             // } catch (Exception e) {
//             //     log.warn("Failed to initialize Weaviate vector database, falling back to Redis", e);
//             // }
//         } else if (milvusEnabled) {
//             // try {
//             //     this.primaryVectorDBService = context.getBean(MilvusVectorDBService.class);
//             //     log.info("Using Milvus as the primary vector database");
//             // } catch (Exception e) {
//             //     log.warn("Failed to initialize Milvus vector database, falling back to Redis", e);
//             // }
//         } else {
//             log.info("Using Redis as the primary vector database");
//         }
//     }
    
//     /**
//      * 获取主要的向量数据库服务实现
//      * 
//      * @return 向量数据库服务
//      */
//     public VectorDBService getVectorDBService() {
//         return primaryVectorDBService;
//     }
    
//     /**
//      * 根据类型获取特定的向量数据库服务
//      * 
//      * @param type 向量数据库类型
//      * @return 向量数据库服务
//      */
//     public VectorDBService getVectorDBService(String type) {
//         if (type == null) {
//             return primaryVectorDBService;
//         }
        
//         switch (type.toLowerCase()) {
//             // case "chroma":
//             //     if (chromaEnabled) {
//             //         return context.getBean(ChromaVectorDBService.class);
//             //     }
//             //     break;
//             case "elasticsearch":
//                 if (elasticsearchEnabled) {
//                     return context.getBean(ElasticsearchVectorDBService.class);
//                 }
//                 break;
//             // case "weaviate":
//             //     if (weaviateEnabled) {
//             //         return context.getBean(WeaviateVectorDBService.class);
//             //     }
//             //     break;
//             // case "milvus":
//             //     if (milvusEnabled) {
//             //         return context.getBean(MilvusVectorDBService.class);
//             //     }
//             //     break;
//             case "redis":
//                 return redisVectorDBService;
//             default:
//                 // 默认返回主要向量数据库
//                 break;
//         }
        
//         return primaryVectorDBService;
//     }
// }
