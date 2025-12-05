/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-03-08 10:32:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.todo_list;

import com.bytedesk.core.base.BasePermissions;

public class TodoListPermissions extends BasePermissions {

    public static final String TODOLIST_PREFIX = "TODOLIST_";

    // TodoList Permission Constants - Platform Level
    public static final String TODOLIST_PLATFORM_CREATE = "TODOLIST_PLATFORM_CREATE";
    public static final String TODOLIST_PLATFORM_READ = "TODOLIST_PLATFORM_READ";
    public static final String TODOLIST_PLATFORM_UPDATE = "TODOLIST_PLATFORM_UPDATE";
    public static final String TODOLIST_PLATFORM_DELETE = "TODOLIST_PLATFORM_DELETE";
    public static final String TODOLIST_PLATFORM_EXPORT = "TODOLIST_PLATFORM_EXPORT";

    // TodoList Permission Constants - Organization Level
    public static final String TODOLIST_ORGANIZATION_CREATE = "TODOLIST_ORGANIZATION_CREATE";
    public static final String TODOLIST_ORGANIZATION_READ = "TODOLIST_ORGANIZATION_READ";
    public static final String TODOLIST_ORGANIZATION_UPDATE = "TODOLIST_ORGANIZATION_UPDATE";
    public static final String TODOLIST_ORGANIZATION_DELETE = "TODOLIST_ORGANIZATION_DELETE";
    public static final String TODOLIST_ORGANIZATION_EXPORT = "TODOLIST_ORGANIZATION_EXPORT";

    // TodoList Permission Constants - Department Level
    public static final String TODOLIST_DEPARTMENT_CREATE = "TODOLIST_DEPARTMENT_CREATE";
    public static final String TODOLIST_DEPARTMENT_READ = "TODOLIST_DEPARTMENT_READ";
    public static final String TODOLIST_DEPARTMENT_UPDATE = "TODOLIST_DEPARTMENT_UPDATE";
    public static final String TODOLIST_DEPARTMENT_DELETE = "TODOLIST_DEPARTMENT_DELETE";
    public static final String TODOLIST_DEPARTMENT_EXPORT = "TODOLIST_DEPARTMENT_EXPORT";

    // TodoList Permission Constants - Workgroup Level
    public static final String TODOLIST_WORKGROUP_CREATE = "TODOLIST_WORKGROUP_CREATE";
    public static final String TODOLIST_WORKGROUP_READ = "TODOLIST_WORKGROUP_READ";
    public static final String TODOLIST_WORKGROUP_UPDATE = "TODOLIST_WORKGROUP_UPDATE";
    public static final String TODOLIST_WORKGROUP_DELETE = "TODOLIST_WORKGROUP_DELETE";
    public static final String TODOLIST_WORKGROUP_EXPORT = "TODOLIST_WORKGROUP_EXPORT";

    // TodoList Permission Constants - User Level
    public static final String TODOLIST_AGENT_CREATE = "TODOLIST_AGENT_CREATE";
    public static final String TODOLIST_AGENT_READ = "TODOLIST_AGENT_READ";
    public static final String TODOLIST_AGENT_UPDATE = "TODOLIST_AGENT_UPDATE";
    public static final String TODOLIST_AGENT_DELETE = "TODOLIST_AGENT_DELETE";
    public static final String TODOLIST_AGENT_EXPORT = "TODOLIST_AGENT_EXPORT";
    // 用户级权限
    public static final String TODOLIST_USER_READ = "TODOLIST_USER_READ";
    public static final String TODOLIST_USER_CREATE = "TODOLIST_USER_CREATE";
    public static final String TODOLIST_USER_UPDATE = "TODOLIST_USER_UPDATE";
    public static final String TODOLIST_USER_DELETE = "TODOLIST_USER_DELETE";
    public static final String TODOLIST_USER_EXPORT = "TODOLIST_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_TODOLIST_CREATE_ANY_LEVEL = "hasAnyAuthority('TODOLIST_PLATFORM_CREATE', 'TODOLIST_ORGANIZATION_CREATE', 'TODOLIST_DEPARTMENT_CREATE', 'TODOLIST_WORKGROUP_CREATE', 'TODOLIST_AGENT_CREATE', 'TODOLIST_USER_CREATE')";
    public static final String HAS_TODOLIST_READ_ANY_LEVEL = "hasAnyAuthority('TODOLIST_PLATFORM_READ', 'TODOLIST_ORGANIZATION_READ', 'TODOLIST_DEPARTMENT_READ', 'TODOLIST_WORKGROUP_READ', 'TODOLIST_AGENT_READ', 'TODOLIST_USER_READ')";
    public static final String HAS_TODOLIST_UPDATE_ANY_LEVEL = "hasAnyAuthority('TODOLIST_PLATFORM_UPDATE', 'TODOLIST_ORGANIZATION_UPDATE', 'TODOLIST_DEPARTMENT_UPDATE', 'TODOLIST_WORKGROUP_UPDATE', 'TODOLIST_AGENT_UPDATE', 'TODOLIST_USER_UPDATE')";
    public static final String HAS_TODOLIST_DELETE_ANY_LEVEL = "hasAnyAuthority('TODOLIST_PLATFORM_DELETE', 'TODOLIST_ORGANIZATION_DELETE', 'TODOLIST_DEPARTMENT_DELETE', 'TODOLIST_WORKGROUP_DELETE', 'TODOLIST_AGENT_DELETE', 'TODOLIST_USER_DELETE')";
    public static final String HAS_TODOLIST_EXPORT_ANY_LEVEL = "hasAnyAuthority('TODOLIST_PLATFORM_EXPORT', 'TODOLIST_ORGANIZATION_EXPORT', 'TODOLIST_DEPARTMENT_EXPORT', 'TODOLIST_WORKGROUP_EXPORT', 'TODOLIST_AGENT_EXPORT', 'TODOLIST_USER_EXPORT')";

}