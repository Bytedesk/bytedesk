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
package com.bytedesk.core.favorite;

import com.bytedesk.core.base.BasePermissions;

public class FavoritePermissions extends BasePermissions {

    // 模块前缀
    public static final String FAVORITE_PREFIX = "FAVORITE_";

    // 平台级权限
    public static final String FAVORITE_PLATFORM_READ = "FAVORITE_PLATFORM_READ";
    public static final String FAVORITE_PLATFORM_CREATE = "FAVORITE_PLATFORM_CREATE";
    public static final String FAVORITE_PLATFORM_UPDATE = "FAVORITE_PLATFORM_UPDATE";
    public static final String FAVORITE_PLATFORM_DELETE = "FAVORITE_PLATFORM_DELETE";
    public static final String FAVORITE_PLATFORM_EXPORT = "FAVORITE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String FAVORITE_ORGANIZATION_READ = "FAVORITE_ORGANIZATION_READ";
    public static final String FAVORITE_ORGANIZATION_CREATE = "FAVORITE_ORGANIZATION_CREATE";
    public static final String FAVORITE_ORGANIZATION_UPDATE = "FAVORITE_ORGANIZATION_UPDATE";
    public static final String FAVORITE_ORGANIZATION_DELETE = "FAVORITE_ORGANIZATION_DELETE";
    public static final String FAVORITE_ORGANIZATION_EXPORT = "FAVORITE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String FAVORITE_DEPARTMENT_READ = "FAVORITE_DEPARTMENT_READ";
    public static final String FAVORITE_DEPARTMENT_CREATE = "FAVORITE_DEPARTMENT_CREATE";
    public static final String FAVORITE_DEPARTMENT_UPDATE = "FAVORITE_DEPARTMENT_UPDATE";
    public static final String FAVORITE_DEPARTMENT_DELETE = "FAVORITE_DEPARTMENT_DELETE";
    public static final String FAVORITE_DEPARTMENT_EXPORT = "FAVORITE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String FAVORITE_WORKGROUP_READ = "FAVORITE_WORKGROUP_READ";
    public static final String FAVORITE_WORKGROUP_CREATE = "FAVORITE_WORKGROUP_CREATE";
    public static final String FAVORITE_WORKGROUP_UPDATE = "FAVORITE_WORKGROUP_UPDATE";
    public static final String FAVORITE_WORKGROUP_DELETE = "FAVORITE_WORKGROUP_DELETE";
    public static final String FAVORITE_WORKGROUP_EXPORT = "FAVORITE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String FAVORITE_AGENT_READ = "FAVORITE_AGENT_READ";
    public static final String FAVORITE_AGENT_CREATE = "FAVORITE_AGENT_CREATE";
    public static final String FAVORITE_AGENT_UPDATE = "FAVORITE_AGENT_UPDATE";
    public static final String FAVORITE_AGENT_DELETE = "FAVORITE_AGENT_DELETE";
    public static final String FAVORITE_AGENT_EXPORT = "FAVORITE_AGENT_EXPORT";
    // 用户级权限
    public static final String FAVORITE_USER_READ = "FAVORITE_USER_READ";
    public static final String FAVORITE_USER_CREATE = "FAVORITE_USER_CREATE";
    public static final String FAVORITE_USER_UPDATE = "FAVORITE_USER_UPDATE";
    public static final String FAVORITE_USER_DELETE = "FAVORITE_USER_DELETE";
    public static final String FAVORITE_USER_EXPORT = "FAVORITE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_FAVORITE_READ_ANY_LEVEL = "hasAnyAuthority('FAVORITE_PLATFORM_READ', 'FAVORITE_ORGANIZATION_READ', 'FAVORITE_DEPARTMENT_READ', 'FAVORITE_WORKGROUP_READ', 'FAVORITE_AGENT_READ', 'FAVORITE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_FAVORITE_CREATE_ANY_LEVEL = "hasAnyAuthority('FAVORITE_PLATFORM_CREATE', 'FAVORITE_ORGANIZATION_CREATE', 'FAVORITE_DEPARTMENT_CREATE', 'FAVORITE_WORKGROUP_CREATE', 'FAVORITE_AGENT_CREATE', 'FAVORITE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_FAVORITE_UPDATE_ANY_LEVEL = "hasAnyAuthority('FAVORITE_PLATFORM_UPDATE', 'FAVORITE_ORGANIZATION_UPDATE', 'FAVORITE_DEPARTMENT_UPDATE', 'FAVORITE_WORKGROUP_UPDATE', 'FAVORITE_AGENT_UPDATE', 'FAVORITE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_FAVORITE_DELETE_ANY_LEVEL = "hasAnyAuthority('FAVORITE_PLATFORM_DELETE', 'FAVORITE_ORGANIZATION_DELETE', 'FAVORITE_DEPARTMENT_DELETE', 'FAVORITE_WORKGROUP_DELETE', 'FAVORITE_AGENT_DELETE', 'FAVORITE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_FAVORITE_EXPORT_ANY_LEVEL = "hasAnyAuthority('FAVORITE_PLATFORM_EXPORT', 'FAVORITE_ORGANIZATION_EXPORT', 'FAVORITE_DEPARTMENT_EXPORT', 'FAVORITE_WORKGROUP_EXPORT', 'FAVORITE_AGENT_EXPORT', 'FAVORITE_USER_EXPORT')";

}