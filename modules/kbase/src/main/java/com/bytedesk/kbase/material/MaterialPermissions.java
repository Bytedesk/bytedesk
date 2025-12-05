/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.material;

import com.bytedesk.core.base.BasePermissions;

public class MaterialPermissions extends BasePermissions {

    // 模块前缀
    public static final String MATERIAL_PREFIX = "MATERIAL_";

    // 平台级权限
    public static final String MATERIAL_PLATFORM_READ = "MATERIAL_PLATFORM_READ";
    public static final String MATERIAL_PLATFORM_CREATE = "MATERIAL_PLATFORM_CREATE";
    public static final String MATERIAL_PLATFORM_UPDATE = "MATERIAL_PLATFORM_UPDATE";
    public static final String MATERIAL_PLATFORM_DELETE = "MATERIAL_PLATFORM_DELETE";
    public static final String MATERIAL_PLATFORM_EXPORT = "MATERIAL_PLATFORM_EXPORT";

    // 组织级权限
    public static final String MATERIAL_ORGANIZATION_READ = "MATERIAL_ORGANIZATION_READ";
    public static final String MATERIAL_ORGANIZATION_CREATE = "MATERIAL_ORGANIZATION_CREATE";
    public static final String MATERIAL_ORGANIZATION_UPDATE = "MATERIAL_ORGANIZATION_UPDATE";
    public static final String MATERIAL_ORGANIZATION_DELETE = "MATERIAL_ORGANIZATION_DELETE";
    public static final String MATERIAL_ORGANIZATION_EXPORT = "MATERIAL_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String MATERIAL_DEPARTMENT_READ = "MATERIAL_DEPARTMENT_READ";
    public static final String MATERIAL_DEPARTMENT_CREATE = "MATERIAL_DEPARTMENT_CREATE";
    public static final String MATERIAL_DEPARTMENT_UPDATE = "MATERIAL_DEPARTMENT_UPDATE";
    public static final String MATERIAL_DEPARTMENT_DELETE = "MATERIAL_DEPARTMENT_DELETE";
    public static final String MATERIAL_DEPARTMENT_EXPORT = "MATERIAL_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String MATERIAL_WORKGROUP_READ = "MATERIAL_WORKGROUP_READ";
    public static final String MATERIAL_WORKGROUP_CREATE = "MATERIAL_WORKGROUP_CREATE";
    public static final String MATERIAL_WORKGROUP_UPDATE = "MATERIAL_WORKGROUP_UPDATE";
    public static final String MATERIAL_WORKGROUP_DELETE = "MATERIAL_WORKGROUP_DELETE";
    public static final String MATERIAL_WORKGROUP_EXPORT = "MATERIAL_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String MATERIAL_AGENT_READ = "MATERIAL_AGENT_READ";
    public static final String MATERIAL_AGENT_CREATE = "MATERIAL_AGENT_CREATE";
    public static final String MATERIAL_AGENT_UPDATE = "MATERIAL_AGENT_UPDATE";
    public static final String MATERIAL_AGENT_DELETE = "MATERIAL_AGENT_DELETE";
    public static final String MATERIAL_AGENT_EXPORT = "MATERIAL_AGENT_EXPORT";
    // 用户级权限
    public static final String MATERIAL_USER_READ = "MATERIAL_USER_READ";
    public static final String MATERIAL_USER_CREATE = "MATERIAL_USER_CREATE";
    public static final String MATERIAL_USER_UPDATE = "MATERIAL_USER_UPDATE";
    public static final String MATERIAL_USER_DELETE = "MATERIAL_USER_DELETE";
    public static final String MATERIAL_USER_EXPORT = "MATERIAL_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_MATERIAL_READ_ANY_LEVEL = "hasAnyAuthority('MATERIAL_PLATFORM_READ', 'MATERIAL_ORGANIZATION_READ', 'MATERIAL_DEPARTMENT_READ', 'MATERIAL_WORKGROUP_READ', 'MATERIAL_AGENT_READ', 'MATERIAL_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_MATERIAL_CREATE_ANY_LEVEL = "hasAnyAuthority('MATERIAL_PLATFORM_CREATE', 'MATERIAL_ORGANIZATION_CREATE', 'MATERIAL_DEPARTMENT_CREATE', 'MATERIAL_WORKGROUP_CREATE', 'MATERIAL_AGENT_CREATE', 'MATERIAL_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_MATERIAL_UPDATE_ANY_LEVEL = "hasAnyAuthority('MATERIAL_PLATFORM_UPDATE', 'MATERIAL_ORGANIZATION_UPDATE', 'MATERIAL_DEPARTMENT_UPDATE', 'MATERIAL_WORKGROUP_UPDATE', 'MATERIAL_AGENT_UPDATE', 'MATERIAL_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_MATERIAL_DELETE_ANY_LEVEL = "hasAnyAuthority('MATERIAL_PLATFORM_DELETE', 'MATERIAL_ORGANIZATION_DELETE', 'MATERIAL_DEPARTMENT_DELETE', 'MATERIAL_WORKGROUP_DELETE', 'MATERIAL_AGENT_DELETE', 'MATERIAL_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_MATERIAL_EXPORT_ANY_LEVEL = "hasAnyAuthority('MATERIAL_PLATFORM_EXPORT', 'MATERIAL_ORGANIZATION_EXPORT', 'MATERIAL_DEPARTMENT_EXPORT', 'MATERIAL_WORKGROUP_EXPORT', 'MATERIAL_AGENT_EXPORT', 'MATERIAL_USER_EXPORT')";

}
