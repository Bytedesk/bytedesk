/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-01 14:08:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 14:09:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.event.ThreadCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketThreadEventListener {


    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        log.info("ticket onThreadCreateEvent: {}", event);
    }


    
}
