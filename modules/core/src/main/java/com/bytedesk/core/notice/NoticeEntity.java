/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:27:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 17:49:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.MessageStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 用于发布：用户登录通知、会话转接、会话邀请通知，待重构到 MessageEntity
 * 
 * System notice entity for announcements and notifications
 * Different from message type notices - used for system-wide announcements
 * 
 * Database Table: bytedesk_core_notice
 * Purpose: Stores system announcements, alerts, and important notifications
 */
@Getter
@Setter
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({NoticeEntityListener.class})
@Table(name = "bytedesk_core_notice")
public class NoticeEntity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Title of the notice or announcement
     */
    private String title;

    /**
     * Main content of the notice
     */
    private String content;

    /**
     * Type of notice (LOGIN, SYSTEM, MAINTENANCE, etc.)
     */
    @Builder.Default
    @Column(name = "notice_type")
    private String type = NoticeTypeEnum.LOGIN.name();

    /**
     * Current status of the notice (PENDING, PUBLISHED, ARCHIVED, etc.)
     */
    @Builder.Default
    @Column(name = "notice_status")
    private String status = MessageStatusEnum.TRANSFER_PENDING.name();

    /**
     * Additional notice information stored as JSON format
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

}
