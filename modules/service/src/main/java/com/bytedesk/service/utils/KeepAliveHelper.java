/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-24 12:30:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 12:28:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 * KeepAliveHelper - 处理SSE连接心跳功能
 */
package com.bytedesk.service.utils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeepAliveHelper {
    
    // 使用ConcurrentHashMap来存储每个emitter的心跳任务
    private static final Map<SseEmitter, ScheduledFuture<?>> keepAliveMap = new ConcurrentHashMap<>();
    
    // 创建一个单线程的调度器来执行所有的心跳任务
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * 为SSE连接添加心跳任务
     * 
     * @param emitter SSE发射器
     * @param intervalMs 心跳间隔(毫秒)
     */
    public static void addKeepAliveEvent(SseEmitter emitter, long intervalMs) {
        if (emitter == null) return;
        
        // 创建一个定期任务，发送心跳消息
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            try {
                if (emitter != null) {
                    // 发送一个注释类型的事件作为心跳
                    emitter.send(SseEmitter.event()
                            .comment("heartbeat")
                            .name("heartbeat")
                            .data(""));
                    log.debug("Sent heartbeat to SSE connection");
                }
            } catch (IOException e) {
                log.warn("Failed to send heartbeat, removing keep-alive task", e);
                removeKeepAliveEvent(emitter);
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
        
        // 保存任务引用，以便后续取消
        keepAliveMap.put(emitter, task);
    }
    
    /**
     * 移除SSE连接的心跳任务
     * 
     * @param emitter SSE发射器
     */
    public static void removeKeepAliveEvent(SseEmitter emitter) {
        if (emitter == null) return;
        
        ScheduledFuture<?> task = keepAliveMap.remove(emitter);
        if (task != null) {
            task.cancel(false);
            log.debug("Removed keep-alive task for SSE connection");
        }
    }
    
    /**
     * 应用程序关闭时清理所有资源
     */
    public static void shutdown() {
        keepAliveMap.forEach((emitter, task) -> {
            task.cancel(false);
        });
        keepAliveMap.clear();
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        log.info("KeepAliveHelper shutdown completed");
    }
}
