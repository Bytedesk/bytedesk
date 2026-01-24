/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.permission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限检查服务
 * 用于检查用户对不同层级数据的访问权限
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final AuthService authService;

    /**
     * 获取当前用户的所有权限
     */
    public Set<String> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return new HashSet<>();
        }
        
        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        return authorities;
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public boolean hasAuthority(String authority) {
        return getCurrentUserAuthorities().contains(authority);
    }

    /**
     * 检查当前用户是否有指定权限中的任意一个
     */
    public boolean hasAnyAuthority(String... authorities) {
        Set<String> userAuthorities = getCurrentUserAuthorities();
        for (String authority : authorities) {
            if (userAuthorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查用户是否有权限访问指定层级的数据
     * 
     * @param module 模块名称，如 TAG, QUICKREPLY 等
     * @param action 操作类型，如 READ, CREATE, UPDATE, DELETE
     * @param dataLevel 数据的层级
     * @return 是否有权限
     */
    public boolean hasLevelPermission(String module, String action, String dataLevel) {
        UserEntity user = authService.getUser();
        if (user == null) {
            return false;
        }
        
        // 超级管理员拥有所有权限
        if (user.isSuperUser()) {
            return true;
        }
        
        Set<String> authorities = getCurrentUserAuthorities();
        
        // 检查层级权限（高层级可以访问低层级数据）
        // 层级顺序: PLATFORM > ORGANIZATION > DEPARTMENT > WORKGROUP > AGENT/ROBOT > USER
        String[] levelHierarchy = {
            LevelEnum.PLATFORM.name(),
            LevelEnum.ORGANIZATION.name(),
            LevelEnum.DEPARTMENT.name(),
            LevelEnum.WORKGROUP.name(),
            LevelEnum.AGENT.name(),
            LevelEnum.ROBOT.name(),
            LevelEnum.USER.name()
        };
        
        int dataLevelIndex = indexOf(levelHierarchy, dataLevel);
        if (dataLevelIndex == -1) {
            // 未知层级，默认拒绝
            return false;
        }
        
        // 检查用户是否有高于或等于数据层级的权限
        for (int i = 0; i <= dataLevelIndex; i++) {
            String levelPermission = module + "_" + levelHierarchy[i] + "_" + action;
            if (authorities.contains(levelPermission)) {
                return true;
            }
        }

        // 兼容“统一权限”（不在权限字符串中编码层级）的模块
        // 例如：CALENDAR_CREATE / DEPARTMENT_READ
        String unifiedPermission = module + "_" + action;
        if (authorities.contains(unifiedPermission)) {
            // 默认视为组织级可访问/可操作（及以下层级）
            int organizationIndex = indexOf(levelHierarchy, LevelEnum.ORGANIZATION.name());
            return organizationIndex != -1 && dataLevelIndex >= organizationIndex;
        }
        
        return false;
    }

    /**
     * 检查用户是否有权限操作指定实体
     * 
     * @param module 模块名称
     * @param action 操作类型
     * @param entity 数据实体
     * @return 是否有权限
     */
    public boolean hasEntityPermission(String module, String action, BaseEntity entity) {
        if (entity == null) {
            return false;
        }
        
        UserEntity user = authService.getUser();
        if (user == null) {
            return false;
        }
        
        // 超级管理员拥有所有权限
        if (user.isSuperUser()) {
            return true;
        }
        
        String entityLevel = entity.getLevel();
        String entityOrgUid = entity.getOrgUid();
        // String entityUserUid = entity.getUserUid();
        
        // 1. 检查是否有层级权限
        if (!hasLevelPermission(module, action, entityLevel)) {
            return false;
        }
        
        // 2. 检查组织归属
        String userOrgUid = user.getOrgUid();
        if (entityOrgUid != null && !entityOrgUid.equals(userOrgUid)) {
            // 平台级别的数据可以被任何有PLATFORM权限的用户访问
            if (!LevelEnum.PLATFORM.name().equals(entityLevel)) {
                return false;
            }
        }
        
        // 3. 用户级别的数据只能由创建者或有更高权限的用户访问
        // if (LevelEnum.USER.name().equals(entityLevel)) {
        //     if (entityUserUid != null && !entityUserUid.equals(user.getUid())) {
        //         // 检查是否有组织级别以上的权限
        //         if (!hasLevelPermission(module, action, LevelEnum.ORGANIZATION.name())) {
        //             return false;
        //         }
        //     }
        // }
        
        return true;
    }

    /**
     * 获取用户可访问的数据层级列表
     * 
     * @param module 模块名称
     * @param action 操作类型
     * @return 可访问的层级列表
     */
    public Set<String> getAccessibleLevels(String module, String action) {
        UserEntity user = authService.getUser();
        Set<String> accessibleLevels = new HashSet<>();
        
        if (user == null) {
            return accessibleLevels;
        }
        
        // 超级管理员可以访问所有层级
        if (user.isSuperUser()) {
            accessibleLevels.addAll(Arrays.asList(
                LevelEnum.PLATFORM.name(),
                LevelEnum.ORGANIZATION.name(),
                LevelEnum.DEPARTMENT.name(),
                LevelEnum.WORKGROUP.name(),
                LevelEnum.AGENT.name(),
                LevelEnum.ROBOT.name(),
                LevelEnum.USER.name()
            ));
            return accessibleLevels;
        }
        
        Set<String> authorities = getCurrentUserAuthorities();
        
        // 检查各层级权限
        String[] levels = {
            LevelEnum.PLATFORM.name(),
            LevelEnum.ORGANIZATION.name(),
            LevelEnum.DEPARTMENT.name(),
            LevelEnum.WORKGROUP.name(),
            LevelEnum.AGENT.name(),
            LevelEnum.ROBOT.name(),
            LevelEnum.USER.name()
        };
        
        for (int i = 0; i < levels.length; i++) {
            String levelPermission = module + "_" + levels[i] + "_" + action;
            if (authorities.contains(levelPermission)) {
                // 有该层级权限，可以访问该层级及以下的所有数据
                for (int j = i; j < levels.length; j++) {
                    accessibleLevels.add(levels[j]);
                }
                break;
            }
        }

        // 兼容“统一权限”（不在权限字符串中编码层级）的模块
        // 例如：DEPARTMENT_READ / MEMBER_READ
        if (accessibleLevels.isEmpty()) {
            String unifiedPermission = module + "_" + action;
            if (authorities.contains(unifiedPermission)) {
                // 默认视为组织级可访问（及以下层级）
                boolean startAdding = false;
                for (String level : levels) {
                    if (LevelEnum.ORGANIZATION.name().equals(level)) {
                        startAdding = true;
                    }
                    if (startAdding) {
                        accessibleLevels.add(level);
                    }
                }
            }
        }
        
        // 用户始终可以访问自己创建的数据
        // accessibleLevels.add(LevelEnum.USER.name());
        
        return accessibleLevels;
    }

    /**
     * 检查用户是否可以创建指定层级的数据
     */
    public boolean canCreateAtLevel(String module, String level) {
        return hasLevelPermission(module, "CREATE", level);
    }

    /**
     * 检查用户是否可以更新指定层级的数据
     */
    public boolean canUpdateAtLevel(String module, String level) {
        return hasLevelPermission(module, "UPDATE", level);
    }

    /**
     * 检查用户是否可以删除指定层级的数据
     */
    public boolean canDeleteAtLevel(String module, String level) {
        return hasLevelPermission(module, "DELETE", level);
    }

    /**
     * 检查用户是否可以读取指定层级的数据
     */
    public boolean canReadAtLevel(String module, String level) {
        return hasLevelPermission(module, "READ", level);
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
