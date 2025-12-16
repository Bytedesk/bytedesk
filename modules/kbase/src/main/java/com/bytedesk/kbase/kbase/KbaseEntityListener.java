/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-18 15:55:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.kbase.event.KbaseCreateEvent;
import com.bytedesk.kbase.kbase.event.KbaseUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KbaseEntityListener {

    @PostPersist
    void postPersist(KbaseEntity kbase) {
        // log.info("kbase postPersist: {}", kbase.getName());
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new KbaseCreateEvent(this, kbase));
    }

    // 
    @PostUpdate
    void postUpdate(KbaseEntity kbase) {
        // log.info("kbase postUpdate: {}", kbase.getName());
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new KbaseUpdateEvent(this, kbase));
    }




}
