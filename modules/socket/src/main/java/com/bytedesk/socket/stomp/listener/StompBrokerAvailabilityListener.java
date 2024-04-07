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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

/**
 * indicates when the broker becomes available/unavailable
 *
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-appplication-context-events
 * 
 * @author bytedesk.com
 */
@Slf4j
@Component
public class StompBrokerAvailabilityListener implements ApplicationListener<BrokerAvailabilityEvent> {

    /**
     * 来自ApplicationListener的接口
     * 监听 "system" connection to the broker is lost and re-established
     *
     * TODO: SimpMessagingTemplate should subscribe to this event and avoid sending
     * messages at times when the broker is not available.
     * TODO: In any case, SimpMessagingTemplate should be prepared to handle
     * MessageDeliveryException when sending a message.
     *
     * TODO: 持久化断开、建立连接时间，并在建立连接后通知前端
     *
     * @param brokerAvailabilityEvent event
     */
    @Override
    public void onApplicationEvent(@NonNull BrokerAvailabilityEvent brokerAvailabilityEvent) {
        //
        if (brokerAvailabilityEvent.isBrokerAvailable()) {
            log.debug("broker available: " + brokerAvailabilityEvent.toString());
        } else {
            log.error("lost connection to broker: " + brokerAvailabilityEvent.toString());
        }
    }

}
