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
package com.bytedesk.ai.zhipuai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/zhipuai")
@RequiredArgsConstructor
public class ZhipuaiController {

    private final ZhipuaiService zhipuaiService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 同步调用
     * GET /zhipuai/sync?message=xxx
     */
    @GetMapping("/sync")
    public ResponseEntity<String> chatSync(@RequestParam("message") String message) {
        String result = zhipuaiService.chatSync(message);
        return ResponseEntity.ok(result);
    }

    /**
     * 流式调用
     * GET /zhipuai/stream?message=xxx
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam("message") String message) {
        return zhipuaiService.chatStream(message);
    }

    /**
     * SSE调用
     * GET /zhipuai/sse?message=xxx
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSse(@RequestParam("message") String message) {
        SseEmitter emitter = new SseEmitter(180_000L);
        executorService.execute(() -> {
            try {
                zhipuaiService.chatSSE(message, emitter);
            } catch (Exception e) {
                log.error("Error in SSE", e);
                emitter.completeWithError(e);
            }
        });
        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> log.info("SSE completed"));
        return emitter;
    }

    /**
     * 角色扮演
     * GET /zhipuai/roleplay?message=xxx&userInfo=...&botInfo=...&botName=...&userName=...
     */
    @GetMapping("/roleplay")
    public ResponseEntity<String> rolePlayChat(
            @RequestParam("message") String message,
            @RequestParam("userInfo") String userInfo,
            @RequestParam("botInfo") String botInfo,
            @RequestParam("botName") String botName,
            @RequestParam("userName") String userName) {
        String result = zhipuaiService.rolePlayChat(message, userInfo, botInfo, botName, userName);
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        boolean healthy = zhipuaiService.isHealthy();
        return ResponseEntity.ok(healthy ? "ok" : "fail");
    }
} 