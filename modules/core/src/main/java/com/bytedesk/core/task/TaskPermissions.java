/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
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
package com.bytedesk.core.task;

import com.bytedesk.core.base.BasePermissions;

public class TaskPermissions extends BasePermissions {

    // 模块前缀
    public static final String TASK_PREFIX = "TASK_";

    // 平台级权限
    public static final String TASK_PLATFORM_READ = "TASK_PLATFORM_READ";
    public static final String TASK_PLATFORM_CREATE = "TASK_PLATFORM_CREATE";
    public static final String TASK_PLATFORM_UPDATE = "TASK_PLATFORM_UPDATE";
    public static final String TASK_PLATFORM_DELETE = "TASK_PLATFORM_DELETE";
    public static final String TASK_PLATFORM_EXPORT = "TASK_PLATFORM_EXPORT";

    // 组织级权限
    public static final String TASK_ORGANIZATION_READ = "TASK_ORGANIZATION_READ";
    public static final String TASK_ORGANIZATION_CREATE = "TASK_ORGANIZATION_CREATE";
    public static final String TASK_ORGANIZATION_UPDATE = "TASK_ORGANIZATION_UPDATE";
    public static final String TASK_ORGANIZATION_DELETE = "TASK_ORGANIZATION_DELETE";
    public static final String TASK_ORGANIZATION_EXPORT = "TASK_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String TASK_DEPARTMENT_READ = "TASK_DEPARTMENT_READ";
    public static final String TASK_DEPARTMENT_CREATE = "TASK_DEPARTMENT_CREATE";
    public static final String TASK_DEPARTMENT_UPDATE = "TASK_DEPARTMENT_UPDATE";
    public static final String TASK_DEPARTMENT_DELETE = "TASK_DEPARTMENT_DELETE";
    public static final String TASK_DEPARTMENT_EXPORT = "TASK_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String TASK_WORKGROUP_READ = "TASK_WORKGROUP_READ";
    public static final String TASK_WORKGROUP_CREATE = "TASK_WORKGROUP_CREATE";
    public static final String TASK_WORKGROUP_UPDATE = "TASK_WORKGROUP_UPDATE";
    public static final String TASK_WORKGROUP_DELETE = "TASK_WORKGROUP_DELETE";
    public static final String TASK_WORKGROUP_EXPORT = "TASK_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String TASK_AGENT_READ = "TASK_AGENT_READ";
    public static final String TASK_AGENT_CREATE = "TASK_AGENT_CREATE";
    public static final String TASK_AGENT_UPDATE = "TASK_AGENT_UPDATE";
    public static final String TASK_AGENT_DELETE = "TASK_AGENT_DELETE";
    public static final String TASK_AGENT_EXPORT = "TASK_AGENT_EXPORT";
    // 用户级权限
    public static final String TASK_USER_READ = "TASK_USER_READ";
    public static final String TASK_USER_CREATE = "TASK_USER_CREATE";
    public static final String TASK_USER_UPDATE = "TASK_USER_UPDATE";
    public static final String TASK_USER_DELETE = "TASK_USER_DELETE";
    public static final String TASK_USER_EXPORT = "TASK_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_TASK_READ_ANY_LEVEL = "hasAnyAuthority('TASK_PLATFORM_READ', 'TASK_ORGANIZATION_READ', 'TASK_DEPARTMENT_READ', 'TASK_WORKGROUP_READ', 'TASK_AGENT_READ', 'TASK_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_TASK_CREATE_ANY_LEVEL = "hasAnyAuthority('TASK_PLATFORM_CREATE', 'TASK_ORGANIZATION_CREATE', 'TASK_DEPARTMENT_CREATE', 'TASK_WORKGROUP_CREATE', 'TASK_AGENT_CREATE', 'TASK_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_TASK_UPDATE_ANY_LEVEL = "hasAnyAuthority('TASK_PLATFORM_UPDATE', 'TASK_ORGANIZATION_UPDATE', 'TASK_DEPARTMENT_UPDATE', 'TASK_WORKGROUP_UPDATE', 'TASK_AGENT_UPDATE', 'TASK_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_TASK_DELETE_ANY_LEVEL = "hasAnyAuthority('TASK_PLATFORM_DELETE', 'TASK_ORGANIZATION_DELETE', 'TASK_DEPARTMENT_DELETE', 'TASK_WORKGROUP_DELETE', 'TASK_AGENT_DELETE', 'TASK_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_TASK_EXPORT_ANY_LEVEL = "hasAnyAuthority('TASK_PLATFORM_EXPORT', 'TASK_ORGANIZATION_EXPORT', 'TASK_DEPARTMENT_EXPORT', 'TASK_WORKGROUP_EXPORT', 'TASK_AGENT_EXPORT', 'TASK_USER_EXPORT')";

}
