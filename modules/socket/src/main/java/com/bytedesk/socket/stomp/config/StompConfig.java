/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 15:12:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.stomp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html
 *
 * @author bytedesk.com
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    //
    static final String MESSAGE_PREFIX = "/topic"; // <3>

    /**
     * setAllowedOrigins 解决跨域问题
     * /stomp is the HTTP URL for the endpoint to which a WebSocket (or SockJS)
     * client needs to connect for the WebSocket handshake.
     *
     * @param registry registry
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // web/h5
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*");
        // for old browser
        registry.addEndpoint("/sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();
                // .setClientLibraryUrl("https://cdn.bytedesk.com/js/vendor/sockjs/1.1.4/sockjs.min.js"); // 不添加也不会影响使用;
        // for mini program
        registry.addEndpoint("/mini")
                .setAllowedOriginPatterns("*");
    }

    /**
     * SpringBoot官方说明 Using WebSocket to build an interactive web application:
     * https://spring.io/guides/gs/messaging-stomp-websocket/
     * https://www.sitepoint.com/implementing-spring-websocket-server-and-client/
     *
     * @param config config
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/message-flow.html
        // /topic and /queue messages may be routed directly to the message broker.
        config.enableSimpleBroker(MESSAGE_PREFIX, "/queue");
        // STOMP messages whose destination header begins with /app are routed to
        // @MessageMapping methods in @Controller classes.
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
        // 保证消息有序性，启用有开销，必要时开启
        config.setPreservePublishOrder(true);
    }

    /**
     * 用于优化 clientOutboundChannel 性能
     *
     * @param registration registration
     */
    @Override
    public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registration) {
        registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
        registration.setMessageSizeLimit(128 * 1024);
        // registration.setTimeToFirstMessage(30000);
    }

}
