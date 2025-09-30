/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 16:04:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.comment;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.ticket.TicketEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity(name = "bytedesk_ticket_comment")
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommentEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    
    @ManyToOne
    private TicketEntity ticket;         // 关联的工单

    // 评论内容
    private String content;         // 评论内容

    // 评论人
    @ManyToOne
    private AgentEntity author;          // 评论人

    // 评论附件
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketAttachmentEntity> attachments;
} 