/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 15:26:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.model;
import java.time.LocalDateTime;
import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "bytedesk_ticket_comment")
public class TicketComment extends BaseEntity {
    
    @ManyToOne
    private TicketEntity ticket;         // 关联的工单
    private LocalDateTime commentTime;      // 评论时间
    private String content;         // 评论内容
    private String author;          // 评论人
} 