/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-19 06:55:22
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

import com.bytedesk.core.assistant.AssistantPermissions;
import com.bytedesk.core.black.BlackPermissions;
import com.bytedesk.core.category.CategoryPermissions;
// import com.bytedesk.core.channel.ChannelPermissions;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.ip.IpPermissions;
import com.bytedesk.core.message.MessagePermissions;
import com.bytedesk.core.push.PushPermissions;
import com.bytedesk.core.rbac.authority.AuthorityPermissions;
import com.bytedesk.core.rbac.organization.OrganizationPermissions;
import com.bytedesk.core.rbac.user.UserPermissions;
import com.bytedesk.core.thread.ThreadPermissions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RoleInitializer {

    private final RoleRestService roleService;

    // 初始化角色, 在 OrganizationInitializer 中调用
    public void init() {
        // 初始化角色
        // 1. 超级管理员
        createSuper();
        // 2. 管理员
        createAdmin();
        // 3. 团队成员
        createMember();
        // 4. 客服
        createAgent();
    }

    private void createSuper() {
        if (roleService.existsByNamePlatform(RoleConsts.ROLE_SUPER)) {
            return;
        }

        RoleRequest roleRequest = RoleRequest.builder()
                .name(RoleConsts.ROLE_SUPER)
                .description("Super")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();

        // 初始化权限前缀数组
        String[] authorities = {
            AuthorityPermissions.AUTHORITY_PREFIX,
            RolePermissions.ROLE_PREFIX,
            OrganizationPermissions.ORGANIZATION_PREFIX,
            UserPermissions.USER_PREFIX,
            AssistantPermissions.ASSISTANT_PREFIX,
            BlackPermissions.BLACK_PREFIX,
            CategoryPermissions.CATEGORY_PREFIX,
            // ChannelPermissions.CHANNEL_PREFIX,
            IpPermissions.IP_PREFIX,
            MessagePermissions.MESSAGE_PREFIX,
            ThreadPermissions.THREAD_PREFIX,
            PushPermissions.PUSH_PREFIX
        };

        // 遍历权限前缀数组
        for (String prefix : authorities) {
            for (PermissionEnum permission : PermissionEnum.values()) {
                String permissionValue = prefix + permission.name();
                roleRequest.getAuthorityUids().add(permissionValue.toLowerCase());
            }
        }
        roleService.create(roleRequest);
    }

    private void createAdmin() {
        if (roleService.existsByNamePlatform(RoleConsts.ROLE_ADMIN)) {
            return;
        }
        RoleRequest roleRequest = RoleRequest.builder()
                .name(RoleConsts.ROLE_ADMIN)
                .description("Admin")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();

        // 初始化权限前缀数组
        String[] authorities = {
            RolePermissions.ROLE_PREFIX,
            OrganizationPermissions.ORGANIZATION_PREFIX,
            BlackPermissions.BLACK_PREFIX,
            CategoryPermissions.CATEGORY_PREFIX,
            IpPermissions.IP_PREFIX,
            MessagePermissions.MESSAGE_PREFIX,
            ThreadPermissions.THREAD_PREFIX,
        };
        // 遍历权限前缀数组
        for (String prefix : authorities) {
            for (PermissionEnum permission : PermissionEnum.values()) {
                String permissionValue = prefix + permission.name();
                roleRequest.getAuthorityUids().add(permissionValue.toLowerCase());
            }
        }
        roleService.create(roleRequest);
    }

    private void createMember() {
        if (roleService.existsByNamePlatform(RoleConsts.ROLE_MEMBER)) {
            return;
        }
        RoleRequest roleRequest = RoleRequest.builder()
                .name(RoleConsts.ROLE_MEMBER)
                .description("Member")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();

        // 初始化权限前缀数组
        String[] authorities = {
            BlackPermissions.BLACK_PREFIX,
            MessagePermissions.MESSAGE_PREFIX,
            ThreadPermissions.THREAD_PREFIX,
        };
        // 遍历权限前缀数组
        for (String prefix : authorities) {
            for (PermissionEnum permission : PermissionEnum.values()) {
                String permissionValue = prefix + permission.name();
                roleRequest.getAuthorityUids().add(permissionValue.toLowerCase());
            }
        }
        roleService.create(roleRequest);
    }

    private void createAgent() {
        if (roleService.existsByNamePlatform(RoleConsts.ROLE_AGENT)) {
            return;
        }
        RoleRequest roleRequest = RoleRequest.builder()
                .name(RoleConsts.ROLE_AGENT)
                .description("Agent")
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        // 初始化权限前缀数组
        String[] authorities = {
            BlackPermissions.BLACK_PREFIX,
            MessagePermissions.MESSAGE_PREFIX,
            ThreadPermissions.THREAD_PREFIX,
        };
        // 遍历权限前缀数组
        for (String prefix : authorities) {
            for (PermissionEnum permission : PermissionEnum.values()) {
                String permissionValue = prefix + permission.name();
                roleRequest.getAuthorityUids().add(permissionValue.toLowerCase());
            }
        }
        roleService.create(roleRequest);
    }


}