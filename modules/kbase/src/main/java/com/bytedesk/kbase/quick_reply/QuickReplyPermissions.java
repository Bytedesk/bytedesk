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
package com.bytedesk.kbase.quick_reply;

import com.bytedesk.core.base.BasePermissions;

public class QuickReplyPermissions extends BasePermissions {

    // 模块前缀
    public static final String QUICKREPLY_PREFIX = "QUICKREPLY_";

    // 平台级权限
    public static final String QUICKREPLY_PLATFORM_READ = "QUICKREPLY_PLATFORM_READ";
    public static final String QUICKREPLY_PLATFORM_CREATE = "QUICKREPLY_PLATFORM_CREATE";
    public static final String QUICKREPLY_PLATFORM_UPDATE = "QUICKREPLY_PLATFORM_UPDATE";
    public static final String QUICKREPLY_PLATFORM_DELETE = "QUICKREPLY_PLATFORM_DELETE";
    public static final String QUICKREPLY_PLATFORM_EXPORT = "QUICKREPLY_PLATFORM_EXPORT";

    // 组织级权限
    public static final String QUICKREPLY_ORGANIZATION_READ = "QUICKREPLY_ORGANIZATION_READ";
    public static final String QUICKREPLY_ORGANIZATION_CREATE = "QUICKREPLY_ORGANIZATION_CREATE";
    public static final String QUICKREPLY_ORGANIZATION_UPDATE = "QUICKREPLY_ORGANIZATION_UPDATE";
    public static final String QUICKREPLY_ORGANIZATION_DELETE = "QUICKREPLY_ORGANIZATION_DELETE";
    public static final String QUICKREPLY_ORGANIZATION_EXPORT = "QUICKREPLY_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String QUICKREPLY_DEPARTMENT_READ = "QUICKREPLY_DEPARTMENT_READ";
    public static final String QUICKREPLY_DEPARTMENT_CREATE = "QUICKREPLY_DEPARTMENT_CREATE";
    public static final String QUICKREPLY_DEPARTMENT_UPDATE = "QUICKREPLY_DEPARTMENT_UPDATE";
    public static final String QUICKREPLY_DEPARTMENT_DELETE = "QUICKREPLY_DEPARTMENT_DELETE";
    public static final String QUICKREPLY_DEPARTMENT_EXPORT = "QUICKREPLY_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String QUICKREPLY_WORKGROUP_READ = "QUICKREPLY_WORKGROUP_READ";
    public static final String QUICKREPLY_WORKGROUP_CREATE = "QUICKREPLY_WORKGROUP_CREATE";
    public static final String QUICKREPLY_WORKGROUP_UPDATE = "QUICKREPLY_WORKGROUP_UPDATE";
    public static final String QUICKREPLY_WORKGROUP_DELETE = "QUICKREPLY_WORKGROUP_DELETE";
    public static final String QUICKREPLY_WORKGROUP_EXPORT = "QUICKREPLY_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String QUICKREPLY_AGENT_READ = "QUICKREPLY_AGENT_READ";
    public static final String QUICKREPLY_AGENT_CREATE = "QUICKREPLY_AGENT_CREATE";
    public static final String QUICKREPLY_AGENT_UPDATE = "QUICKREPLY_AGENT_UPDATE";
    public static final String QUICKREPLY_AGENT_DELETE = "QUICKREPLY_AGENT_DELETE";
    public static final String QUICKREPLY_AGENT_EXPORT = "QUICKREPLY_AGENT_EXPORT";
    // 用户级权限
    public static final String QUICKREPLY_USER_READ = "QUICKREPLY_USER_READ";
    public static final String QUICKREPLY_USER_CREATE = "QUICKREPLY_USER_CREATE";
    public static final String QUICKREPLY_USER_UPDATE = "QUICKREPLY_USER_UPDATE";
    public static final String QUICKREPLY_USER_DELETE = "QUICKREPLY_USER_DELETE";
    public static final String QUICKREPLY_USER_EXPORT = "QUICKREPLY_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_QUICKREPLY_READ_ANY_LEVEL = "hasAnyAuthority('QUICKREPLY_PLATFORM_READ', 'QUICKREPLY_ORGANIZATION_READ', 'QUICKREPLY_DEPARTMENT_READ', 'QUICKREPLY_WORKGROUP_READ', 'QUICKREPLY_AGENT_READ', 'QUICKREPLY_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_QUICKREPLY_CREATE_ANY_LEVEL = "hasAnyAuthority('QUICKREPLY_PLATFORM_CREATE', 'QUICKREPLY_ORGANIZATION_CREATE', 'QUICKREPLY_DEPARTMENT_CREATE', 'QUICKREPLY_WORKGROUP_CREATE', 'QUICKREPLY_AGENT_CREATE', 'QUICKREPLY_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_QUICKREPLY_UPDATE_ANY_LEVEL = "hasAnyAuthority('QUICKREPLY_PLATFORM_UPDATE', 'QUICKREPLY_ORGANIZATION_UPDATE', 'QUICKREPLY_DEPARTMENT_UPDATE', 'QUICKREPLY_WORKGROUP_UPDATE', 'QUICKREPLY_AGENT_UPDATE', 'QUICKREPLY_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_QUICKREPLY_DELETE_ANY_LEVEL = "hasAnyAuthority('QUICKREPLY_PLATFORM_DELETE', 'QUICKREPLY_ORGANIZATION_DELETE', 'QUICKREPLY_DEPARTMENT_DELETE', 'QUICKREPLY_WORKGROUP_DELETE', 'QUICKREPLY_AGENT_DELETE', 'QUICKREPLY_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_QUICKREPLY_EXPORT_ANY_LEVEL = "hasAnyAuthority('QUICKREPLY_PLATFORM_EXPORT', 'QUICKREPLY_ORGANIZATION_EXPORT', 'QUICKREPLY_DEPARTMENT_EXPORT', 'QUICKREPLY_WORKGROUP_EXPORT', 'QUICKREPLY_AGENT_EXPORT', 'QUICKREPLY_USER_EXPORT')";

}