/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:08:01
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
package com.bytedesk.core.push;

import com.bytedesk.core.base.BasePermissions;

public class PushPermissions extends BasePermissions {

    // 模块前缀
    public static final String PUSH_PREFIX = "PUSH_";

    // 平台级权限
    public static final String PUSH_PLATFORM_READ = "PUSH_PLATFORM_READ";
    public static final String PUSH_PLATFORM_CREATE = "PUSH_PLATFORM_CREATE";
    public static final String PUSH_PLATFORM_UPDATE = "PUSH_PLATFORM_UPDATE";
    public static final String PUSH_PLATFORM_DELETE = "PUSH_PLATFORM_DELETE";
    public static final String PUSH_PLATFORM_EXPORT = "PUSH_PLATFORM_EXPORT";

    // 组织级权限
    public static final String PUSH_ORGANIZATION_READ = "PUSH_ORGANIZATION_READ";
    public static final String PUSH_ORGANIZATION_CREATE = "PUSH_ORGANIZATION_CREATE";
    public static final String PUSH_ORGANIZATION_UPDATE = "PUSH_ORGANIZATION_UPDATE";
    public static final String PUSH_ORGANIZATION_DELETE = "PUSH_ORGANIZATION_DELETE";
    public static final String PUSH_ORGANIZATION_EXPORT = "PUSH_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String PUSH_DEPARTMENT_READ = "PUSH_DEPARTMENT_READ";
    public static final String PUSH_DEPARTMENT_CREATE = "PUSH_DEPARTMENT_CREATE";
    public static final String PUSH_DEPARTMENT_UPDATE = "PUSH_DEPARTMENT_UPDATE";
    public static final String PUSH_DEPARTMENT_DELETE = "PUSH_DEPARTMENT_DELETE";
    public static final String PUSH_DEPARTMENT_EXPORT = "PUSH_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String PUSH_WORKGROUP_READ = "PUSH_WORKGROUP_READ";
    public static final String PUSH_WORKGROUP_CREATE = "PUSH_WORKGROUP_CREATE";
    public static final String PUSH_WORKGROUP_UPDATE = "PUSH_WORKGROUP_UPDATE";
    public static final String PUSH_WORKGROUP_DELETE = "PUSH_WORKGROUP_DELETE";
    public static final String PUSH_WORKGROUP_EXPORT = "PUSH_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String PUSH_AGENT_READ = "PUSH_AGENT_READ";
    public static final String PUSH_AGENT_CREATE = "PUSH_AGENT_CREATE";
    public static final String PUSH_AGENT_UPDATE = "PUSH_AGENT_UPDATE";
    public static final String PUSH_AGENT_DELETE = "PUSH_AGENT_DELETE";
    public static final String PUSH_AGENT_EXPORT = "PUSH_AGENT_EXPORT";
    // 用户级权限
    public static final String PUSH_USER_READ = "PUSH_USER_READ";
    public static final String PUSH_USER_CREATE = "PUSH_USER_CREATE";
    public static final String PUSH_USER_UPDATE = "PUSH_USER_UPDATE";
    public static final String PUSH_USER_DELETE = "PUSH_USER_DELETE";
    public static final String PUSH_USER_EXPORT = "PUSH_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_PUSH_READ_ANY_LEVEL = "hasAnyAuthority('PUSH_PLATFORM_READ', 'PUSH_ORGANIZATION_READ', 'PUSH_DEPARTMENT_READ', 'PUSH_WORKGROUP_READ', 'PUSH_AGENT_READ', 'PUSH_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_PUSH_CREATE_ANY_LEVEL = "hasAnyAuthority('PUSH_PLATFORM_CREATE', 'PUSH_ORGANIZATION_CREATE', 'PUSH_DEPARTMENT_CREATE', 'PUSH_WORKGROUP_CREATE', 'PUSH_AGENT_CREATE', 'PUSH_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_PUSH_UPDATE_ANY_LEVEL = "hasAnyAuthority('PUSH_PLATFORM_UPDATE', 'PUSH_ORGANIZATION_UPDATE', 'PUSH_DEPARTMENT_UPDATE', 'PUSH_WORKGROUP_UPDATE', 'PUSH_AGENT_UPDATE', 'PUSH_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_PUSH_DELETE_ANY_LEVEL = "hasAnyAuthority('PUSH_PLATFORM_DELETE', 'PUSH_ORGANIZATION_DELETE', 'PUSH_DEPARTMENT_DELETE', 'PUSH_WORKGROUP_DELETE', 'PUSH_AGENT_DELETE', 'PUSH_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_PUSH_EXPORT_ANY_LEVEL = "hasAnyAuthority('PUSH_PLATFORM_EXPORT', 'PUSH_ORGANIZATION_EXPORT', 'PUSH_DEPARTMENT_EXPORT', 'PUSH_WORKGROUP_EXPORT', 'PUSH_AGENT_EXPORT', 'PUSH_USER_EXPORT')";

}