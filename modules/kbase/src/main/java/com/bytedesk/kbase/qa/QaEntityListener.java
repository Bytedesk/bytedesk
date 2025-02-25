/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 15:46:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.qa;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.qa.event.QaCreateEvent;
import com.bytedesk.kbase.qa.event.QaUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QaEntityListener {

    @PostPersist
    public void onPostPersist(QaCreateEvent event) {
        QaEntity qa = event.getQa();
        log.info("QaEntityListener onPostPersist: {}", qa.toString());
        // 
        QaEntity clonedQa = SerializationUtils.clone(qa);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new QaCreateEvent(clonedQa));
    }

    @PostUpdate
    public void onPostUpdate(QaUpdateEvent event) {
        QaEntity qa = event.getQa();
        log.info("QaEntityListener onPostUpdate: {}", qa.toString());
        // 
        QaEntity clonedQa = SerializationUtils.clone(qa);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new QaUpdateEvent(clonedQa));
    }
}
