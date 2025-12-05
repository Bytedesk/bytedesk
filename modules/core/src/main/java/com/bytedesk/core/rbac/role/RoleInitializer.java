/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 17:56:51
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

    private final RoleRestService roleRestService;

    private final AuthorityRestService authorityRestService;

    // 初始化角色, 在 OrganizationInitializer 中调用
    public void init() {
        // 初始化角色
        initRoles();
        // 
        initPermissions();
    }

    private void initRoles() {
        // 0 用户
        createUser();
        // 1. 客服
        createAgent();
        // 2. 工作组管理员
        createWorkgroupAdmin();
        // 3. 部门管理员
        createDeptAdmin();
        // 4. 组织管理员
        createAdmin();
        // 5. 超级管理员
        createSuper();
    }

    private void createSuper() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_SUPER_UID)
                .name(RoleConsts.ROLE_SUPER)
                .description("Super")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }
    
    private void createAdmin() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                .name(RoleConsts.ROLE_ADMIN)
                .description("Organization Admin")
                .level(LevelEnum.ORGANIZATION.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void createDeptAdmin() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_DEPT_ADMIN_UID)
                .name(RoleConsts.ROLE_DEPT_ADMIN)
                .description("Department Admin")
                .level(LevelEnum.DEPARTMENT.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void createWorkgroupAdmin() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_WORKGROUP_ADMIN_UID)
                .name(RoleConsts.ROLE_WORKGROUP_ADMIN)
                .description("Workgroup Admin")
                .level(LevelEnum.WORKGROUP.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void createAgent() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_AGENT_UID)
                .name(RoleConsts.ROLE_AGENT)
                .description("Agent")
                .level(LevelEnum.AGENT.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void createUser() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_USER_UID)
                .name(RoleConsts.ROLE_USER)
                .description("User")
                .level(LevelEnum.USER.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = RolePermissions.ROLE_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }


}