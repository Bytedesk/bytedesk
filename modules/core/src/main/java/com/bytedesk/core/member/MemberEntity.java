/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-17 18:06:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.converter.StringSetConverter;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

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

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultUserAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    private String jobNo;

    private String jobTitle; // 职位

    private String seatNo;

    private String telephone;

    @Email(message = "email format error")
    private String email;

    // only support chinese mobile number
    private String mobile;

    @Builder.Default
    private String status = MemberStatusEnum.INVITING.name();

    @Builder.Default
    @Convert(converter = StringSetConverter.class)
    @Column(length = 512)
	private Set<String> roleUids = new HashSet<>(); 

    // 一个人只能属于一个部门，一个部门可以有多个成员
    private String deptUid;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

}
