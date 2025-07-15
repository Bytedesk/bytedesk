/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 09:39:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 11:45:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.zhipuai;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 智谱AI接口
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 */
@Slf4j
@RestController
@RequestMapping("/springai/zhipuai")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAIZhipuaiController {

    private final BytedeskProperties bytedeskProperties;
    private final SpringAIZhipuaiService springAIZhipuaiService;
    private final ZhiPuAiChatModel bytedeskZhipuaiChatModel;
    private final ZhiPuAiImageModel bytedeskZhipuaiImageModel;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 方式1：同步调用
     * http://127.0.0.1:9003/springai/zhipuai/chat/sync?message=hello
     */
    @GetMapping("/chat/sync")
    public ResponseEntity<JsonResult<?>> chatSync(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        String response = springAIZhipuaiService.processPromptSync(message, null);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 方式2：异步流式调用
     * http://127.0.0.1:9003/springai/zhipuai/chat/stream?message=hello
     */
    @GetMapping("/chat/stream")
    public Flux<ChatResponse> chatStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return Flux.empty();
        }
        
        Prompt prompt = new Prompt(new UserMessage(message));
        return bytedeskZhipuaiChatModel.stream(prompt);
    }

    /**
     * 方式3：SSE调用
     * http://127.0.0.1:9003/springai/zhipuai/chat/sse?message=hello
     */
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSSE(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return null;
        }
        
        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时
        
        executorService.execute(() -> {
            try {
                // springAIZhipuaiService.processPromptSSE(message, emitter);
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
     * http://127.0.0.1:9003/springai/zhipuai/chat/custom?message=hello
     */
    @GetMapping("/chat/custom")
    public ResponseEntity<JsonResult<?>> chatCustom(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        ChatResponse response = bytedeskZhipuaiChatModel.call(
            new Prompt(
                message,
                ZhiPuAiChatOptions.builder()
                    .model(ZhiPuAiApi.ChatModel.GLM_4_Flash.getValue())
                    .temperature(0.7)
                    .topP(0.9)
                    .build()
            ));
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 图像生成接口
     * http://127.0.0.1:9003/springai/zhipuai/image
     */
    @GetMapping("/image")
    public ResponseEntity<JsonResult<?>> generateImage(
            @RequestParam(defaultValue = "A cute cat") String prompt) {
        
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Zhipuai service is not available"));
        }
        
        ImageResponse response = bytedeskZhipuaiImageModel.call(new ImagePrompt(prompt));
        return ResponseEntity.ok(JsonResult.success(response));
    }

    // 在 Bean 销毁时关闭线程池
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // http://127.0.0.1:9003/springai/zhipuai/stream-sse
    @GetMapping("/stream-sse")
    public void streamSse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!bytedeskProperties.getDebug()) {
            return;
        }
        
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        for (int i = 0; i < 10; i++) {
            writer.write("data: " + System.currentTimeMillis() + "\n\n");
            writer.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer.close();
    }


}
