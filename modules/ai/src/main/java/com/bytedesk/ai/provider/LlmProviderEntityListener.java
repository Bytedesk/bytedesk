/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:05:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 22:51:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.ai.provider.event.LlmProviderCreateEvent;
import com.bytedesk.ai.provider.event.LlmProviderUpdateEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LlmProviderEntityListener {

    @PostPersist
    public void postPersist(LlmProviderEntity entity) {
        LlmProviderEntity clonedEntity = SerializationUtils.clone(entity);
        // log.info("LlmProviderEntityListener postPersist: {}", clonedEntity.getName());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new LlmProviderCreateEvent(this, clonedEntity));
    }

    @PostUpdate
    public void postUpdate(LlmProviderEntity entity) {
        LlmProviderEntity clonedEntity = SerializationUtils.clone(entity);
        // log.info("LlmProviderEntityListener postUpdate: {}", clonedEntity.getName());
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new LlmProviderUpdateEvent(this, clonedEntity));
    }
    
}
