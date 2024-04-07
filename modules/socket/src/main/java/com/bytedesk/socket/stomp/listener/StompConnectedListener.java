/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 12:07:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.stomp.listener;

// import com.bytedesk.core.model.entity.User;
// import com.bytedesk.core.publisher.EventPublisher;
// import com.bytedesk.core.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
// import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 * published shortly after a SessionConnectEvent when the broker has sent a
 * STOMP CONNECTED frame in response to the CONNECT
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-appplication-context-events
 *
 * @author bytedesk.com
 */
@Slf4j
@Component
@AllArgsConstructor
public class StompConnectedListener implements ApplicationListener<SessionConnectedEvent> {

    @Override
    public void onApplicationEvent(@NonNull SessionConnectedEvent sessionConnectedEvent) {
        log.debug(sessionConnectedEvent.toString());
    }

}
