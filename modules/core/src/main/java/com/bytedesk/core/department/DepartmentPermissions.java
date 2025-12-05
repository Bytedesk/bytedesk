/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
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
package com.bytedesk.core.department;

import com.bytedesk.core.base.BasePermissions;

public class DepartmentPermissions extends BasePermissions {

    // 模块前缀
    public static final String DEPARTMENT_PREFIX = "DEPARTMENT_";

    // 平台级权限
    public static final String DEPARTMENT_PLATFORM_READ = "DEPARTMENT_PLATFORM_READ";
    public static final String DEPARTMENT_PLATFORM_CREATE = "DEPARTMENT_PLATFORM_CREATE";
    public static final String DEPARTMENT_PLATFORM_UPDATE = "DEPARTMENT_PLATFORM_UPDATE";
    public static final String DEPARTMENT_PLATFORM_DELETE = "DEPARTMENT_PLATFORM_DELETE";
    public static final String DEPARTMENT_PLATFORM_EXPORT = "DEPARTMENT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String DEPARTMENT_ORGANIZATION_READ = "DEPARTMENT_ORGANIZATION_READ";
    public static final String DEPARTMENT_ORGANIZATION_CREATE = "DEPARTMENT_ORGANIZATION_CREATE";
    public static final String DEPARTMENT_ORGANIZATION_UPDATE = "DEPARTMENT_ORGANIZATION_UPDATE";
    public static final String DEPARTMENT_ORGANIZATION_DELETE = "DEPARTMENT_ORGANIZATION_DELETE";
    public static final String DEPARTMENT_ORGANIZATION_EXPORT = "DEPARTMENT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String DEPARTMENT_DEPARTMENT_READ = "DEPARTMENT_DEPARTMENT_READ";
    public static final String DEPARTMENT_DEPARTMENT_CREATE = "DEPARTMENT_DEPARTMENT_CREATE";
    public static final String DEPARTMENT_DEPARTMENT_UPDATE = "DEPARTMENT_DEPARTMENT_UPDATE";
    public static final String DEPARTMENT_DEPARTMENT_DELETE = "DEPARTMENT_DEPARTMENT_DELETE";
    public static final String DEPARTMENT_DEPARTMENT_EXPORT = "DEPARTMENT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String DEPARTMENT_WORKGROUP_READ = "DEPARTMENT_WORKGROUP_READ";
    public static final String DEPARTMENT_WORKGROUP_CREATE = "DEPARTMENT_WORKGROUP_CREATE";
    public static final String DEPARTMENT_WORKGROUP_UPDATE = "DEPARTMENT_WORKGROUP_UPDATE";
    public static final String DEPARTMENT_WORKGROUP_DELETE = "DEPARTMENT_WORKGROUP_DELETE";
    public static final String DEPARTMENT_WORKGROUP_EXPORT = "DEPARTMENT_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String DEPARTMENT_AGENT_READ = "DEPARTMENT_AGENT_READ";
    public static final String DEPARTMENT_AGENT_CREATE = "DEPARTMENT_AGENT_CREATE";
    public static final String DEPARTMENT_AGENT_UPDATE = "DEPARTMENT_AGENT_UPDATE";
    public static final String DEPARTMENT_AGENT_DELETE = "DEPARTMENT_AGENT_DELETE";
    public static final String DEPARTMENT_AGENT_EXPORT = "DEPARTMENT_AGENT_EXPORT";
    // 用户级权限
    public static final String DEPARTMENT_USER_READ = "DEPARTMENT_USER_READ";
    public static final String DEPARTMENT_USER_CREATE = "DEPARTMENT_USER_CREATE";
    public static final String DEPARTMENT_USER_UPDATE = "DEPARTMENT_USER_UPDATE";
    public static final String DEPARTMENT_USER_DELETE = "DEPARTMENT_USER_DELETE";
    public static final String DEPARTMENT_USER_EXPORT = "DEPARTMENT_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_DEPARTMENT_READ_ANY_LEVEL = "hasAnyAuthority('DEPARTMENT_PLATFORM_READ', 'DEPARTMENT_ORGANIZATION_READ', 'DEPARTMENT_DEPARTMENT_READ', 'DEPARTMENT_WORKGROUP_READ', 'DEPARTMENT_AGENT_READ', 'DEPARTMENT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_DEPARTMENT_CREATE_ANY_LEVEL = "hasAnyAuthority('DEPARTMENT_PLATFORM_CREATE', 'DEPARTMENT_ORGANIZATION_CREATE', 'DEPARTMENT_DEPARTMENT_CREATE', 'DEPARTMENT_WORKGROUP_CREATE', 'DEPARTMENT_AGENT_CREATE', 'DEPARTMENT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_DEPARTMENT_UPDATE_ANY_LEVEL = "hasAnyAuthority('DEPARTMENT_PLATFORM_UPDATE', 'DEPARTMENT_ORGANIZATION_UPDATE', 'DEPARTMENT_DEPARTMENT_UPDATE', 'DEPARTMENT_WORKGROUP_UPDATE', 'DEPARTMENT_AGENT_UPDATE', 'DEPARTMENT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_DEPARTMENT_DELETE_ANY_LEVEL = "hasAnyAuthority('DEPARTMENT_PLATFORM_DELETE', 'DEPARTMENT_ORGANIZATION_DELETE', 'DEPARTMENT_DEPARTMENT_DELETE', 'DEPARTMENT_WORKGROUP_DELETE', 'DEPARTMENT_AGENT_DELETE', 'DEPARTMENT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_DEPARTMENT_EXPORT_ANY_LEVEL = "hasAnyAuthority('DEPARTMENT_PLATFORM_EXPORT', 'DEPARTMENT_ORGANIZATION_EXPORT', 'DEPARTMENT_DEPARTMENT_EXPORT', 'DEPARTMENT_WORKGROUP_EXPORT', 'DEPARTMENT_AGENT_EXPORT', 'DEPARTMENT_USER_EXPORT')";

}