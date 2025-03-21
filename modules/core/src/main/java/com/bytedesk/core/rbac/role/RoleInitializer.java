/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 12:17:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRestService roleService;

    private final AuthorityRestService authorityService;

    // 初始化角色, 在 OrganizationInitializer 中调用
    public void init() {
        // 初始化角色
        initRoles();
        // 
        initPermissions();
    }

    private void initRoles() {
        // 4. 客服
        createAgent();
        // 3. 团队成员
        createMember();
        // 2. 管理员
        createAdmin();
        // 1. 超级管理员
        createSuper();
    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = RolePermissions.ROLE_PREFIX + permission.name();
            authorityService.createForPlatform(permissionValue);
        }
    }

    private void createSuper() {

        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_SUPER_UID)
                .name(RoleConsts.ROLE_SUPER)
                .description("Super")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleService.create(roleRequest);
    }
    private void createAdmin() {

        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                .name(RoleConsts.ROLE_ADMIN)
                .description("Admin")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleService.create(roleRequest);
    }

    private void createMember() {
   
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_MEMBER_UID)
                .name(RoleConsts.ROLE_MEMBER)
                .description("Member")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleService.create(roleRequest);
    }

    private void createAgent() {
  
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_AGENT_UID)
                .name(RoleConsts.ROLE_AGENT)
                .description("Agent")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleService.create(roleRequest);
    }


}