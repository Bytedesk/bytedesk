/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.gateway;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.call.config.CallEventPublisher;
import com.bytedesk.call.gateway.event.CallGatewayCreateEvent;
import com.bytedesk.call.gateway.event.CallGatewayUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Call网关实体监听器
 */
@Slf4j
@Component
public class CallGatewayEntityListener {

    @PostPersist
    public void postPersist(CallGatewayEntity entity) {
        log.info("Call网关创建: name={}, proxy={}", 
                entity.getGatewayName(), entity.getProxy());
                
        CallGatewayEntity cloneEntity = SerializationUtils.clone(entity);
        CallEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(CallEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new CallGatewayCreateEvent(cloneEntity));
    }

    @PostUpdate
    public void postUpdate(CallGatewayEntity entity) {
        log.info("Call网关更新: name={}, status={}, enabled={}", 
                entity.getGatewayName(), entity.getStatus(), entity.getEnabled());
                
        CallGatewayEntity cloneEntity = SerializationUtils.clone(entity);
        CallEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(CallEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new CallGatewayUpdateEvent(cloneEntity));
    }
}
