/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:56:40
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
package com.bytedesk.core.rbac.user;

import com.bytedesk.core.base.BasePermissions;

public class UserPermissions extends BasePermissions {

    // 模块前缀
    public static final String USER_PREFIX = "USER_";

    // 平台级权限
    public static final String USER_PLATFORM_READ = "USER_PLATFORM_READ";
    public static final String USER_PLATFORM_CREATE = "USER_PLATFORM_CREATE";
    public static final String USER_PLATFORM_UPDATE = "USER_PLATFORM_UPDATE";
    public static final String USER_PLATFORM_DELETE = "USER_PLATFORM_DELETE";
    public static final String USER_PLATFORM_EXPORT = "USER_PLATFORM_EXPORT";

    // 组织级权限
    public static final String USER_ORGANIZATION_READ = "USER_ORGANIZATION_READ";
    public static final String USER_ORGANIZATION_CREATE = "USER_ORGANIZATION_CREATE";
    public static final String USER_ORGANIZATION_UPDATE = "USER_ORGANIZATION_UPDATE";
    public static final String USER_ORGANIZATION_DELETE = "USER_ORGANIZATION_DELETE";
    public static final String USER_ORGANIZATION_EXPORT = "USER_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String USER_DEPARTMENT_READ = "USER_DEPARTMENT_READ";
    public static final String USER_DEPARTMENT_CREATE = "USER_DEPARTMENT_CREATE";
    public static final String USER_DEPARTMENT_UPDATE = "USER_DEPARTMENT_UPDATE";
    public static final String USER_DEPARTMENT_DELETE = "USER_DEPARTMENT_DELETE";
    public static final String USER_DEPARTMENT_EXPORT = "USER_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String USER_WORKGROUP_READ = "USER_WORKGROUP_READ";
    public static final String USER_WORKGROUP_CREATE = "USER_WORKGROUP_CREATE";
    public static final String USER_WORKGROUP_UPDATE = "USER_WORKGROUP_UPDATE";
    public static final String USER_WORKGROUP_DELETE = "USER_WORKGROUP_DELETE";
    public static final String USER_WORKGROUP_EXPORT = "USER_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String USER_AGENT_READ = "USER_AGENT_READ";
    public static final String USER_AGENT_CREATE = "USER_AGENT_CREATE";
    public static final String USER_AGENT_UPDATE = "USER_AGENT_UPDATE";
    public static final String USER_AGENT_DELETE = "USER_AGENT_DELETE";
    public static final String USER_AGENT_EXPORT = "USER_AGENT_EXPORT";
    // 用户级权限
    public static final String USER_USER_READ = "USER_USER_READ";
    public static final String USER_USER_CREATE = "USER_USER_CREATE";
    public static final String USER_USER_UPDATE = "USER_USER_UPDATE";
    public static final String USER_USER_DELETE = "USER_USER_DELETE";
    public static final String USER_USER_EXPORT = "USER_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_AGENT_READ_ANY_LEVEL = "hasAnyAuthority('USER_PLATFORM_READ', 'USER_ORGANIZATION_READ', 'USER_DEPARTMENT_READ', 'USER_WORKGROUP_READ', 'USER_AGENT_READ', 'USER_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_AGENT_CREATE_ANY_LEVEL = "hasAnyAuthority('USER_PLATFORM_CREATE', 'USER_ORGANIZATION_CREATE', 'USER_DEPARTMENT_CREATE', 'USER_WORKGROUP_CREATE', 'USER_AGENT_CREATE', 'USER_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_AGENT_UPDATE_ANY_LEVEL = "hasAnyAuthority('USER_PLATFORM_UPDATE', 'USER_ORGANIZATION_UPDATE', 'USER_DEPARTMENT_UPDATE', 'USER_WORKGROUP_UPDATE', 'USER_AGENT_UPDATE', 'USER_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_AGENT_DELETE_ANY_LEVEL = "hasAnyAuthority('USER_PLATFORM_DELETE', 'USER_ORGANIZATION_DELETE', 'USER_DEPARTMENT_DELETE', 'USER_WORKGROUP_DELETE', 'USER_AGENT_DELETE', 'USER_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_AGENT_EXPORT_ANY_LEVEL = "hasAnyAuthority('USER_PLATFORM_EXPORT', 'USER_ORGANIZATION_EXPORT', 'USER_DEPARTMENT_EXPORT', 'USER_WORKGROUP_EXPORT', 'USER_AGENT_EXPORT', 'USER_USER_EXPORT')";

}