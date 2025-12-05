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
package com.bytedesk.service.agent;

import com.bytedesk.core.base.BasePermissions;

public class AgentPermissions extends BasePermissions {

    // 模块前缀
    public static final String AGENT_PREFIX = "AGENT_";

    // 平台级权限
    public static final String AGENT_PLATFORM_READ = "AGENT_PLATFORM_READ";
    public static final String AGENT_PLATFORM_CREATE = "AGENT_PLATFORM_CREATE";
    public static final String AGENT_PLATFORM_UPDATE = "AGENT_PLATFORM_UPDATE";
    public static final String AGENT_PLATFORM_DELETE = "AGENT_PLATFORM_DELETE";
    public static final String AGENT_PLATFORM_EXPORT = "AGENT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String AGENT_ORGANIZATION_READ = "AGENT_ORGANIZATION_READ";
    public static final String AGENT_ORGANIZATION_CREATE = "AGENT_ORGANIZATION_CREATE";
    public static final String AGENT_ORGANIZATION_UPDATE = "AGENT_ORGANIZATION_UPDATE";
    public static final String AGENT_ORGANIZATION_DELETE = "AGENT_ORGANIZATION_DELETE";
    public static final String AGENT_ORGANIZATION_EXPORT = "AGENT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String AGENT_DEPARTMENT_READ = "AGENT_DEPARTMENT_READ";
    public static final String AGENT_DEPARTMENT_CREATE = "AGENT_DEPARTMENT_CREATE";
    public static final String AGENT_DEPARTMENT_UPDATE = "AGENT_DEPARTMENT_UPDATE";
    public static final String AGENT_DEPARTMENT_DELETE = "AGENT_DEPARTMENT_DELETE";
    public static final String AGENT_DEPARTMENT_EXPORT = "AGENT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String AGENT_WORKGROUP_READ = "AGENT_WORKGROUP_READ";
    public static final String AGENT_WORKGROUP_CREATE = "AGENT_WORKGROUP_CREATE";
    public static final String AGENT_WORKGROUP_UPDATE = "AGENT_WORKGROUP_UPDATE";
    public static final String AGENT_WORKGROUP_DELETE = "AGENT_WORKGROUP_DELETE";
    public static final String AGENT_WORKGROUP_EXPORT = "AGENT_WORKGROUP_EXPORT";

    // 用户级权限
    public static final String AGENT_USER_READ = "AGENT_USER_READ";
    public static final String AGENT_USER_CREATE = "AGENT_USER_CREATE";
    public static final String AGENT_USER_UPDATE = "AGENT_USER_UPDATE";
    public static final String AGENT_USER_DELETE = "AGENT_USER_DELETE";
    public static final String AGENT_USER_EXPORT = "AGENT_USER_EXPORT";

    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_AGENT_READ_ANY_LEVEL = "hasAnyAuthority('AGENT_PLATFORM_READ', 'AGENT_ORGANIZATION_READ', 'AGENT_DEPARTMENT_READ', 'AGENT_WORKGROUP_READ', 'AGENT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_AGENT_CREATE_ANY_LEVEL = "hasAnyAuthority('AGENT_PLATFORM_CREATE', 'AGENT_ORGANIZATION_CREATE', 'AGENT_DEPARTMENT_CREATE', 'AGENT_WORKGROUP_CREATE', 'AGENT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_AGENT_UPDATE_ANY_LEVEL = "hasAnyAuthority('AGENT_PLATFORM_UPDATE', 'AGENT_ORGANIZATION_UPDATE', 'AGENT_DEPARTMENT_UPDATE', 'AGENT_WORKGROUP_UPDATE', 'AGENT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_AGENT_DELETE_ANY_LEVEL = "hasAnyAuthority('AGENT_PLATFORM_DELETE', 'AGENT_ORGANIZATION_DELETE', 'AGENT_DEPARTMENT_DELETE', 'AGENT_WORKGROUP_DELETE', 'AGENT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_AGENT_EXPORT_ANY_LEVEL = "hasAnyAuthority('AGENT_PLATFORM_EXPORT', 'AGENT_ORGANIZATION_EXPORT', 'AGENT_DEPARTMENT_EXPORT', 'AGENT_WORKGROUP_EXPORT', 'AGENT_USER_EXPORT')";

}