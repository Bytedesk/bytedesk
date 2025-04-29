/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 13:37:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_website.event.WebsiteCreateEvent;
import com.bytedesk.kbase.llm_website.event.WebsiteDeleteEvent;
import com.bytedesk.kbase.llm_website.event.WebsiteUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebsiteEntityListener {

    @PostPersist
    public void onPostPersist(WebsiteCreateEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("WebsiteEntityListener onPostPersist: {}", website.toString());
        // 
        WebsiteEntity clonedWebsite = SerializationUtils.clone(website);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new WebsiteCreateEvent(clonedWebsite));
    }

    @PostUpdate 
    public void onPostUpdate(WebsiteUpdateEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("WebsiteEntityListener onPostUpdate: {}", website.toString());
        // 
        WebsiteEntity clonedWebsite = SerializationUtils.clone(website);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (website.isDeleted()) {
            publisher.publishEvent(new WebsiteDeleteEvent(clonedWebsite));
        } else {
            publisher.publishEvent(new WebsiteUpdateEvent(clonedWebsite));
        }
    }
    
}
