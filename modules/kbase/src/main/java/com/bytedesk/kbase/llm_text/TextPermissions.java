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
package com.bytedesk.kbase.llm_text;

import com.bytedesk.core.base.BasePermissions;

public class TextPermissions extends BasePermissions {

    // 模块前缀
    public static final String TEXT_PREFIX = "TEXT_";

    // 平台级权限
    public static final String TEXT_PLATFORM_READ = "TEXT_PLATFORM_READ";
    public static final String TEXT_PLATFORM_CREATE = "TEXT_PLATFORM_CREATE";
    public static final String TEXT_PLATFORM_UPDATE = "TEXT_PLATFORM_UPDATE";
    public static final String TEXT_PLATFORM_DELETE = "TEXT_PLATFORM_DELETE";
    public static final String TEXT_PLATFORM_EXPORT = "TEXT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String TEXT_ORGANIZATION_READ = "TEXT_ORGANIZATION_READ";
    public static final String TEXT_ORGANIZATION_CREATE = "TEXT_ORGANIZATION_CREATE";
    public static final String TEXT_ORGANIZATION_UPDATE = "TEXT_ORGANIZATION_UPDATE";
    public static final String TEXT_ORGANIZATION_DELETE = "TEXT_ORGANIZATION_DELETE";
    public static final String TEXT_ORGANIZATION_EXPORT = "TEXT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String TEXT_DEPARTMENT_READ = "TEXT_DEPARTMENT_READ";
    public static final String TEXT_DEPARTMENT_CREATE = "TEXT_DEPARTMENT_CREATE";
    public static final String TEXT_DEPARTMENT_UPDATE = "TEXT_DEPARTMENT_UPDATE";
    public static final String TEXT_DEPARTMENT_DELETE = "TEXT_DEPARTMENT_DELETE";
    public static final String TEXT_DEPARTMENT_EXPORT = "TEXT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String TEXT_WORKGROUP_READ = "TEXT_WORKGROUP_READ";
    public static final String TEXT_WORKGROUP_CREATE = "TEXT_WORKGROUP_CREATE";
    public static final String TEXT_WORKGROUP_UPDATE = "TEXT_WORKGROUP_UPDATE";
    public static final String TEXT_WORKGROUP_DELETE = "TEXT_WORKGROUP_DELETE";
    public static final String TEXT_WORKGROUP_EXPORT = "TEXT_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String TEXT_AGENT_READ = "TEXT_AGENT_READ";
    public static final String TEXT_AGENT_CREATE = "TEXT_AGENT_CREATE";
    public static final String TEXT_AGENT_UPDATE = "TEXT_AGENT_UPDATE";
    public static final String TEXT_AGENT_DELETE = "TEXT_AGENT_DELETE";
    public static final String TEXT_AGENT_EXPORT = "TEXT_AGENT_EXPORT";
    // 用户级权限
    public static final String TEXT_USER_READ = "TEXT_USER_READ";
    public static final String TEXT_USER_CREATE = "TEXT_USER_CREATE";
    public static final String TEXT_USER_UPDATE = "TEXT_USER_UPDATE";
    public static final String TEXT_USER_DELETE = "TEXT_USER_DELETE";
    public static final String TEXT_USER_EXPORT = "TEXT_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_TEXT_READ_ANY_LEVEL = "hasAnyAuthority('TEXT_PLATFORM_READ', 'TEXT_ORGANIZATION_READ', 'TEXT_DEPARTMENT_READ', 'TEXT_WORKGROUP_READ', 'TEXT_AGENT_READ', 'TEXT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_TEXT_CREATE_ANY_LEVEL = "hasAnyAuthority('TEXT_PLATFORM_CREATE', 'TEXT_ORGANIZATION_CREATE', 'TEXT_DEPARTMENT_CREATE', 'TEXT_WORKGROUP_CREATE', 'TEXT_AGENT_CREATE', 'TEXT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_TEXT_UPDATE_ANY_LEVEL = "hasAnyAuthority('TEXT_PLATFORM_UPDATE', 'TEXT_ORGANIZATION_UPDATE', 'TEXT_DEPARTMENT_UPDATE', 'TEXT_WORKGROUP_UPDATE', 'TEXT_AGENT_UPDATE', 'TEXT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_TEXT_DELETE_ANY_LEVEL = "hasAnyAuthority('TEXT_PLATFORM_DELETE', 'TEXT_ORGANIZATION_DELETE', 'TEXT_DEPARTMENT_DELETE', 'TEXT_WORKGROUP_DELETE', 'TEXT_AGENT_DELETE', 'TEXT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_TEXT_EXPORT_ANY_LEVEL = "hasAnyAuthority('TEXT_PLATFORM_EXPORT', 'TEXT_ORGANIZATION_EXPORT', 'TEXT_DEPARTMENT_EXPORT', 'TEXT_WORKGROUP_EXPORT', 'TEXT_AGENT_EXPORT', 'TEXT_USER_EXPORT')";

}