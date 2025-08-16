/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 15:40:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp.listener;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;

/**
 * published when a new STOMP SUBSCRIBE is received.
 *
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-appplication-context-events
 * 
 * @author bytedesk.com
 */
@Slf4j
@Component
@AllArgsConstructor
public class StompSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private final Set<String> subscriptions = new HashSet<>();

    @Override
    public void onApplicationEvent(@NonNull SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        // log.debug("SessionSubscribeEvent: {}", event.toString());
        // log.debug("SessionSubscribeEvent: sessionId={}, destination={}", sessionId, destination);

        if (sessionId == null || destination == null) {
            return;
        }

        String subscriptionKey = sessionId + ":" + destination;

        synchronized (subscriptions) {
            if (subscriptions.contains(subscriptionKey)) {
                // log.debug("Duplicate subscription detected: {}", subscriptionKey);
                return;
            }
            subscriptions.add(subscriptionKey);
        }

        // 处理订阅逻辑，例如存储到数据库或其他操作
    }

    // 清理过期的订阅
    public void removeSubscription(String sessionId, String destination) {
        String subscriptionKey = sessionId + ":" + destination;
        synchronized (subscriptions) {
            subscriptions.remove(subscriptionKey);
        }
    }

}
