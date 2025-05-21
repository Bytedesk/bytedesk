/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-18 17:06:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 12:16:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.message;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Entity
// @Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
// @NoArgsConstructor
@Table(name = "bytedesk_ticket_message")
public class TicketMessageEntity extends BaseEntity {

    // private String ticketUid;
    // private String processInstanceId;
    // private String type;
    // private String assignee;
    // private String description;

    // private TicketMessageStatusEnum status;
    // private Integer retryCount;
}