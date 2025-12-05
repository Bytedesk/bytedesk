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
package com.bytedesk.core.category;

import com.bytedesk.core.base.BasePermissions;

public class CategoryPermissions extends BasePermissions {

    // 模块前缀
    public static final String CATEGORY_PREFIX = "CATEGORY_";

    // 平台级权限
    public static final String CATEGORY_PLATFORM_READ = "CATEGORY_PLATFORM_READ";
    public static final String CATEGORY_PLATFORM_CREATE = "CATEGORY_PLATFORM_CREATE";
    public static final String CATEGORY_PLATFORM_UPDATE = "CATEGORY_PLATFORM_UPDATE";
    public static final String CATEGORY_PLATFORM_DELETE = "CATEGORY_PLATFORM_DELETE";
    public static final String CATEGORY_PLATFORM_EXPORT = "CATEGORY_PLATFORM_EXPORT";

    // 组织级权限
    public static final String CATEGORY_ORGANIZATION_READ = "CATEGORY_ORGANIZATION_READ";
    public static final String CATEGORY_ORGANIZATION_CREATE = "CATEGORY_ORGANIZATION_CREATE";
    public static final String CATEGORY_ORGANIZATION_UPDATE = "CATEGORY_ORGANIZATION_UPDATE";
    public static final String CATEGORY_ORGANIZATION_DELETE = "CATEGORY_ORGANIZATION_DELETE";
    public static final String CATEGORY_ORGANIZATION_EXPORT = "CATEGORY_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String CATEGORY_DEPARTMENT_READ = "CATEGORY_DEPARTMENT_READ";
    public static final String CATEGORY_DEPARTMENT_CREATE = "CATEGORY_DEPARTMENT_CREATE";
    public static final String CATEGORY_DEPARTMENT_UPDATE = "CATEGORY_DEPARTMENT_UPDATE";
    public static final String CATEGORY_DEPARTMENT_DELETE = "CATEGORY_DEPARTMENT_DELETE";
    public static final String CATEGORY_DEPARTMENT_EXPORT = "CATEGORY_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String CATEGORY_WORKGROUP_READ = "CATEGORY_WORKGROUP_READ";
    public static final String CATEGORY_WORKGROUP_CREATE = "CATEGORY_WORKGROUP_CREATE";
    public static final String CATEGORY_WORKGROUP_UPDATE = "CATEGORY_WORKGROUP_UPDATE";
    public static final String CATEGORY_WORKGROUP_DELETE = "CATEGORY_WORKGROUP_DELETE";
    public static final String CATEGORY_WORKGROUP_EXPORT = "CATEGORY_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String CATEGORY_AGENT_READ = "CATEGORY_AGENT_READ";
    public static final String CATEGORY_AGENT_CREATE = "CATEGORY_AGENT_CREATE";
    public static final String CATEGORY_AGENT_UPDATE = "CATEGORY_AGENT_UPDATE";
    public static final String CATEGORY_AGENT_DELETE = "CATEGORY_AGENT_DELETE";
    public static final String CATEGORY_AGENT_EXPORT = "CATEGORY_AGENT_EXPORT";
    
    // 用户级权限
    public static final String CATEGORY_USER_READ = "CATEGORY_USER_READ";
    public static final String CATEGORY_USER_CREATE = "CATEGORY_USER_CREATE";
    public static final String CATEGORY_USER_UPDATE = "CATEGORY_USER_UPDATE";
    public static final String CATEGORY_USER_DELETE = "CATEGORY_USER_DELETE";
    public static final String CATEGORY_USER_EXPORT = "CATEGORY_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_CATEGORY_READ_ANY_LEVEL = "hasAnyAuthority('CATEGORY_PLATFORM_READ', 'CATEGORY_ORGANIZATION_READ', 'CATEGORY_DEPARTMENT_READ', 'CATEGORY_WORKGROUP_READ', 'CATEGORY_AGENT_READ', 'CATEGORY_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_CATEGORY_CREATE_ANY_LEVEL = "hasAnyAuthority('CATEGORY_PLATFORM_CREATE', 'CATEGORY_ORGANIZATION_CREATE', 'CATEGORY_DEPARTMENT_CREATE', 'CATEGORY_WORKGROUP_CREATE', 'CATEGORY_AGENT_CREATE', 'CATEGORY_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_CATEGORY_UPDATE_ANY_LEVEL = "hasAnyAuthority('CATEGORY_PLATFORM_UPDATE', 'CATEGORY_ORGANIZATION_UPDATE', 'CATEGORY_DEPARTMENT_UPDATE', 'CATEGORY_WORKGROUP_UPDATE', 'CATEGORY_AGENT_UPDATE', 'CATEGORY_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_CATEGORY_DELETE_ANY_LEVEL = "hasAnyAuthority('CATEGORY_PLATFORM_DELETE', 'CATEGORY_ORGANIZATION_DELETE', 'CATEGORY_DEPARTMENT_DELETE', 'CATEGORY_WORKGROUP_DELETE', 'CATEGORY_AGENT_DELETE', 'CATEGORY_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_CATEGORY_EXPORT_ANY_LEVEL = "hasAnyAuthority('CATEGORY_PLATFORM_EXPORT', 'CATEGORY_ORGANIZATION_EXPORT', 'CATEGORY_DEPARTMENT_EXPORT', 'CATEGORY_WORKGROUP_EXPORT', 'CATEGORY_AGENT_EXPORT', 'CATEGORY_USER_EXPORT')";

}