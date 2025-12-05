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
package com.bytedesk.core.thread;

import com.bytedesk.core.base.BasePermissions;

public class ThreadPermissions extends BasePermissions {

    // 模块前缀
    public static final String THREAD_PREFIX = "THREAD_";

    // 平台级权限
    public static final String THREAD_PLATFORM_READ = "THREAD_PLATFORM_READ";
    public static final String THREAD_PLATFORM_CREATE = "THREAD_PLATFORM_CREATE";
    public static final String THREAD_PLATFORM_UPDATE = "THREAD_PLATFORM_UPDATE";
    public static final String THREAD_PLATFORM_DELETE = "THREAD_PLATFORM_DELETE";
    public static final String THREAD_PLATFORM_EXPORT = "THREAD_PLATFORM_EXPORT";

    // 组织级权限
    public static final String THREAD_ORGANIZATION_READ = "THREAD_ORGANIZATION_READ";
    public static final String THREAD_ORGANIZATION_CREATE = "THREAD_ORGANIZATION_CREATE";
    public static final String THREAD_ORGANIZATION_UPDATE = "THREAD_ORGANIZATION_UPDATE";
    public static final String THREAD_ORGANIZATION_DELETE = "THREAD_ORGANIZATION_DELETE";
    public static final String THREAD_ORGANIZATION_EXPORT = "THREAD_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String THREAD_DEPARTMENT_READ = "THREAD_DEPARTMENT_READ";
    public static final String THREAD_DEPARTMENT_CREATE = "THREAD_DEPARTMENT_CREATE";
    public static final String THREAD_DEPARTMENT_UPDATE = "THREAD_DEPARTMENT_UPDATE";
    public static final String THREAD_DEPARTMENT_DELETE = "THREAD_DEPARTMENT_DELETE";
    public static final String THREAD_DEPARTMENT_EXPORT = "THREAD_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String THREAD_WORKGROUP_READ = "THREAD_WORKGROUP_READ";
    public static final String THREAD_WORKGROUP_CREATE = "THREAD_WORKGROUP_CREATE";
    public static final String THREAD_WORKGROUP_UPDATE = "THREAD_WORKGROUP_UPDATE";
    public static final String THREAD_WORKGROUP_DELETE = "THREAD_WORKGROUP_DELETE";
    public static final String THREAD_WORKGROUP_EXPORT = "THREAD_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String THREAD_AGENT_READ = "THREAD_AGENT_READ";
    public static final String THREAD_AGENT_CREATE = "THREAD_AGENT_CREATE";
    public static final String THREAD_AGENT_UPDATE = "THREAD_AGENT_UPDATE";
    public static final String THREAD_AGENT_DELETE = "THREAD_AGENT_DELETE";
    public static final String THREAD_AGENT_EXPORT = "THREAD_AGENT_EXPORT";
    // 用户级权限
    public static final String THREAD_USER_READ = "THREAD_USER_READ";
    public static final String THREAD_USER_CREATE = "THREAD_USER_CREATE";
    public static final String THREAD_USER_UPDATE = "THREAD_USER_UPDATE";
    public static final String THREAD_USER_DELETE = "THREAD_USER_DELETE";
    public static final String THREAD_USER_EXPORT = "THREAD_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_THREAD_READ_ANY_LEVEL = "hasAnyAuthority('THREAD_PLATFORM_READ', 'THREAD_ORGANIZATION_READ', 'THREAD_DEPARTMENT_READ', 'THREAD_WORKGROUP_READ', 'THREAD_AGENT_READ', 'THREAD_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_THREAD_CREATE_ANY_LEVEL = "hasAnyAuthority('THREAD_PLATFORM_CREATE', 'THREAD_ORGANIZATION_CREATE', 'THREAD_DEPARTMENT_CREATE', 'THREAD_WORKGROUP_CREATE', 'THREAD_AGENT_CREATE', 'THREAD_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_THREAD_UPDATE_ANY_LEVEL = "hasAnyAuthority('THREAD_PLATFORM_UPDATE', 'THREAD_ORGANIZATION_UPDATE', 'THREAD_DEPARTMENT_UPDATE', 'THREAD_WORKGROUP_UPDATE', 'THREAD_AGENT_UPDATE', 'THREAD_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_THREAD_DELETE_ANY_LEVEL = "hasAnyAuthority('THREAD_PLATFORM_DELETE', 'THREAD_ORGANIZATION_DELETE', 'THREAD_DEPARTMENT_DELETE', 'THREAD_WORKGROUP_DELETE', 'THREAD_AGENT_DELETE', 'THREAD_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_THREAD_EXPORT_ANY_LEVEL = "hasAnyAuthority('THREAD_PLATFORM_EXPORT', 'THREAD_ORGANIZATION_EXPORT', 'THREAD_DEPARTMENT_EXPORT', 'THREAD_WORKGROUP_EXPORT', 'THREAD_AGENT_EXPORT', 'THREAD_USER_EXPORT')";

}
