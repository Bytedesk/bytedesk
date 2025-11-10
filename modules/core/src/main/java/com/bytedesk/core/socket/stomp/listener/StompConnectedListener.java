/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 15:40:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import com.bytedesk.core.socket.connection.ConnectionRestService;

// import com.bytedesk.core.event.BytedeskEventPublisher;

/**
 * published shortly after a SessionConnectEvent when the broker has sent a
 * STOMP CONNECTED frame in response to the CONNECT
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-appplication-context-events
 *
 * @author bytedesk.com
 */
@Slf4j
@Component
@AllArgsConstructor
public class StompConnectedListener implements ApplicationListener<SessionConnectedEvent> {

    private final ConnectionRestService connectionRestService;
    
    @Override
    public void onApplicationEvent(@NonNull SessionConnectedEvent event) {
        // log.debug("stomp sessionConnectedEvent {}", event.toString());
        // stomp sessionConnectedEvent SessionConnectedEvent[GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT_ACK, simpConnectMessage=GenericMessage [payload=byte[0], headers={simpMessageType=CONNECT, stompCommand=CONNECT, nativeHeaders={login=[1513088171901063], accept-version=[1.2,1.1,1.0], heart-beat=[10000,10000]}, simpSessionAttributes={}, simpHeartbeat=[J@68e0e40, simpSessionId=18cf80bd-cbbb-4a62-7ac3-1eaa0a465184}], simpHeartbeat=[J@5ceab5fa, simpSessionId=18cf80bd-cbbb-4a62-7ac3-1eaa0a465184}]]
        // StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        // 
        // MessageHeaders headers = event.getMessage().getHeaders();
        // String uid = SimpMessageHeaderAccessor.getFirstNativeHeader("login", headers);
        // log.info("stomp sessionConnectedEvent uid:  {}", uid);
        // 
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        if (headerAccessor == null) {
            log.info("stomp connection without headerAccessor");
            return;
        }
        String uid = headerAccessor.getLogin();
        // 尝试从原始 native headers 解析 login
        if (uid == null && headerAccessor.getNativeHeader("login") != null && !headerAccessor.getNativeHeader("login").isEmpty()) {
            uid = headerAccessor.getNativeHeader("login").get(0);
        }
        if (uid == null) {
            log.info("stomp connection missing login header");
            return;
        }
        String sessionId = headerAccessor.getSessionId();
        String clientId = uid + "/stomp/" + sessionId;
        // 标记连接，仅记录会话；坐席在线布尔状态由上层定时任务/事件统一刷新
        connectionRestService.markConnected(uid, null, clientId, null, "STOMP", "WEB", null, null, 90);
        log.debug("stomp connected uid {} session {}", uid, sessionId);
    }
}
