/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:42:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import com.bytedesk.core.base.BasePermissions;

public class AutoReplyFixedPermissions extends BasePermissions {

    // 模块前缀
    public static final String AUTO_REPLY_FIXED_PREFIX = "AUTO_REPLY_FIXED_";

    // 平台级权限
    public static final String AUTO_REPLY_FIXED_PLATFORM_READ = "AUTO_REPLY_FIXED_PLATFORM_READ";
    public static final String AUTO_REPLY_FIXED_PLATFORM_CREATE = "AUTO_REPLY_FIXED_PLATFORM_CREATE";
    public static final String AUTO_REPLY_FIXED_PLATFORM_UPDATE = "AUTO_REPLY_FIXED_PLATFORM_UPDATE";
    public static final String AUTO_REPLY_FIXED_PLATFORM_DELETE = "AUTO_REPLY_FIXED_PLATFORM_DELETE";
    public static final String AUTO_REPLY_FIXED_PLATFORM_EXPORT = "AUTO_REPLY_FIXED_PLATFORM_EXPORT";

    // 组织级权限
    public static final String AUTO_REPLY_FIXED_ORGANIZATION_READ = "AUTO_REPLY_FIXED_ORGANIZATION_READ";
    public static final String AUTO_REPLY_FIXED_ORGANIZATION_CREATE = "AUTO_REPLY_FIXED_ORGANIZATION_CREATE";
    public static final String AUTO_REPLY_FIXED_ORGANIZATION_UPDATE = "AUTO_REPLY_FIXED_ORGANIZATION_UPDATE";
    public static final String AUTO_REPLY_FIXED_ORGANIZATION_DELETE = "AUTO_REPLY_FIXED_ORGANIZATION_DELETE";
    public static final String AUTO_REPLY_FIXED_ORGANIZATION_EXPORT = "AUTO_REPLY_FIXED_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String AUTO_REPLY_FIXED_DEPARTMENT_READ = "AUTO_REPLY_FIXED_DEPARTMENT_READ";
    public static final String AUTO_REPLY_FIXED_DEPARTMENT_CREATE = "AUTO_REPLY_FIXED_DEPARTMENT_CREATE";
    public static final String AUTO_REPLY_FIXED_DEPARTMENT_UPDATE = "AUTO_REPLY_FIXED_DEPARTMENT_UPDATE";
    public static final String AUTO_REPLY_FIXED_DEPARTMENT_DELETE = "AUTO_REPLY_FIXED_DEPARTMENT_DELETE";
    public static final String AUTO_REPLY_FIXED_DEPARTMENT_EXPORT = "AUTO_REPLY_FIXED_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String AUTO_REPLY_FIXED_WORKGROUP_READ = "AUTO_REPLY_FIXED_WORKGROUP_READ";
    public static final String AUTO_REPLY_FIXED_WORKGROUP_CREATE = "AUTO_REPLY_FIXED_WORKGROUP_CREATE";
    public static final String AUTO_REPLY_FIXED_WORKGROUP_UPDATE = "AUTO_REPLY_FIXED_WORKGROUP_UPDATE";
    public static final String AUTO_REPLY_FIXED_WORKGROUP_DELETE = "AUTO_REPLY_FIXED_WORKGROUP_DELETE";
    public static final String AUTO_REPLY_FIXED_WORKGROUP_EXPORT = "AUTO_REPLY_FIXED_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String AUTO_REPLY_FIXED_AGENT_READ = "AUTO_REPLY_FIXED_AGENT_READ";
    public static final String AUTO_REPLY_FIXED_AGENT_CREATE = "AUTO_REPLY_FIXED_AGENT_CREATE";
    public static final String AUTO_REPLY_FIXED_AGENT_UPDATE = "AUTO_REPLY_FIXED_AGENT_UPDATE";
    public static final String AUTO_REPLY_FIXED_AGENT_DELETE = "AUTO_REPLY_FIXED_AGENT_DELETE";
    public static final String AUTO_REPLY_FIXED_AGENT_EXPORT = "AUTO_REPLY_FIXED_AGENT_EXPORT";
    // 用户级权限
    public static final String AUTO_REPLY_FIXED_USER_READ = "AUTO_REPLY_FIXED_USER_READ";
    public static final String AUTO_REPLY_FIXED_USER_CREATE = "AUTO_REPLY_FIXED_USER_CREATE";
    public static final String AUTO_REPLY_FIXED_USER_UPDATE = "AUTO_REPLY_FIXED_USER_UPDATE";
    public static final String AUTO_REPLY_FIXED_USER_DELETE = "AUTO_REPLY_FIXED_USER_DELETE";
    public static final String AUTO_REPLY_FIXED_USER_EXPORT = "AUTO_REPLY_FIXED_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_AUTO_REPLY_FIXED_READ_ANY_LEVEL = "hasAnyAuthority('AUTO_REPLY_FIXED_PLATFORM_READ', 'AUTO_REPLY_FIXED_ORGANIZATION_READ', 'AUTO_REPLY_FIXED_DEPARTMENT_READ', 'AUTO_REPLY_FIXED_WORKGROUP_READ', 'AUTO_REPLY_FIXED_AGENT_READ', 'AUTO_REPLY_FIXED_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_AUTO_REPLY_FIXED_CREATE_ANY_LEVEL = "hasAnyAuthority('AUTO_REPLY_FIXED_PLATFORM_CREATE', 'AUTO_REPLY_FIXED_ORGANIZATION_CREATE', 'AUTO_REPLY_FIXED_DEPARTMENT_CREATE', 'AUTO_REPLY_FIXED_WORKGROUP_CREATE', 'AUTO_REPLY_FIXED_AGENT_CREATE', 'AUTO_REPLY_FIXED_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_AUTO_REPLY_FIXED_UPDATE_ANY_LEVEL = "hasAnyAuthority('AUTO_REPLY_FIXED_PLATFORM_UPDATE', 'AUTO_REPLY_FIXED_ORGANIZATION_UPDATE', 'AUTO_REPLY_FIXED_DEPARTMENT_UPDATE', 'AUTO_REPLY_FIXED_WORKGROUP_UPDATE', 'AUTO_REPLY_FIXED_AGENT_UPDATE', 'AUTO_REPLY_FIXED_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_AUTO_REPLY_FIXED_DELETE_ANY_LEVEL = "hasAnyAuthority('AUTO_REPLY_FIXED_PLATFORM_DELETE', 'AUTO_REPLY_FIXED_ORGANIZATION_DELETE', 'AUTO_REPLY_FIXED_DEPARTMENT_DELETE', 'AUTO_REPLY_FIXED_WORKGROUP_DELETE', 'AUTO_REPLY_FIXED_AGENT_DELETE', 'AUTO_REPLY_FIXED_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_AUTO_REPLY_FIXED_EXPORT_ANY_LEVEL = "hasAnyAuthority('AUTO_REPLY_FIXED_PLATFORM_EXPORT', 'AUTO_REPLY_FIXED_ORGANIZATION_EXPORT', 'AUTO_REPLY_FIXED_DEPARTMENT_EXPORT', 'AUTO_REPLY_FIXED_WORKGROUP_EXPORT', 'AUTO_REPLY_FIXED_AGENT_EXPORT', 'AUTO_REPLY_FIXED_USER_EXPORT')";

}