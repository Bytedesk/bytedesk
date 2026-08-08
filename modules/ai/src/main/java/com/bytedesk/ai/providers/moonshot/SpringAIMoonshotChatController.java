/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-04-23 15:18:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-04-23 15:18:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.providers.moonshot;

import java.util.concurrent.ExecutorService;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.providers.moonshot.api.MoonshotChatModel;
import com.bytedesk.ai.providers.moonshot.api.MoonshotChatOptions;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/moonshot")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.moonshot.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIMoonshotChatController {

    private final BytedeskProperties bytedeskProperties;
    private final SpringAIMoonshotService springAIMoonshotService;

    @Qualifier("virtualAsyncExecutor")
    private final ExecutorService executorService;

    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!Boolean.TRUE.equals(bytedeskProperties.getDebug())) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        MoonshotChatModel model = springAIMoonshotService.getChatModel();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Moonshot service is not available"));
        }

        String response = springAIMoonshotService.processPromptSync(message, null);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @GetMapping("/chat/stream")
    public Flux<ChatResponse> chatStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!Boolean.TRUE.equals(bytedeskProperties.getDebug())) {
            return Flux.empty();
        }

        MoonshotChatModel model = springAIMoonshotService.getChatModel();
        if (model == null) {
            return Flux.empty();
        }

        Prompt prompt = new Prompt(new UserMessage(message));
        return model.stream(prompt);
    }

    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSSE(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        SseEmitter emitter = new SseEmitter(180_000L);

        if (!Boolean.TRUE.equals(bytedeskProperties.getDebug())) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Service is not available"));
            } catch (Exception e) {
                log.debug("Unable to send moonshot disabled error event", e);
            }
            emitter.complete();
            return emitter;
        }

        MoonshotChatModel model = springAIMoonshotService.getChatModel();
        if (model == null) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Moonshot service is not available"));
            } catch (Exception e) {
                log.debug("Unable to send moonshot unavailable error event", e);
            }
            emitter.complete();
            return emitter;
        }

        executorService.execute(() -> {
            try {
                model.stream(new Prompt(new UserMessage(message)))
                        .doOnNext(response -> {
                            try {
                                emitter.send(SseEmitter.event().name("message").data(response));
                            } catch (Exception sendException) {
                                throw new RuntimeException(sendException);
                            }
                        })
                        .doOnError(emitter::completeWithError)
                        .doOnComplete(emitter::complete)
                        .blockLast();
            } catch (Exception e) {
                log.error("Error processing Moonshot SSE request", e);
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> log.info("Moonshot SSE connection completed"));
        return emitter;
    }

    @GetMapping("/chat/custom")
    public ResponseEntity<JsonResult<?>> chatCustom(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!Boolean.TRUE.equals(bytedeskProperties.getDebug())) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }

        MoonshotChatModel model = springAIMoonshotService.getChatModel();
        if (model == null) {
            return ResponseEntity.ok(JsonResult.error("Moonshot service is not available"));
        }

        try {
            ChatResponse response = model.call(new Prompt(
                    message,
                    MoonshotChatOptions.builder()
                            .model("kimi-k2.6")
                            .temperature(1.0D)
                            .topP(0.95D)
                            .build()));
            return ResponseEntity.ok(JsonResult.success(response.getResult().getOutput().getText()));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }

    public void destroy() {
        // shared virtual executor managed by Spring container
    }
}