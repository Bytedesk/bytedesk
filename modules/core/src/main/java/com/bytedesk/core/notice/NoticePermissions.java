/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:48
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
package com.bytedesk.core.notice;

import com.bytedesk.core.base.BasePermissions;

public class NoticePermissions extends BasePermissions {

    // 模块前缀
    public static final String NOTICE_PREFIX = "NOTICE_";

    // 平台级权限
    public static final String NOTICE_PLATFORM_READ = "NOTICE_PLATFORM_READ";
    public static final String NOTICE_PLATFORM_CREATE = "NOTICE_PLATFORM_CREATE";
    public static final String NOTICE_PLATFORM_UPDATE = "NOTICE_PLATFORM_UPDATE";
    public static final String NOTICE_PLATFORM_DELETE = "NOTICE_PLATFORM_DELETE";
    public static final String NOTICE_PLATFORM_EXPORT = "NOTICE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String NOTICE_ORGANIZATION_READ = "NOTICE_ORGANIZATION_READ";
    public static final String NOTICE_ORGANIZATION_CREATE = "NOTICE_ORGANIZATION_CREATE";
    public static final String NOTICE_ORGANIZATION_UPDATE = "NOTICE_ORGANIZATION_UPDATE";
    public static final String NOTICE_ORGANIZATION_DELETE = "NOTICE_ORGANIZATION_DELETE";
    public static final String NOTICE_ORGANIZATION_EXPORT = "NOTICE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String NOTICE_DEPARTMENT_READ = "NOTICE_DEPARTMENT_READ";
    public static final String NOTICE_DEPARTMENT_CREATE = "NOTICE_DEPARTMENT_CREATE";
    public static final String NOTICE_DEPARTMENT_UPDATE = "NOTICE_DEPARTMENT_UPDATE";
    public static final String NOTICE_DEPARTMENT_DELETE = "NOTICE_DEPARTMENT_DELETE";
    public static final String NOTICE_DEPARTMENT_EXPORT = "NOTICE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String NOTICE_WORKGROUP_READ = "NOTICE_WORKGROUP_READ";
    public static final String NOTICE_WORKGROUP_CREATE = "NOTICE_WORKGROUP_CREATE";
    public static final String NOTICE_WORKGROUP_UPDATE = "NOTICE_WORKGROUP_UPDATE";
    public static final String NOTICE_WORKGROUP_DELETE = "NOTICE_WORKGROUP_DELETE";
    public static final String NOTICE_WORKGROUP_EXPORT = "NOTICE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String NOTICE_AGENT_READ = "NOTICE_AGENT_READ";
    public static final String NOTICE_AGENT_CREATE = "NOTICE_AGENT_CREATE";
    public static final String NOTICE_AGENT_UPDATE = "NOTICE_AGENT_UPDATE";
    public static final String NOTICE_AGENT_DELETE = "NOTICE_AGENT_DELETE";
    public static final String NOTICE_AGENT_EXPORT = "NOTICE_AGENT_EXPORT";
    // 用户级权限
    public static final String NOTICE_USER_READ = "NOTICE_USER_READ";
    public static final String NOTICE_USER_CREATE = "NOTICE_USER_CREATE";
    public static final String NOTICE_USER_UPDATE = "NOTICE_USER_UPDATE";
    public static final String NOTICE_USER_DELETE = "NOTICE_USER_DELETE";
    public static final String NOTICE_USER_EXPORT = "NOTICE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_NOTICE_READ_ANY_LEVEL = "hasAnyAuthority('NOTICE_PLATFORM_READ', 'NOTICE_ORGANIZATION_READ', 'NOTICE_DEPARTMENT_READ', 'NOTICE_WORKGROUP_READ', 'NOTICE_AGENT_READ', 'NOTICE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_NOTICE_CREATE_ANY_LEVEL = "hasAnyAuthority('NOTICE_PLATFORM_CREATE', 'NOTICE_ORGANIZATION_CREATE', 'NOTICE_DEPARTMENT_CREATE', 'NOTICE_WORKGROUP_CREATE', 'NOTICE_AGENT_CREATE', 'NOTICE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_NOTICE_UPDATE_ANY_LEVEL = "hasAnyAuthority('NOTICE_PLATFORM_UPDATE', 'NOTICE_ORGANIZATION_UPDATE', 'NOTICE_DEPARTMENT_UPDATE', 'NOTICE_WORKGROUP_UPDATE', 'NOTICE_AGENT_UPDATE', 'NOTICE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_NOTICE_DELETE_ANY_LEVEL = "hasAnyAuthority('NOTICE_PLATFORM_DELETE', 'NOTICE_ORGANIZATION_DELETE', 'NOTICE_DEPARTMENT_DELETE', 'NOTICE_WORKGROUP_DELETE', 'NOTICE_AGENT_DELETE', 'NOTICE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_NOTICE_EXPORT_ANY_LEVEL = "hasAnyAuthority('NOTICE_PLATFORM_EXPORT', 'NOTICE_ORGANIZATION_EXPORT', 'NOTICE_DEPARTMENT_EXPORT', 'NOTICE_WORKGROUP_EXPORT', 'NOTICE_AGENT_EXPORT', 'NOTICE_USER_EXPORT')";

}
