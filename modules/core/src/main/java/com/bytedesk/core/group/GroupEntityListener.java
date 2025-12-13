/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:44:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 10:01:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.group.event.GroupCreateEvent;
import com.bytedesk.core.group.event.GroupUpdateEvent;

import jakarta.persistence.PostPersist;
// import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
// import jakarta.persistence.PrePersist;
// import jakarta.persistence.PreRemove;
// import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GroupEntityListener {

    @PostPersist
    public void postPersist(GroupEntity group) {
        log.info("GroupEntityListener postPersist {}", group.getUid());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new GroupCreateEvent(this, group));
    }

    @PostUpdate
    public void postUpdate(GroupEntity group) {
        log.info("GroupEntityListener postUpdate {}", group.getUid());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new GroupUpdateEvent(this, group));
    }

}
