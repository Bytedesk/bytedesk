/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 13:30:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 10:43:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
// import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest extends BaseRequest {

    @NotBlank
    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

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
    private String status = MemberStatusEnum.PENDING.name();

    @Builder.Default
	private Set<String> roleUids = new HashSet<>(); 

    private String userUid;

    @NotBlank
    private String deptUid;
    // 
    // @NotBlank
    // private String orgUid;
}
