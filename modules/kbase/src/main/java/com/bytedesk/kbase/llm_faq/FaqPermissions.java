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
package com.bytedesk.kbase.llm_faq;

import com.bytedesk.core.base.BasePermissions;

public class FaqPermissions extends BasePermissions {

    // 模块前缀
    public static final String FAQ_PREFIX = "FAQ_";

    // 平台级权限
    public static final String FAQ_PLATFORM_READ = "FAQ_PLATFORM_READ";
    public static final String FAQ_PLATFORM_CREATE = "FAQ_PLATFORM_CREATE";
    public static final String FAQ_PLATFORM_UPDATE = "FAQ_PLATFORM_UPDATE";
    public static final String FAQ_PLATFORM_DELETE = "FAQ_PLATFORM_DELETE";
    public static final String FAQ_PLATFORM_EXPORT = "FAQ_PLATFORM_EXPORT";

    // 组织级权限
    public static final String FAQ_ORGANIZATION_READ = "FAQ_ORGANIZATION_READ";
    public static final String FAQ_ORGANIZATION_CREATE = "FAQ_ORGANIZATION_CREATE";
    public static final String FAQ_ORGANIZATION_UPDATE = "FAQ_ORGANIZATION_UPDATE";
    public static final String FAQ_ORGANIZATION_DELETE = "FAQ_ORGANIZATION_DELETE";
    public static final String FAQ_ORGANIZATION_EXPORT = "FAQ_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String FAQ_DEPARTMENT_READ = "FAQ_DEPARTMENT_READ";
    public static final String FAQ_DEPARTMENT_CREATE = "FAQ_DEPARTMENT_CREATE";
    public static final String FAQ_DEPARTMENT_UPDATE = "FAQ_DEPARTMENT_UPDATE";
    public static final String FAQ_DEPARTMENT_DELETE = "FAQ_DEPARTMENT_DELETE";
    public static final String FAQ_DEPARTMENT_EXPORT = "FAQ_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String FAQ_WORKGROUP_READ = "FAQ_WORKGROUP_READ";
    public static final String FAQ_WORKGROUP_CREATE = "FAQ_WORKGROUP_CREATE";
    public static final String FAQ_WORKGROUP_UPDATE = "FAQ_WORKGROUP_UPDATE";
    public static final String FAQ_WORKGROUP_DELETE = "FAQ_WORKGROUP_DELETE";
    public static final String FAQ_WORKGROUP_EXPORT = "FAQ_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String FAQ_AGENT_READ = "FAQ_AGENT_READ";
    public static final String FAQ_AGENT_CREATE = "FAQ_AGENT_CREATE";
    public static final String FAQ_AGENT_UPDATE = "FAQ_AGENT_UPDATE";
    public static final String FAQ_AGENT_DELETE = "FAQ_AGENT_DELETE";
    public static final String FAQ_AGENT_EXPORT = "FAQ_AGENT_EXPORT";
    // 用户级权限
    public static final String FAQ_USER_READ = "FAQ_USER_READ";
    public static final String FAQ_USER_CREATE = "FAQ_USER_CREATE";
    public static final String FAQ_USER_UPDATE = "FAQ_USER_UPDATE";
    public static final String FAQ_USER_DELETE = "FAQ_USER_DELETE";
    public static final String FAQ_USER_EXPORT = "FAQ_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_FAQ_READ_ANY_LEVEL = "hasAnyAuthority('FAQ_PLATFORM_READ', 'FAQ_ORGANIZATION_READ', 'FAQ_DEPARTMENT_READ', 'FAQ_WORKGROUP_READ', 'FAQ_AGENT_READ', 'FAQ_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_FAQ_CREATE_ANY_LEVEL = "hasAnyAuthority('FAQ_PLATFORM_CREATE', 'FAQ_ORGANIZATION_CREATE', 'FAQ_DEPARTMENT_CREATE', 'FAQ_WORKGROUP_CREATE', 'FAQ_AGENT_CREATE', 'FAQ_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_FAQ_UPDATE_ANY_LEVEL = "hasAnyAuthority('FAQ_PLATFORM_UPDATE', 'FAQ_ORGANIZATION_UPDATE', 'FAQ_DEPARTMENT_UPDATE', 'FAQ_WORKGROUP_UPDATE', 'FAQ_AGENT_UPDATE', 'FAQ_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_FAQ_DELETE_ANY_LEVEL = "hasAnyAuthority('FAQ_PLATFORM_DELETE', 'FAQ_ORGANIZATION_DELETE', 'FAQ_DEPARTMENT_DELETE', 'FAQ_WORKGROUP_DELETE', 'FAQ_AGENT_DELETE', 'FAQ_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_FAQ_EXPORT_ANY_LEVEL = "hasAnyAuthority('FAQ_PLATFORM_EXPORT', 'FAQ_ORGANIZATION_EXPORT', 'FAQ_DEPARTMENT_EXPORT', 'FAQ_WORKGROUP_EXPORT', 'FAQ_AGENT_EXPORT', 'FAQ_USER_EXPORT')";

}