/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:57:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 09:00:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;
import com.bytedesk.kbase.faq.event.FaqDeleteEvent;
import com.bytedesk.kbase.faq.event.FaqUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j  
@Component
public class FaqEntityListener {

    @PostPersist
    public void onPostPersist(FaqEntity faq) {
        // log.info("FaqEntityListener onPostPersist: {}", faq.getUid());
        FaqEntity clonedFaq = SerializationUtils.clone(faq);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new FaqCreateEvent(clonedFaq));
    }

    @PostUpdate
    public void onPostUpdate(FaqEntity faq) {
        // log.info("FaqEntityListener onPostUpdate: {}", faq.getUid());
        FaqEntity clonedFaq = SerializationUtils.clone(faq);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (faq.getDeleted()) {
            log.info("FaqEntityListener FaqDeleteEvent: {}", faq.getQuestion());
            publisher.publishEvent(new FaqDeleteEvent(clonedFaq));
        } else {
            // log.info("FaqEntityListener FaqUpdateEvent: {}", faq.getQuestion());
            publisher.publishEvent(new FaqUpdateEvent(clonedFaq));
        }
    }
    
}
