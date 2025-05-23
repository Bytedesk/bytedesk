/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 09:51:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.bytedesk.core.base.BaseResponse;
// import com.bytedesk.core.rbac.organization.OrganizationResponse;
import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.user.UserEntity.Sex;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;
    // 
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    private String country;
    private String avatar;
    private String description;
    private String platform;
    //
    private Sex sex;
    private Boolean enabled;
    private Boolean superUser;
    private Boolean emailVerified;
    private Boolean mobileVerified;
    //
    private OrganizationResponseSimple currentOrganization;
    private Set<RoleResponse> currentRoles;
    private Set<UserOrganizationRoleResponse> userOrganizationRoles;
    private Set<GrantedAuthority> authorities;
}
