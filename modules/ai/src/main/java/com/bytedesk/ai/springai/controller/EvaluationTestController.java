/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-31 20:54:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-31 20:54:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI回答评测控制器
 * 使用AI模型本身评估AI生成的回答，检测幻觉、评估回答与上下文的相关性
 * https://docs.spring.io/spring-ai/reference/api/testing.html
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/evaluation")
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class EvaluationTestController {

    private final ChatModel primaryChatModel;

    private final ObjectProvider<VectorStore> vectorStoreProvider;

    private final ChatClientBuilderFactory chatClientBuilderFactory;

    private final BytedeskProperties bytedeskProperties;

    // 相关性评测：RAG流程回答评测
    // 评估AI模型的回答相对于用户提问和检索到的上下文是否相关，可用于评估RAG流程质量
    // https://docs.spring.io/spring-ai/reference/api/testing.html#_relevancy_evaluator
    // http://127.0.0.1:9003/spring/ai/api/v1/evaluation/relevancy-rag?message=什么时间考试？
    @GetMapping("/relevancy-rag")
    ResponseEntity<JsonResult<?>> relevancyRag(
            @RequestParam(value = "message", defaultValue = "什么时间考试？") String message) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        VectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null) {
            return ResponseEntity.ok(JsonResult.error("VectorStore is not available"));
        }

        try {
            // 1. RAG流程：检索上下文并生成回答
            RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                    .documentRetriever(VectorStoreDocumentRetriever.builder()
                            .similarityThreshold(0.50)
                            .vectorStore(vectorStore)
                            .build())
                    .build();

            ChatResponse chatResponse = chatClientBuilderFactory.builder(primaryChatModel)
                    .build()
                    .prompt()
                    .advisors(ragAdvisor)
                    .user(message)
                    .call()
                    .chatResponse();

            // 2. 从ChatResponse元数据中获取检索到的上下文文档
            List<Document> contextDocuments = chatResponse
                    .getMetadata()
                    .get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT);
            if (contextDocuments == null) {
                contextDocuments = List.of();
            }

            // 3. 获取AI模型的回答内容
            String responseContent = chatResponse.getResult().getOutput().getText();

            // 4. 使用RelevancyEvaluator评估回答相关性
            RelevancyEvaluator evaluator = new RelevancyEvaluator(
                    chatClientBuilderFactory.builder(primaryChatModel));

            EvaluationRequest evaluationRequest = new EvaluationRequest(
                    // 用户原始提问
                    message,
                    // RAG流程检索到的上下文
                    contextDocuments,
                    // AI模型的回答
                    responseContent);

            EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
            log.info("relevancy evaluation pass: {}, score: {}", evaluationResponse.isPass(),
                    evaluationResponse.getScore());

            Map<String, Object> result = new HashMap<>();
            result.put("query", message);
            result.put("response", responseContent);
            result.put("contextCount", contextDocuments.size());
            result.put("pass", evaluationResponse.isPass());
            result.put("score", evaluationResponse.getScore());
            result.put("feedback", evaluationResponse.getFeedback());

            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("relevancy rag evaluation failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Evaluation failed: " + e.getMessage()));
        }
    }

    // 相关性评测：直接评测指定的问答对
    // http://127.0.0.1:9003/spring/ai/api/v1/evaluation/relevancy?query=什么时间考试？&response=考试时间是下周一开始
    @GetMapping("/relevancy")
    ResponseEntity<JsonResult<?>> relevancy(
            @RequestParam(value = "query", defaultValue = "什么时间考试？") String query,
            @RequestParam(value = "response", defaultValue = "考试时间是下周一开始，持续三天。") String response) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        try {
            RelevancyEvaluator evaluator = new RelevancyEvaluator(
                    chatClientBuilderFactory.builder(primaryChatModel));

            EvaluationRequest evaluationRequest = new EvaluationRequest(query, List.of(), response);

            EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
            log.info("relevancy evaluation pass: {}, score: {}", evaluationResponse.isPass(),
                    evaluationResponse.getScore());

            Map<String, Object> result = new HashMap<>();
            result.put("query", query);
            result.put("response", response);
            result.put("pass", evaluationResponse.isPass());
            result.put("score", evaluationResponse.getScore());
            result.put("feedback", evaluationResponse.getFeedback());

            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("relevancy evaluation failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Evaluation failed: " + e.getMessage()));
        }
    }

    // 事实核查评测：评估AI回答相对于上下文的事实准确性，检测幻觉
    // https://docs.spring.io/spring-ai/reference/api/testing.html#_factcheckingevaluator
    // http://127.0.0.1:9003/spring/ai/api/v1/evaluation/fact-checking?claim=地球是距离太阳第四近的行星&context=地球是距离太阳第三近的行星
    @GetMapping("/fact-checking")
    ResponseEntity<JsonResult<?>> factChecking(
            @RequestParam(value = "claim", defaultValue = "The Earth is the fourth planet from the Sun.") String claim,
            @RequestParam(value = "context", defaultValue = "The Earth is the third planet from the Sun and the only astronomical object known to harbor life.") String context) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        try {
            // claim为待核查的陈述（AI回答），context为参考文档（上下文信息）
            FactCheckingEvaluator evaluator = FactCheckingEvaluator
                    .builder(chatClientBuilderFactory.builder(primaryChatModel))
                    .build();

            EvaluationRequest evaluationRequest = new EvaluationRequest(context, List.of(), claim);

            EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
            log.info("fact checking evaluation pass: {}, score: {}", evaluationResponse.isPass(),
                    evaluationResponse.getScore());

            Map<String, Object> result = new HashMap<>();
            result.put("claim", claim);
            result.put("context", context);
            result.put("pass", evaluationResponse.isPass());
            result.put("score", evaluationResponse.getScore());
            result.put("feedback", evaluationResponse.getFeedback());

            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("fact checking evaluation failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(JsonResult.error("Evaluation failed: " + e.getMessage()));
        }
    }
}
