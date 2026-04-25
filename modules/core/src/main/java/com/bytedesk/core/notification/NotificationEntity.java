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
package com.bytedesk.core.notification;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;

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

@Getter
@Setter
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({NotificationEntityListener.class})
@Table(name = "bytedesk_core_notification")
public class NotificationEntity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Title of the notification or announcement.
     */
    private String title;

    /**
     * Main content of the notification.
     */
    private String content;

    /**
     * Notification type (GENERAL, ANNOUNCEMENT, MAINTENANCE, SECURITY).
     */
    @Builder.Default
    @Column(name = "notification_type")
    private String type = NotificationTypeEnum.GENERAL.name();

    /**
     * Current status of the notification (PENDING, PUBLISHED, ARCHIVED, etc.).
     */
    @Builder.Default
    @Column(name = "notification_status")
    private String status = NotificationStatusEnum.UNREAD.name();

    /**
     * Department UID when this notification is scoped to a department.
     */
    private String deptUid;

    /**
     * Creator UID of the notification dispatch action.
     */
    private String creatorUid;

    /**
     * Additional notification data stored in JSON format.
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

}
