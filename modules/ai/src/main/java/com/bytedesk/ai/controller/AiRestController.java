/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-16 10:40:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-16 11:10:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.springai.openai.SpringAIOpenaiService;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class AiRestController {
    
    @Autowired
    private Optional<SpringAIOpenaiService> openaiService;
    
    @Autowired
    private Optional<OpenAiApi> openAiApi;
    
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
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
        return openAiApi.orElseThrow(() -> new IllegalStateException("OpenAI API not available")).chatCompletionEntity(request);
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
        return openAiApi.orElseThrow(() -> new IllegalStateException("OpenAI API not available")).chatCompletionEntity(request);
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
        return openAiApi.orElseThrow(() -> new IllegalStateException("OpenAI API not available")).chatCompletionStream(request);
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
        // 明确指定泛型类型为String
        return openAiApi.orElseThrow(() -> new IllegalStateException("OpenAI API not available")).embeddings(request);
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
                // 明确指定泛型类型为String
                ResponseEntity<OpenAiApi.EmbeddingList<OpenAiApi.Embedding>> response = 
                    openAiApi.orElseThrow(() -> new IllegalStateException("OpenAI API not available")).embeddings(request);
                
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
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("You are a helpful assistant."));
        messages.add(new UserMessage(content));
        
        Prompt prompt = new Prompt(messages);
        
        ChatResponse response = openaiService
            .map(service -> service.getOpenaiChatModel().call(prompt))
            .orElseThrow(() -> new IllegalStateException("OpenAI service not available"));
            
        return ResponseEntity.ok(response);
    }
    
    /**
     * 流式聊天接口，使用Spring AI的ChatResponse
     * 
     * @param content 聊天内容
     * @return Flux<ChatResponse>
     */
    @PostMapping(path = "/chat/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponse> chatStream(@RequestBody String content) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("You are a helpful assistant."));
        messages.add(new UserMessage(content));
        
        Prompt prompt = new Prompt(messages);
        
        return openaiService
            .map(service -> service.getOpenaiChatModel().stream(prompt))
            .orElse(Flux.empty());
    }
    
    @PreDestroy
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
