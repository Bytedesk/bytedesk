/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-06 17:50:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.webhook_message;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.webhook_message.event.WebhookMessageCreateEvent;
import com.bytedesk.core.webhook_message.event.WebhookMessageUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebhookMessageEntityListener {

    @PostPersist
    public void onPostPersist(WebhookMessageEntity webhook) {
        log.info("onPostPersist: {}", webhook);
        WebhookMessageEntity cloneWebhookMessage = SerializationUtils.clone(webhook);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new WebhookMessageCreateEvent(cloneWebhookMessage));
    }

    @PostUpdate
    public void onPostUpdate(WebhookMessageEntity webhook) {
        log.info("onPostUpdate: {}", webhook);
        WebhookMessageEntity cloneWebhookMessage = SerializationUtils.clone(webhook);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new WebhookMessageUpdateEvent(cloneWebhookMessage));
    }
    
}
