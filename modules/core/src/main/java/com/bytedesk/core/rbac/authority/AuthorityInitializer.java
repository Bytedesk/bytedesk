/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 07:05:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 13:42:40
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

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuthorityInitializer {

    // private AuthorityRestService authorityService;

    // 在 OrganizationInitializer 中调用
    // public void init() {

    //     // 平台权限数组
    //     // String[] authoritiesPlatform = {
    //     //     AuthorityPermissions.AUTHORITY_PREFIX,
    //     // };
    //     // for (String prefix : authoritiesPlatform) {
    //     // for (PermissionEnum permission : PermissionEnum.values()) {
    //     //     String permissionValue = AuthorityPermissions.AUTHORITY_PREFIX + permission.name();
    //     //     authorityService.createForPlatform(permissionValue);
    //     // }
    //     // }
        
    //     /// 
    //     // 组织权限：初始化权限前缀数组
    //     // String[] authoritiesOrganization = {
    //     //     RolePermissions.ROLE_PREFIX,
    //     //     // OrganizationPermissions.ORGANIZATION_PREFIX,
    //     //     // UserPermissions.USER_PREFIX,
    //     //     // AssistantPermissions.ASSISTANT_PREFIX,
    //     //     // BlackPermissions.BLACK_PREFIX,
    //     //     // CategoryPermissions.CATEGORY_PREFIX,
    //     //     // ChannelPermissions.CHANNEL_PREFIX,
    //     //     // IpPermissions.IP_PREFIX,
    //     //     // MessagePermissions.MESSAGE_PREFIX,
    //     //     // ThreadPermissions.THREAD_PREFIX,
    //     //     // PushPermissions.PUSH_PREFIX
    //     // };
    //     // // 遍历权限前缀数组
    //     // for (String prefix : authoritiesOrganization) {
    //     // for (PermissionEnum permission : PermissionEnum.values()) {
    //     //     String permissionValue = RolePermissions.ROLE_PREFIX + permission.name();
    //     //     authorityService.createForPlatform(permissionValue);
    //     // }
    //     // }
    // }
}