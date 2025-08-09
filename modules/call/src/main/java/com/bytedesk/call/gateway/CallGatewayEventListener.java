/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 21:37:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.call.gateway.event.CallGatewayCreateEvent;
import com.bytedesk.call.gateway.event.CallGatewayUpdateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Call事件监听器
 */
@Slf4j
@Component
public class CallGatewayEventListener {

    
    @EventListener
    public void onCallGatewayCreateEvent(CallGatewayCreateEvent event) {
        CallGatewayEntity gateway = event.getGateway();
        log.info("收到Call网关创建事件: name={}, proxy={}", 
                gateway.getGatewayName(), gateway.getProxy());
        
        // 这里可以添加额外的处理逻辑，比如：
        // 1. 自动注册网关到Call服务器
        // 2. 更新网关状态监控信息
    }
    
    @EventListener
    public void onCallGatewayUpdateEvent(CallGatewayUpdateEvent event) {
        CallGatewayEntity gateway = event.getGateway();
        log.info("收到Call网关更新事件: name={}, status={}", 
                gateway.getGatewayName(), gateway.getStatus());
        
        // 这里可以添加额外的处理逻辑
    }
    

}
