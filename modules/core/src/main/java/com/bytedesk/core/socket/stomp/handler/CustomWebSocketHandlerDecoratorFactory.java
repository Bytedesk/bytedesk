/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-22 18:13:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 23:17:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp.handler;

import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class CustomWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {

            private final AtomicLong lastHeartbeat = new AtomicLong();

            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                super.afterConnectionEstablished(session);
                lastHeartbeat.set(System.currentTimeMillis());
                log.debug("stomp Connected to client: " + session.getId());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                super.handleTransportError(session, exception);
                log.debug("stomp Transport error: " + exception.getMessage());
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                super.afterConnectionClosed(session, closeStatus);
                log.debug("stomp Connection closed: " + closeStatus.getReason());
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                super.handleMessage(session, message);
                if (message instanceof PongMessage) {
                    lastHeartbeat.set(System.currentTimeMillis());
                    log.debug("stomp Received pong message from client: " + session.toString());
                } 
                // else if (message instanceof PingMessage) {
                //     log.info("stomp Received ping message from client: " + session.toString());
                // } else {
                //     log.info("stomp Received message from client: " + session.toString() + ", payload: " + message.toString());
                // }
            }
        };
    }
}