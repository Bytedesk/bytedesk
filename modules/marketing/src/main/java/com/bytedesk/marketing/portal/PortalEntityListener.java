/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 17:00:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.marketing.portal;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.marketing.portal.event.PortalCreateEvent;
import com.bytedesk.marketing.portal.event.PortalDeleteEvent;
import com.bytedesk.marketing.portal.event.PortalUpdateEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PortalEntityListener {

    @PostPersist
    public void onPostPersist(PortalEntity portal) {
        log.info("onPostPersist: {}", portal);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new PortalCreateEvent(portal));
    }

    @PostUpdate
    public void onPostUpdate(PortalEntity portal) {
        log.info("onPostUpdate: {}", portal);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (portal.isDeleted()) {
            bytedeskEventPublisher.publishEvent(new PortalDeleteEvent(portal));
        } else {
            bytedeskEventPublisher.publishEvent(new PortalUpdateEvent(portal));
        }
    }
    
}
