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
package com.bytedesk.kbase.llm_webpage;

import com.bytedesk.core.base.BasePermissions;

public class WebpagePermissions extends BasePermissions {

    // 模块前缀
    public static final String WEBPAGE_PREFIX = "WEBPAGE_";

    // 平台级权限
    public static final String WEBPAGE_PLATFORM_READ = "WEBPAGE_PLATFORM_READ";
    public static final String WEBPAGE_PLATFORM_CREATE = "WEBPAGE_PLATFORM_CREATE";
    public static final String WEBPAGE_PLATFORM_UPDATE = "WEBPAGE_PLATFORM_UPDATE";
    public static final String WEBPAGE_PLATFORM_DELETE = "WEBPAGE_PLATFORM_DELETE";
    public static final String WEBPAGE_PLATFORM_EXPORT = "WEBPAGE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String WEBPAGE_ORGANIZATION_READ = "WEBPAGE_ORGANIZATION_READ";
    public static final String WEBPAGE_ORGANIZATION_CREATE = "WEBPAGE_ORGANIZATION_CREATE";
    public static final String WEBPAGE_ORGANIZATION_UPDATE = "WEBPAGE_ORGANIZATION_UPDATE";
    public static final String WEBPAGE_ORGANIZATION_DELETE = "WEBPAGE_ORGANIZATION_DELETE";
    public static final String WEBPAGE_ORGANIZATION_EXPORT = "WEBPAGE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String WEBPAGE_DEPARTMENT_READ = "WEBPAGE_DEPARTMENT_READ";
    public static final String WEBPAGE_DEPARTMENT_CREATE = "WEBPAGE_DEPARTMENT_CREATE";
    public static final String WEBPAGE_DEPARTMENT_UPDATE = "WEBPAGE_DEPARTMENT_UPDATE";
    public static final String WEBPAGE_DEPARTMENT_DELETE = "WEBPAGE_DEPARTMENT_DELETE";
    public static final String WEBPAGE_DEPARTMENT_EXPORT = "WEBPAGE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String WEBPAGE_WORKGROUP_READ = "WEBPAGE_WORKGROUP_READ";
    public static final String WEBPAGE_WORKGROUP_CREATE = "WEBPAGE_WORKGROUP_CREATE";
    public static final String WEBPAGE_WORKGROUP_UPDATE = "WEBPAGE_WORKGROUP_UPDATE";
    public static final String WEBPAGE_WORKGROUP_DELETE = "WEBPAGE_WORKGROUP_DELETE";
    public static final String WEBPAGE_WORKGROUP_EXPORT = "WEBPAGE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String WEBPAGE_AGENT_READ = "WEBPAGE_AGENT_READ";
    public static final String WEBPAGE_AGENT_CREATE = "WEBPAGE_AGENT_CREATE";
    public static final String WEBPAGE_AGENT_UPDATE = "WEBPAGE_AGENT_UPDATE";
    public static final String WEBPAGE_AGENT_DELETE = "WEBPAGE_AGENT_DELETE";
    public static final String WEBPAGE_AGENT_EXPORT = "WEBPAGE_AGENT_EXPORT";
    // 用户级权限
    public static final String WEBPAGE_USER_READ = "WEBPAGE_USER_READ";
    public static final String WEBPAGE_USER_CREATE = "WEBPAGE_USER_CREATE";
    public static final String WEBPAGE_USER_UPDATE = "WEBPAGE_USER_UPDATE";
    public static final String WEBPAGE_USER_DELETE = "WEBPAGE_USER_DELETE";
    public static final String WEBPAGE_USER_EXPORT = "WEBPAGE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_WEBPAGE_READ_ANY_LEVEL = "hasAnyAuthority('WEBPAGE_PLATFORM_READ', 'WEBPAGE_ORGANIZATION_READ', 'WEBPAGE_DEPARTMENT_READ', 'WEBPAGE_WORKGROUP_READ', 'WEBPAGE_AGENT_READ', 'WEBPAGE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_WEBPAGE_CREATE_ANY_LEVEL = "hasAnyAuthority('WEBPAGE_PLATFORM_CREATE', 'WEBPAGE_ORGANIZATION_CREATE', 'WEBPAGE_DEPARTMENT_CREATE', 'WEBPAGE_WORKGROUP_CREATE', 'WEBPAGE_AGENT_CREATE', 'WEBPAGE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_WEBPAGE_UPDATE_ANY_LEVEL = "hasAnyAuthority('WEBPAGE_PLATFORM_UPDATE', 'WEBPAGE_ORGANIZATION_UPDATE', 'WEBPAGE_DEPARTMENT_UPDATE', 'WEBPAGE_WORKGROUP_UPDATE', 'WEBPAGE_AGENT_UPDATE', 'WEBPAGE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_WEBPAGE_DELETE_ANY_LEVEL = "hasAnyAuthority('WEBPAGE_PLATFORM_DELETE', 'WEBPAGE_ORGANIZATION_DELETE', 'WEBPAGE_DEPARTMENT_DELETE', 'WEBPAGE_WORKGROUP_DELETE', 'WEBPAGE_AGENT_DELETE', 'WEBPAGE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_WEBPAGE_EXPORT_ANY_LEVEL = "hasAnyAuthority('WEBPAGE_PLATFORM_EXPORT', 'WEBPAGE_ORGANIZATION_EXPORT', 'WEBPAGE_DEPARTMENT_EXPORT', 'WEBPAGE_WORKGROUP_EXPORT', 'WEBPAGE_AGENT_EXPORT', 'WEBPAGE_USER_EXPORT')";

}