/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 10:20:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 09:03:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Set;

import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;
import com.bytedesk.core.rbac.role.RoleResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserOrganizationRoleResponse {

    private OrganizationResponseSimple organization;

    private Set<RoleResponse> roles;
}
