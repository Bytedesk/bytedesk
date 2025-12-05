/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 16:01:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_thread;

import com.bytedesk.core.base.BasePermissions;

/**
 * Robot Thread权限控制 - 五级权限体系
 */
public class RobotThreadPermissions extends BasePermissions {

    public static final String ROBOTTHREAD_PREFIX = "ROBOTTHREAD_";

    // 平台级别权限
    public static final String ROBOTTHREAD_PLATFORM_CREATE = "ROBOTTHREAD_PLATFORM_CREATE";
    public static final String ROBOTTHREAD_PLATFORM_READ = "ROBOTTHREAD_PLATFORM_READ";
    public static final String ROBOTTHREAD_PLATFORM_UPDATE = "ROBOTTHREAD_PLATFORM_UPDATE";
    public static final String ROBOTTHREAD_PLATFORM_DELETE = "ROBOTTHREAD_PLATFORM_DELETE";
    public static final String ROBOTTHREAD_PLATFORM_EXPORT = "ROBOTTHREAD_PLATFORM_EXPORT";

    // 组织级别权限
    public static final String ROBOTTHREAD_ORGANIZATION_CREATE = "ROBOTTHREAD_ORGANIZATION_CREATE";
    public static final String ROBOTTHREAD_ORGANIZATION_READ = "ROBOTTHREAD_ORGANIZATION_READ";
    public static final String ROBOTTHREAD_ORGANIZATION_UPDATE = "ROBOTTHREAD_ORGANIZATION_UPDATE";
    public static final String ROBOTTHREAD_ORGANIZATION_DELETE = "ROBOTTHREAD_ORGANIZATION_DELETE";
    public static final String ROBOTTHREAD_ORGANIZATION_EXPORT = "ROBOTTHREAD_ORGANIZATION_EXPORT";

    // 部门级别权限
    public static final String ROBOTTHREAD_DEPARTMENT_CREATE = "ROBOTTHREAD_DEPARTMENT_CREATE";
    public static final String ROBOTTHREAD_DEPARTMENT_READ = "ROBOTTHREAD_DEPARTMENT_READ";
    public static final String ROBOTTHREAD_DEPARTMENT_UPDATE = "ROBOTTHREAD_DEPARTMENT_UPDATE";
    public static final String ROBOTTHREAD_DEPARTMENT_DELETE = "ROBOTTHREAD_DEPARTMENT_DELETE";
    public static final String ROBOTTHREAD_DEPARTMENT_EXPORT = "ROBOTTHREAD_DEPARTMENT_EXPORT";

    // 工作组级别权限
    public static final String ROBOTTHREAD_WORKGROUP_CREATE = "ROBOTTHREAD_WORKGROUP_CREATE";
    public static final String ROBOTTHREAD_WORKGROUP_READ = "ROBOTTHREAD_WORKGROUP_READ";
    public static final String ROBOTTHREAD_WORKGROUP_UPDATE = "ROBOTTHREAD_WORKGROUP_UPDATE";
    public static final String ROBOTTHREAD_WORKGROUP_DELETE = "ROBOTTHREAD_WORKGROUP_DELETE";
    public static final String ROBOTTHREAD_WORKGROUP_EXPORT = "ROBOTTHREAD_WORKGROUP_EXPORT";

    // 客服级别权限
    public static final String ROBOTTHREAD_AGENT_CREATE = "ROBOTTHREAD_AGENT_CREATE";
    public static final String ROBOTTHREAD_AGENT_READ = "ROBOTTHREAD_AGENT_READ";
    public static final String ROBOTTHREAD_AGENT_UPDATE = "ROBOTTHREAD_AGENT_UPDATE";
    public static final String ROBOTTHREAD_AGENT_DELETE = "ROBOTTHREAD_AGENT_DELETE";
    public static final String ROBOTTHREAD_AGENT_EXPORT = "ROBOTTHREAD_AGENT_EXPORT";
    // 用户级权限
    public static final String ROBOTTHREAD_USER_READ = "ROBOTTHREAD_USER_READ";
    public static final String ROBOTTHREAD_USER_CREATE = "ROBOTTHREAD_USER_CREATE";
    public static final String ROBOTTHREAD_USER_UPDATE = "ROBOTTHREAD_USER_UPDATE";
    public static final String ROBOTTHREAD_USER_DELETE = "ROBOTTHREAD_USER_DELETE";
    public static final String ROBOTTHREAD_USER_EXPORT = "ROBOTTHREAD_USER_EXPORT";


    // PreAuthorize注解使用的SpEL表达式 - 任意级别权限检查
    public static final String HAS_ROBOTTHREAD_CREATE_ANY_LEVEL = "hasAnyAuthority('ROBOTTHREAD_PLATFORM_CREATE', 'ROBOTTHREAD_ORGANIZATION_CREATE', 'ROBOTTHREAD_DEPARTMENT_CREATE', 'ROBOTTHREAD_WORKGROUP_CREATE', 'ROBOTTHREAD_AGENT_CREATE', 'ROBOTTHREAD_USER_CREATE')";
    public static final String HAS_ROBOTTHREAD_READ_ANY_LEVEL = "hasAnyAuthority('ROBOTTHREAD_PLATFORM_READ', 'ROBOTTHREAD_ORGANIZATION_READ', 'ROBOTTHREAD_DEPARTMENT_READ', 'ROBOTTHREAD_WORKGROUP_READ', 'ROBOTTHREAD_AGENT_READ', 'ROBOTTHREAD_USER_READ')";
    public static final String HAS_ROBOTTHREAD_UPDATE_ANY_LEVEL = "hasAnyAuthority('ROBOTTHREAD_PLATFORM_UPDATE', 'ROBOTTHREAD_ORGANIZATION_UPDATE', 'ROBOTTHREAD_DEPARTMENT_UPDATE', 'ROBOTTHREAD_WORKGROUP_UPDATE', 'ROBOTTHREAD_AGENT_UPDATE', 'ROBOTTHREAD_USER_UPDATE')";
    public static final String HAS_ROBOTTHREAD_DELETE_ANY_LEVEL = "hasAnyAuthority('ROBOTTHREAD_PLATFORM_DELETE', 'ROBOTTHREAD_ORGANIZATION_DELETE', 'ROBOTTHREAD_DEPARTMENT_DELETE', 'ROBOTTHREAD_WORKGROUP_DELETE', 'ROBOTTHREAD_AGENT_DELETE', 'ROBOTTHREAD_USER_DELETE')";
    public static final String HAS_ROBOTTHREAD_EXPORT_ANY_LEVEL = "hasAnyAuthority('ROBOTTHREAD_PLATFORM_EXPORT', 'ROBOTTHREAD_ORGANIZATION_EXPORT', 'ROBOTTHREAD_DEPARTMENT_EXPORT', 'ROBOTTHREAD_WORKGROUP_EXPORT', 'ROBOTTHREAD_AGENT_EXPORT', 'ROBOTTHREAD_USER_EXPORT')";
}