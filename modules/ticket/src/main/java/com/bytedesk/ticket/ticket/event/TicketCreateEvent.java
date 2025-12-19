/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 14:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-18 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.event;

import com.bytedesk.ticket.ticket.TicketEntity;

/**
 * Event published when a new ticket is created.
 */
public class TicketCreateEvent extends AbstractTicketEvent {
    
    private static final long serialVersionUID = 1L;

    public TicketCreateEvent(TicketEntity ticket) {
        super(ticket, ticket);
    }
}
