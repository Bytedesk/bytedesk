/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_comment;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * TicketComment entity - 工单备注/评论
 * Supports internal remarks (INTERNAL) and external comments (EXTERNAL).
 * 
 * Database Table: bytedesk_ticket_comment
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "attachments" })
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_comment")
public class TicketCommentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联工单 uid
     */
    @Column(name = "ticket_uid")
    private String ticketUid;

    /**
     * 评论/备注内容 (TEXT)
     */
    @Builder.Default
    @Column(name = "content", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content = "";

    /**
     * 类型: INTERNAL(内部备注) / EXTERNAL(外部评论)
     */
    @Builder.Default
    @Column(name = "ticket_comment_type")
    private String type = TicketCommentTypeEnum.INTERNAL.name();

    /**
     * 提交人信息快照 (JSON: {"uid","nickname","avatar"})
     */
    @Builder.Default
    @Column(name = "user_info", columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    private String user = "{}";

    /**
     * 附件列表
     */
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TicketAttachmentEntity> attachments = new HashSet<>();
}
