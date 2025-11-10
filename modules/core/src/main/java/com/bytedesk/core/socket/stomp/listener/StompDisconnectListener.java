/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-28 12:03:15
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
// import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// import com.bytedesk.core.event.BytedeskEventPublisher;
import com.bytedesk.core.socket.connection.ConnectionRestService;

/**
 * published when a STOMP session ends.
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-appplication-context-events
 * 
 * @author bytedesk.com
 */
@Slf4j
@Component
@AllArgsConstructor
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final ConnectionRestService connectionRestService;

    @Override
    public void onApplicationEvent(@NonNull SessionDisconnectEvent event) {
        // log.debug("stomp sessionDisconnectEvent {}", event.toString());
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        if (headerAccessor == null) {
            return;
        }
        String uid = headerAccessor.getLogin();
        if (uid == null && headerAccessor.getNativeHeader("login") != null && !headerAccessor.getNativeHeader("login").isEmpty()) {
            uid = headerAccessor.getNativeHeader("login").get(0);
        }
        if (uid == null) {
            return; // 无法识别用户
        }
        String sessionId = headerAccessor.getSessionId();
        String clientId = uid + "/stomp/" + sessionId;
        // 标记该 STOMP 会话断开；坐席在线状态由定时任务统一刷新
        connectionRestService.markDisconnected(clientId);
        log.debug("stomp disconnected uid {} session {}", uid, sessionId);
        
    }

}
