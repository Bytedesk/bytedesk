/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 15:56:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.attachment;
import java.time.LocalDateTime;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.ticket.comment.TicketCommentEntity;
import com.bytedesk.ticket.ticket.TicketEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity(name = "bytedesk_ticket_attachment")
@NoArgsConstructor
@AllArgsConstructor
public class TicketAttachmentEntity extends BaseEntity {
    
    @ManyToOne
    private TicketEntity ticket;         // 关联的工单

    @ManyToOne
    private TicketCommentEntity comment;

    // 
    private String fileName;        // 文件名
    // 
    private String fileType;        // 文件类型
    // 
    private String filePath;        // 文件路径
    // 
    private Long fileSize;         // 文件大小
    // 
    @Builder.Default
    private LocalDateTime uploadTime = LocalDateTime.now();       // 上传时间
} 