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
package com.bytedesk.core.group;

import com.bytedesk.core.base.BasePermissions;

public class GroupPermissions extends BasePermissions {

    // 模块前缀
    public static final String GROUP_PREFIX = "GROUP_";

    // 平台级权限
    public static final String GROUP_PLATFORM_READ = "GROUP_PLATFORM_READ";
    public static final String GROUP_PLATFORM_CREATE = "GROUP_PLATFORM_CREATE";
    public static final String GROUP_PLATFORM_UPDATE = "GROUP_PLATFORM_UPDATE";
    public static final String GROUP_PLATFORM_DELETE = "GROUP_PLATFORM_DELETE";
    public static final String GROUP_PLATFORM_EXPORT = "GROUP_PLATFORM_EXPORT";

    // 组织级权限
    public static final String GROUP_ORGANIZATION_READ = "GROUP_ORGANIZATION_READ";
    public static final String GROUP_ORGANIZATION_CREATE = "GROUP_ORGANIZATION_CREATE";
    public static final String GROUP_ORGANIZATION_UPDATE = "GROUP_ORGANIZATION_UPDATE";
    public static final String GROUP_ORGANIZATION_DELETE = "GROUP_ORGANIZATION_DELETE";
    public static final String GROUP_ORGANIZATION_EXPORT = "GROUP_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String GROUP_DEPARTMENT_READ = "GROUP_DEPARTMENT_READ";
    public static final String GROUP_DEPARTMENT_CREATE = "GROUP_DEPARTMENT_CREATE";
    public static final String GROUP_DEPARTMENT_UPDATE = "GROUP_DEPARTMENT_UPDATE";
    public static final String GROUP_DEPARTMENT_DELETE = "GROUP_DEPARTMENT_DELETE";
    public static final String GROUP_DEPARTMENT_EXPORT = "GROUP_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String GROUP_WORKGROUP_READ = "GROUP_WORKGROUP_READ";
    public static final String GROUP_WORKGROUP_CREATE = "GROUP_WORKGROUP_CREATE";
    public static final String GROUP_WORKGROUP_UPDATE = "GROUP_WORKGROUP_UPDATE";
    public static final String GROUP_WORKGROUP_DELETE = "GROUP_WORKGROUP_DELETE";
    public static final String GROUP_WORKGROUP_EXPORT = "GROUP_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String GROUP_AGENT_READ = "GROUP_AGENT_READ";
    public static final String GROUP_AGENT_CREATE = "GROUP_AGENT_CREATE";
    public static final String GROUP_AGENT_UPDATE = "GROUP_AGENT_UPDATE";
    public static final String GROUP_AGENT_DELETE = "GROUP_AGENT_DELETE";
    public static final String GROUP_AGENT_EXPORT = "GROUP_AGENT_EXPORT";
    // 用户级权限
    public static final String GROUP_USER_READ = "GROUP_USER_READ";
    public static final String GROUP_USER_CREATE = "GROUP_USER_CREATE";
    public static final String GROUP_USER_UPDATE = "GROUP_USER_UPDATE";
    public static final String GROUP_USER_DELETE = "GROUP_USER_DELETE";
    public static final String GROUP_USER_EXPORT = "GROUP_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_GROUP_READ_ANY_LEVEL = "hasAnyAuthority('GROUP_PLATFORM_READ', 'GROUP_ORGANIZATION_READ', 'GROUP_DEPARTMENT_READ', 'GROUP_WORKGROUP_READ', 'GROUP_AGENT_READ', 'GROUP_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_GROUP_CREATE_ANY_LEVEL = "hasAnyAuthority('GROUP_PLATFORM_CREATE', 'GROUP_ORGANIZATION_CREATE', 'GROUP_DEPARTMENT_CREATE', 'GROUP_WORKGROUP_CREATE', 'GROUP_AGENT_CREATE', 'GROUP_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_GROUP_UPDATE_ANY_LEVEL = "hasAnyAuthority('GROUP_PLATFORM_UPDATE', 'GROUP_ORGANIZATION_UPDATE', 'GROUP_DEPARTMENT_UPDATE', 'GROUP_WORKGROUP_UPDATE', 'GROUP_AGENT_UPDATE', 'GROUP_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_GROUP_DELETE_ANY_LEVEL = "hasAnyAuthority('GROUP_PLATFORM_DELETE', 'GROUP_ORGANIZATION_DELETE', 'GROUP_DEPARTMENT_DELETE', 'GROUP_WORKGROUP_DELETE', 'GROUP_AGENT_DELETE', 'GROUP_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_GROUP_EXPORT_ANY_LEVEL = "hasAnyAuthority('GROUP_PLATFORM_EXPORT', 'GROUP_ORGANIZATION_EXPORT', 'GROUP_DEPARTMENT_EXPORT', 'GROUP_WORKGROUP_EXPORT', 'GROUP_AGENT_EXPORT', 'GROUP_USER_EXPORT')";

}