/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:45:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:24:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

public class BasePermissions {

    protected static final String HAS_AUTHORITY = "hasAuthority('%s')";
    protected static final String HAS_ANY_AUTHORITY = "hasAnyAuthority(%s)";
    // 
    protected static final String HAS_ROLE = "hasRole('%s')";
    protected static final String HAS_ANY_ROLE = "hasAnyRole(%s)";
    
    // 基础操作类型
    protected static final String READ = "READ";
    protected static final String CREATE = "CREATE";
    protected static final String UPDATE = "UPDATE";
    protected static final String DELETE = "DELETE";
    protected static final String EXPORT = "EXPORT";
    protected static final String ANY = "ANY";

    // 层级前缀 - 对应 LevelEnum
    protected static final String LEVEL_PLATFORM = "PLATFORM";
    protected static final String LEVEL_ORGANIZATION = "ORGANIZATION";
    protected static final String LEVEL_DEPARTMENT = "DEPARTMENT";
    protected static final String LEVEL_WORKGROUP = "WORKGROUP";
    protected static final String LEVEL_AGENT = "AGENT";
    protected static final String LEVEL_ROBOT = "ROBOT";
    protected static final String LEVEL_GROUP = "GROUP";
    protected static final String LEVEL_USER = "USER";

    protected static String hasAuthority(String authority) {
        return String.format(HAS_AUTHORITY, authority);
    }

    protected static String hasAnyAuthority(String... authorities) {
        return String.format(HAS_ANY_AUTHORITY, String.join(", ", authorities));
    }

    /**
     * 生成模块权限字符串，格式: MODULE_ACTION
     * 例如: TAG_CREATE, TAG_READ
     */
    protected static String buildPermission(String module, String action) {
        return module + "_" + action;
    }

    /**
     * 生成带层级的权限字符串，格式: MODULE_LEVEL_ACTION
     * 例如: TAG_PLATFORM_CREATE, TAG_ORGANIZATION_READ
     */
    protected static String buildLevelPermission(String module, String level, String action) {
        return module + "_" + level + "_" + action;
    }

    /**
     * 生成 hasAuthority 表达式，格式: hasAuthority('MODULE_ACTION')
     */
    protected static String hasPermission(String module, String action) {
        return hasAuthority(buildPermission(module, action));
    }

    /**
     * 生成带层级的 hasAuthority 表达式
     */
    protected static String hasLevelPermission(String module, String level, String action) {
        return hasAuthority(buildLevelPermission(module, level, action));
    }

    /**
     * 生成 hasAnyAuthority 表达式，用于同时支持多个层级权限
     * 例如: hasAnyAuthority('TAG_PLATFORM_CREATE', 'TAG_ORGANIZATION_CREATE')
     */
    protected static String hasAnyLevelPermission(String module, String action, String... levels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < levels.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("'").append(buildLevelPermission(module, levels[i], action)).append("'");
        }
        return String.format(HAS_ANY_AUTHORITY, sb.toString());
    }
}