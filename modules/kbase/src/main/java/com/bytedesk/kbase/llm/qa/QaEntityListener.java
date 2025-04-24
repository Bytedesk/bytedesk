/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:57:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 10:13:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm.qa.event.QaCreateEvent;
import com.bytedesk.kbase.llm.qa.event.QaDeleteEvent;
import com.bytedesk.kbase.llm.qa.event.QaUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j  
@Component
public class QaEntityListener {

    @PostPersist
    public void onPostPersist(QaEntity qa) {
        // log.info("QaEntityListener onPostPersist: {}", qa.getUid());
        QaEntity clonedQa = SerializationUtils.clone(qa);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new QaCreateEvent(clonedQa));
    }

    @PostUpdate
    public void onPostUpdate(QaEntity qa) {
        // log.info("QaEntityListener onPostUpdate: {}", qa.getUid());
        QaEntity clonedQa = SerializationUtils.clone(qa);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (qa.isDeleted()) {
            publisher.publishEvent(new QaDeleteEvent(clonedQa));
        } else {
            publisher.publishEvent(new QaUpdateEvent(clonedQa));
        }
    }
    
}
