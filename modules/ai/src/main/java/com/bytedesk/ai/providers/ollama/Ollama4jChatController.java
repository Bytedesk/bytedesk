/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 13:45:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 13:28:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.ollama;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JsonResultCodeEnum;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.generate.OllamaGenerateStreamObserver;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.response.OllamaAsyncResultStreamer;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.Options;
import lombok.extern.slf4j.Slf4j;

// https://ollama4j.github.io/ollama4j/apis-generate/generate/
@Slf4j
@RestController
@RequestMapping("/api/v1/ollama4j/chat")
@ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class Ollama4jChatController {

    public Ollama4jChatController(
            @Qualifier("ollama4jApi") Ollama ollama4jApi) {
        this.ollama4jApi = ollama4jApi;
    }

    private final Ollama ollama4jApi;

    @Value("${spring.ai.ollama.chat.model}")
    private String ollamaDefaultModel;

    // 同步接口
    // http://127.0.0.1:9003/ollama4j/chat/sync?message=Tell%20me%20a%20j&apiUrl=http://127.0.0.1:11474&model=llama3
    @RequestMapping("/sync")
    public ResponseEntity<?> getSyncAnswer(OllamaRequest request) {
        //
        OllamaResult result;
        try {
            OllamaGenerateRequest generateRequest = OllamaGenerateRequest.builder()
                    .withModel(request.getModel())
                    .withPrompt(request.getMessage())
                    .withOptions(Options.builder().build())
                    .build();
            result = ollama4jApi.generate(generateRequest, null);

            return ResponseEntity.ok(JsonResult.success(result.getResponse()));
        } catch (Exception e) {
            log.error("Unhandled exception", e);
        }
        return ResponseEntity.ok(JsonResult.error());
    }

    // 同步接口
    // http://127.0.0.1:9003/ollama4j/chat/stream?message=Tell%20me%20a%2joke&apiUrl=http://127.0.0.1:11474&model=llama3
    @RequestMapping(value = "/stream")
    public ResponseEntity<?> getSyncAnswerStream(OllamaRequest request) {
        // define a stream handler (Consumer<String>)
        OllamaGenerateTokenHandler streamHandler = (content) -> {
            log.info("streamHandler: {}", content);
        };
        //
        try {
            OllamaGenerateRequest generateRequest = OllamaGenerateRequest.builder()
                    .withModel(request.getModel())
                    .withPrompt(request.getMessage())
                    .withStreaming(true)
                    .withOptions(Options.builder().build())
                    .build();
            OllamaGenerateStreamObserver observer = new OllamaGenerateStreamObserver(streamHandler, null);
            // Should be called using separate thread to gain non blocking streaming effect.
            OllamaResult result = ollama4jApi.generate(generateRequest, observer);
            log.info("getSyncAnswerStream result: {}", result);
            return ResponseEntity.ok(JsonResult.success(result.getResponse()));
        } catch (Exception e) {
            log.error("Unhandled exception", e);
        }
        return ResponseEntity.ok(JsonResult.error());
    }

    // 使用SSE返回流结果的接口
    // http://127.0.0.1:9003/ollama4j/chat/stream-sse?message=Tell%20me%20a%20joke&apiUrl=http://127.0.0.1:11474&model=llama3
    @RequestMapping(value = "/stream-sse")
    public SseEmitter getStreamAnswerSse(OllamaRequest request) {

        // 创建一个SseEmitter，设置超时时间（例如30分钟）
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 定义一个stream handler来发送SSE事件
        OllamaGenerateTokenHandler streamHandler = (content) -> {
            try {
                log.info("streamHandler: {}", content);
                // 使用SseEmitter发送内容
                emitter.send(SseEmitter.event().data(JsonResult.success(
                        JsonResultCodeEnum.ROBOT_ANSWER_CONTINUE.getName(),
                        JsonResultCodeEnum.ROBOT_ANSWER_CONTINUE.getValue(), content)));

            } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                log.debug("SSE connection no longer usable in Ollama stream handler: {}", e.getMessage());
            } catch (Exception e) {
                // 处理发送事件时的异常，例如客户端断开连接
                log.error("Error in Ollama stream handler", e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception completeException) {
                    log.debug("Failed to complete emitter with error: {}", completeException.getMessage());
                }
            }
        };

        // 在新线程中调用generate方法以避免阻塞
        Thread.startVirtualThread(() -> {
            try {
                // 发送开始事件
                emitter.send(SseEmitter.event().data(JsonResult.success(
                        JsonResultCodeEnum.ROBOT_ANSWER_START.getName(),
                        JsonResultCodeEnum.ROBOT_ANSWER_START.getValue(),
                        JsonResultCodeEnum.ROBOT_ANSWER_START.getName())));

                OllamaGenerateRequest generateRequest = OllamaGenerateRequest.builder()
                        .withModel(request.getModel())
                        .withPrompt(request.getMessage())
                        .withStreaming(true)
                        .withOptions(Options.builder().build())
                        .build();
                OllamaGenerateStreamObserver observer = new OllamaGenerateStreamObserver(streamHandler, null);
                ollama4jApi.generate(generateRequest, observer);

                // 发送完成事件
                emitter.send(SseEmitter.event().data(JsonResult.success(
                        JsonResultCodeEnum.ROBOT_ANSWER_END.getName(),
                        JsonResultCodeEnum.ROBOT_ANSWER_END.getValue(),
                        JsonResultCodeEnum.ROBOT_ANSWER_END.getName())));
                // 完成SseEmitter
                emitter.complete();
            } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                log.debug("SSE connection no longer usable in Ollama thread: {}", e.getMessage());
            } catch (Exception e) {
                // 处理异常，并完成SseEmitter
                log.error("Error generating content with Ollama", e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception completeException) {
                    log.debug("Failed to complete emitter with error: {}", completeException.getMessage());
                }
            }
        });

        // 返回SseEmitter以开始发送事件
        return emitter;
    }

    // 异步接口
    // http://127.0.0.1:9003/ollama4j/chat/async?message=Tell%20me%20a%2joke&apiUrl=http://127.0.0.1:11474&model=llama3
    @GetMapping("/async")
    public ResponseEntity<?> getAsyncAnswer(OllamaRequest request)
            throws OllamaException, InterruptedException {

        OllamaAsyncResultStreamer streamer = ollama4jApi.generateAsync(request.getModel(), request.getMessage(), false, null);
        // Set the poll interval according to your needs.
        // Smaller the poll interval, more frequently you receive the tokens.
        int pollIntervalMilliseconds = 1000;
        while (true) {
            String tokens = streamer.getResponseStream().poll();
            log.info("getAsyncAnswer tokens {}", tokens);
            if (!streamer.isAlive()) {
                break;
            }
            Thread.sleep(pollIntervalMilliseconds);
        }
        log.info("Complete Response {}", streamer.getCompleteResponse());

        return ResponseEntity.ok(JsonResult.success(streamer.getCompleteResponse()));
    }

    // 添加-聊天上下文
    // https://ollama4j.github.io/ollama4j/apis-generate/chat
    // http://127.0.0.1:9003/ollama4j/chat/context?message=Tell%20me%20a%2joke&apiUrl=http://127.0.0.1:11474&model=llama3
    @GetMapping("/context")
    public ResponseEntity<?> getChatWithContext(OllamaRequest request)
            throws OllamaException, IOException, InterruptedException {
        // create first user question
        OllamaChatRequest requestModel = OllamaChatRequest.builder()
                .withModel(request.getModel())
                .withMessage(OllamaChatMessageRole.USER, "What is the capital of France?")
                .build();
        // start conversation with model
        OllamaChatResult chatResult;
        try {

            // "start" conversation with model
            chatResult = ollama4jApi.chat(requestModel, null);

            log.info("First answer: {}", chatResult);

            // create next userQuestion
            requestModel = OllamaChatRequest.builder()
                    .withModel(request.getModel())
                    .withMessages(chatResult.getChatHistory())
                    .withMessage(OllamaChatMessageRole.USER, "And what is the second largest city?")
                    .build();
            // "continue" conversation with model
            chatResult = ollama4jApi.chat(requestModel, null);
            log.info("Second answer: {}", chatResult);
            log.info("Chat History: {}", chatResult.getChatHistory());

            return ResponseEntity.ok(JsonResult.success(chatResult.toString()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("Unhandled exception", e);
        }

        return null;
    }

}
