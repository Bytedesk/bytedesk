/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:08:27
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
package com.bytedesk.core.action;

import com.bytedesk.core.base.BasePermissions;

public class ActionPermissions extends BasePermissions {
    
    // 模块前缀
    public static final String ACTION_PREFIX = "ACTION_";

    // 平台级权限
    public static final String ACTION_PLATFORM_READ = "ACTION_PLATFORM_READ";
    public static final String ACTION_PLATFORM_CREATE = "ACTION_PLATFORM_CREATE";
    public static final String ACTION_PLATFORM_UPDATE = "ACTION_PLATFORM_UPDATE";
    public static final String ACTION_PLATFORM_DELETE = "ACTION_PLATFORM_DELETE";
    public static final String ACTION_PLATFORM_EXPORT = "ACTION_PLATFORM_EXPORT";

    // 组织级权限
    public static final String ACTION_ORGANIZATION_READ = "ACTION_ORGANIZATION_READ";
    public static final String ACTION_ORGANIZATION_CREATE = "ACTION_ORGANIZATION_CREATE";
    public static final String ACTION_ORGANIZATION_UPDATE = "ACTION_ORGANIZATION_UPDATE";
    public static final String ACTION_ORGANIZATION_DELETE = "ACTION_ORGANIZATION_DELETE";
    public static final String ACTION_ORGANIZATION_EXPORT = "ACTION_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String ACTION_DEPARTMENT_READ = "ACTION_DEPARTMENT_READ";
    public static final String ACTION_DEPARTMENT_CREATE = "ACTION_DEPARTMENT_CREATE";
    public static final String ACTION_DEPARTMENT_UPDATE = "ACTION_DEPARTMENT_UPDATE";
    public static final String ACTION_DEPARTMENT_DELETE = "ACTION_DEPARTMENT_DELETE";
    public static final String ACTION_DEPARTMENT_EXPORT = "ACTION_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String ACTION_WORKGROUP_READ = "ACTION_WORKGROUP_READ";
    public static final String ACTION_WORKGROUP_CREATE = "ACTION_WORKGROUP_CREATE";
    public static final String ACTION_WORKGROUP_UPDATE = "ACTION_WORKGROUP_UPDATE";
    public static final String ACTION_WORKGROUP_DELETE = "ACTION_WORKGROUP_DELETE";
    public static final String ACTION_WORKGROUP_EXPORT = "ACTION_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String ACTION_AGENT_READ = "ACTION_AGENT_READ";
    public static final String ACTION_AGENT_CREATE = "ACTION_AGENT_CREATE";
    public static final String ACTION_AGENT_UPDATE = "ACTION_AGENT_UPDATE";
    public static final String ACTION_AGENT_DELETE = "ACTION_AGENT_DELETE";
    public static final String ACTION_AGENT_EXPORT = "ACTION_AGENT_EXPORT";
    // 用户级权限
    public static final String ACTION_USER_READ = "ACTION_USER_READ";
    public static final String ACTION_USER_CREATE = "ACTION_USER_CREATE";
    public static final String ACTION_USER_UPDATE = "ACTION_USER_UPDATE";
    public static final String ACTION_USER_DELETE = "ACTION_USER_DELETE";
    public static final String ACTION_USER_EXPORT = "ACTION_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_ACTION_READ_ANY_LEVEL = "hasAnyAuthority('ACTION_PLATFORM_READ', 'ACTION_ORGANIZATION_READ', 'ACTION_DEPARTMENT_READ', 'ACTION_WORKGROUP_READ', 'ACTION_AGENT_READ', 'ACTION_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_ACTION_CREATE_ANY_LEVEL = "hasAnyAuthority('ACTION_PLATFORM_CREATE', 'ACTION_ORGANIZATION_CREATE', 'ACTION_DEPARTMENT_CREATE', 'ACTION_WORKGROUP_CREATE', 'ACTION_AGENT_CREATE', 'ACTION_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_ACTION_UPDATE_ANY_LEVEL = "hasAnyAuthority('ACTION_PLATFORM_UPDATE', 'ACTION_ORGANIZATION_UPDATE', 'ACTION_DEPARTMENT_UPDATE', 'ACTION_WORKGROUP_UPDATE', 'ACTION_AGENT_UPDATE', 'ACTION_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_ACTION_DELETE_ANY_LEVEL = "hasAnyAuthority('ACTION_PLATFORM_DELETE', 'ACTION_ORGANIZATION_DELETE', 'ACTION_DEPARTMENT_DELETE', 'ACTION_WORKGROUP_DELETE', 'ACTION_AGENT_DELETE', 'ACTION_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_ACTION_EXPORT_ANY_LEVEL = "hasAnyAuthority('ACTION_PLATFORM_EXPORT', 'ACTION_ORGANIZATION_EXPORT', 'ACTION_DEPARTMENT_EXPORT', 'ACTION_WORKGROUP_EXPORT', 'ACTION_AGENT_EXPORT', 'ACTION_USER_EXPORT')";

}
