/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:35:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BasePermissions;

public class TicketPermissions extends BasePermissions {

    // 模块前缀
    public static final String TICKET_PREFIX = "TICKET_";

    // 平台级权限
    public static final String TICKET_PLATFORM_READ = "TICKET_PLATFORM_READ";
    public static final String TICKET_PLATFORM_CREATE = "TICKET_PLATFORM_CREATE";
    public static final String TICKET_PLATFORM_UPDATE = "TICKET_PLATFORM_UPDATE";
    public static final String TICKET_PLATFORM_DELETE = "TICKET_PLATFORM_DELETE";
    public static final String TICKET_PLATFORM_EXPORT = "TICKET_PLATFORM_EXPORT";

    // 组织级权限
    public static final String TICKET_ORGANIZATION_READ = "TICKET_ORGANIZATION_READ";
    public static final String TICKET_ORGANIZATION_CREATE = "TICKET_ORGANIZATION_CREATE";
    public static final String TICKET_ORGANIZATION_UPDATE = "TICKET_ORGANIZATION_UPDATE";
    public static final String TICKET_ORGANIZATION_DELETE = "TICKET_ORGANIZATION_DELETE";
    public static final String TICKET_ORGANIZATION_EXPORT = "TICKET_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String TICKET_DEPARTMENT_READ = "TICKET_DEPARTMENT_READ";
    public static final String TICKET_DEPARTMENT_CREATE = "TICKET_DEPARTMENT_CREATE";
    public static final String TICKET_DEPARTMENT_UPDATE = "TICKET_DEPARTMENT_UPDATE";
    public static final String TICKET_DEPARTMENT_DELETE = "TICKET_DEPARTMENT_DELETE";
    public static final String TICKET_DEPARTMENT_EXPORT = "TICKET_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String TICKET_WORKGROUP_READ = "TICKET_WORKGROUP_READ";
    public static final String TICKET_WORKGROUP_CREATE = "TICKET_WORKGROUP_CREATE";
    public static final String TICKET_WORKGROUP_UPDATE = "TICKET_WORKGROUP_UPDATE";
    public static final String TICKET_WORKGROUP_DELETE = "TICKET_WORKGROUP_DELETE";
    public static final String TICKET_WORKGROUP_EXPORT = "TICKET_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String TICKET_AGENT_READ = "TICKET_AGENT_READ";
    public static final String TICKET_AGENT_CREATE = "TICKET_AGENT_CREATE";
    public static final String TICKET_AGENT_UPDATE = "TICKET_AGENT_UPDATE";
    public static final String TICKET_AGENT_DELETE = "TICKET_AGENT_DELETE";
    public static final String TICKET_AGENT_EXPORT = "TICKET_AGENT_EXPORT";
    
    // 用户级权限
    public static final String TICKET_USER_READ = "TICKET_USER_READ";
    public static final String TICKET_USER_CREATE = "TICKET_USER_CREATE";
    public static final String TICKET_USER_UPDATE = "TICKET_USER_UPDATE";
    public static final String TICKET_USER_DELETE = "TICKET_USER_DELETE";
    public static final String TICKET_USER_EXPORT = "TICKET_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_TICKET_READ_ANY_LEVEL = "hasAnyAuthority('TICKET_PLATFORM_READ', 'TICKET_ORGANIZATION_READ', 'TICKET_DEPARTMENT_READ', 'TICKET_WORKGROUP_READ', 'TICKET_AGENT_READ', 'TICKET_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_TICKET_CREATE_ANY_LEVEL = "hasAnyAuthority('TICKET_PLATFORM_CREATE', 'TICKET_ORGANIZATION_CREATE', 'TICKET_DEPARTMENT_CREATE', 'TICKET_WORKGROUP_CREATE', 'TICKET_AGENT_CREATE', 'TICKET_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_TICKET_UPDATE_ANY_LEVEL = "hasAnyAuthority('TICKET_PLATFORM_UPDATE', 'TICKET_ORGANIZATION_UPDATE', 'TICKET_DEPARTMENT_UPDATE', 'TICKET_WORKGROUP_UPDATE', 'TICKET_AGENT_UPDATE', 'TICKET_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_TICKET_DELETE_ANY_LEVEL = "hasAnyAuthority('TICKET_PLATFORM_DELETE', 'TICKET_ORGANIZATION_DELETE', 'TICKET_DEPARTMENT_DELETE', 'TICKET_WORKGROUP_DELETE', 'TICKET_AGENT_DELETE', 'TICKET_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_TICKET_EXPORT_ANY_LEVEL = "hasAnyAuthority('TICKET_PLATFORM_EXPORT', 'TICKET_ORGANIZATION_EXPORT', 'TICKET_DEPARTMENT_EXPORT', 'TICKET_WORKGROUP_EXPORT', 'TICKET_AGENT_EXPORT', 'TICKET_USER_EXPORT')";

}