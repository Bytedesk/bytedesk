// /*
//  * @Author: jackning 270580156@qq.com
//  * @Date: 2024-01-29 16:21:46
//  * @LastEditors: jackning 270580156@qq.com
//  * @LastEditTime: 2024-04-04 15:31:51
//  * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
//  *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
//  *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
//  *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
//  *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
//  *  contact: 270580156@qq.com 
//  *  联系：270580156@qq.com
//  * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
//  */
// package com.bytedesk.socket.stomp.config;

// import com.bytedesk.socket.stomp.handler.WebSocketBinaryMessageHandler;
// import com.bytedesk.socket.stomp.handler.WebSocketTextMessageHandler;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.lang.NonNull;
// import org.springframework.web.socket.config.annotation.EnableWebSocket;
// import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
// import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
// import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

// /**
//  * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server-handler
//  * FIXME: Caused by: java.lang.IllegalStateException: WebSocketSession not yet initialized
//  *
//  * @author bytedesk.com
//  */
// @Configuration
// @EnableWebSocket
// public class WebSocketConfig implements WebSocketConfigurer {

//     @Override
//     public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
//         // 处理文本消息
//         registry.addHandler(new WebSocketTextMessageHandler(), "/websocket/text")
//                 .setAllowedOriginPatterns("*")
//                 .addInterceptors(new HttpSessionHandshakeInterceptor())
//                 .withSockJS();
//         // 处理二进制消息
//         registry.addHandler(new WebSocketBinaryMessageHandler(), "/websocket/binary")
//                 .setAllowedOriginPatterns("*")
//                 .addInterceptors(new HttpSessionHandshakeInterceptor())
//                 .withSockJS();
//     }

// }
