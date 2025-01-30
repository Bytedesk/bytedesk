/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:53:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 14:56:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.listener;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.event.TicketUpdateEvent;
import com.bytedesk.ticket.ticket.TicketEntity;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketEntityListener {

    @PostPersist
    public void postPersist(TicketEntity ticket) {
        log.info("TicketEntityListener postPersist: {}", ticket);
        TicketEntity cloneTicket = SerializationUtils.clone(ticket);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TicketCreateEvent(cloneTicket));
    }

    @PostUpdate
    public void postUpdate(TicketEntity ticket) {
        log.info("TicketEntityListener postUpdate: {}", ticket);
        TicketEntity cloneTicket = SerializationUtils.clone(ticket);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TicketUpdateEvent(cloneTicket));
    }
    
}
