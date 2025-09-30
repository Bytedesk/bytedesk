/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 17:04:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.attachment;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.ticket.comment.TicketCommentEntity;
import com.bytedesk.ticket.ticket.TicketEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true, exclude = { "ticket", "comment" })
@Entity(name = "bytedesk_ticket_attachment")
@NoArgsConstructor
@AllArgsConstructor
public class TicketAttachmentEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    
    @ManyToOne(cascade = CascadeType.PERSIST)
    private TicketEntity ticket; 

    @ManyToOne
    private TicketCommentEntity comment;

    @ManyToOne
    private UploadEntity upload;
} 