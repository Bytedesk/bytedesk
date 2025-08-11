/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 10:18:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.call.gateway.CallGatewayEntity;

import lombok.Getter;

@Getter
public class CallGatewayUpdateEvent extends ApplicationEvent {
    
    private final CallGatewayEntity gateway;
    
    public CallGatewayUpdateEvent(CallGatewayEntity gateway) {
        super(gateway);
        this.gateway = gateway;
    }
}
