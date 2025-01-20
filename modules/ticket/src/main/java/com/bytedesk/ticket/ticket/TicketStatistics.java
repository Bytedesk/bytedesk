/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 15:07:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 15:25:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "bytedesk_ticket_statistics")
public class TicketStatistics extends BaseEntity {

    private long totalTickets;
    private long openTickets;
    private long closedTickets;
    private double averageResolutionTime;
    private long slaBreaches;

} 