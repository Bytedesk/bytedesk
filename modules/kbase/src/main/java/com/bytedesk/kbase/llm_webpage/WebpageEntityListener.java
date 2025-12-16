/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:19:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_webpage.event.WebpageCreateEvent;
import com.bytedesk.kbase.llm_webpage.event.WebpageDeleteEvent;
import com.bytedesk.kbase.llm_webpage.event.WebpageUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebpageEntityListener {

    @PostPersist
    public void onPostPersist(WebpageEntity webpage) {
        log.info("WebpageEntityListener onPostPersist: {}", webpage);
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new WebpageCreateEvent(this, webpage));
    }

    @PostUpdate 
    public void onPostUpdate(WebpageEntity webpage) {
        log.info("WebpageEntityListener onPostUpdate: {}", webpage);
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (webpage.isDeleted()) {
            publisher.publishEvent(new WebpageDeleteEvent(this, webpage));
        } else {
            publisher.publishEvent(new WebpageUpdateEvent(this, webpage));
        }
    }
    
}
