/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 21:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.EntityListenerHelper;
import com.bytedesk.call.cdr.event.CallCdrCreateEvent;
import com.bytedesk.call.cdr.event.CallCdrUpdateEvent;
import com.bytedesk.call.config.CallEventPublisher;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Call CDR实体监听器
 */
@Slf4j
@Component
public class CallCdrEntityListener {

    @PostPersist
    public void postPersist(CallCdrEntity entity) {
        log.info("Call CDR记录创建: uuid={}, caller={}, destination={}, duration={}", 
                entity.getUid(), entity.getCallerIdNumber(), 
                entity.getDestinationNumber(), entity.getDuration());
        
        EntityListenerHelper.safeExecuteWithBean(CallEventPublisher.class, entity.getUid(), 
            eventPublisher -> {
                CallCdrEntity cloneEntity = SerializationUtils.clone(entity);
                eventPublisher.publishEvent(new CallCdrCreateEvent(cloneEntity));
            });
    }
    
    @PostUpdate
    public void onPostUpdate(CallCdrEntity entity) {
        log.info("Call CDR记录更新: uuid={}, caller={}, destination={}, duration={}", 
                entity.getUid(), entity.getCallerIdNumber(), 
                entity.getDestinationNumber(), entity.getDuration());
        
        EntityListenerHelper.safeExecuteWithBean(CallEventPublisher.class, entity.getUid(), 
            eventPublisher -> {
                CallCdrEntity cloneEntity = SerializationUtils.clone(entity);
                eventPublisher.publishEvent(new CallCdrUpdateEvent(cloneEntity));
            });
    }
}
