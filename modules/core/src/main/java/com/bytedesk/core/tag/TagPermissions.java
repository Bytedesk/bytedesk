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
package com.bytedesk.core.tag;

import com.bytedesk.core.base.BasePermissions;

public class TagPermissions extends BasePermissions {

    // 模块前缀
    public static final String TAG_PREFIX = "TAG_";

    // 平台级权限
    public static final String TAG_PLATFORM_READ = "TAG_PLATFORM_READ";
    public static final String TAG_PLATFORM_CREATE = "TAG_PLATFORM_CREATE";
    public static final String TAG_PLATFORM_UPDATE = "TAG_PLATFORM_UPDATE";
    public static final String TAG_PLATFORM_DELETE = "TAG_PLATFORM_DELETE";
    public static final String TAG_PLATFORM_EXPORT = "TAG_PLATFORM_EXPORT";

    // 组织级权限
    public static final String TAG_ORGANIZATION_READ = "TAG_ORGANIZATION_READ";
    public static final String TAG_ORGANIZATION_CREATE = "TAG_ORGANIZATION_CREATE";
    public static final String TAG_ORGANIZATION_UPDATE = "TAG_ORGANIZATION_UPDATE";
    public static final String TAG_ORGANIZATION_DELETE = "TAG_ORGANIZATION_DELETE";
    public static final String TAG_ORGANIZATION_EXPORT = "TAG_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String TAG_DEPARTMENT_READ = "TAG_DEPARTMENT_READ";
    public static final String TAG_DEPARTMENT_CREATE = "TAG_DEPARTMENT_CREATE";
    public static final String TAG_DEPARTMENT_UPDATE = "TAG_DEPARTMENT_UPDATE";
    public static final String TAG_DEPARTMENT_DELETE = "TAG_DEPARTMENT_DELETE";
    public static final String TAG_DEPARTMENT_EXPORT = "TAG_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String TAG_WORKGROUP_READ = "TAG_WORKGROUP_READ";
    public static final String TAG_WORKGROUP_CREATE = "TAG_WORKGROUP_CREATE";
    public static final String TAG_WORKGROUP_UPDATE = "TAG_WORKGROUP_UPDATE";
    public static final String TAG_WORKGROUP_DELETE = "TAG_WORKGROUP_DELETE";
    public static final String TAG_WORKGROUP_EXPORT = "TAG_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String TAG_AGENT_READ = "TAG_AGENT_READ";
    public static final String TAG_AGENT_CREATE = "TAG_AGENT_CREATE";
    public static final String TAG_AGENT_UPDATE = "TAG_AGENT_UPDATE";
    public static final String TAG_AGENT_DELETE = "TAG_AGENT_DELETE";
    public static final String TAG_AGENT_EXPORT = "TAG_AGENT_EXPORT";
    // 用户级权限
    public static final String TAG_USER_READ = "TAG_USER_READ";
    public static final String TAG_USER_CREATE = "TAG_USER_CREATE";
    public static final String TAG_USER_UPDATE = "TAG_USER_UPDATE";
    public static final String TAG_USER_DELETE = "TAG_USER_DELETE";
    public static final String TAG_USER_EXPORT = "TAG_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_TAG_READ_ANY_LEVEL = "hasAnyAuthority('TAG_PLATFORM_READ', 'TAG_ORGANIZATION_READ', 'TAG_DEPARTMENT_READ', 'TAG_WORKGROUP_READ', 'TAG_AGENT_READ', 'TAG_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_TAG_CREATE_ANY_LEVEL = "hasAnyAuthority('TAG_PLATFORM_CREATE', 'TAG_ORGANIZATION_CREATE', 'TAG_DEPARTMENT_CREATE', 'TAG_WORKGROUP_CREATE', 'TAG_AGENT_CREATE', 'TAG_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_TAG_UPDATE_ANY_LEVEL = "hasAnyAuthority('TAG_PLATFORM_UPDATE', 'TAG_ORGANIZATION_UPDATE', 'TAG_DEPARTMENT_UPDATE', 'TAG_WORKGROUP_UPDATE', 'TAG_AGENT_UPDATE', 'TAG_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_TAG_DELETE_ANY_LEVEL = "hasAnyAuthority('TAG_PLATFORM_DELETE', 'TAG_ORGANIZATION_DELETE', 'TAG_DEPARTMENT_DELETE', 'TAG_WORKGROUP_DELETE', 'TAG_AGENT_DELETE', 'TAG_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_TAG_EXPORT_ANY_LEVEL = "hasAnyAuthority('TAG_PLATFORM_EXPORT', 'TAG_ORGANIZATION_EXPORT', 'TAG_DEPARTMENT_EXPORT', 'TAG_WORKGROUP_EXPORT', 'TAG_AGENT_EXPORT', 'TAG_USER_EXPORT')";

}
