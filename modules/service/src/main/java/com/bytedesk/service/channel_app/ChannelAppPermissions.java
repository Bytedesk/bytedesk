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
package com.bytedesk.service.channel_app;

import com.bytedesk.core.base.BasePermissions;

public class ChannelAppPermissions extends BasePermissions {

    // 模块前缀
    public static final String CHANNEL_APP_PREFIX = "CHANNEL_APP_";

    // 平台级权限
    public static final String CHANNEL_APP_PLATFORM_READ = "CHANNEL_APP_PLATFORM_READ";
    public static final String CHANNEL_APP_PLATFORM_CREATE = "CHANNEL_APP_PLATFORM_CREATE";
    public static final String CHANNEL_APP_PLATFORM_UPDATE = "CHANNEL_APP_PLATFORM_UPDATE";
    public static final String CHANNEL_APP_PLATFORM_DELETE = "CHANNEL_APP_PLATFORM_DELETE";
    public static final String CHANNEL_APP_PLATFORM_EXPORT = "CHANNEL_APP_PLATFORM_EXPORT";

    // 组织级权限
    public static final String CHANNEL_APP_ORGANIZATION_READ = "CHANNEL_APP_ORGANIZATION_READ";
    public static final String CHANNEL_APP_ORGANIZATION_CREATE = "CHANNEL_APP_ORGANIZATION_CREATE";
    public static final String CHANNEL_APP_ORGANIZATION_UPDATE = "CHANNEL_APP_ORGANIZATION_UPDATE";
    public static final String CHANNEL_APP_ORGANIZATION_DELETE = "CHANNEL_APP_ORGANIZATION_DELETE";
    public static final String CHANNEL_APP_ORGANIZATION_EXPORT = "CHANNEL_APP_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String CHANNEL_APP_DEPARTMENT_READ = "CHANNEL_APP_DEPARTMENT_READ";
    public static final String CHANNEL_APP_DEPARTMENT_CREATE = "CHANNEL_APP_DEPARTMENT_CREATE";
    public static final String CHANNEL_APP_DEPARTMENT_UPDATE = "CHANNEL_APP_DEPARTMENT_UPDATE";
    public static final String CHANNEL_APP_DEPARTMENT_DELETE = "CHANNEL_APP_DEPARTMENT_DELETE";
    public static final String CHANNEL_APP_DEPARTMENT_EXPORT = "CHANNEL_APP_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String CHANNEL_APP_WORKGROUP_READ = "CHANNEL_APP_WORKGROUP_READ";
    public static final String CHANNEL_APP_WORKGROUP_CREATE = "CHANNEL_APP_WORKGROUP_CREATE";
    public static final String CHANNEL_APP_WORKGROUP_UPDATE = "CHANNEL_APP_WORKGROUP_UPDATE";
    public static final String CHANNEL_APP_WORKGROUP_DELETE = "CHANNEL_APP_WORKGROUP_DELETE";
    public static final String CHANNEL_APP_WORKGROUP_EXPORT = "CHANNEL_APP_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String CHANNEL_APP_AGENT_READ = "CHANNEL_APP_AGENT_READ";
    public static final String CHANNEL_APP_AGENT_CREATE = "CHANNEL_APP_AGENT_CREATE";
    public static final String CHANNEL_APP_AGENT_UPDATE = "CHANNEL_APP_AGENT_UPDATE";
    public static final String CHANNEL_APP_AGENT_DELETE = "CHANNEL_APP_AGENT_DELETE";
    public static final String CHANNEL_APP_AGENT_EXPORT = "CHANNEL_APP_AGENT_EXPORT";
    // 用户级权限
    public static final String CHANNEL_APP_USER_READ = "CHANNEL_APP_USER_READ";
    public static final String CHANNEL_APP_USER_CREATE = "CHANNEL_APP_USER_CREATE";
    public static final String CHANNEL_APP_USER_UPDATE = "CHANNEL_APP_USER_UPDATE";
    public static final String CHANNEL_APP_USER_DELETE = "CHANNEL_APP_USER_DELETE";
    public static final String CHANNEL_APP_USER_EXPORT = "CHANNEL_APP_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_CHANNEL_APP_READ_ANY_LEVEL = "hasAnyAuthority('CHANNEL_APP_PLATFORM_READ', 'CHANNEL_APP_ORGANIZATION_READ', 'CHANNEL_APP_DEPARTMENT_READ', 'CHANNEL_APP_WORKGROUP_READ', 'CHANNEL_APP_AGENT_READ', 'CHANNEL_APP_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_CHANNEL_APP_CREATE_ANY_LEVEL = "hasAnyAuthority('CHANNEL_APP_PLATFORM_CREATE', 'CHANNEL_APP_ORGANIZATION_CREATE', 'CHANNEL_APP_DEPARTMENT_CREATE', 'CHANNEL_APP_WORKGROUP_CREATE', 'CHANNEL_APP_AGENT_CREATE', 'CHANNEL_APP_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_CHANNEL_APP_UPDATE_ANY_LEVEL = "hasAnyAuthority('CHANNEL_APP_PLATFORM_UPDATE', 'CHANNEL_APP_ORGANIZATION_UPDATE', 'CHANNEL_APP_DEPARTMENT_UPDATE', 'CHANNEL_APP_WORKGROUP_UPDATE', 'CHANNEL_APP_AGENT_UPDATE', 'CHANNEL_APP_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_CHANNEL_APP_DELETE_ANY_LEVEL = "hasAnyAuthority('CHANNEL_APP_PLATFORM_DELETE', 'CHANNEL_APP_ORGANIZATION_DELETE', 'CHANNEL_APP_DEPARTMENT_DELETE', 'CHANNEL_APP_WORKGROUP_DELETE', 'CHANNEL_APP_AGENT_DELETE', 'CHANNEL_APP_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_CHANNEL_APP_EXPORT_ANY_LEVEL = "hasAnyAuthority('CHANNEL_APP_PLATFORM_EXPORT', 'CHANNEL_APP_ORGANIZATION_EXPORT', 'CHANNEL_APP_DEPARTMENT_EXPORT', 'CHANNEL_APP_WORKGROUP_EXPORT', 'CHANNEL_APP_AGENT_EXPORT', 'CHANNEL_APP_USER_EXPORT')";

}
