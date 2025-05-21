/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 18:38:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_text.event.TextCreateEvent;
import com.bytedesk.kbase.llm_text.event.TextDeleteEvent;
import com.bytedesk.kbase.llm_text.event.TextUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TextEntityListener {

    @PostPersist
    public void onPostPersist(TextEntity text) {
        log.info("TextEntityListener onPostPersist: {}", text.getTitle());
        // 
        TextEntity clonedText = SerializationUtils.clone(text);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new TextCreateEvent(clonedText));
    }

    @PostUpdate
    public void onPostUpdate(TextEntity text) {
        log.info("TextEntityListener onPostUpdate: {}", text.getTitle());
        // 
        TextEntity clonedText = SerializationUtils.clone(text);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (text.getDeleted()) {
            publisher.publishEvent(new TextDeleteEvent(clonedText));
        } else {
            publisher.publishEvent(new TextUpdateEvent(clonedText));
        }
    }
    
}
