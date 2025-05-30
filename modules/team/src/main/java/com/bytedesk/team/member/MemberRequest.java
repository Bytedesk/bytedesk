/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 13:30:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 13:10:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest extends BaseRequest {

    /**
     * 群组的uid，用于查询特定群组的成员
     */
    private String groupUid;

    private String username;

    @NotBlank
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultAvatarUrl();

    /**
     * 用于筛选成员的关键词，可以是昵称等
     */
    private String keyword;

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    private String password;

    @NotBlank
    private String mobile;

    // @NotBlank
    @Email(message = "email format error")
    private String email;

    private String jobNo;

    private String jobTitle; // 职位

    private String seatNo;

    private String telephone;

    private Boolean inviteAccepted;

    @Builder.Default
    private String status = MemberStatusEnum.INVITING.name();

    @Builder.Default
	private Set<String> roleUids = new HashSet<>(); 

    @NotBlank
    private String deptUid;

    // 存储deptUid对应的子部门uid列表
    @Builder.Default
    private Set<String> subDeptUids = new HashSet<>();
}
