/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-30 07:07:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-30 10:44:45
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

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CategoryEntityListener {

    @PostPersist
    public void postPersist(CategoryEntity category) {
        log.info("CategoryEntity postPersist: {}", category);
        CategoryEntity clonedEntity = SerializationUtils.clone(category);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishGenericApplicationEvent(new GenericApplicationEvent<CategoryCreateEvent>(this, new CategoryCreateEvent(this, clonedEntity)));
    }

    @PostUpdate
    public void postUpdate(CategoryEntity category) {
        log.info("CategoryEntity postUpdate: {}", category);
        CategoryEntity clonedEntity = SerializationUtils.clone(category);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishGenericApplicationEvent(new GenericApplicationEvent<CategoryUpdateEvent>(this, new CategoryUpdateEvent(this, clonedEntity)));
    }
    
}
