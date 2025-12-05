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
package com.bytedesk.core.socket.connection;

import com.bytedesk.core.base.BasePermissions;

public class ConnectionPermissions extends BasePermissions {

    // 模块前缀
    public static final String CONNECTION_PREFIX = "CONNECTION_";

    // 平台级权限
    public static final String CONNECTION_PLATFORM_READ = "CONNECTION_PLATFORM_READ";
    public static final String CONNECTION_PLATFORM_CREATE = "CONNECTION_PLATFORM_CREATE";
    public static final String CONNECTION_PLATFORM_UPDATE = "CONNECTION_PLATFORM_UPDATE";
    public static final String CONNECTION_PLATFORM_DELETE = "CONNECTION_PLATFORM_DELETE";
    public static final String CONNECTION_PLATFORM_EXPORT = "CONNECTION_PLATFORM_EXPORT";

    // 组织级权限
    public static final String CONNECTION_ORGANIZATION_READ = "CONNECTION_ORGANIZATION_READ";
    public static final String CONNECTION_ORGANIZATION_CREATE = "CONNECTION_ORGANIZATION_CREATE";
    public static final String CONNECTION_ORGANIZATION_UPDATE = "CONNECTION_ORGANIZATION_UPDATE";
    public static final String CONNECTION_ORGANIZATION_DELETE = "CONNECTION_ORGANIZATION_DELETE";
    public static final String CONNECTION_ORGANIZATION_EXPORT = "CONNECTION_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String CONNECTION_DEPARTMENT_READ = "CONNECTION_DEPARTMENT_READ";
    public static final String CONNECTION_DEPARTMENT_CREATE = "CONNECTION_DEPARTMENT_CREATE";
    public static final String CONNECTION_DEPARTMENT_UPDATE = "CONNECTION_DEPARTMENT_UPDATE";
    public static final String CONNECTION_DEPARTMENT_DELETE = "CONNECTION_DEPARTMENT_DELETE";
    public static final String CONNECTION_DEPARTMENT_EXPORT = "CONNECTION_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String CONNECTION_WORKGROUP_READ = "CONNECTION_WORKGROUP_READ";
    public static final String CONNECTION_WORKGROUP_CREATE = "CONNECTION_WORKGROUP_CREATE";
    public static final String CONNECTION_WORKGROUP_UPDATE = "CONNECTION_WORKGROUP_UPDATE";
    public static final String CONNECTION_WORKGROUP_DELETE = "CONNECTION_WORKGROUP_DELETE";
    public static final String CONNECTION_WORKGROUP_EXPORT = "CONNECTION_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String CONNECTION_AGENT_READ = "CONNECTION_AGENT_READ";
    public static final String CONNECTION_AGENT_CREATE = "CONNECTION_AGENT_CREATE";
    public static final String CONNECTION_AGENT_UPDATE = "CONNECTION_AGENT_UPDATE";
    public static final String CONNECTION_AGENT_DELETE = "CONNECTION_AGENT_DELETE";
    public static final String CONNECTION_AGENT_EXPORT = "CONNECTION_AGENT_EXPORT";
    // 用户级权限
    public static final String CONNECTION_USER_READ = "CONNECTION_USER_READ";
    public static final String CONNECTION_USER_CREATE = "CONNECTION_USER_CREATE";
    public static final String CONNECTION_USER_UPDATE = "CONNECTION_USER_UPDATE";
    public static final String CONNECTION_USER_DELETE = "CONNECTION_USER_DELETE";
    public static final String CONNECTION_USER_EXPORT = "CONNECTION_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_CONNECTION_READ_ANY_LEVEL = "hasAnyAuthority('CONNECTION_PLATFORM_READ', 'CONNECTION_ORGANIZATION_READ', 'CONNECTION_DEPARTMENT_READ', 'CONNECTION_WORKGROUP_READ', 'CONNECTION_AGENT_READ', 'CONNECTION_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_CONNECTION_CREATE_ANY_LEVEL = "hasAnyAuthority('CONNECTION_PLATFORM_CREATE', 'CONNECTION_ORGANIZATION_CREATE', 'CONNECTION_DEPARTMENT_CREATE', 'CONNECTION_WORKGROUP_CREATE', 'CONNECTION_AGENT_CREATE', 'CONNECTION_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_CONNECTION_UPDATE_ANY_LEVEL = "hasAnyAuthority('CONNECTION_PLATFORM_UPDATE', 'CONNECTION_ORGANIZATION_UPDATE', 'CONNECTION_DEPARTMENT_UPDATE', 'CONNECTION_WORKGROUP_UPDATE', 'CONNECTION_AGENT_UPDATE', 'CONNECTION_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_CONNECTION_DELETE_ANY_LEVEL = "hasAnyAuthority('CONNECTION_PLATFORM_DELETE', 'CONNECTION_ORGANIZATION_DELETE', 'CONNECTION_DEPARTMENT_DELETE', 'CONNECTION_WORKGROUP_DELETE', 'CONNECTION_AGENT_DELETE', 'CONNECTION_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_CONNECTION_EXPORT_ANY_LEVEL = "hasAnyAuthority('CONNECTION_PLATFORM_EXPORT', 'CONNECTION_ORGANIZATION_EXPORT', 'CONNECTION_DEPARTMENT_EXPORT', 'CONNECTION_WORKGROUP_EXPORT', 'CONNECTION_AGENT_EXPORT', 'CONNECTION_USER_EXPORT')";

}
