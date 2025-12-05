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
package com.bytedesk.service.message_leave;

import com.bytedesk.core.base.BasePermissions;

public class MessageLeavePermissions extends BasePermissions {

    // 模块前缀
    public static final String LEAVEMSG_PREFIX = "LEAVEMSG_";

    // 平台级权限
    public static final String LEAVEMSG_PLATFORM_READ = "LEAVEMSG_PLATFORM_READ";
    public static final String LEAVEMSG_PLATFORM_CREATE = "LEAVEMSG_PLATFORM_CREATE";
    public static final String LEAVEMSG_PLATFORM_UPDATE = "LEAVEMSG_PLATFORM_UPDATE";
    public static final String LEAVEMSG_PLATFORM_DELETE = "LEAVEMSG_PLATFORM_DELETE";
    public static final String LEAVEMSG_PLATFORM_EXPORT = "LEAVEMSG_PLATFORM_EXPORT";

    // 组织级权限
    public static final String LEAVEMSG_ORGANIZATION_READ = "LEAVEMSG_ORGANIZATION_READ";
    public static final String LEAVEMSG_ORGANIZATION_CREATE = "LEAVEMSG_ORGANIZATION_CREATE";
    public static final String LEAVEMSG_ORGANIZATION_UPDATE = "LEAVEMSG_ORGANIZATION_UPDATE";
    public static final String LEAVEMSG_ORGANIZATION_DELETE = "LEAVEMSG_ORGANIZATION_DELETE";
    public static final String LEAVEMSG_ORGANIZATION_EXPORT = "LEAVEMSG_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String LEAVEMSG_DEPARTMENT_READ = "LEAVEMSG_DEPARTMENT_READ";
    public static final String LEAVEMSG_DEPARTMENT_CREATE = "LEAVEMSG_DEPARTMENT_CREATE";
    public static final String LEAVEMSG_DEPARTMENT_UPDATE = "LEAVEMSG_DEPARTMENT_UPDATE";
    public static final String LEAVEMSG_DEPARTMENT_DELETE = "LEAVEMSG_DEPARTMENT_DELETE";
    public static final String LEAVEMSG_DEPARTMENT_EXPORT = "LEAVEMSG_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String LEAVEMSG_WORKGROUP_READ = "LEAVEMSG_WORKGROUP_READ";
    public static final String LEAVEMSG_WORKGROUP_CREATE = "LEAVEMSG_WORKGROUP_CREATE";
    public static final String LEAVEMSG_WORKGROUP_UPDATE = "LEAVEMSG_WORKGROUP_UPDATE";
    public static final String LEAVEMSG_WORKGROUP_DELETE = "LEAVEMSG_WORKGROUP_DELETE";
    public static final String LEAVEMSG_WORKGROUP_EXPORT = "LEAVEMSG_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String LEAVEMSG_AGENT_READ = "LEAVEMSG_AGENT_READ";
    public static final String LEAVEMSG_AGENT_CREATE = "LEAVEMSG_AGENT_CREATE";
    public static final String LEAVEMSG_AGENT_UPDATE = "LEAVEMSG_AGENT_UPDATE";
    public static final String LEAVEMSG_AGENT_DELETE = "LEAVEMSG_AGENT_DELETE";
    public static final String LEAVEMSG_AGENT_EXPORT = "LEAVEMSG_AGENT_EXPORT";
    // 用户级权限
    public static final String LEAVEMSG_USER_READ = "LEAVEMSG_USER_READ";
    public static final String LEAVEMSG_USER_CREATE = "LEAVEMSG_USER_CREATE";
    public static final String LEAVEMSG_USER_UPDATE = "LEAVEMSG_USER_UPDATE";
    public static final String LEAVEMSG_USER_DELETE = "LEAVEMSG_USER_DELETE";
    public static final String LEAVEMSG_USER_EXPORT = "LEAVEMSG_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_LEAVEMSG_READ_ANY_LEVEL = "hasAnyAuthority('LEAVEMSG_PLATFORM_READ', 'LEAVEMSG_ORGANIZATION_READ', 'LEAVEMSG_DEPARTMENT_READ', 'LEAVEMSG_WORKGROUP_READ', 'LEAVEMSG_AGENT_READ', 'LEAVEMSG_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_LEAVEMSG_CREATE_ANY_LEVEL = "hasAnyAuthority('LEAVEMSG_PLATFORM_CREATE', 'LEAVEMSG_ORGANIZATION_CREATE', 'LEAVEMSG_DEPARTMENT_CREATE', 'LEAVEMSG_WORKGROUP_CREATE', 'LEAVEMSG_AGENT_CREATE', 'LEAVEMSG_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_LEAVEMSG_UPDATE_ANY_LEVEL = "hasAnyAuthority('LEAVEMSG_PLATFORM_UPDATE', 'LEAVEMSG_ORGANIZATION_UPDATE', 'LEAVEMSG_DEPARTMENT_UPDATE', 'LEAVEMSG_WORKGROUP_UPDATE', 'LEAVEMSG_AGENT_UPDATE', 'LEAVEMSG_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_LEAVEMSG_DELETE_ANY_LEVEL = "hasAnyAuthority('LEAVEMSG_PLATFORM_DELETE', 'LEAVEMSG_ORGANIZATION_DELETE', 'LEAVEMSG_DEPARTMENT_DELETE', 'LEAVEMSG_WORKGROUP_DELETE', 'LEAVEMSG_AGENT_DELETE', 'LEAVEMSG_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_LEAVEMSG_EXPORT_ANY_LEVEL = "hasAnyAuthority('LEAVEMSG_PLATFORM_EXPORT', 'LEAVEMSG_ORGANIZATION_EXPORT', 'LEAVEMSG_DEPARTMENT_EXPORT', 'LEAVEMSG_WORKGROUP_EXPORT', 'LEAVEMSG_AGENT_EXPORT', 'LEAVEMSG_USER_EXPORT')";

}