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
package com.bytedesk.core.member;

import com.bytedesk.core.base.BasePermissions;

public class MemberPermissions extends BasePermissions {

    // 模块前缀
    public static final String MEMBER_PREFIX = "MEMBER_";

    // 平台级权限
    public static final String MEMBER_PLATFORM_READ = "MEMBER_PLATFORM_READ";
    public static final String MEMBER_PLATFORM_CREATE = "MEMBER_PLATFORM_CREATE";
    public static final String MEMBER_PLATFORM_UPDATE = "MEMBER_PLATFORM_UPDATE";
    public static final String MEMBER_PLATFORM_DELETE = "MEMBER_PLATFORM_DELETE";
    public static final String MEMBER_PLATFORM_EXPORT = "MEMBER_PLATFORM_EXPORT";

    // 组织级权限
    public static final String MEMBER_ORGANIZATION_READ = "MEMBER_ORGANIZATION_READ";
    public static final String MEMBER_ORGANIZATION_CREATE = "MEMBER_ORGANIZATION_CREATE";
    public static final String MEMBER_ORGANIZATION_UPDATE = "MEMBER_ORGANIZATION_UPDATE";
    public static final String MEMBER_ORGANIZATION_DELETE = "MEMBER_ORGANIZATION_DELETE";
    public static final String MEMBER_ORGANIZATION_EXPORT = "MEMBER_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String MEMBER_DEPARTMENT_READ = "MEMBER_DEPARTMENT_READ";
    public static final String MEMBER_DEPARTMENT_CREATE = "MEMBER_DEPARTMENT_CREATE";
    public static final String MEMBER_DEPARTMENT_UPDATE = "MEMBER_DEPARTMENT_UPDATE";
    public static final String MEMBER_DEPARTMENT_DELETE = "MEMBER_DEPARTMENT_DELETE";
    public static final String MEMBER_DEPARTMENT_EXPORT = "MEMBER_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String MEMBER_WORKGROUP_READ = "MEMBER_WORKGROUP_READ";
    public static final String MEMBER_WORKGROUP_CREATE = "MEMBER_WORKGROUP_CREATE";
    public static final String MEMBER_WORKGROUP_UPDATE = "MEMBER_WORKGROUP_UPDATE";
    public static final String MEMBER_WORKGROUP_DELETE = "MEMBER_WORKGROUP_DELETE";
    public static final String MEMBER_WORKGROUP_EXPORT = "MEMBER_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String MEMBER_AGENT_READ = "MEMBER_AGENT_READ";
    public static final String MEMBER_AGENT_CREATE = "MEMBER_AGENT_CREATE";
    public static final String MEMBER_AGENT_UPDATE = "MEMBER_AGENT_UPDATE";
    public static final String MEMBER_AGENT_DELETE = "MEMBER_AGENT_DELETE";
    public static final String MEMBER_AGENT_EXPORT = "MEMBER_AGENT_EXPORT";
    // 用户级权限
    public static final String MEMBER_USER_READ = "MEMBER_USER_READ";
    public static final String MEMBER_USER_CREATE = "MEMBER_USER_CREATE";
    public static final String MEMBER_USER_UPDATE = "MEMBER_USER_UPDATE";
    public static final String MEMBER_USER_DELETE = "MEMBER_USER_DELETE";
    public static final String MEMBER_USER_EXPORT = "MEMBER_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_MEMBER_READ_ANY_LEVEL = "hasAnyAuthority('MEMBER_PLATFORM_READ', 'MEMBER_ORGANIZATION_READ', 'MEMBER_DEPARTMENT_READ', 'MEMBER_WORKGROUP_READ', 'MEMBER_AGENT_READ', 'MEMBER_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_MEMBER_CREATE_ANY_LEVEL = "hasAnyAuthority('MEMBER_PLATFORM_CREATE', 'MEMBER_ORGANIZATION_CREATE', 'MEMBER_DEPARTMENT_CREATE', 'MEMBER_WORKGROUP_CREATE', 'MEMBER_AGENT_CREATE', 'MEMBER_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_MEMBER_UPDATE_ANY_LEVEL = "hasAnyAuthority('MEMBER_PLATFORM_UPDATE', 'MEMBER_ORGANIZATION_UPDATE', 'MEMBER_DEPARTMENT_UPDATE', 'MEMBER_WORKGROUP_UPDATE', 'MEMBER_AGENT_UPDATE', 'MEMBER_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_MEMBER_DELETE_ANY_LEVEL = "hasAnyAuthority('MEMBER_PLATFORM_DELETE', 'MEMBER_ORGANIZATION_DELETE', 'MEMBER_DEPARTMENT_DELETE', 'MEMBER_WORKGROUP_DELETE', 'MEMBER_AGENT_DELETE', 'MEMBER_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_MEMBER_EXPORT_ANY_LEVEL = "hasAnyAuthority('MEMBER_PLATFORM_EXPORT', 'MEMBER_ORGANIZATION_EXPORT', 'MEMBER_DEPARTMENT_EXPORT', 'MEMBER_WORKGROUP_EXPORT', 'MEMBER_AGENT_EXPORT', 'MEMBER_USER_EXPORT')";

}
