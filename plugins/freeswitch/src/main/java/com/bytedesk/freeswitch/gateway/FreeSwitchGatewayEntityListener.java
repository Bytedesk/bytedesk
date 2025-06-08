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
package com.bytedesk.freeswitch.gateway;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.freeswitch.config.FreeSwitchEventPublisher;
import com.bytedesk.freeswitch.gateway.event.FreeSwitchGatewayCreateEvent;
import com.bytedesk.freeswitch.gateway.event.FreeSwitchGatewayUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch网关实体监听器
 */
@Slf4j
@Component
public class FreeSwitchGatewayEntityListener {

    @PostPersist
    public void postPersist(FreeSwitchGatewayEntity entity) {
        log.info("FreeSwitch网关创建: name={}, proxy={}", 
                entity.getGatewayName(), entity.getProxy());
                
        FreeSwitchGatewayEntity cloneEntity = SerializationUtils.clone(entity);
        FreeSwitchEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(FreeSwitchEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new FreeSwitchGatewayCreateEvent(cloneEntity));
    }

    @PostUpdate
    public void postUpdate(FreeSwitchGatewayEntity entity) {
        log.info("FreeSwitch网关更新: name={}, status={}, enabled={}", 
                entity.getGatewayName(), entity.getStatus(), entity.getEnabled());
                
        FreeSwitchGatewayEntity cloneEntity = SerializationUtils.clone(entity);
        FreeSwitchEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(FreeSwitchEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new FreeSwitchGatewayUpdateEvent(cloneEntity));
    }
}
