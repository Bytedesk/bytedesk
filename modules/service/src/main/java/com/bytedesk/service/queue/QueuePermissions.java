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
package com.bytedesk.service.queue;

import com.bytedesk.core.base.BasePermissions;

public class QueuePermissions extends BasePermissions {

    // 模块前缀
    public static final String QUEUE_PREFIX = "QUEUE_";

    // 平台级权限
    public static final String QUEUE_PLATFORM_READ = "QUEUE_PLATFORM_READ";
    public static final String QUEUE_PLATFORM_CREATE = "QUEUE_PLATFORM_CREATE";
    public static final String QUEUE_PLATFORM_UPDATE = "QUEUE_PLATFORM_UPDATE";
    public static final String QUEUE_PLATFORM_DELETE = "QUEUE_PLATFORM_DELETE";
    public static final String QUEUE_PLATFORM_EXPORT = "QUEUE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String QUEUE_ORGANIZATION_READ = "QUEUE_ORGANIZATION_READ";
    public static final String QUEUE_ORGANIZATION_CREATE = "QUEUE_ORGANIZATION_CREATE";
    public static final String QUEUE_ORGANIZATION_UPDATE = "QUEUE_ORGANIZATION_UPDATE";
    public static final String QUEUE_ORGANIZATION_DELETE = "QUEUE_ORGANIZATION_DELETE";
    public static final String QUEUE_ORGANIZATION_EXPORT = "QUEUE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String QUEUE_DEPARTMENT_READ = "QUEUE_DEPARTMENT_READ";
    public static final String QUEUE_DEPARTMENT_CREATE = "QUEUE_DEPARTMENT_CREATE";
    public static final String QUEUE_DEPARTMENT_UPDATE = "QUEUE_DEPARTMENT_UPDATE";
    public static final String QUEUE_DEPARTMENT_DELETE = "QUEUE_DEPARTMENT_DELETE";
    public static final String QUEUE_DEPARTMENT_EXPORT = "QUEUE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String QUEUE_WORKGROUP_READ = "QUEUE_WORKGROUP_READ";
    public static final String QUEUE_WORKGROUP_CREATE = "QUEUE_WORKGROUP_CREATE";
    public static final String QUEUE_WORKGROUP_UPDATE = "QUEUE_WORKGROUP_UPDATE";
    public static final String QUEUE_WORKGROUP_DELETE = "QUEUE_WORKGROUP_DELETE";
    public static final String QUEUE_WORKGROUP_EXPORT = "QUEUE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String QUEUE_AGENT_READ = "QUEUE_AGENT_READ";
    public static final String QUEUE_AGENT_CREATE = "QUEUE_AGENT_CREATE";
    public static final String QUEUE_AGENT_UPDATE = "QUEUE_AGENT_UPDATE";
    public static final String QUEUE_AGENT_DELETE = "QUEUE_AGENT_DELETE";
    public static final String QUEUE_AGENT_EXPORT = "QUEUE_AGENT_EXPORT";
    // 用户级权限
    public static final String QUEUE_USER_READ = "QUEUE_USER_READ";
    public static final String QUEUE_USER_CREATE = "QUEUE_USER_CREATE";
    public static final String QUEUE_USER_UPDATE = "QUEUE_USER_UPDATE";
    public static final String QUEUE_USER_DELETE = "QUEUE_USER_DELETE";
    public static final String QUEUE_USER_EXPORT = "QUEUE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_QUEUE_READ_ANY_LEVEL = "hasAnyAuthority('QUEUE_PLATFORM_READ', 'QUEUE_ORGANIZATION_READ', 'QUEUE_DEPARTMENT_READ', 'QUEUE_WORKGROUP_READ', 'QUEUE_AGENT_READ', 'QUEUE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_QUEUE_CREATE_ANY_LEVEL = "hasAnyAuthority('QUEUE_PLATFORM_CREATE', 'QUEUE_ORGANIZATION_CREATE', 'QUEUE_DEPARTMENT_CREATE', 'QUEUE_WORKGROUP_CREATE', 'QUEUE_AGENT_CREATE', 'QUEUE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_QUEUE_UPDATE_ANY_LEVEL = "hasAnyAuthority('QUEUE_PLATFORM_UPDATE', 'QUEUE_ORGANIZATION_UPDATE', 'QUEUE_DEPARTMENT_UPDATE', 'QUEUE_WORKGROUP_UPDATE', 'QUEUE_AGENT_UPDATE', 'QUEUE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_QUEUE_DELETE_ANY_LEVEL = "hasAnyAuthority('QUEUE_PLATFORM_DELETE', 'QUEUE_ORGANIZATION_DELETE', 'QUEUE_DEPARTMENT_DELETE', 'QUEUE_WORKGROUP_DELETE', 'QUEUE_AGENT_DELETE', 'QUEUE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_QUEUE_EXPORT_ANY_LEVEL = "hasAnyAuthority('QUEUE_PLATFORM_EXPORT', 'QUEUE_ORGANIZATION_EXPORT', 'QUEUE_DEPARTMENT_EXPORT', 'QUEUE_WORKGROUP_EXPORT', 'QUEUE_AGENT_EXPORT', 'QUEUE_USER_EXPORT')";

}