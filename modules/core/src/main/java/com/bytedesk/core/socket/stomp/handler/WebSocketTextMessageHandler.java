/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 12:06:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp.handler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server
 *
 * @author bytedesk.com
 */
@Slf4j
@Component
public class WebSocketTextMessageHandler extends TextWebSocketHandler {

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // The WebSocket has been closed
        log.debug("session {}, status {}", session.toString(), status.toString());

        sessions.remove(session);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        // The WebSocket has been opened
        // I might saveAdmin this session object so that I can send messages to it
        // outside of this method
        log.debug("session {} connection established", session.toString());

        // the messages will be broadcasted to all users.
        sessions.add(session);

        // Let's send the first message
        session.sendMessage(new TextMessage("You are now connected to the server. This is the first message."));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage textMessage) {

        String content = textMessage.getPayload();
        // A message has been received
        log.debug("Message received: {}", content);

        // try {
        // String reverseContent = new StringBuffer(content).reverse().toString();
        // session.sendMessage(new TextMessage(reverseContent));
        // } catch (IOException e) {
        // }
    }

}
