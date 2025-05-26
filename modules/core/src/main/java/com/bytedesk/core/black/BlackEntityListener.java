/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 07:01:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 16:45:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.core.black.event.BlackCreateEvent;
import com.bytedesk.core.black.event.BlackDeleteEvent;
import com.bytedesk.core.black.event.BlackUpdateEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BlackEntityListener {

    @PostPersist
    public void onPostPersist(BlackEntity blackEntity) {
        log.info("BlackEntityListener onPostPersist: " + blackEntity);
        BlackEntity clonedBlackEntity = SerializationUtils.clone(blackEntity);

        // 发布黑名单创建事件
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new BlackCreateEvent(this, clonedBlackEntity));
    }

    @PostUpdate
    public void onPostUpdate(BlackEntity blackEntity) {
        log.info("BlackEntityListener onPostUpdate: " + blackEntity);
        BlackEntity clonedBlackEntity = SerializationUtils.clone(blackEntity);

        if (clonedBlackEntity.isDeleted()) {
            // 发布黑名单删除事件
            BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder
                    .getBean(BytedeskEventPublisher.class);
            bytedeskEventPublisher.publishEvent(new BlackDeleteEvent(this, clonedBlackEntity));
        } else {
            // 发布黑名单更新事件
            BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder
                    .getBean(BytedeskEventPublisher.class);
            bytedeskEventPublisher.publishEvent(new BlackUpdateEvent(this, clonedBlackEntity));
        }

    }

    @PostRemove
    public void onPostRemove(BlackEntity blackEntity) {
        log.info("BlackEntityListener onPostRemove: " + blackEntity);
        BlackEntity clonedBlackEntity = SerializationUtils.clone(blackEntity);

        // 发布黑名单删除事件
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new BlackDeleteEvent(this, clonedBlackEntity));
    }
}
