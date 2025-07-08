/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:31:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 16:08:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * System action log entity
 * Represents user actions and system events for audit and monitoring purposes
 * 
 * Database Table: bytedesk_core_action
 * Purpose: Tracks user activities, system events, and security-related actions
 */
@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ ActionEntityListener.class })
@Table(name = "bytedesk_core_action")
public class ActionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Title or name of the action performed
     */
    private String title;

    /**
     * The specific action or operation performed
     */
    private String action;

    /**
     * Detailed description of the action
     */
    private String description;

    /**
     * IP address of the user who performed the action
     */
    private String ip;

    /**
     * Geographic location derived from the IP address
     */
    private String ipLocation;

    /**
     * Type of action (LOG, LOGIN, LOGOUT, CREATE, UPDATE, DELETE, etc.)
     */
    @Builder.Default
    // @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private String type = ActionTypeEnum.LOG.name();

    /**
     * Additional data or context information about the action in JSON format
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * User who performed the action
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

}
