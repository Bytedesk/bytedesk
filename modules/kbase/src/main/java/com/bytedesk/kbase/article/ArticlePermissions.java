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
package com.bytedesk.kbase.article;

import com.bytedesk.core.base.BasePermissions;

public class ArticlePermissions extends BasePermissions {

    // 模块前缀
    public static final String ARTICLE_PREFIX = "ARTICLE_";

    // 平台级权限
    public static final String ARTICLE_PLATFORM_READ = "ARTICLE_PLATFORM_READ";
    public static final String ARTICLE_PLATFORM_CREATE = "ARTICLE_PLATFORM_CREATE";
    public static final String ARTICLE_PLATFORM_UPDATE = "ARTICLE_PLATFORM_UPDATE";
    public static final String ARTICLE_PLATFORM_DELETE = "ARTICLE_PLATFORM_DELETE";
    public static final String ARTICLE_PLATFORM_EXPORT = "ARTICLE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String ARTICLE_ORGANIZATION_READ = "ARTICLE_ORGANIZATION_READ";
    public static final String ARTICLE_ORGANIZATION_CREATE = "ARTICLE_ORGANIZATION_CREATE";
    public static final String ARTICLE_ORGANIZATION_UPDATE = "ARTICLE_ORGANIZATION_UPDATE";
    public static final String ARTICLE_ORGANIZATION_DELETE = "ARTICLE_ORGANIZATION_DELETE";
    public static final String ARTICLE_ORGANIZATION_EXPORT = "ARTICLE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String ARTICLE_DEPARTMENT_READ = "ARTICLE_DEPARTMENT_READ";
    public static final String ARTICLE_DEPARTMENT_CREATE = "ARTICLE_DEPARTMENT_CREATE";
    public static final String ARTICLE_DEPARTMENT_UPDATE = "ARTICLE_DEPARTMENT_UPDATE";
    public static final String ARTICLE_DEPARTMENT_DELETE = "ARTICLE_DEPARTMENT_DELETE";
    public static final String ARTICLE_DEPARTMENT_EXPORT = "ARTICLE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String ARTICLE_WORKGROUP_READ = "ARTICLE_WORKGROUP_READ";
    public static final String ARTICLE_WORKGROUP_CREATE = "ARTICLE_WORKGROUP_CREATE";
    public static final String ARTICLE_WORKGROUP_UPDATE = "ARTICLE_WORKGROUP_UPDATE";
    public static final String ARTICLE_WORKGROUP_DELETE = "ARTICLE_WORKGROUP_DELETE";
    public static final String ARTICLE_WORKGROUP_EXPORT = "ARTICLE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String ARTICLE_AGENT_READ = "ARTICLE_AGENT_READ";
    public static final String ARTICLE_AGENT_CREATE = "ARTICLE_AGENT_CREATE";
    public static final String ARTICLE_AGENT_UPDATE = "ARTICLE_AGENT_UPDATE";
    public static final String ARTICLE_AGENT_DELETE = "ARTICLE_AGENT_DELETE";
    public static final String ARTICLE_AGENT_EXPORT = "ARTICLE_AGENT_EXPORT";
    // 用户级权限
    public static final String ARTICLE_USER_READ = "ARTICLE_USER_READ";
    public static final String ARTICLE_USER_CREATE = "ARTICLE_USER_CREATE";
    public static final String ARTICLE_USER_UPDATE = "ARTICLE_USER_UPDATE";
    public static final String ARTICLE_USER_DELETE = "ARTICLE_USER_DELETE";
    public static final String ARTICLE_USER_EXPORT = "ARTICLE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_ARTICLE_READ_ANY_LEVEL = "hasAnyAuthority('ARTICLE_PLATFORM_READ', 'ARTICLE_ORGANIZATION_READ', 'ARTICLE_DEPARTMENT_READ', 'ARTICLE_WORKGROUP_READ', 'ARTICLE_AGENT_READ', 'ARTICLE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_ARTICLE_CREATE_ANY_LEVEL = "hasAnyAuthority('ARTICLE_PLATFORM_CREATE', 'ARTICLE_ORGANIZATION_CREATE', 'ARTICLE_DEPARTMENT_CREATE', 'ARTICLE_WORKGROUP_CREATE', 'ARTICLE_AGENT_CREATE', 'ARTICLE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_ARTICLE_UPDATE_ANY_LEVEL = "hasAnyAuthority('ARTICLE_PLATFORM_UPDATE', 'ARTICLE_ORGANIZATION_UPDATE', 'ARTICLE_DEPARTMENT_UPDATE', 'ARTICLE_WORKGROUP_UPDATE', 'ARTICLE_AGENT_UPDATE', 'ARTICLE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_ARTICLE_DELETE_ANY_LEVEL = "hasAnyAuthority('ARTICLE_PLATFORM_DELETE', 'ARTICLE_ORGANIZATION_DELETE', 'ARTICLE_DEPARTMENT_DELETE', 'ARTICLE_WORKGROUP_DELETE', 'ARTICLE_AGENT_DELETE', 'ARTICLE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_ARTICLE_EXPORT_ANY_LEVEL = "hasAnyAuthority('ARTICLE_PLATFORM_EXPORT', 'ARTICLE_ORGANIZATION_EXPORT', 'ARTICLE_DEPARTMENT_EXPORT', 'ARTICLE_WORKGROUP_EXPORT', 'ARTICLE_AGENT_EXPORT', 'ARTICLE_USER_EXPORT')";

}