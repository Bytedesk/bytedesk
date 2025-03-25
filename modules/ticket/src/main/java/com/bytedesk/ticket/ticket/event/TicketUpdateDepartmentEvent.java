/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-18 11:16:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 17:20:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.ticket.ticket.TicketEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TicketUpdateDepartmentEvent extends ApplicationEvent {

    private TicketEntity ticket;

    private String oldDepartmentUid;

    private String newDepartmentUid;

    public TicketUpdateDepartmentEvent(TicketEntity ticket, String oldDepartmentUid, String newDepartmentUid) {
        super(ticket);
        this.ticket = ticket;
        this.oldDepartmentUid = oldDepartmentUid;
        this.newDepartmentUid = newDepartmentUid;
    }
}
