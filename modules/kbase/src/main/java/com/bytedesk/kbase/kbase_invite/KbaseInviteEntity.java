/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:56:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase_invite;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Knowledge base invite entity for member invitation management
 * Manages invitations to join knowledge bases and collaboration spaces
 * 
 * Database Table: bytedesk_kbase_invite
 * Purpose: Stores invitation configurations and member access management
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({KbaseInviteEntityListener.class})
@Table(name = "bytedesk_kbase_invite")
public class KbaseInviteEntity extends BaseEntity {

    /**
     * Name of the invitation
     */
    private String name;

    /**
     * Description of the invitation
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of invitation (CUSTOMER, MEMBER, etc.)
     */
    @Builder.Default
    @Column(name = "tag_type")
    private String type = KbaseInviteTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the invitation display
     */
    @Builder.Default
    @Column(name = "tag_color")
    private String color = "red";

    /**
     * Display order of the invitation
     */
    @Builder.Default
    @Column(name = "tag_order")
    private Integer order = 0;

    // @Builder.Default
    // private String level = LevelEnum.ORGANIZATION.name();

    // @Builder.Default
    // private String platform = PlatformEnum.BYTEDESK.name();

    // private String userUid;
}
