/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-03 16:23:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.stomp.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

// import com.bytedesk.core.event.BytedeskEventPublisher;

import lombok.AllArgsConstructor;

// import lombok.extern.slf4j.Slf4j;

/**
 * published when a new STOMP SUBSCRIBE is received.
 *
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-appplication-context-events
 * 
 * @author bytedesk.com
 */
// @Slf4j
@Component
@AllArgsConstructor
public class StompSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    // private final BytedeskEventPublisher bytedeskEventPublisher;
    /**
     * 监听来自stomp，也即web端的订阅事件
     *
     * @param sessionSubscribeEvent event
     */
    @Override
    public void onApplicationEvent(@NonNull SessionSubscribeEvent sessionSubscribeEvent) {
        // log.debug(sessionSubscribeEvent.toString());

        //
        // MessageHeaders headers = sessionSubscribeEvent.getMessage().getHeaders();
        // Principal principal = SimpMessageHeaderAccessor.getUser(headers);
        // if (principal == null) {
        // return;
        // }
        // log.debug("principal.getName(): " + principal.getName());
        //
        // Optional<User> userOptional =
        // userRepository.findFirstByUsername(principal.getName());
        // if (!userOptional.isPresent()) {
        // return;
        // }
        // String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        // String destination = SimpMessageHeaderAccessor.getDestination(headers);
        // log.debug("subscribe: " + destination);

    }

}
