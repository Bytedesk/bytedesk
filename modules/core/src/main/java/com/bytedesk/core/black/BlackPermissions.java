/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:16
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
package com.bytedesk.core.black;

import com.bytedesk.core.base.BasePermissions;

public class BlackPermissions extends BasePermissions {

    // 模块前缀
    public static final String BLACK_PREFIX = "BLACK_";

    // 平台级权限
    public static final String BLACK_PLATFORM_READ = "BLACK_PLATFORM_READ";
    public static final String BLACK_PLATFORM_CREATE = "BLACK_PLATFORM_CREATE";
    public static final String BLACK_PLATFORM_UPDATE = "BLACK_PLATFORM_UPDATE";
    public static final String BLACK_PLATFORM_DELETE = "BLACK_PLATFORM_DELETE";
    public static final String BLACK_PLATFORM_EXPORT = "BLACK_PLATFORM_EXPORT";

    // 组织级权限
    public static final String BLACK_ORGANIZATION_READ = "BLACK_ORGANIZATION_READ";
    public static final String BLACK_ORGANIZATION_CREATE = "BLACK_ORGANIZATION_CREATE";
    public static final String BLACK_ORGANIZATION_UPDATE = "BLACK_ORGANIZATION_UPDATE";
    public static final String BLACK_ORGANIZATION_DELETE = "BLACK_ORGANIZATION_DELETE";
    public static final String BLACK_ORGANIZATION_EXPORT = "BLACK_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String BLACK_DEPARTMENT_READ = "BLACK_DEPARTMENT_READ";
    public static final String BLACK_DEPARTMENT_CREATE = "BLACK_DEPARTMENT_CREATE";
    public static final String BLACK_DEPARTMENT_UPDATE = "BLACK_DEPARTMENT_UPDATE";
    public static final String BLACK_DEPARTMENT_DELETE = "BLACK_DEPARTMENT_DELETE";
    public static final String BLACK_DEPARTMENT_EXPORT = "BLACK_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String BLACK_WORKGROUP_READ = "BLACK_WORKGROUP_READ";
    public static final String BLACK_WORKGROUP_CREATE = "BLACK_WORKGROUP_CREATE";
    public static final String BLACK_WORKGROUP_UPDATE = "BLACK_WORKGROUP_UPDATE";
    public static final String BLACK_WORKGROUP_DELETE = "BLACK_WORKGROUP_DELETE";
    public static final String BLACK_WORKGROUP_EXPORT = "BLACK_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String BLACK_AGENT_READ = "BLACK_AGENT_READ";
    public static final String BLACK_AGENT_CREATE = "BLACK_AGENT_CREATE";
    public static final String BLACK_AGENT_UPDATE = "BLACK_AGENT_UPDATE";
    public static final String BLACK_AGENT_DELETE = "BLACK_AGENT_DELETE";
    public static final String BLACK_AGENT_EXPORT = "BLACK_AGENT_EXPORT";
    // 用户级权限
    public static final String BLACK_USER_READ = "BLACK_USER_READ";
    public static final String BLACK_USER_CREATE = "BLACK_USER_CREATE";
    public static final String BLACK_USER_UPDATE = "BLACK_USER_UPDATE";
    public static final String BLACK_USER_DELETE = "BLACK_USER_DELETE";
    public static final String BLACK_USER_EXPORT = "BLACK_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_BLACK_READ_ANY_LEVEL = "hasAnyAuthority('BLACK_PLATFORM_READ', 'BLACK_ORGANIZATION_READ', 'BLACK_DEPARTMENT_READ', 'BLACK_WORKGROUP_READ', 'BLACK_AGENT_READ', 'BLACK_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_BLACK_CREATE_ANY_LEVEL = "hasAnyAuthority('BLACK_PLATFORM_CREATE', 'BLACK_ORGANIZATION_CREATE', 'BLACK_DEPARTMENT_CREATE', 'BLACK_WORKGROUP_CREATE', 'BLACK_AGENT_CREATE', 'BLACK_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_BLACK_UPDATE_ANY_LEVEL = "hasAnyAuthority('BLACK_PLATFORM_UPDATE', 'BLACK_ORGANIZATION_UPDATE', 'BLACK_DEPARTMENT_UPDATE', 'BLACK_WORKGROUP_UPDATE', 'BLACK_AGENT_UPDATE', 'BLACK_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_BLACK_DELETE_ANY_LEVEL = "hasAnyAuthority('BLACK_PLATFORM_DELETE', 'BLACK_ORGANIZATION_DELETE', 'BLACK_DEPARTMENT_DELETE', 'BLACK_WORKGROUP_DELETE', 'BLACK_AGENT_DELETE', 'BLACK_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_BLACK_EXPORT_ANY_LEVEL = "hasAnyAuthority('BLACK_PLATFORM_EXPORT', 'BLACK_ORGANIZATION_EXPORT', 'BLACK_DEPARTMENT_EXPORT', 'BLACK_WORKGROUP_EXPORT', 'BLACK_AGENT_EXPORT', 'BLACK_USER_EXPORT')";

}