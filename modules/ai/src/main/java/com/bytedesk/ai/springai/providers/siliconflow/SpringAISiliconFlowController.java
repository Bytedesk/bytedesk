/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-13 13:41:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 16:37:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.providers.siliconflow;

import com.bytedesk.core.utils.JsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
 * @author https://github.com/fzj111
 * date: 2025-03-19
 * 
 * siliconFlow
 * https://docs.spring.io/spring-ai/reference/api/chat/siliconFlow-chat.html#chat-options
 * 参考：
 * https://github.com/spring-projects/spring-ai/blob/main/models/spring-ai-openai/src/test/java/org/springframework/ai/openai/chat/proxy/siliconFlowWithOpenAiChatModelIT.java
 */
@Slf4j
@RestController
@RequestMapping("/springai/siliconflow")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.siliconflow.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowController {

    private final SpringAISiliconFlowService springAISiliconFlowService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 方式1：同步调用
     * http://127.0.0.1:9003/springai/deepseek/chat/sync?message=hello
     */
    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String response = springAISiliconFlowService.processPromptSync(message);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式2：异步流式调用
     * http://127.0.0.1:9003/springai/deepseek/chat/stream?message=hello
     */
    @GetMapping("/chat/stream")
    public Flux<ChatResponse> chatStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return springAISiliconFlowService.getSiliconFlowChatModel()
                .map(model -> model.stream(prompt))
                .orElse(Flux.empty());
    }

    /**
     * 方式3：SSE调用
     * http://127.0.0.1:9003/springai/deepseek/chat/sse?message=hello
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSSE(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时

        executorService.execute(() -> {
            try {
                // springAISiliconFlowService.processPromptSSE(message, emitter);
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
     * http://127.0.0.1:9003/springai/deepseek/chat/custom?message=hello
     */
    @GetMapping("/chat/custom")
    public ResponseEntity<?> chatCustom(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        if (!springAISiliconFlowService.getSiliconFlowChatModel().isPresent()) {
            return ResponseEntity.ok(JsonResult.error("DeepSeek service is not available"));
        }

        try {
            ChatResponse response = springAISiliconFlowService.getSiliconFlowChatModel().get().call(
                    new Prompt(
                            message,
                            OpenAiChatOptions.builder()
                                    .model("deepseek-chat")
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

    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
