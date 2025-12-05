/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 13:40:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website;

import com.bytedesk.core.base.BasePermissions;

public class WebsitePermissions extends BasePermissions {

    // 模块前缀
    public static final String WEBSITE_PREFIX = "WEBSITE_";

    // 平台级权限
    public static final String WEBSITE_PLATFORM_READ = "WEBSITE_PLATFORM_READ";
    public static final String WEBSITE_PLATFORM_CREATE = "WEBSITE_PLATFORM_CREATE";
    public static final String WEBSITE_PLATFORM_UPDATE = "WEBSITE_PLATFORM_UPDATE";
    public static final String WEBSITE_PLATFORM_DELETE = "WEBSITE_PLATFORM_DELETE";
    public static final String WEBSITE_PLATFORM_EXPORT = "WEBSITE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String WEBSITE_ORGANIZATION_READ = "WEBSITE_ORGANIZATION_READ";
    public static final String WEBSITE_ORGANIZATION_CREATE = "WEBSITE_ORGANIZATION_CREATE";
    public static final String WEBSITE_ORGANIZATION_UPDATE = "WEBSITE_ORGANIZATION_UPDATE";
    public static final String WEBSITE_ORGANIZATION_DELETE = "WEBSITE_ORGANIZATION_DELETE";
    public static final String WEBSITE_ORGANIZATION_EXPORT = "WEBSITE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String WEBSITE_DEPARTMENT_READ = "WEBSITE_DEPARTMENT_READ";
    public static final String WEBSITE_DEPARTMENT_CREATE = "WEBSITE_DEPARTMENT_CREATE";
    public static final String WEBSITE_DEPARTMENT_UPDATE = "WEBSITE_DEPARTMENT_UPDATE";
    public static final String WEBSITE_DEPARTMENT_DELETE = "WEBSITE_DEPARTMENT_DELETE";
    public static final String WEBSITE_DEPARTMENT_EXPORT = "WEBSITE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String WEBSITE_WORKGROUP_READ = "WEBSITE_WORKGROUP_READ";
    public static final String WEBSITE_WORKGROUP_CREATE = "WEBSITE_WORKGROUP_CREATE";
    public static final String WEBSITE_WORKGROUP_UPDATE = "WEBSITE_WORKGROUP_UPDATE";
    public static final String WEBSITE_WORKGROUP_DELETE = "WEBSITE_WORKGROUP_DELETE";
    public static final String WEBSITE_WORKGROUP_EXPORT = "WEBSITE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String WEBSITE_AGENT_READ = "WEBSITE_AGENT_READ";
    public static final String WEBSITE_AGENT_CREATE = "WEBSITE_AGENT_CREATE";
    public static final String WEBSITE_AGENT_UPDATE = "WEBSITE_AGENT_UPDATE";
    public static final String WEBSITE_AGENT_DELETE = "WEBSITE_AGENT_DELETE";
    public static final String WEBSITE_AGENT_EXPORT = "WEBSITE_AGENT_EXPORT";
    // 用户级权限
    public static final String WEBSITE_USER_READ = "WEBSITE_USER_READ";
    public static final String WEBSITE_USER_CREATE = "WEBSITE_USER_CREATE";
    public static final String WEBSITE_USER_UPDATE = "WEBSITE_USER_UPDATE";
    public static final String WEBSITE_USER_DELETE = "WEBSITE_USER_DELETE";
    public static final String WEBSITE_USER_EXPORT = "WEBSITE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_WEBSITE_READ_ANY_LEVEL = "hasAnyAuthority('WEBSITE_PLATFORM_READ', 'WEBSITE_ORGANIZATION_READ', 'WEBSITE_DEPARTMENT_READ', 'WEBSITE_WORKGROUP_READ', 'WEBSITE_AGENT_READ', 'WEBSITE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_WEBSITE_CREATE_ANY_LEVEL = "hasAnyAuthority('WEBSITE_PLATFORM_CREATE', 'WEBSITE_ORGANIZATION_CREATE', 'WEBSITE_DEPARTMENT_CREATE', 'WEBSITE_WORKGROUP_CREATE', 'WEBSITE_AGENT_CREATE', 'WEBSITE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_WEBSITE_UPDATE_ANY_LEVEL = "hasAnyAuthority('WEBSITE_PLATFORM_UPDATE', 'WEBSITE_ORGANIZATION_UPDATE', 'WEBSITE_DEPARTMENT_UPDATE', 'WEBSITE_WORKGROUP_UPDATE', 'WEBSITE_AGENT_UPDATE', 'WEBSITE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_WEBSITE_DELETE_ANY_LEVEL = "hasAnyAuthority('WEBSITE_PLATFORM_DELETE', 'WEBSITE_ORGANIZATION_DELETE', 'WEBSITE_DEPARTMENT_DELETE', 'WEBSITE_WORKGROUP_DELETE', 'WEBSITE_AGENT_DELETE', 'WEBSITE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_WEBSITE_EXPORT_ANY_LEVEL = "hasAnyAuthority('WEBSITE_PLATFORM_EXPORT', 'WEBSITE_ORGANIZATION_EXPORT', 'WEBSITE_DEPARTMENT_EXPORT', 'WEBSITE_WORKGROUP_EXPORT', 'WEBSITE_AGENT_EXPORT', 'WEBSITE_USER_EXPORT')";

}