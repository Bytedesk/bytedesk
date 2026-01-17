/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Desktop WebSocket Configuration
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// /**
//  * Desktop WebSocket Configuration
//  * Configures STOMP/WebSocket support for remote desktop
//  */
// @Configuration
// @EnableWebSocketMessageBroker
// public class DesktopWebSocketConfig implements WebSocketMessageBrokerConfigurer {

//     /**
//      * Configure message broker
//      */
//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry config) {
//         // Enable a simple memory-based message broker to send messages to clients
//         // on destinations prefixed with "/topic" and "/queue"
//         config.enableSimpleBroker("/topic", "/queue");

//         // Designate the "/app" prefix for messages bound for @MessageMapping methods
//         config.setApplicationDestinationPrefixes("/app");

//         // Designate the "/user" prefix for user-specific destinations
//         config.setUserDestinationPrefix("/user");
//     }

//     /**
//      * Register STOMP endpoints
//      */
//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
//         // Register STOMP endpoint for desktop connections
//         // Clients can connect to: ws://host:port/ws/desktop
//         registry.addEndpoint("/ws/desktop")
//                 .setAllowedOriginPatterns("*") // Configure CORS as needed
//                 .withSockJS(); // Enable SockJS fallback options
//     }
// }
