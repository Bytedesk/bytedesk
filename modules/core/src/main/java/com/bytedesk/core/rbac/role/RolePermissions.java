/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 21:13:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import com.bytedesk.core.base.BasePermissions;

public class RolePermissions extends BasePermissions {

    // 模块前缀
    public static final String ROLE_PREFIX = "ROLE_";

    // 平台级权限
    public static final String ROLE_PLATFORM_READ = "ROLE_PLATFORM_READ";
    public static final String ROLE_PLATFORM_CREATE = "ROLE_PLATFORM_CREATE";
    public static final String ROLE_PLATFORM_UPDATE = "ROLE_PLATFORM_UPDATE";
    public static final String ROLE_PLATFORM_DELETE = "ROLE_PLATFORM_DELETE";
    public static final String ROLE_PLATFORM_EXPORT = "ROLE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String ROLE_ORGANIZATION_READ = "ROLE_ORGANIZATION_READ";
    public static final String ROLE_ORGANIZATION_CREATE = "ROLE_ORGANIZATION_CREATE";
    public static final String ROLE_ORGANIZATION_UPDATE = "ROLE_ORGANIZATION_UPDATE";
    public static final String ROLE_ORGANIZATION_DELETE = "ROLE_ORGANIZATION_DELETE";
    public static final String ROLE_ORGANIZATION_EXPORT = "ROLE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String ROLE_DEPARTMENT_READ = "ROLE_DEPARTMENT_READ";
    public static final String ROLE_DEPARTMENT_CREATE = "ROLE_DEPARTMENT_CREATE";
    public static final String ROLE_DEPARTMENT_UPDATE = "ROLE_DEPARTMENT_UPDATE";
    public static final String ROLE_DEPARTMENT_DELETE = "ROLE_DEPARTMENT_DELETE";
    public static final String ROLE_DEPARTMENT_EXPORT = "ROLE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String ROLE_WORKGROUP_READ = "ROLE_WORKGROUP_READ";
    public static final String ROLE_WORKGROUP_CREATE = "ROLE_WORKGROUP_CREATE";
    public static final String ROLE_WORKGROUP_UPDATE = "ROLE_WORKGROUP_UPDATE";
    public static final String ROLE_WORKGROUP_DELETE = "ROLE_WORKGROUP_DELETE";
    public static final String ROLE_WORKGROUP_EXPORT = "ROLE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String ROLE_AGENT_READ = "ROLE_AGENT_READ";
    public static final String ROLE_AGENT_CREATE = "ROLE_AGENT_CREATE";
    public static final String ROLE_AGENT_UPDATE = "ROLE_AGENT_UPDATE";
    public static final String ROLE_AGENT_DELETE = "ROLE_AGENT_DELETE";
    public static final String ROLE_AGENT_EXPORT = "ROLE_AGENT_EXPORT";

    // 用户级权限
    public static final String ROLE_USER_READ = "ROLE_USER_READ";
    public static final String ROLE_USER_CREATE = "ROLE_USER_CREATE";
    public static final String ROLE_USER_UPDATE = "ROLE_USER_UPDATE";
    public static final String ROLE_USER_DELETE = "ROLE_USER_DELETE";
    public static final String ROLE_USER_EXPORT = "ROLE_USER_EXPORT";

    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_ROLE_READ_ANY_LEVEL = "hasAnyAuthority('ROLE_PLATFORM_READ', 'ROLE_ORGANIZATION_READ', 'ROLE_DEPARTMENT_READ', 'ROLE_WORKGROUP_READ', 'ROLE_AGENT_READ', 'ROLE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_ROLE_CREATE_ANY_LEVEL = "hasAnyAuthority('ROLE_PLATFORM_CREATE', 'ROLE_ORGANIZATION_CREATE', 'ROLE_DEPARTMENT_CREATE', 'ROLE_WORKGROUP_CREATE', 'ROLE_AGENT_CREATE', 'ROLE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_ROLE_UPDATE_ANY_LEVEL = "hasAnyAuthority('ROLE_PLATFORM_UPDATE', 'ROLE_ORGANIZATION_UPDATE', 'ROLE_DEPARTMENT_UPDATE', 'ROLE_WORKGROUP_UPDATE', 'ROLE_AGENT_UPDATE', 'ROLE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_ROLE_DELETE_ANY_LEVEL = "hasAnyAuthority('ROLE_PLATFORM_DELETE', 'ROLE_ORGANIZATION_DELETE', 'ROLE_DEPARTMENT_DELETE', 'ROLE_WORKGROUP_DELETE', 'ROLE_AGENT_DELETE', 'ROLE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_ROLE_EXPORT_ANY_LEVEL = "hasAnyAuthority('ROLE_PLATFORM_EXPORT', 'ROLE_ORGANIZATION_EXPORT', 'ROLE_DEPARTMENT_EXPORT', 'ROLE_WORKGROUP_EXPORT', 'ROLE_AGENT_EXPORT', 'ROLE_USER_EXPORT')";

    // 角色检查常量
    public static final String ROLE_SUPER = "hasRole('SUPER')";
    public static final String ROLE_ADMIN = "hasAnyRole('SUPER', 'ADMIN')";
    public static final String ROLE_AGENT = "hasAnyRole('SUPER', 'ADMIN', 'AGENT')";

}
