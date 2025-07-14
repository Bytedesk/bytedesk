/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 18:14:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.bytedesk.core.socket.stomp.handler.CustomWebSocketHandlerDecoratorFactory;

/**
 * https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html
 *
 * @author bytedesk.com
 */
@Configuration
@EnableWebSocketMessageBroker
@Description("STOMP WebSocket Configuration - STOMP over WebSocket configuration for real-time messaging")
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * /stomp is the HTTP URL for the endpoint to which a WebSocket (or SockJS)
     * client needs to connect for the WebSocket handshake.
     *
     * @param stompEndpointRegistry registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // web/h5
        stompEndpointRegistry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(handshakeHandler());
        // https://docs.spring.io/spring-security/reference/servlet/channels/websocket.html
        // FIXME: Access to XMLHttpRequest at
        // 'http://127.0.0.1:9003/sockjs/info?t=1718785780963' from origin
        // 'http://127.0.0.1:9006' has been blocked by CORS policy: The value of the
        // 'Access-Control-Allow-Origin' header in the response must not be the wildcard
        // '*' when the request's credentials mode is 'include'. The credentials mode of
        // requests initiated by the XMLHttpRequest is controlled by the withCredentials
        // attribute.
        // sockjs only used for older browsers
        stompEndpointRegistry.addEndpoint("/sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setHeartbeatTime(5 * 1000)
                .setDisconnectDelay(30 * 1000)
                // https://docs.spring.io/spring-framework/reference/web/websocket/fallback.html
                .setClientLibraryUrl("https://cdn.bytedesk.com/js/vendor/sockjs/1.1.4/sockjs.min.js"); //
        // for mini program
        stompEndpointRegistry.addEndpoint("/mini")
                .setAllowedOriginPatterns("*");
    }

    // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/server-config.html
    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        JettyRequestUpgradeStrategy strategy = new JettyRequestUpgradeStrategy();
        strategy.addWebSocketConfigurer(configurable -> {
            configurable.setInputBufferSize(4 * 8192);
            // configurable.setIdleTimeout(600000);
        });
        return new DefaultHandshakeHandler(strategy);
    }

    /**
     * SpringBoot官方说明 Using WebSocket to build an interactive web application:
     * https://spring.io/guides/gs/messaging-stomp-websocket/
     * https://www.sitepoint.com/implementing-spring-websocket-server-and-client/
     *
     * @param messageBrokerRegistry config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        taskScheduler.initialize();
        // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/message-flow.html
        // /topic and /queue messages may be routed directly to the message broker.
        messageBrokerRegistry.enableSimpleBroker(
                "/topic", "/queue")
                .setTaskScheduler(taskScheduler)
                .setHeartbeatValue(new long[] { 10 * 1000, 10 * 1000 });
        // STOMP messages whose destination header begins with /app are routed to
        // @MessageMapping methods in @Controller classes.
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
        messageBrokerRegistry.setUserDestinationPrefix("/user");
        // 保证消息有序性，启用有开销，必要时开启
        messageBrokerRegistry.setPreservePublishOrder(true);
    }

    /**
     * 用于优化 clientOutboundChannel 性能
     *
     * @param registration registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
        registration.setMessageSizeLimit(128 * 1024);
        // registration.setTimeToFirstMessage(30000);
        registration.addDecoratorFactory(customWebSocketHandlerDecoratorFactory());
    }

    @Bean
    public CustomWebSocketHandlerDecoratorFactory customWebSocketHandlerDecoratorFactory() {
        return new CustomWebSocketHandlerDecoratorFactory();
    }

}
