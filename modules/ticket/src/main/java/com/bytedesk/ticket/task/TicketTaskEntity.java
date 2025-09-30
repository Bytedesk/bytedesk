/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-21 09:58:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-24 23:52:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.task;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bytedesk_ticket_task")
public class TicketTaskEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    private String title;

    private String description;

    private String status;

    private String priority;

    private String assignee;

    private String reporter;
    
}
