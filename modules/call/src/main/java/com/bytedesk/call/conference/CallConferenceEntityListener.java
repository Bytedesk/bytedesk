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
package com.bytedesk.call.conference;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.call.config.CallEventPublisher;
import com.bytedesk.call.conference.event.CallConferenceCreateEvent;
import com.bytedesk.call.conference.event.CallConferenceUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Call会议室实体监听器
 */
@Slf4j
@Component
public class CallConferenceEntityListener {

    @PostPersist
    public void postPersist(CallConferenceEntity entity) {
        log.info("Call会议室创建: name={}, maxMembers={}", 
                entity.getConferenceName(), entity.getMaxMembers());
                
        CallConferenceEntity cloneEntity = SerializationUtils.clone(entity);
        CallEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(CallEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new CallConferenceCreateEvent(cloneEntity));
    }

    @PostUpdate
    public void postUpdate(CallConferenceEntity entity) {
        log.info("Call会议室更新: name={}, enabled={}", 
                entity.getConferenceName(), entity.getEnabled());
                
        CallConferenceEntity cloneEntity = SerializationUtils.clone(entity);
        CallEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(CallEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new CallConferenceUpdateEvent(cloneEntity));
    }
}
