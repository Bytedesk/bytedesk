/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 06:16:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-25 14:12:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.coze;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.core.utils.JsonResult;
import com.coze.openapi.client.chat.model.ChatPoll;

import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://www.coze.cn/open/docs/developer_guides
 */
@Slf4j
@RestController
@RequestMapping("/coze")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.coze.enabled", havingValue = "true", matchIfMissing = false)
public class CozeChatController {

    private final CozeChatService cozeChatService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // http://localhost:9003/coze/chat?content=what%20can%20you%20do?&userUid=123456
    @GetMapping("/chat")
    public ResponseEntity<?> chat(CozeRequest request) {

        ChatPoll chatPoll = cozeChatService.Chat(request.getContent(), request.getUserUid());

        return ResponseEntity.ok(JsonResult.success(chatPoll));
    }

    // http://localhost:9003/coze/chatWithImage?imagePath=xxx&userUid=123456
    @GetMapping("/cha/image")
    public ResponseEntity<?> chatWithImage(CozeRequest request) {
        
        cozeChatService.ChatWithImage(request.getContent(), request.getUserUid());

        return ResponseEntity.ok(JsonResult.success(""));
    }

    // chat stream - SSE方式
    // http://localhost:9003/coze/chat/stream?content=what%20can%20you%20do?&userUid=123456
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(CozeRequest request) {
        
        SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时
        
        // 验证请求参数
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("Content cannot be empty"));
                emitter.completeWithError(new IllegalArgumentException("Content cannot be empty"));
            } catch (Exception e) {
                log.error("Error sending validation error for content", e);
            }
            return emitter;
        }
        
        if (request.getUserUid() == null || request.getUserUid().trim().isEmpty()) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("UserUid cannot be empty"));
                emitter.completeWithError(new IllegalArgumentException("UserUid cannot be empty"));
            } catch (Exception e) {
                log.error("Error sending validation error for userUid", e);
            }
            return emitter;
        }
        
        executorService.execute(() -> {
            try {
                // 发送开始事件
                emitter.send(SseEmitter.event()
                        .name("start")
                        .data("Starting conversation..."));
                
                // 调用Coze聊天服务
                cozeChatService.ChatStream(request.getContent(), request.getUserUid(), emitter);
                
            } catch (Exception e) {
                log.error("Error processing SSE chat stream request", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("Error: " + e.getMessage()));
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.error("Error sending error event", ex);
                }
            }
        });
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.warn("SSE chat stream connection timed out");
            try {
                emitter.send(SseEmitter.event()
                        .name("timeout")
                        .data("Connection timed out"));
            } catch (Exception e) {
                log.error("Error sending timeout event", e);
            }
            emitter.complete();
        });
        
        emitter.onCompletion(() -> {
            log.info("SSE chat stream connection completed");
        });
        
        emitter.onError((throwable) -> {
            log.error("SSE chat stream connection error", throwable);
        });
        
        return emitter;
    }

    // 在Bean销毁时关闭线程池
    @PreDestroy
    public void destroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    
}
