/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 12:25:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

/**
 * Team member entity for organizational structure management
 * Manages member profiles, roles, and department assignments
 * 
 * Database Table: bytedesk_team_member
 * Purpose: Stores member information, contact details, and organizational relationships
 */
@Entity
@SuperBuilder
@Accessors(chain = true)
@lombok.Getter
@lombok.Setter
@lombok.ToString
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ MemberEntityListener.class })
@Table(name = "bytedesk_team_member")
public class MemberEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Display name of the team member
     */
    private String nickname;

    /**
     * Profile picture URL for the member
     */
    @Builder.Default
    private String avatar = AvatarConsts.getDefaultUserAvatarUrl();

    /**
     * Brief description or bio of the member
     */
    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    /**
     * Email address of the member
     */
    @Email(message = "email format error")
    private String email;

    /**
     * Mobile phone number (Chinese format supported)
     */
    private String mobile;

    /**
     * Employee job number or ID
     */
    private String jobNo;

    /**
     * Job title or position of the member
     */
    private String jobTitle; // 职位

    /**
     * Seat or desk number of the member
     */
    private String seatNo;

    /**
     * Office telephone number
     */
    private String telephone;

    /**
     * Current status of the member (INVITING, ACTIVE, INACTIVE, etc.)
     */
    @Builder.Default
    private String status = MemberStatusEnum.INVITING.name();

    /**
     * Department UID that this member belongs to
     */
    private String deptUid;

    /**
     * Associated user account for this member
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    /**
     * 重写 equals 方法，仅使用基类中的 uid 字段进行比较
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getUid(), that.getUid());
    }

    /**
     * 重写 hashCode 方法，仅使用基类中的 uid 字段
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUid());
    }
}
