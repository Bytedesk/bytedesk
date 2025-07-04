/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 21:03:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.cdr;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.freeswitch.cdr.event.FreeSwitchCdrCreateEvent;
import com.bytedesk.freeswitch.cdr.event.FreeSwitchCdrUpdateEvent;
import com.bytedesk.freeswitch.config.FreeSwitchEventPublisher;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch CDR实体监听器
 */
@Slf4j
@Component
public class FreeSwitchCdrEntityListener {

    @PostPersist
    public void postPersist(FreeSwitchCdrEntity entity) {
        log.info("FreeSwitch CDR记录创建: uuid={}, caller={}, destination={}, duration={}", 
                entity.getUid(), entity.getCallerIdNumber(), 
                entity.getDestinationNumber(), entity.getDuration());
                
        FreeSwitchCdrEntity cloneEntity = SerializationUtils.clone(entity);
        FreeSwitchEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(FreeSwitchEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new FreeSwitchCdrCreateEvent(cloneEntity));
    }
    
    @PostUpdate
    public void onPostUpdate(FreeSwitchCdrEntity entity) {
        log.info("FreeSwitch CDR记录更新: uuid={}, caller={}, destination={}, duration={}", 
                entity.getUid(), entity.getCallerIdNumber(), 
                entity.getDestinationNumber(), entity.getDuration());
                
        FreeSwitchCdrEntity cloneEntity = SerializationUtils.clone(entity);
        FreeSwitchEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(FreeSwitchEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new FreeSwitchCdrUpdateEvent(cloneEntity));
    }
}
