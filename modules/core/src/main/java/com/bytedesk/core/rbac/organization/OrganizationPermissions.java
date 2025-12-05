/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:57:29
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
package com.bytedesk.core.rbac.organization;

import com.bytedesk.core.base.BasePermissions;

public class OrganizationPermissions extends BasePermissions {

    // 模块前缀
    public static final String ORGANIZATION_PREFIX = "ORGANIZATION_";

    // 平台级权限
    public static final String ORGANIZATION_PLATFORM_READ = "ORGANIZATION_PLATFORM_READ";
    public static final String ORGANIZATION_PLATFORM_CREATE = "ORGANIZATION_PLATFORM_CREATE";
    public static final String ORGANIZATION_PLATFORM_UPDATE = "ORGANIZATION_PLATFORM_UPDATE";
    public static final String ORGANIZATION_PLATFORM_DELETE = "ORGANIZATION_PLATFORM_DELETE";
    public static final String ORGANIZATION_PLATFORM_EXPORT = "ORGANIZATION_PLATFORM_EXPORT";

    // 组织级权限
    public static final String ORGANIZATION_ORGANIZATION_READ = "ORGANIZATION_ORGANIZATION_READ";
    public static final String ORGANIZATION_ORGANIZATION_CREATE = "ORGANIZATION_ORGANIZATION_CREATE";
    public static final String ORGANIZATION_ORGANIZATION_UPDATE = "ORGANIZATION_ORGANIZATION_UPDATE";
    public static final String ORGANIZATION_ORGANIZATION_DELETE = "ORGANIZATION_ORGANIZATION_DELETE";
    public static final String ORGANIZATION_ORGANIZATION_EXPORT = "ORGANIZATION_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String ORGANIZATION_DEPARTMENT_READ = "ORGANIZATION_DEPARTMENT_READ";
    public static final String ORGANIZATION_DEPARTMENT_CREATE = "ORGANIZATION_DEPARTMENT_CREATE";
    public static final String ORGANIZATION_DEPARTMENT_UPDATE = "ORGANIZATION_DEPARTMENT_UPDATE";
    public static final String ORGANIZATION_DEPARTMENT_DELETE = "ORGANIZATION_DEPARTMENT_DELETE";
    public static final String ORGANIZATION_DEPARTMENT_EXPORT = "ORGANIZATION_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String ORGANIZATION_WORKGROUP_READ = "ORGANIZATION_WORKGROUP_READ";
    public static final String ORGANIZATION_WORKGROUP_CREATE = "ORGANIZATION_WORKGROUP_CREATE";
    public static final String ORGANIZATION_WORKGROUP_UPDATE = "ORGANIZATION_WORKGROUP_UPDATE";
    public static final String ORGANIZATION_WORKGROUP_DELETE = "ORGANIZATION_WORKGROUP_DELETE";
    public static final String ORGANIZATION_WORKGROUP_EXPORT = "ORGANIZATION_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String ORGANIZATION_AGENT_READ = "ORGANIZATION_AGENT_READ";
    public static final String ORGANIZATION_AGENT_CREATE = "ORGANIZATION_AGENT_CREATE";
    public static final String ORGANIZATION_AGENT_UPDATE = "ORGANIZATION_AGENT_UPDATE";
    public static final String ORGANIZATION_AGENT_DELETE = "ORGANIZATION_AGENT_DELETE";
    public static final String ORGANIZATION_AGENT_EXPORT = "ORGANIZATION_AGENT_EXPORT";
    
    // 用户级权限
    public static final String ORGANIZATION_USER_READ = "ORGANIZATION_USER_READ";
    public static final String ORGANIZATION_USER_CREATE = "ORGANIZATION_USER_CREATE";
    public static final String ORGANIZATION_USER_UPDATE = "ORGANIZATION_USER_UPDATE";
    public static final String ORGANIZATION_USER_DELETE = "ORGANIZATION_USER_DELETE";
    public static final String ORGANIZATION_USER_EXPORT = "ORGANIZATION_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_ORGANIZATION_READ_ANY_LEVEL = "hasAnyAuthority('ORGANIZATION_PLATFORM_READ', 'ORGANIZATION_ORGANIZATION_READ', 'ORGANIZATION_DEPARTMENT_READ', 'ORGANIZATION_WORKGROUP_READ', 'ORGANIZATION_AGENT_READ', 'ORGANIZATION_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_ORGANIZATION_CREATE_ANY_LEVEL = "hasAnyAuthority('ORGANIZATION_PLATFORM_CREATE', 'ORGANIZATION_ORGANIZATION_CREATE', 'ORGANIZATION_DEPARTMENT_CREATE', 'ORGANIZATION_WORKGROUP_CREATE', 'ORGANIZATION_AGENT_CREATE', 'ORGANIZATION_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_ORGANIZATION_UPDATE_ANY_LEVEL = "hasAnyAuthority('ORGANIZATION_PLATFORM_UPDATE', 'ORGANIZATION_ORGANIZATION_UPDATE', 'ORGANIZATION_DEPARTMENT_UPDATE', 'ORGANIZATION_WORKGROUP_UPDATE', 'ORGANIZATION_AGENT_UPDATE', 'ORGANIZATION_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_ORGANIZATION_DELETE_ANY_LEVEL = "hasAnyAuthority('ORGANIZATION_PLATFORM_DELETE', 'ORGANIZATION_ORGANIZATION_DELETE', 'ORGANIZATION_DEPARTMENT_DELETE', 'ORGANIZATION_WORKGROUP_DELETE', 'ORGANIZATION_AGENT_DELETE', 'ORGANIZATION_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_ORGANIZATION_EXPORT_ANY_LEVEL = "hasAnyAuthority('ORGANIZATION_PLATFORM_EXPORT', 'ORGANIZATION_ORGANIZATION_EXPORT', 'ORGANIZATION_DEPARTMENT_EXPORT', 'ORGANIZATION_WORKGROUP_EXPORT', 'ORGANIZATION_AGENT_EXPORT', 'ORGANIZATION_USER_EXPORT')";

}