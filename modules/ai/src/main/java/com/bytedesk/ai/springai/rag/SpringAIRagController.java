/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-18 10:45:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 14:31:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import com.bytedesk.core.config.properties.BytedeskProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RAG知识库问答
 * https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html
 * https://mp.weixin.qq.com/s/ETmbEAE7lNligcM_A_GF8A
 * https://www.xiaohongshu.com/explore/67b422600000000029008c9f?note_flow_source=wechat&xsec_token=CBKBzlIFWDRmY2lKlLXYLivyqzKeFCE-9sd-_DNTLH-rM=
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/rag")
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class SpringAIRagController {

    private final VectorStore vectorStore;

    private final EmbeddingModel embeddingModel;

    private final ChatModel bytedeskZhipuaiChatModel;

    private final ObservationRegistry observationRegistry;

    private final BytedeskProperties bytedeskProperties;

    // rag
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_questionansweradvisor
    // http://127.0.0.1:9003/spring/ai/rag?message=什么时间考试？
    @GetMapping("/rag")
    ResponseEntity<JsonResult<?>> rag(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message, 
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        // 创建qaAdvisor - 使用构建器模式替代已弃用的构造函数
        var qaAdvisor = QuestionAnswerAdvisor.builder(this.vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.8d)
                        // .filterExpression(expression)
                        .topK(6)
                        .build())
                .build();
        // 使用chatClient，添加ObservationRegistry
        ChatResponse response = ChatClient.builder(bytedeskZhipuaiChatModel, observationRegistry, null)
                .build()
                .prompt()
                .advisors(qaAdvisor)
                .user(message)
                .call()
                .chatResponse();
        log.info("response: {}", response);

        // 获取chatResponse中的content
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // filter
    // http://127.0.0.1:9003/spring/ai/filter?message=什么时间考试？
    @GetMapping("/filter")
    ResponseEntity<JsonResult<?>> filter(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        ChatClient chatClient = ChatClient.builder(bytedeskZhipuaiChatModel)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().build())
                        .build())
                .build();

        // Update filter expression at runtime
        String content = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == 'Spring'"))
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(content));
    }

    // naive rag
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_naive_rag
    // http://127.0.0.1:9003/spring/ai/naive-rag?message=什么时间考试？
    @GetMapping("/naive-rag")
    ResponseEntity<JsonResult<?>> naiveRag(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
        // 允许为空
        // .queryAugmenter(ContextualQueryAugmenter.builder()
        // .allowEmptyContext(true)
        // .build())
                .build();

        String answer = ChatClient.builder(bytedeskZhipuaiChatModel)
                .defaultAdvisors(retrievalAugmentationAdvisor)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(answer));
    }

    // advanced rag
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_advanced_rag
    // http://127.0.0.1:9003/spring/ai/advanced-rag?message=什么时间考试？
    @GetMapping("/advanced-rag")
    ResponseEntity<JsonResult<?>> advancedRag(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(ChatClient.builder(bytedeskZhipuaiChatModel).build().mutate())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        String answer = ChatClient.builder(bytedeskZhipuaiChatModel)
                .defaultAdvisors(retrievalAugmentationAdvisor)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(answer));
    }

    // modules

    // 压缩查询 CompressionQueryTransformer
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#modules
    // http://127.0.0.1:9003/spring/ai/compression-query-transformer?message=什么时间考试？
    @GetMapping("/compression-query-transformer")
    ResponseEntity<JsonResult<?>> compressionQueryTransformer(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_compressionquerytransformer
        // 压缩查询

        Query query = Query.builder()
                .text("And what is its second largest city?")
                .history(new UserMessage("What is the capital of Denmark?"),
                        new AssistantMessage("Copenhagen is the capital of Denmark."))
                .build();

        // 压缩历史聊天记录
        // A CompressionQueryTransformer uses a large language model to compress a
        // conversation history and a follow-up query into a standalone query that
        // captures the essence of the conversation.
        CompressionQueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(bytedeskZhipuaiChatModel).build().mutate())
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        // 使用chatClient
        String answer = ChatClient.builder(bytedeskZhipuaiChatModel)
                // .defaultAdvisors(retrievalAugmentationAdvisor)
                .build()
                .prompt()
                .user(transformedQuery.text())
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(answer));
    }

    // 重写查询 RewriteQueryTransformer
    // This transformer is useful when the user query is verbose, ambiguous, or
    // contains irrelevant information that may affect the quality of the search
    // results
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_rewritequerytransformer
    // http://127.0.0.1:9003/spring/ai/rewrite-query-transformer?message=什么时间考试？
    @GetMapping("/rewrite-query-transformer")
    ResponseEntity<JsonResult<?>> rewriteQueryTransformer(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        Query query = new Query("I'm studying machine learning. What is an LLM?");

        // 重写查询
        // A RewriteQueryTransformer uses a large language model to rewrite a user query
        // to provide better results when querying a target system, such as a vector
        // store or a web search engine.
        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(bytedeskZhipuaiChatModel).build().mutate())
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        // 使用chatClient
        String answer = ChatClient.builder(bytedeskZhipuaiChatModel)
                // .defaultAdvisors(retrievalAugmentationAdvisor)
                .build()
                .prompt()
                .user(transformedQuery.text())
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(answer));
    }

    // 翻译重写 TranslationQueryTransformer
    // A TranslationQueryTransformer uses a large language model to translate a
    // query to a target language that is supported by the embedding model used to
    // generate the document embeddings
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_translationquerytransformer
    // http://127.0.0.1:9003/spring/ai/translation-query-transformer?message=什么时间考试？
    @GetMapping("/translation-query-transformer")
    ResponseEntity<JsonResult<?>> translationQueryTransformer(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        Query query = new Query("Hvad er Danmarks hovedstad?");

        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(bytedeskZhipuaiChatModel).build().mutate())
                .targetLanguage("english")
                .build();

        Query transformedQuery = queryTransformer.transform(query);

        // 使用chatClient
        String answer = ChatClient.builder(bytedeskZhipuaiChatModel)
                // .defaultAdvisors(retrievalAugmentationAdvisor)
                .build()
                .prompt()
                .user(transformedQuery.text())
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(answer));
    }

    // 多查询扩写 MultiQueryExpander
    // A MultiQueryExpander uses a large language model to expand a query into
    // multiple
    // semantically diverse variations to capture different perspectives
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_multiqueryexpander
    // http://127.0.0.1:9003/spring/ai/multi-query-expander?message=什么时间考试？
    @GetMapping("/multi-query-expander")
    ResponseEntity<JsonResult<?>> multiQueryExpander(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(ChatClient.builder(bytedeskZhipuaiChatModel).build().mutate())
                .numberOfQueries(3)
                // .includeOriginal(false)
                .build();
        List<Query> queries = queryExpander.expand(new Query("How to run a Spring Boot app?"));

        // 使用chatClient
        String answer = ChatClient.builder(bytedeskZhipuaiChatModel)
                // .defaultAdvisors(retrievalAugmentationAdvisor)
                .build()
                .prompt()
                .user(queries.get(0).text())
                .call()
                .content();

        return ResponseEntity.ok(JsonResult.success(answer));
    }

    // VectorStoreDocumentRetriever
    // A VectorStoreDocumentRetriever retrieves documents from a vector store that
    // are semantically similar to the input query.
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_vectorstoredocumentretriever
    // http://127.0.0.1:9003/spring/ai/vector-store-document-retriever?message=什么时间考试？
    @GetMapping("/vector-store-document-retriever")
    ResponseEntity<JsonResult<?>> vectorStoreDocumentRetriever(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.73)
                .topK(5)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("genre", "fairytale")
                        // .eq("tenant", TenantContextHolder.getTenantIdentifier())
                        .build())
                .build();
        List<Document> documents = retriever.retrieve(new Query("What is the main character of the story?"));
        log.info("documents: {}", documents);

        // 使用chatClient
        // String answer = ChatClient.builder(ollamaChatModel)
        // // .defaultAdvisors(retrievalAugmentationAdvisor)
        // .build()
        // .prompt()
        // .user(message)
        // .call()
        // .content();

        return ResponseEntity.ok(JsonResult.success(documents));
    }

    // 上下文扩展 ContextualQueryAugmenter
    // The ContextualQueryAugmenter augments the user query with contextual data
    // from the content of the provided documents.
    // https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html#_contextualqueryaugmenter
    // http://127.0.0.1:9003/spring/ai/contextual-query-augmenter?message=什么时间考试？
    @GetMapping("/contextual-query-augmenter")
    ResponseEntity<JsonResult<?>> contextualQueryAugmenter(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message,
            @RequestParam(value = "kbUid", defaultValue = "") String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                // .allowEmptyContext(true)
                .build();

        // Query query = new Query("What is the capital of Denmark?");
        // Query augmentedQuery = queryAugmenter.augment(query);

        return ResponseEntity.ok(JsonResult.success(queryAugmenter));
    }

    // 测试向量搜索
    // http://127.0.0.1:9003/spring/ai/search?kbUid=1626719517671552&query=什么时间考试
    @GetMapping("/search")
    ResponseEntity<JsonResult<?>> search(
            @RequestParam(value = "query", defaultValue = "什么时间考试？") String query,
            @RequestParam(value = "kbUid", required = true) String kbUid) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        log.info("搜索向量数据，query: {}, kbUid: {}", query, kbUid);
        // List<String> results = springAIVectorService.searchText(query, kbUid);

        return ResponseEntity.ok(JsonResult.success());
    }

    // 添加一个新的端点，展示如何使用带观察功能的ChatClient
    // http://127.0.0.1:9003/spring/ai/rag/observed?message=什么时间考试？
    @GetMapping("/observed")
    ResponseEntity<JsonResult<?>> observedChat(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        ChatClient chatClient = ChatClient.builder(bytedeskZhipuaiChatModel, observationRegistry, null)
                .build();
                
        ChatResponse response = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();
                
        log.info("Observed chat response: {}", response);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // 查看当前ElasticsearchVectorStore详情
    // http://127.0.0.1:9003/spring/ai/rag/vector-store-info
    @GetMapping("/vector-store-info")
    ResponseEntity<JsonResult<?>> getVectorStoreInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting VectorStore info");
        
        Map<String, Object> vectorStoreInfo = new HashMap<>();
        
        try {
            vectorStoreInfo.put("type", vectorStore.getClass().getSimpleName());
            vectorStoreInfo.put("className", vectorStore.getClass().getName());
            
            // 如果是ElasticsearchVectorStore，获取更详细的信息
            if (vectorStore instanceof ElasticsearchVectorStore) {
                vectorStoreInfo.put("vectorStoreType", "ElasticsearchVectorStore");
                vectorStoreInfo.put("status", "Active");
                // 注意：由于ElasticsearchVectorStore的内部属性可能是私有的，
                // 这里只能获取到公开的信息
                vectorStoreInfo.put("description", "Elasticsearch-based vector store for semantic search");
            } else {
                vectorStoreInfo.put("vectorStoreType", "Unknown");
            }
            
            vectorStoreInfo.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting vector store info: {}", e.getMessage());
            vectorStoreInfo.put("error", e.getMessage());
            vectorStoreInfo.put("status", "Error");
        }
        
        return ResponseEntity.ok(JsonResult.success(vectorStoreInfo));
    }

    // 查看当前EmbeddingModel详情
    // http://127.0.0.1:9003/spring/ai/rag/embedding-model-info
    @GetMapping("/embedding-model-info")
    ResponseEntity<JsonResult<?>> getEmbeddingModelInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting EmbeddingModel info");
        
        Map<String, Object> embeddingModelInfo = new HashMap<>();
        
        try {
            embeddingModelInfo.put("type", embeddingModel.getClass().getSimpleName());
            embeddingModelInfo.put("className", embeddingModel.getClass().getName());
            
            // 获取embedding维度信息
            try {
                // 使用一个简单的测试文本来获取embedding维度
                var testEmbedding = embeddingModel.embed("test");
                embeddingModelInfo.put("dimensions", testEmbedding.length);
                embeddingModelInfo.put("status", "Active");
                embeddingModelInfo.put("testEmbeddingSize", testEmbedding.length);
            } catch (Exception e) {
                log.warn("Could not get embedding dimensions: {}", e.getMessage());
                embeddingModelInfo.put("dimensionsError", e.getMessage());
                embeddingModelInfo.put("status", "Error");
            }
            
            // 根据类名判断embedding模型类型
            String className = embeddingModel.getClass().getSimpleName();
            if (className.contains("Ollama")) {
                embeddingModelInfo.put("modelType", "Ollama Embedding Model");
                embeddingModelInfo.put("description", "Local Ollama embedding model (e.g., bgm-m3)");
            } else if (className.contains("Zhipuai") || className.contains("ZhiPu")) {
                embeddingModelInfo.put("modelType", "Zhipuai Embedding Model");
                embeddingModelInfo.put("description", "Zhipuai cloud embedding model");
            } else if (className.contains("OpenAi")) {
                embeddingModelInfo.put("modelType", "Dashscope Embedding Model");
                embeddingModelInfo.put("description", "Dashscope cloud embedding model");
            } else {
                embeddingModelInfo.put("modelType", "Unknown Embedding Model");
            }
            
            embeddingModelInfo.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting embedding model info: {}", e.getMessage());
            embeddingModelInfo.put("error", e.getMessage());
            embeddingModelInfo.put("status", "Error");
        }
        
        return ResponseEntity.ok(JsonResult.success(embeddingModelInfo));
    }

}
