/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 16:15:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_template;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.ticket.ticket_template.event.TicketTemplateCreateEvent;
import com.bytedesk.ticket.ticket_template.event.TicketTemplateUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketTemplateEntityListener {

    @PostPersist
    public void onPostPersist(TicketTemplateEntity ticketTemplate) {
        log.info("onPostPersist: {}", ticketTemplate);
        TicketTemplateEntity cloneTicketTemplate = SerializationUtils.clone(ticketTemplate);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TicketTemplateCreateEvent(cloneTicketTemplate));
    }

    @PostUpdate
    public void onPostUpdate(TicketTemplateEntity ticketTemplate) {
        log.info("onPostUpdate: {}", ticketTemplate);
        TicketTemplateEntity cloneTicketTemplate = SerializationUtils.clone(ticketTemplate);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TicketTemplateUpdateEvent(cloneTicketTemplate));
    }
    
}
