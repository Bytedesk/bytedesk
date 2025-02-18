/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-18 11:16:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 11:18:05
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
public class TicketUpdateWorkgroupEvent extends ApplicationEvent {

    private TicketEntity ticket;

    private String oldWorkgroupUid;

    private String newWorkgroupUid;

    public TicketUpdateWorkgroupEvent(TicketEntity ticket, String oldWorkgroupUid, String newWorkgroupUid) {
        super(ticket);
        this.ticket = ticket;
        this.oldWorkgroupUid = oldWorkgroupUid;
        this.newWorkgroupUid = newWorkgroupUid;
    }
}
