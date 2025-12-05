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
package com.bytedesk.kbase.kbase;

import com.bytedesk.core.base.BasePermissions;

public class KbasePermissions extends BasePermissions {

    // 模块前缀
    public static final String KBASE_PREFIX = "KBASE_";

    // 平台级权限
    public static final String KBASE_PLATFORM_READ = "KBASE_PLATFORM_READ";
    public static final String KBASE_PLATFORM_CREATE = "KBASE_PLATFORM_CREATE";
    public static final String KBASE_PLATFORM_UPDATE = "KBASE_PLATFORM_UPDATE";
    public static final String KBASE_PLATFORM_DELETE = "KBASE_PLATFORM_DELETE";
    public static final String KBASE_PLATFORM_EXPORT = "KBASE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String KBASE_ORGANIZATION_READ = "KBASE_ORGANIZATION_READ";
    public static final String KBASE_ORGANIZATION_CREATE = "KBASE_ORGANIZATION_CREATE";
    public static final String KBASE_ORGANIZATION_UPDATE = "KBASE_ORGANIZATION_UPDATE";
    public static final String KBASE_ORGANIZATION_DELETE = "KBASE_ORGANIZATION_DELETE";
    public static final String KBASE_ORGANIZATION_EXPORT = "KBASE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String KBASE_DEPARTMENT_READ = "KBASE_DEPARTMENT_READ";
    public static final String KBASE_DEPARTMENT_CREATE = "KBASE_DEPARTMENT_CREATE";
    public static final String KBASE_DEPARTMENT_UPDATE = "KBASE_DEPARTMENT_UPDATE";
    public static final String KBASE_DEPARTMENT_DELETE = "KBASE_DEPARTMENT_DELETE";
    public static final String KBASE_DEPARTMENT_EXPORT = "KBASE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String KBASE_WORKGROUP_READ = "KBASE_WORKGROUP_READ";
    public static final String KBASE_WORKGROUP_CREATE = "KBASE_WORKGROUP_CREATE";
    public static final String KBASE_WORKGROUP_UPDATE = "KBASE_WORKGROUP_UPDATE";
    public static final String KBASE_WORKGROUP_DELETE = "KBASE_WORKGROUP_DELETE";
    public static final String KBASE_WORKGROUP_EXPORT = "KBASE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String KBASE_AGENT_READ = "KBASE_AGENT_READ";
    public static final String KBASE_AGENT_CREATE = "KBASE_AGENT_CREATE";
    public static final String KBASE_AGENT_UPDATE = "KBASE_AGENT_UPDATE";
    public static final String KBASE_AGENT_DELETE = "KBASE_AGENT_DELETE";
    public static final String KBASE_AGENT_EXPORT = "KBASE_AGENT_EXPORT";
    // 用户级权限
    public static final String KBASE_USER_READ = "KBASE_USER_READ";
    public static final String KBASE_USER_CREATE = "KBASE_USER_CREATE";
    public static final String KBASE_USER_UPDATE = "KBASE_USER_UPDATE";
    public static final String KBASE_USER_DELETE = "KBASE_USER_DELETE";
    public static final String KBASE_USER_EXPORT = "KBASE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_KBASE_READ_ANY_LEVEL = "hasAnyAuthority('KBASE_PLATFORM_READ', 'KBASE_ORGANIZATION_READ', 'KBASE_DEPARTMENT_READ', 'KBASE_WORKGROUP_READ', 'KBASE_AGENT_READ', 'KBASE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_KBASE_CREATE_ANY_LEVEL = "hasAnyAuthority('KBASE_PLATFORM_CREATE', 'KBASE_ORGANIZATION_CREATE', 'KBASE_DEPARTMENT_CREATE', 'KBASE_WORKGROUP_CREATE', 'KBASE_AGENT_CREATE', 'KBASE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_KBASE_UPDATE_ANY_LEVEL = "hasAnyAuthority('KBASE_PLATFORM_UPDATE', 'KBASE_ORGANIZATION_UPDATE', 'KBASE_DEPARTMENT_UPDATE', 'KBASE_WORKGROUP_UPDATE', 'KBASE_AGENT_UPDATE', 'KBASE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_KBASE_DELETE_ANY_LEVEL = "hasAnyAuthority('KBASE_PLATFORM_DELETE', 'KBASE_ORGANIZATION_DELETE', 'KBASE_DEPARTMENT_DELETE', 'KBASE_WORKGROUP_DELETE', 'KBASE_AGENT_DELETE', 'KBASE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_KBASE_EXPORT_ANY_LEVEL = "hasAnyAuthority('KBASE_PLATFORM_EXPORT', 'KBASE_ORGANIZATION_EXPORT', 'KBASE_DEPARTMENT_EXPORT', 'KBASE_WORKGROUP_EXPORT', 'KBASE_AGENT_EXPORT', 'KBASE_USER_EXPORT')";

}
