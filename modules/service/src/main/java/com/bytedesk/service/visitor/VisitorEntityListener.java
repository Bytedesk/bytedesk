/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 13:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 18:24:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.visitor.event.VisitorCreateEvent;
import com.bytedesk.service.visitor.event.VisitorUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VisitorEntityListener {

    @PostPersist
    public void onPostPersist(VisitorEntity visitor) {
        log.info("onPostPersist: {}", visitor);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new VisitorCreateEvent(this, visitor));
    }

    @PostUpdate
    public void onPostUpdate(VisitorEntity visitor) {
        log.info("onPostUpdate: {}", visitor);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new VisitorUpdateEvent(this, visitor));
    }
    
}
