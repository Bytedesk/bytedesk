/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-16 10:40:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 14:32:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.controller;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@Description("AI REST Controller - AI service APIs for chat completions, embeddings, and streaming responses")
public class AiRestController {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private AiProviderProperties aiProperties;
    
    private OpenAiApi selectedApi;
    
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    @PostConstruct
    public void init() {
        String providerName = aiProperties.getProvider() + "Api";
        log.info("Using AI provider: {}", providerName);
        
        Map<String, OpenAiApi> apiMap = applicationContext.getBeansOfType(OpenAiApi.class);
        // OpenAiApi beans: [baiduApi, deepseekApi, giteeApi, siliconFlowApi, tencentApi, volcengineApi]
        log.info("OpenAiApi beans: {}", apiMap.keySet());
        if (apiMap.containsKey(providerName)) {
            selectedApi = apiMap.get(providerName);
        } else {
            log.warn("Specified provider '{}' not found. Available providers: {}", 
                    providerName, apiMap.keySet());
            // 如果指定的提供商不存在，则使用第一个可用的提供商
            if (!apiMap.isEmpty()) {
                String firstProvider = apiMap.keySet().iterator().next();
                selectedApi = apiMap.get(firstProvider);
                log.info("Falling back to first available provider: {}", firstProvider);
            } else {
                log.error("No OpenAiApi providers available");
            }
        }
    }
    
    /**
     * 文本补全接口
     * 类似OpenAI的completions接口
     * 
     * @param request 补全请求
     * @return 补全响应
     */
    @PostMapping("/completions")
    public ResponseEntity<OpenAiApi.ChatCompletion> createCompletion(
            @RequestBody OpenAiApi.ChatCompletionRequest request) {
        // OpenAI不再支持单独的completions接口，我们使用chat completions代替
        if (selectedApi == null) {
            throw new IllegalStateException("OpenAI API not available");
        }
        return selectedApi.chatCompletionEntity(request);
    }
    
    /**
     * 聊天对话接口
     * 类似OpenAI的chat/completions接口
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat/completions")
    public ResponseEntity<OpenAiApi.ChatCompletion> createChatCompletion(
            @RequestBody OpenAiApi.ChatCompletionRequest request) {
        if (selectedApi == null) {
            throw new IllegalStateException("OpenAI API not available");
        }
        return selectedApi.chatCompletionEntity(request);
    }
    
    /**
     * 聊天对话流式接口
     * 类似OpenAI的chat/completions流式接口
     * 
     * @param request 聊天请求
     * @return SSE流
     */
    @PostMapping(path = "/chat/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<OpenAiApi.ChatCompletionChunk> createChatCompletionStream(
            @RequestBody OpenAiApi.ChatCompletionRequest request) {
        if (selectedApi == null) {
            throw new IllegalStateException("OpenAI API not available");
        }
        return selectedApi.chatCompletionStream(request);
    }
    
    /**
     * 嵌入向量接口
     * 类似OpenAI的embeddings接口
     * 
     * @param request 嵌入请求
     * @return 嵌入响应
     */
    @PostMapping("/embeddings")
    public ResponseEntity<OpenAiApi.EmbeddingList<OpenAiApi.Embedding>> createEmbedding(
            @RequestBody OpenAiApi.EmbeddingRequest<String> request) {
        if (selectedApi == null) {
            throw new IllegalStateException("OpenAI API not available");
        }
        return selectedApi.embeddings(request);
    }
    
    /**
     * 使用SSE的嵌入向量接口
     * 
     * @param request 嵌入请求
     * @return SSE流
     */
    @PostMapping(path = "/embeddings/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createEmbeddingStream(@RequestBody OpenAiApi.EmbeddingRequest<String> request) {
        SseEmitter emitter = new SseEmitter(60_000L); // 1分钟超时
        
        executorService.execute(() -> {
            try {
                if (selectedApi == null) {
                    throw new IllegalStateException("OpenAI API not available");
                }
                // 明确指定泛型类型为String
                ResponseEntity<OpenAiApi.EmbeddingList<OpenAiApi.Embedding>> response = 
                    selectedApi.embeddings(request);
                
                if (response != null && response.getBody() != null && response.getBody().data() != null) {
                    for (OpenAiApi.Embedding embedding : response.getBody().data()) {
                        emitter.send(embedding);
                    }
                }
                
                emitter.complete();
            } catch (Exception e) {
                log.error("Error processing embedding request", e);
                emitter.completeWithError(e);
            }
        });
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.warn("SSE connection timed out");
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
        });
        
        return emitter;
    }
    
    /**
     * 传统的聊天接口，使用Spring AI的ChatResponse
     * 
     * @param content 聊天内容
     * @return ChatResponse
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody String content) {
        return null;
    }
    
    /**
     * 流式聊天接口，使用Spring AI的ChatResponse
     * 
     * @param content 聊天内容
     * @return Flux<ChatResponse>
     */
    @PostMapping(path = "/chat/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponse> chatStream(@RequestBody String content) {
        return null;
    }
    
    @PreDestroy
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
