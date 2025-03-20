/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 07:05:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:44:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import org.springframework.stereotype.Component;

// import com.bytedesk.core.assistant.AssistantPermissions;
import com.bytedesk.core.black.BlackPermissions;
// import com.bytedesk.core.category.CategoryPermissions;
// import com.bytedesk.core.channel.ChannelPermissions;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
// import com.bytedesk.core.ip.IpPermissions;
// import com.bytedesk.core.message.MessagePermissions;
// import com.bytedesk.core.push.PushPermissions;
// import com.bytedesk.core.rbac.organization.OrganizationPermissions;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.rbac.user.UserPermissions;
// import com.bytedesk.core.thread.ThreadPermissions;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuthorityInitializer {

    private AuthorityRestService authorityService;

    // 在 OrganizationInitializer 中调用
    public void init() {

        // 平台权限数组
        String[] authoritiesPlatform = {
            AuthorityPermissions.AUTHORITY_PREFIX,
        };
        for (String prefix : authoritiesPlatform) {
            for (PermissionEnum permission : PermissionEnum.values()) {
                String permissionValue = prefix + permission.name();
                if (authorityService.existsByValue(permissionValue)) {
                    continue;
                }
                AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(I18Consts.I18N_PREFIX + permissionValue)
                    .value(permissionValue)
                    .description("Permission for " + permissionValue)
                    .level(LevelEnum.PLATFORM.name())
                    .build();
                authRequest.setUid(permissionValue.toLowerCase());
                authorityService.create(authRequest);
            }
        }
        
        /// 
        // 组织权限：初始化权限前缀数组
        String[] authoritiesOrganization = {
            RolePermissions.ROLE_PREFIX,
            // OrganizationPermissions.ORGANIZATION_PREFIX,
            UserPermissions.USER_PREFIX,
            // AssistantPermissions.ASSISTANT_PREFIX,
            BlackPermissions.BLACK_PREFIX,
            // CategoryPermissions.CATEGORY_PREFIX,
            // ChannelPermissions.CHANNEL_PREFIX,
            // IpPermissions.IP_PREFIX,
            // MessagePermissions.MESSAGE_PREFIX,
            // ThreadPermissions.THREAD_PREFIX,
            // PushPermissions.PUSH_PREFIX
        };
        // 遍历权限前缀数组
        for (String prefix : authoritiesOrganization) {
            for (PermissionEnum permission : PermissionEnum.values()) {
                String permissionValue = prefix + permission.name();
                if (authorityService.existsByValue(permissionValue)) {
                    continue;
                }
                AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(I18Consts.I18N_PREFIX + permissionValue)
                    .value(permissionValue)
                    .description("Permission for " + permissionValue)
                    .level(LevelEnum.ORGANIZATION.name())
                    .build();
                authRequest.setUid(permissionValue.toLowerCase());
                authorityService.create(authRequest);
            }
        }
    }
}