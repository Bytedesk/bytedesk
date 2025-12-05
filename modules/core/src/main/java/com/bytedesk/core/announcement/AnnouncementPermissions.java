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
package com.bytedesk.core.announcement;

import com.bytedesk.core.base.BasePermissions;

public class AnnouncementPermissions extends BasePermissions {

    // 模块前缀
    public static final String ANNOUNCEMENT_PREFIX = "ANNOUNCEMENT_";

    // 平台级权限
    public static final String ANNOUNCEMENT_PLATFORM_READ = "ANNOUNCEMENT_PLATFORM_READ";
    public static final String ANNOUNCEMENT_PLATFORM_CREATE = "ANNOUNCEMENT_PLATFORM_CREATE";
    public static final String ANNOUNCEMENT_PLATFORM_UPDATE = "ANNOUNCEMENT_PLATFORM_UPDATE";
    public static final String ANNOUNCEMENT_PLATFORM_DELETE = "ANNOUNCEMENT_PLATFORM_DELETE";
    public static final String ANNOUNCEMENT_PLATFORM_EXPORT = "ANNOUNCEMENT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String ANNOUNCEMENT_ORGANIZATION_READ = "ANNOUNCEMENT_ORGANIZATION_READ";
    public static final String ANNOUNCEMENT_ORGANIZATION_CREATE = "ANNOUNCEMENT_ORGANIZATION_CREATE";
    public static final String ANNOUNCEMENT_ORGANIZATION_UPDATE = "ANNOUNCEMENT_ORGANIZATION_UPDATE";
    public static final String ANNOUNCEMENT_ORGANIZATION_DELETE = "ANNOUNCEMENT_ORGANIZATION_DELETE";
    public static final String ANNOUNCEMENT_ORGANIZATION_EXPORT = "ANNOUNCEMENT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String ANNOUNCEMENT_DEPARTMENT_READ = "ANNOUNCEMENT_DEPARTMENT_READ";
    public static final String ANNOUNCEMENT_DEPARTMENT_CREATE = "ANNOUNCEMENT_DEPARTMENT_CREATE";
    public static final String ANNOUNCEMENT_DEPARTMENT_UPDATE = "ANNOUNCEMENT_DEPARTMENT_UPDATE";
    public static final String ANNOUNCEMENT_DEPARTMENT_DELETE = "ANNOUNCEMENT_DEPARTMENT_DELETE";
    public static final String ANNOUNCEMENT_DEPARTMENT_EXPORT = "ANNOUNCEMENT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String ANNOUNCEMENT_WORKGROUP_READ = "ANNOUNCEMENT_WORKGROUP_READ";
    public static final String ANNOUNCEMENT_WORKGROUP_CREATE = "ANNOUNCEMENT_WORKGROUP_CREATE";
    public static final String ANNOUNCEMENT_WORKGROUP_UPDATE = "ANNOUNCEMENT_WORKGROUP_UPDATE";
    public static final String ANNOUNCEMENT_WORKGROUP_DELETE = "ANNOUNCEMENT_WORKGROUP_DELETE";
    public static final String ANNOUNCEMENT_WORKGROUP_EXPORT = "ANNOUNCEMENT_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String ANNOUNCEMENT_AGENT_READ = "ANNOUNCEMENT_AGENT_READ";
    public static final String ANNOUNCEMENT_AGENT_CREATE = "ANNOUNCEMENT_AGENT_CREATE";
    public static final String ANNOUNCEMENT_AGENT_UPDATE = "ANNOUNCEMENT_AGENT_UPDATE";
    public static final String ANNOUNCEMENT_AGENT_DELETE = "ANNOUNCEMENT_AGENT_DELETE";
    public static final String ANNOUNCEMENT_AGENT_EXPORT = "ANNOUNCEMENT_AGENT_EXPORT";
    // 用户级权限
    public static final String ANNOUNCEMENT_USER_READ = "ANNOUNCEMENT_USER_READ";
    public static final String ANNOUNCEMENT_USER_CREATE = "ANNOUNCEMENT_USER_CREATE";
    public static final String ANNOUNCEMENT_USER_UPDATE = "ANNOUNCEMENT_USER_UPDATE";
    public static final String ANNOUNCEMENT_USER_DELETE = "ANNOUNCEMENT_USER_DELETE";
    public static final String ANNOUNCEMENT_USER_EXPORT = "ANNOUNCEMENT_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_ANNOUNCEMENT_READ_ANY_LEVEL = "hasAnyAuthority('ANNOUNCEMENT_PLATFORM_READ', 'ANNOUNCEMENT_ORGANIZATION_READ', 'ANNOUNCEMENT_DEPARTMENT_READ', 'ANNOUNCEMENT_WORKGROUP_READ', 'ANNOUNCEMENT_AGENT_READ', 'ANNOUNCEMENT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_ANNOUNCEMENT_CREATE_ANY_LEVEL = "hasAnyAuthority('ANNOUNCEMENT_PLATFORM_CREATE', 'ANNOUNCEMENT_ORGANIZATION_CREATE', 'ANNOUNCEMENT_DEPARTMENT_CREATE', 'ANNOUNCEMENT_WORKGROUP_CREATE', 'ANNOUNCEMENT_AGENT_CREATE', 'ANNOUNCEMENT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_ANNOUNCEMENT_UPDATE_ANY_LEVEL = "hasAnyAuthority('ANNOUNCEMENT_PLATFORM_UPDATE', 'ANNOUNCEMENT_ORGANIZATION_UPDATE', 'ANNOUNCEMENT_DEPARTMENT_UPDATE', 'ANNOUNCEMENT_WORKGROUP_UPDATE', 'ANNOUNCEMENT_AGENT_UPDATE', 'ANNOUNCEMENT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_ANNOUNCEMENT_DELETE_ANY_LEVEL = "hasAnyAuthority('ANNOUNCEMENT_PLATFORM_DELETE', 'ANNOUNCEMENT_ORGANIZATION_DELETE', 'ANNOUNCEMENT_DEPARTMENT_DELETE', 'ANNOUNCEMENT_WORKGROUP_DELETE', 'ANNOUNCEMENT_AGENT_DELETE', 'ANNOUNCEMENT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_ANNOUNCEMENT_EXPORT_ANY_LEVEL = "hasAnyAuthority('ANNOUNCEMENT_PLATFORM_EXPORT', 'ANNOUNCEMENT_ORGANIZATION_EXPORT', 'ANNOUNCEMENT_DEPARTMENT_EXPORT', 'ANNOUNCEMENT_WORKGROUP_EXPORT', 'ANNOUNCEMENT_AGENT_EXPORT', 'ANNOUNCEMENT_USER_EXPORT')";

}
