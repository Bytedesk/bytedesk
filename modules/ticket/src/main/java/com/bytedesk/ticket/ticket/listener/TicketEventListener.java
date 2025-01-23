/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:52:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 14:59:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.listener;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.consts.TicketConsts;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventListener {

    private final RuntimeService runtimeService;

    @EventListener
    public void handleTicketCreateEvent(TicketCreateEvent event) {
        log.info("TicketEventListener handleTicketCreateEvent: {}", event);
        TicketEntity ticket = event.getTicket();

        // 启动工单流程
        Map<String, Object> variables = new HashMap<>();
        variables.put("ticket", ticket);
        variables.put("reporter", ticket.getReporter());
        
        // 
        runtimeService.startProcessInstanceByKey(TicketConsts.TICKET_PROCESS_KEY, 
            ticket.getId().toString(), 
            variables);
    }

    @EventListener
    public void handleTicketUpdateEvent(TicketUpdateEvent event) {
        log.info("TicketEventListener handleTicketUpdateEvent: {}", event);
    }
    
}

