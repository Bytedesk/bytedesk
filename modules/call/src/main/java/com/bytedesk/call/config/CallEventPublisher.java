/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 10:16:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.config;

// import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// import com.bytedesk.call.gateway.event.CallGatewayCreateEvent;
// import com.bytedesk.call.gateway.event.CallGatewayUpdateEvent;
// import com.bytedesk.call.users.event.CallUserCreateEvent;
// import com.bytedesk.call.users.event.CallUserUpdateEvent;

import lombok.AllArgsConstructor;

/**
 * Call事件发布器
 */
@Async
@Component
@AllArgsConstructor
public class CallEventPublisher {
    
    // private final ApplicationEventPublisher applicationEventPublisher;
    
    // CDR事件
    // public void publishEvent(CallCdrCreateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallCdrUpdateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // // Conference事件
    // public void publishEvent(CallConferenceCreateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallConferenceUpdateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // Gateway事件
    // public void publishEvent(CallGatewayCreateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallGatewayUpdateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // User事件
    // public void publishEvent(CallUserCreateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallUserUpdateEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // Call事件
    // public void publishEvent(CallCallStartEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallCallAnsweredEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallCallHangupEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
    
    // public void publishEvent(CallDtmfEvent event) {
    //     applicationEventPublisher.publishEvent(event);
    // }
}
