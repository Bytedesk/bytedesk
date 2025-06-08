/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 22:05:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.gateway.event;

import com.bytedesk.freeswitch.gateway.FreeSwitchGatewayEntity;

import lombok.Getter;

@Getter
public class FreeSwitchGatewayCreateEvent {
    
    private final FreeSwitchGatewayEntity gateway;
    
    public FreeSwitchGatewayCreateEvent(FreeSwitchGatewayEntity gateway) {
        this.gateway = gateway;
    }
}
