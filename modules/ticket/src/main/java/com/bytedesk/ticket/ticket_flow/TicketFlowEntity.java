/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 10:04:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 13:47:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_flow;

import com.bytedesk.core.base.BaseEntity;

// import jakarta.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
// @Entity(name = "bytedesk_ticket_flow")
public class TicketFlowEntity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    
    private String name;
    private String description;
    private String status;
    private String type;
    private String flow;
    private String flowType;
    private String flowStatus;
}