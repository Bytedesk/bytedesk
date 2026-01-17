/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @LastEditors: bytedesk.com
 * @LastEditTime: 2025-01-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: support@bytedesk.com
 *  联系：support@bytedesk.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.conference.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// /**
//  * Conference WebSocket Configuration
//  *
//  * 会议WebSocket配置，用于实时信令传输
//  */
// @Configuration
// @EnableWebSocketMessageBroker
// public class ConferenceWebSocketConfig implements WebSocketMessageBrokerConfigurer {

//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry config) {
//         // 启用简单消息代理，用于向客户端发送消息
//         config.enableSimpleBroker("/topic", "/queue");

//         // 设置应用程序目标前缀，用于客户端发送消息到服务器
//         config.setApplicationDestinationPrefixes("/app");

//         // 设置用户目标前缀
//         config.setUserDestinationPrefix("/user");
//     }

//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
//         // 注册STOMP端点，用于WebSocket连接
//         registry.addEndpoint("/ws/conference")
//                 .setAllowedOriginPatterns("*")
//                 .withSockJS(); // 启用SockJS后备选项
//     }
// }
