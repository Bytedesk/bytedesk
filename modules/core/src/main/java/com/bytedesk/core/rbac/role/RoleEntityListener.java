/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-07 16:27:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 22:52:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RoleEntityListener {

    @PostPersist
    public void postPersist(RoleEntity role) {
        // 这里可以记录日志、发送通知等
        log.info("role postPersist {}", role.getUid());
        RoleEntity clonedRole = SerializationUtils.clone(role);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new RoleCreateEvent(this, clonedRole));
    }

    @PostUpdate
    public void postUpdate(RoleEntity role) {
        // 这里可以记录日志、发送通知等
        log.info("role postUpdate {}", role.getUid());
        RoleEntity clonedRole = SerializationUtils.clone(role);

        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new RoleUpdateEvent(this, clonedRole));

    }
    
}
