/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 09:50:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 13:44:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Ollama接口
 */
@Slf4j
@RestController
@RequestMapping("/springai/ollama")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIOllamaController {

    private final BytedeskProperties bytedeskProperties;
    private final SpringAIOllamaService springAIOllamaService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 方式1：同步调用
     * http://127.0.0.1:9003/springai/ollama/chat/sync?message=hello
     */
    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }

        String response = springAIOllamaService.processPromptSync(message, null, "");
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式2：异步流式调用
     * http://127.0.0.1:9003/springai/ollama/chat/stream?message=hello
     */
    @GetMapping("/chat/stream")
    public Flux<ChatResponse> chatStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!bytedeskProperties.getDebug()) {
            return Flux.empty();
        }

        Prompt prompt = new Prompt(new UserMessage(message));
        OllamaChatModel model = springAIOllamaService.getChatModel();
        if (model != null) {
            return model.stream(prompt);
        } else {
            return Flux.empty();
        }
    }

    /**
     * 方式3：SSE调用
     * http://127.0.0.1:9003/springai/ollama/chat/sse?message=hello
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSSE(@RequestParam(value = "message") String message) {

        if (!bytedeskProperties.getDebug()) {
            return null;
        }

        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时

        executorService.execute(() -> {
            try {
                // springAIOllamaService.processPromptSSE(message, emitter);
            } catch (Exception e) {
                log.error("Error processing SSE request", e);
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
     * 自定义模型参数的调用示例
     * http://127.0.0.1:9003/springai/ollama/chat/custom?message=hello
     */
    @GetMapping("/chat/custom")
    public ResponseEntity<JsonResult<?>> chatCustom(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }

        OllamaChatModel model = springAIOllamaService.getChatModel();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }

        try {
            ChatResponse response = model.call(
                    new Prompt(
                            message,
                            OllamaOptions.builder()
                                    .model("llama2")
                                    .temperature(0.7)
                                    .topP(0.9)
                                    .build()));

            String result = response.getResult().getOutput().getText();
            return ResponseEntity.ok(JsonResult.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 检测Ollama服务是否正常运行
     * http://127.0.0.1:9003/springai/ollama/health
     */
    @GetMapping("/health")
    public ResponseEntity<JsonResult<?>> checkHealth() {

        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
        }

        try {
            boolean isHealthy = springAIOllamaService.isServiceHealthy();
            if (isHealthy) {
                return ResponseEntity.ok(JsonResult.success("Ollama service is running normally", true));
            } else {
                return ResponseEntity.ok(JsonResult.error("Ollama service is not available"));
            }
        } catch (Exception e) {
            log.error("Error checking Ollama health", e);
            return ResponseEntity.ok(JsonResult.error("Failed to check Ollama service: " + e.getMessage()));
        }
    }

    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
