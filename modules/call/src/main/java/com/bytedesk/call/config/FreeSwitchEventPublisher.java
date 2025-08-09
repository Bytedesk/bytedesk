/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 21:55:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.call.cdr.event.FreeSwitchCdrCreateEvent;
import com.bytedesk.call.cdr.event.FreeSwitchCdrUpdateEvent;
import com.bytedesk.call.conference.event.FreeSwitchConferenceCreateEvent;
import com.bytedesk.call.conference.event.FreeSwitchConferenceUpdateEvent;
import com.bytedesk.call.gateway.event.FreeSwitchGatewayCreateEvent;
import com.bytedesk.call.gateway.event.FreeSwitchGatewayUpdateEvent;
import com.bytedesk.call.number.event.FreeSwitchNumberCreateEvent;
import com.bytedesk.call.number.event.FreeSwitchNumberUpdateEvent;

import lombok.AllArgsConstructor;

/**
 * FreeSwitch事件发布器
 */
@Async
@Component
@AllArgsConstructor
public class FreeSwitchEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    // CDR事件
    public void publishEvent(FreeSwitchCdrCreateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishEvent(FreeSwitchCdrUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    // Conference事件
    public void publishEvent(FreeSwitchConferenceCreateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishEvent(FreeSwitchConferenceUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    // Gateway事件
    public void publishEvent(FreeSwitchGatewayCreateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishEvent(FreeSwitchGatewayUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    // User事件
    public void publishEvent(FreeSwitchNumberCreateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishEvent(FreeSwitchNumberUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
