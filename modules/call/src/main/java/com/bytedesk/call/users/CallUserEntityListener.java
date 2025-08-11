/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 10:14:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.users;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.call.config.CallEventPublisher;
import com.bytedesk.call.users.event.CallUserCreateEvent;
import com.bytedesk.call.users.event.CallUserUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Call用户实体监听器
 */
@Slf4j
@Component
public class CallUserEntityListener {

    @PostPersist
    public void postPersist(CallUserEntity entity) {
        log.info("Call用户创建: username={}, domain={}", 
                entity.getUsername(), entity.getDomain());
                
        CallUserEntity cloneEntity = SerializationUtils.clone(entity);
        CallEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(CallEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new CallUserCreateEvent(cloneEntity));
    }

    @PostUpdate
    public void postUpdate(CallUserEntity entity) {
        log.info("Call用户更新: username={}, enabled={}", 
                entity.getUsername(), entity.getEnabled());
                
        CallUserEntity cloneEntity = SerializationUtils.clone(entity);
        CallEventPublisher freeSwitchEventPublisher = ApplicationContextHolder.getBean(CallEventPublisher.class);
        freeSwitchEventPublisher.publishEvent(new CallUserUpdateEvent(cloneEntity));
    }
}
