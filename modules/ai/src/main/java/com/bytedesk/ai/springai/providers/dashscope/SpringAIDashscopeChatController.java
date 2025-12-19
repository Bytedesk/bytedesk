/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-13 13:41:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-21 13:33:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * DashScope接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashscope")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.dashscope.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIDashscopeChatController {

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatModel")
    private ChatModel bytedeskDashscopeChatModel;
    private final SpringAIDashscopeChatService springAIDashscopeService;
    private final ChatClient bytedeskDashscopeChatClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 方式1：同步调用
     * http://127.0.0.1:9003/api/v1/dashscope/chat/sync?message=hello
     */
    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {String response = springAIDashscopeService.processPromptSync(message, null);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式2：异步流式调用
     * http://127.0.0.1:9003/api/v1/dashscope/chat/stream?message=hello
     */
    @GetMapping("/chat/stream")
    public Flux<ChatResponse> chatStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {Prompt prompt = new Prompt(new UserMessage(message));
        ChatModel model = bytedeskDashscopeChatModel;
        if (model != null) {
            return model.stream(prompt);
        } else {
            return Flux.empty();
        }
    }

    /**
     * 方式3：SSE调用
     * http://127.0.0.1:9003/api/v1/dashscope/chat/sse?message=hello
     */
    @GetMapping("/chat/sse")
    public SseEmitter chatSse(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {SseEmitter emitter = new SseEmitter();
        
        executorService.execute(() -> {
            try {
                Prompt prompt = new Prompt(new UserMessage(message));
                ChatModel model = bytedeskDashscopeChatModel;
                if (model != null) {
                    model.stream(prompt).subscribe(
                            response -> {
                                try {
                                    emitter.send(response, MediaType.APPLICATION_JSON);
                                } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                                    log.debug("SSE connection no longer usable in Dashscope response: {}", e.getMessage());
                                } catch (Exception e) {
                                    log.error("Error sending SSE event", e);
                                }
                            },
                            error -> {
                                log.error("Error in chat stream", error);
                                try {
                                    emitter.send(SseEmitter.event().name("error").data(error.getMessage()));
                                } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                                    log.debug("SSE connection no longer usable during error: {}", e.getMessage());
                                } catch (Exception e) {
                                    log.error("Error sending error event", e);
                                }
                            },
                            () -> {
                                try {
                                    emitter.send(SseEmitter.event().name("complete").data(""));
                                    emitter.complete();
                                } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                                    log.debug("SSE connection no longer usable during completion: {}", e.getMessage());
                                } catch (Exception e) {
                                    log.error("Error sending complete event", e);
                                }
                            }
                    );
                } else {
                    try {
                        emitter.send(SseEmitter.event().name("error").data("Chat model not available"));
                        emitter.complete();
                    } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
                        log.debug("SSE connection no longer usable: {}", e.getMessage());
                    } catch (Exception e) {
                        log.error("Error sending model unavailable message", e);
                    }
                }
            } catch (Exception e) {
                log.error("Error in SSE execution", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                    emitter.complete();
                } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException ex) {
                    log.debug("SSE connection no longer usable during error handling: {}", ex.getMessage());
                } catch (Exception ex) {
                    log.error("Error sending final error event", ex);
                }
            }
        });
        
        return emitter;
    }

    /**
     * 方式4：自定义参数调用
     * http://127.0.0.1:9003/api/v1/dashscope/chat/custom?message=hello
     */
    @GetMapping("/chat/custom")
    public ResponseEntity<?> chatCustom(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {ChatModel model = bytedeskDashscopeChatModel;
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("DashScope service is not available"));
        }

        try {
            ChatResponse response = model.call(
                new Prompt(
                    message,
                    DashScopeChatOptions.builder()
                        .model("dashscope-chat")
                        .temperature(0.7)
                        .topP(0.9)
                        .build()
                ));
            
            String result = response.getResult().getOutput().getText();
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 方式5：使用 ChatClient 调用演示
     * http://127.0.0.1:9003/api/v1/dashscope/chat/client?message=hello
     */
    @GetMapping("/chat/client")
    public ResponseEntity<JsonResult<?>> chatWithClient(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {try {
            // 使用 ChatClient 进行同步调用
            var response = bytedeskDashscopeChatClient.prompt()
                    .user(message)
                    .call()
                    .chatResponse();
            String result = response.getResult().getOutput().getText();
            
            log.info("ChatClient response: {}", result);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in ChatClient call", e);
            return ResponseEntity.ok(JsonResult.error("ChatClient error: " + e.getMessage()));
        }
    }

    /**
     * 方式6：使用 ChatClient 流式调用演示
     * http://127.0.0.1:9003/api/v1/dashscope/chat/client/stream?message=hello
     */
    @GetMapping("/chat/client/stream")
    public Flux<ChatResponse> chatWithClientStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {try {
            // 使用 ChatClient 进行流式调用
            return bytedeskDashscopeChatClient.prompt()
                    .user(message)
                    .stream()
                    .chatResponse();
        } catch (Exception e) {
            log.error("Error in ChatClient stream", e);
            return Flux.empty();
        }
    }

    /**
     * 方式7：使用 ChatClient 带系统提示的调用演示
     * http://127.0.0.1:9003/api/v1/dashscope/chat/client/system?message=hello
     */
    @GetMapping("/chat/client/system")
    public ResponseEntity<JsonResult<?>> chatWithClientSystem(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
            @RequestParam(value = "systemPrompt", defaultValue = "You are a helpful assistant.") String systemPrompt) {try {
            // 使用 ChatClient 带系统提示的调用
            var response = bytedeskDashscopeChatClient.prompt()
                    .system(systemPrompt)
                    .user(message)
                    .call()
                    .chatResponse();
            String result = response.getResult().getOutput().getText();
            
            log.info("ChatClient with system prompt response: {}", result);
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            log.error("Error in ChatClient system call", e);
            return ResponseEntity.ok(JsonResult.error("ChatClient system error: " + e.getMessage()));
        }
    }

    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
