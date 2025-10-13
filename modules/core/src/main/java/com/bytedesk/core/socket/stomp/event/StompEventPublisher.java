/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-13
 * @Description: STOMP domain event facade
 */
package com.bytedesk.core.socket.stomp.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StompEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishStompConnectedEvent(String clientId) {
        applicationEventPublisher.publishEvent(new StompConnectedEvent(this, clientId));
    }

    public void publishStompDisconnectedEvent(String clientId) {
        applicationEventPublisher.publishEvent(new StompDisconnectedEvent(this, clientId));
    }

    public void publishStompSubscribeEvent(String topic, String clientId) {
        applicationEventPublisher.publishEvent(new StompSubscribeEvent(this, topic, clientId));
    }

    public void publishStompUnsubscribeEvent(String topic, String clientId) {
        applicationEventPublisher.publishEvent(new StompUnsubscribeEvent(this, topic, clientId));
    }
}
