/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-30 07:07:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 15:48:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.event.CategoryCreateEvent;
import com.bytedesk.core.category.event.CategoryUpdateEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CategoryEntityListener {

    @PostPersist
    public void postPersist(CategoryEntity category) {
        // log.info("CategoryEntity postPersist: {}", category.getName());
        CategoryEntity clonedEntity = SerializationUtils.clone(category);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new CategoryCreateEvent(this, clonedEntity));
    }

    @PostUpdate
    public void postUpdate(CategoryEntity category) {
        // log.info("CategoryEntity postUpdate: {}", category.getName());
        CategoryEntity clonedEntity = SerializationUtils.clone(category);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new CategoryUpdateEvent(this, clonedEntity));
    }
    
}
