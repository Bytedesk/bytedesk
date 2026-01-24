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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.bytedesk.core.rbac.authority.AuthorityEntity;

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
        initAuthority();

        // 为默认 ROLE_USER 绑定基础权限
        initRoleUserDefaultAuthorities();

        // 初始化默认角色权限
        initRoleAgentDefaultAuthorities();
        initRoleAdminDefaultAuthorities();
        initRoleSuperDefaultAuthorities();
    }

    private void initRoles() {
        // 0 用户
        createUser();
        // 1. 客服
        createAgent();
        // 4. 组织管理员
        createAdmin();
        // 5. 超级管理员
        createSuper();
        // 2. 工作组管理员
        // createWorkgroupAdmin();
        // 3. 部门管理员
        // createDeptAdmin();
    }

    private void createSuper() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_SUPER_UID)
                .name(RoleConsts.ROLE_SUPER)
                .value(RoleConsts.ROLE_SUPER)
                .description(I18Consts.I18N_ROLE_SUPER_DESCRIPTION)
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }
    
    private void createAdmin() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                .name(RoleConsts.ROLE_ADMIN)
                .value(RoleConsts.ROLE_ADMIN)
                .description(I18Consts.I18N_ROLE_ADMIN_DESCRIPTION)
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void createAgent() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_AGENT_UID)
                .name(RoleConsts.ROLE_AGENT)
                .value(RoleConsts.ROLE_AGENT)
                .description(I18Consts.I18N_ROLE_AGENT_DESCRIPTION)
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void createUser() {
        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_USER_UID)
                .name(RoleConsts.ROLE_USER)
                .value(RoleConsts.ROLE_USER)
                .description(I18Consts.I18N_ROLE_USER_DESCRIPTION)
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleRestService.create(roleRequest);
    }

    private void initAuthority() {
        // 统一汇总待创建的权限值并去重（避免重复 createForPlatform 调用）
        Set<String> authorityValuesToCreate = new LinkedHashSet<>();
        // 
        for (PermissionEnum permission : PermissionEnum.values()) {
            authorityValuesToCreate.add(RolePermissions.ROLE_PREFIX + permission.name());
        }

        // Settings 权限（enterprise/core）。ROLE_ADMIN 需要读，ROLE_SUPER 需要写入/更新
        authorityValuesToCreate.add(RoleAuthorityRules.SETTINGS_READ);
        authorityValuesToCreate.add(RoleAuthorityRules.SETTINGS_CREATE);
        authorityValuesToCreate.add(RoleAuthorityRules.SETTINGS_UPDATE);
        authorityValuesToCreate.add(RoleAuthorityRules.SETTINGS_DELETE);
        authorityValuesToCreate.add(RoleAuthorityRules.SETTINGS_EXPORT);

        // 确保 ROLE_USER 默认权限对应的 authority 全部存在（后续绑定时才能 findByValue 命中）
        // 这里会包含 USER/MESSAGE/THREAD/TICKET 等基础权限，避免在上面重复 add。
        authorityValuesToCreate.addAll(RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES);

        // 确保 ROLE_AGENT 额外默认权限对应的 authority 全部存在（后续绑定时才能 findByValue 命中）
        // 包含留言/客服/队列/快捷回复等，避免在上面逐条 add 造成维护重复。
        authorityValuesToCreate.addAll(RoleAuthorityRules.DEFAULT_ROLE_AGENT_EXTRA_AUTHORITY_VALUES);

        for (String authorityValue : authorityValuesToCreate) {
            authorityRestService.createForPlatform(authorityValue);
        }
    }

    /**
     * ROLE_AGENT: 知识库（kbase）模块所有 READ 权限
     */
    private void initRoleAgentDefaultAuthorities() {
        Set<String> authorityUids = authorityRestService.findAllActive().stream()
            .filter(a -> RoleAuthorityRules.isKbaseReadPermission(a.getValue()))
                .map(AuthorityEntity::getUid)
                .collect(Collectors.toSet());

        // 追加 ROLE_AGENT 额外默认权限
        for (String authorityValue : RoleAuthorityRules.DEFAULT_ROLE_AGENT_EXTRA_AUTHORITY_VALUES) {
            addAuthorityUidIfExists(authorityUids, authorityValue);
        }

        if (authorityUids.isEmpty()) {
            return;
        }

        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_AGENT_UID)
                .authorityUids(authorityUids)
                .build();
        roleRestService.addAuthoritiesSystem(roleRequest);
    }

    /**
     * ROLE_ADMIN: 除 settings 写入/更新（CREATE/UPDATE）之外的所有权限
     */
    private void initRoleAdminDefaultAuthorities() {
        Set<String> authorityUids = authorityRestService.findAllActive().stream()
            .filter(a -> a != null && !RoleAuthorityRules.isAdminExcludedPermission(a.getValue()))
                .map(AuthorityEntity::getUid)
                .collect(Collectors.toSet());

        if (authorityUids.isEmpty()) {
            return;
        }

        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                .authorityUids(authorityUids)
                .build();
        roleRestService.addAuthoritiesSystem(roleRequest);
    }

    /**
     * ROLE_SUPER: 具备 ROLE_ADMIN 权限 + settings 写入/更新权限（实际为全量权限）
     */
    private void initRoleSuperDefaultAuthorities() {
        Set<String> authorityUids = authorityRestService.findAllActive().stream()
                .map(AuthorityEntity::getUid)
                .collect(Collectors.toSet());

        if (authorityUids.isEmpty()) {
            return;
        }

        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_SUPER_UID)
                .authorityUids(authorityUids)
                .build();
        roleRestService.addAuthoritiesSystem(roleRequest);
    }

    private void initRoleUserDefaultAuthorities() {
        Set<String> authorityUids = new HashSet<>();

        // ROLE_USER 默认权限统一由 RoleAuthorityRules 维护：后续新增/删减只需改规则即可
        for (String authorityValue : RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES) {
            addAuthorityUidIfExists(authorityUids, authorityValue);
        }

        if (authorityUids.isEmpty()) {
            return;
        }

        RoleRequest roleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_USER_UID)
                .authorityUids(authorityUids)
                .build();
        roleRestService.addAuthoritiesSystem(roleRequest);
    }

    private void addAuthorityUidIfExists(Set<String> authorityUids, String authorityValue) {
        authorityRestService.findByValue(authorityValue)
                .map(AuthorityEntity::getUid)
                .ifPresent(authorityUids::add);
    }
    
}