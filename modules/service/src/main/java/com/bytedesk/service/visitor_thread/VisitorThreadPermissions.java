/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 16:01:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import com.bytedesk.core.base.BasePermissions;

public class VisitorThreadPermissions extends BasePermissions {

    // 模块前缀
    public static final String VISITOR_THREAD_PREFIX = "VISITOR_THREAD_";

    // 平台级权限
    public static final String VISITOR_THREAD_PLATFORM_READ = "VISITOR_THREAD_PLATFORM_READ";
    public static final String VISITOR_THREAD_PLATFORM_CREATE = "VISITOR_THREAD_PLATFORM_CREATE";
    public static final String VISITOR_THREAD_PLATFORM_UPDATE = "VISITOR_THREAD_PLATFORM_UPDATE";
    public static final String VISITOR_THREAD_PLATFORM_DELETE = "VISITOR_THREAD_PLATFORM_DELETE";
    public static final String VISITOR_THREAD_PLATFORM_EXPORT = "VISITOR_THREAD_PLATFORM_EXPORT";

    // 组织级权限
    public static final String VISITOR_THREAD_ORGANIZATION_READ = "VISITOR_THREAD_ORGANIZATION_READ";
    public static final String VISITOR_THREAD_ORGANIZATION_CREATE = "VISITOR_THREAD_ORGANIZATION_CREATE";
    public static final String VISITOR_THREAD_ORGANIZATION_UPDATE = "VISITOR_THREAD_ORGANIZATION_UPDATE";
    public static final String VISITOR_THREAD_ORGANIZATION_DELETE = "VISITOR_THREAD_ORGANIZATION_DELETE";
    public static final String VISITOR_THREAD_ORGANIZATION_EXPORT = "VISITOR_THREAD_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String VISITOR_THREAD_DEPARTMENT_READ = "VISITOR_THREAD_DEPARTMENT_READ";
    public static final String VISITOR_THREAD_DEPARTMENT_CREATE = "VISITOR_THREAD_DEPARTMENT_CREATE";
    public static final String VISITOR_THREAD_DEPARTMENT_UPDATE = "VISITOR_THREAD_DEPARTMENT_UPDATE";
    public static final String VISITOR_THREAD_DEPARTMENT_DELETE = "VISITOR_THREAD_DEPARTMENT_DELETE";
    public static final String VISITOR_THREAD_DEPARTMENT_EXPORT = "VISITOR_THREAD_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String VISITOR_THREAD_WORKGROUP_READ = "VISITOR_THREAD_WORKGROUP_READ";
    public static final String VISITOR_THREAD_WORKGROUP_CREATE = "VISITOR_THREAD_WORKGROUP_CREATE";
    public static final String VISITOR_THREAD_WORKGROUP_UPDATE = "VISITOR_THREAD_WORKGROUP_UPDATE";
    public static final String VISITOR_THREAD_WORKGROUP_DELETE = "VISITOR_THREAD_WORKGROUP_DELETE";
    public static final String VISITOR_THREAD_WORKGROUP_EXPORT = "VISITOR_THREAD_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String VISITOR_THREAD_AGENT_READ = "VISITOR_THREAD_AGENT_READ";
    public static final String VISITOR_THREAD_AGENT_CREATE = "VISITOR_THREAD_AGENT_CREATE";
    public static final String VISITOR_THREAD_AGENT_UPDATE = "VISITOR_THREAD_AGENT_UPDATE";
    public static final String VISITOR_THREAD_AGENT_DELETE = "VISITOR_THREAD_AGENT_DELETE";
    public static final String VISITOR_THREAD_AGENT_EXPORT = "VISITOR_THREAD_AGENT_EXPORT";
    // 用户级权限
    public static final String VISITOR_THREAD_USER_READ = "VISITOR_THREAD_USER_READ";
    public static final String VISITOR_THREAD_USER_CREATE = "VISITOR_THREAD_USER_CREATE";
    public static final String VISITOR_THREAD_USER_UPDATE = "VISITOR_THREAD_USER_UPDATE";
    public static final String VISITOR_THREAD_USER_DELETE = "VISITOR_THREAD_USER_DELETE";
    public static final String VISITOR_THREAD_USER_EXPORT = "VISITOR_THREAD_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_VISITOR_THREAD_READ_ANY_LEVEL = "hasAnyAuthority('VISITOR_THREAD_PLATFORM_READ', 'VISITOR_THREAD_ORGANIZATION_READ', 'VISITOR_THREAD_DEPARTMENT_READ', 'VISITOR_THREAD_WORKGROUP_READ', 'VISITOR_THREAD_AGENT_READ', 'VISITOR_THREAD_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_VISITOR_THREAD_CREATE_ANY_LEVEL = "hasAnyAuthority('VISITOR_THREAD_PLATFORM_CREATE', 'VISITOR_THREAD_ORGANIZATION_CREATE', 'VISITOR_THREAD_DEPARTMENT_CREATE', 'VISITOR_THREAD_WORKGROUP_CREATE', 'VISITOR_THREAD_AGENT_CREATE', 'VISITOR_THREAD_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_VISITOR_THREAD_UPDATE_ANY_LEVEL = "hasAnyAuthority('VISITOR_THREAD_PLATFORM_UPDATE', 'VISITOR_THREAD_ORGANIZATION_UPDATE', 'VISITOR_THREAD_DEPARTMENT_UPDATE', 'VISITOR_THREAD_WORKGROUP_UPDATE', 'VISITOR_THREAD_AGENT_UPDATE', 'VISITOR_THREAD_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_VISITOR_THREAD_DELETE_ANY_LEVEL = "hasAnyAuthority('VISITOR_THREAD_PLATFORM_DELETE', 'VISITOR_THREAD_ORGANIZATION_DELETE', 'VISITOR_THREAD_DEPARTMENT_DELETE', 'VISITOR_THREAD_WORKGROUP_DELETE', 'VISITOR_THREAD_AGENT_DELETE', 'VISITOR_THREAD_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_VISITOR_THREAD_EXPORT_ANY_LEVEL = "hasAnyAuthority('VISITOR_THREAD_PLATFORM_EXPORT', 'VISITOR_THREAD_ORGANIZATION_EXPORT', 'VISITOR_THREAD_DEPARTMENT_EXPORT', 'VISITOR_THREAD_WORKGROUP_EXPORT', 'VISITOR_THREAD_AGENT_EXPORT', 'VISITOR_THREAD_USER_EXPORT')";

}