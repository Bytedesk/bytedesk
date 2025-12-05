/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
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
package com.bytedesk.ai.robot;

import com.bytedesk.core.base.BasePermissions;

public class RobotPermissions extends BasePermissions {

    // 模块前缀
    public static final String ROBOT_PREFIX = "ROBOT_";

    // 平台级权限
    public static final String ROBOT_PLATFORM_READ = "ROBOT_PLATFORM_READ";
    public static final String ROBOT_PLATFORM_CREATE = "ROBOT_PLATFORM_CREATE";
    public static final String ROBOT_PLATFORM_UPDATE = "ROBOT_PLATFORM_UPDATE";
    public static final String ROBOT_PLATFORM_DELETE = "ROBOT_PLATFORM_DELETE";
    public static final String ROBOT_PLATFORM_EXPORT = "ROBOT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String ROBOT_ORGANIZATION_READ = "ROBOT_ORGANIZATION_READ";
    public static final String ROBOT_ORGANIZATION_CREATE = "ROBOT_ORGANIZATION_CREATE";
    public static final String ROBOT_ORGANIZATION_UPDATE = "ROBOT_ORGANIZATION_UPDATE";
    public static final String ROBOT_ORGANIZATION_DELETE = "ROBOT_ORGANIZATION_DELETE";
    public static final String ROBOT_ORGANIZATION_EXPORT = "ROBOT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String ROBOT_DEPARTMENT_READ = "ROBOT_DEPARTMENT_READ";
    public static final String ROBOT_DEPARTMENT_CREATE = "ROBOT_DEPARTMENT_CREATE";
    public static final String ROBOT_DEPARTMENT_UPDATE = "ROBOT_DEPARTMENT_UPDATE";
    public static final String ROBOT_DEPARTMENT_DELETE = "ROBOT_DEPARTMENT_DELETE";
    public static final String ROBOT_DEPARTMENT_EXPORT = "ROBOT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String ROBOT_WORKGROUP_READ = "ROBOT_WORKGROUP_READ";
    public static final String ROBOT_WORKGROUP_CREATE = "ROBOT_WORKGROUP_CREATE";
    public static final String ROBOT_WORKGROUP_UPDATE = "ROBOT_WORKGROUP_UPDATE";
    public static final String ROBOT_WORKGROUP_DELETE = "ROBOT_WORKGROUP_DELETE";
    public static final String ROBOT_WORKGROUP_EXPORT = "ROBOT_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String ROBOT_AGENT_READ = "ROBOT_AGENT_READ";
    public static final String ROBOT_AGENT_CREATE = "ROBOT_AGENT_CREATE";
    public static final String ROBOT_AGENT_UPDATE = "ROBOT_AGENT_UPDATE";
    public static final String ROBOT_AGENT_DELETE = "ROBOT_AGENT_DELETE";
    public static final String ROBOT_AGENT_EXPORT = "ROBOT_AGENT_EXPORT";
    // 用户级权限
    public static final String ROBOT_USER_READ = "ROBOT_USER_READ";
    public static final String ROBOT_USER_CREATE = "ROBOT_USER_CREATE";
    public static final String ROBOT_USER_UPDATE = "ROBOT_USER_UPDATE";
    public static final String ROBOT_USER_DELETE = "ROBOT_USER_DELETE";
    public static final String ROBOT_USER_EXPORT = "ROBOT_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_ROBOT_READ_ANY_LEVEL = "hasAnyAuthority('ROBOT_PLATFORM_READ', 'ROBOT_ORGANIZATION_READ', 'ROBOT_DEPARTMENT_READ', 'ROBOT_WORKGROUP_READ', 'ROBOT_AGENT_READ', 'ROBOT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_ROBOT_CREATE_ANY_LEVEL = "hasAnyAuthority('ROBOT_PLATFORM_CREATE', 'ROBOT_ORGANIZATION_CREATE', 'ROBOT_DEPARTMENT_CREATE', 'ROBOT_WORKGROUP_CREATE', 'ROBOT_AGENT_CREATE', 'ROBOT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_ROBOT_UPDATE_ANY_LEVEL = "hasAnyAuthority('ROBOT_PLATFORM_UPDATE', 'ROBOT_ORGANIZATION_UPDATE', 'ROBOT_DEPARTMENT_UPDATE', 'ROBOT_WORKGROUP_UPDATE', 'ROBOT_AGENT_UPDATE', 'ROBOT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_ROBOT_DELETE_ANY_LEVEL = "hasAnyAuthority('ROBOT_PLATFORM_DELETE', 'ROBOT_ORGANIZATION_DELETE', 'ROBOT_DEPARTMENT_DELETE', 'ROBOT_WORKGROUP_DELETE', 'ROBOT_AGENT_DELETE', 'ROBOT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_ROBOT_EXPORT_ANY_LEVEL = "hasAnyAuthority('ROBOT_PLATFORM_EXPORT', 'ROBOT_ORGANIZATION_EXPORT', 'ROBOT_DEPARTMENT_EXPORT', 'ROBOT_WORKGROUP_EXPORT', 'ROBOT_AGENT_EXPORT', 'ROBOT_USER_EXPORT')";

}
