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
package com.bytedesk.freeswitch.conference;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.freeswitch.config.FreeSwitchEventPublisher;
import com.bytedesk.freeswitch.conference.event.FreeSwitchConferenceCreateEvent;
import com.bytedesk.freeswitch.conference.event.FreeSwitchConferenceUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch会议室实体监听器
 */
@Slf4j
@Component
public class FreeSwitchConferenceEntityListener {

    @PostPersist
    public void postPersist(FreeSwitchConferenceEntity entity) {
        log.info("FreeSwitch会议室创建: name={}, maxMembers={}", 
                entity.getConferenceName(), entity.getMaxMembers());
                
        FreeSwitchConferenceEntity cloneEntity = SerializationUtils.clone(entity);
        FreeSwitchEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(FreeSwitchEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new FreeSwitchConferenceCreateEvent(cloneEntity));
    }

    @PostUpdate
    public void postUpdate(FreeSwitchConferenceEntity entity) {
        log.info("FreeSwitch会议室更新: name={}, enabled={}", 
                entity.getConferenceName(), entity.getEnabled());
                
        FreeSwitchConferenceEntity cloneEntity = SerializationUtils.clone(entity);
        FreeSwitchEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(FreeSwitchEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new FreeSwitchConferenceUpdateEvent(cloneEntity));
    }
}
