/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-24 10:10:39
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
// import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// import com.bytedesk.core.event.BytedeskEventPublisher;

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

    // private final BytedeskEventPublisher bytedeskEventPublisher;
    @Override
    public void onApplicationEvent(@NonNull SessionDisconnectEvent evet) {
        log.debug("SessionDisconnectEvent {}", evet.toString());
        // TODO: 访客离线，通知客服端
        
    }

}
