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
package com.bytedesk.service.visitor;

import com.bytedesk.core.base.BasePermissions;

public class VisitorPermissions extends BasePermissions {

    // 模块前缀
    public static final String VISITOR_PREFIX = "VISITOR_";

    // 平台级权限
    public static final String VISITOR_PLATFORM_READ = "VISITOR_PLATFORM_READ";
    public static final String VISITOR_PLATFORM_CREATE = "VISITOR_PLATFORM_CREATE";
    public static final String VISITOR_PLATFORM_UPDATE = "VISITOR_PLATFORM_UPDATE";
    public static final String VISITOR_PLATFORM_DELETE = "VISITOR_PLATFORM_DELETE";
    public static final String VISITOR_PLATFORM_EXPORT = "VISITOR_PLATFORM_EXPORT";

    // 组织级权限
    public static final String VISITOR_ORGANIZATION_READ = "VISITOR_ORGANIZATION_READ";
    public static final String VISITOR_ORGANIZATION_CREATE = "VISITOR_ORGANIZATION_CREATE";
    public static final String VISITOR_ORGANIZATION_UPDATE = "VISITOR_ORGANIZATION_UPDATE";
    public static final String VISITOR_ORGANIZATION_DELETE = "VISITOR_ORGANIZATION_DELETE";
    public static final String VISITOR_ORGANIZATION_EXPORT = "VISITOR_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String VISITOR_DEPARTMENT_READ = "VISITOR_DEPARTMENT_READ";
    public static final String VISITOR_DEPARTMENT_CREATE = "VISITOR_DEPARTMENT_CREATE";
    public static final String VISITOR_DEPARTMENT_UPDATE = "VISITOR_DEPARTMENT_UPDATE";
    public static final String VISITOR_DEPARTMENT_DELETE = "VISITOR_DEPARTMENT_DELETE";
    public static final String VISITOR_DEPARTMENT_EXPORT = "VISITOR_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String VISITOR_WORKGROUP_READ = "VISITOR_WORKGROUP_READ";
    public static final String VISITOR_WORKGROUP_CREATE = "VISITOR_WORKGROUP_CREATE";
    public static final String VISITOR_WORKGROUP_UPDATE = "VISITOR_WORKGROUP_UPDATE";
    public static final String VISITOR_WORKGROUP_DELETE = "VISITOR_WORKGROUP_DELETE";
    public static final String VISITOR_WORKGROUP_EXPORT = "VISITOR_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String VISITOR_AGENT_READ = "VISITOR_AGENT_READ";
    public static final String VISITOR_AGENT_CREATE = "VISITOR_AGENT_CREATE";
    public static final String VISITOR_AGENT_UPDATE = "VISITOR_AGENT_UPDATE";
    public static final String VISITOR_AGENT_DELETE = "VISITOR_AGENT_DELETE";
    public static final String VISITOR_AGENT_EXPORT = "VISITOR_AGENT_EXPORT";
    // 用户级权限
    public static final String VISITOR_USER_READ = "VISITOR_USER_READ";
    public static final String VISITOR_USER_CREATE = "VISITOR_USER_CREATE";
    public static final String VISITOR_USER_UPDATE = "VISITOR_USER_UPDATE";
    public static final String VISITOR_USER_DELETE = "VISITOR_USER_DELETE";
    public static final String VISITOR_USER_EXPORT = "VISITOR_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_VISITOR_READ_ANY_LEVEL = "hasAnyAuthority('VISITOR_PLATFORM_READ', 'VISITOR_ORGANIZATION_READ', 'VISITOR_DEPARTMENT_READ', 'VISITOR_WORKGROUP_READ', 'VISITOR_AGENT_READ', 'VISITOR_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_VISITOR_CREATE_ANY_LEVEL = "hasAnyAuthority('VISITOR_PLATFORM_CREATE', 'VISITOR_ORGANIZATION_CREATE', 'VISITOR_DEPARTMENT_CREATE', 'VISITOR_WORKGROUP_CREATE', 'VISITOR_AGENT_CREATE', 'VISITOR_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_VISITOR_UPDATE_ANY_LEVEL = "hasAnyAuthority('VISITOR_PLATFORM_UPDATE', 'VISITOR_ORGANIZATION_UPDATE', 'VISITOR_DEPARTMENT_UPDATE', 'VISITOR_WORKGROUP_UPDATE', 'VISITOR_AGENT_UPDATE', 'VISITOR_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_VISITOR_DELETE_ANY_LEVEL = "hasAnyAuthority('VISITOR_PLATFORM_DELETE', 'VISITOR_ORGANIZATION_DELETE', 'VISITOR_DEPARTMENT_DELETE', 'VISITOR_WORKGROUP_DELETE', 'VISITOR_AGENT_DELETE', 'VISITOR_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_VISITOR_EXPORT_ANY_LEVEL = "hasAnyAuthority('VISITOR_PLATFORM_EXPORT', 'VISITOR_ORGANIZATION_EXPORT', 'VISITOR_DEPARTMENT_EXPORT', 'VISITOR_WORKGROUP_EXPORT', 'VISITOR_AGENT_EXPORT', 'VISITOR_USER_EXPORT')";

}