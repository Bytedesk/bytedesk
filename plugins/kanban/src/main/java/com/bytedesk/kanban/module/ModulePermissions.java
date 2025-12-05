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
package com.bytedesk.kanban.module;

import com.bytedesk.core.base.BasePermissions;

public class ModulePermissions extends BasePermissions {

    public static final String KANBAN_MODULE_PREFIX = "KANBAN_MODULE_";

    // Kanban Module Permission Constants - Platform Level
    public static final String KANBAN_MODULE_PLATFORM_CREATE = "KANBAN_MODULE_PLATFORM_CREATE";
    public static final String KANBAN_MODULE_PLATFORM_READ = "KANBAN_MODULE_PLATFORM_READ";
    public static final String KANBAN_MODULE_PLATFORM_UPDATE = "KANBAN_MODULE_PLATFORM_UPDATE";
    public static final String KANBAN_MODULE_PLATFORM_DELETE = "KANBAN_MODULE_PLATFORM_DELETE";
    public static final String KANBAN_MODULE_PLATFORM_EXPORT = "KANBAN_MODULE_PLATFORM_EXPORT";

    // Kanban Module Permission Constants - Organization Level
    public static final String KANBAN_MODULE_ORGANIZATION_CREATE = "KANBAN_MODULE_ORGANIZATION_CREATE";
    public static final String KANBAN_MODULE_ORGANIZATION_READ = "KANBAN_MODULE_ORGANIZATION_READ";
    public static final String KANBAN_MODULE_ORGANIZATION_UPDATE = "KANBAN_MODULE_ORGANIZATION_UPDATE";
    public static final String KANBAN_MODULE_ORGANIZATION_DELETE = "KANBAN_MODULE_ORGANIZATION_DELETE";
    public static final String KANBAN_MODULE_ORGANIZATION_EXPORT = "KANBAN_MODULE_ORGANIZATION_EXPORT";

    // Kanban Module Permission Constants - Department Level
    public static final String KANBAN_MODULE_DEPARTMENT_CREATE = "KANBAN_MODULE_DEPARTMENT_CREATE";
    public static final String KANBAN_MODULE_DEPARTMENT_READ = "KANBAN_MODULE_DEPARTMENT_READ";
    public static final String KANBAN_MODULE_DEPARTMENT_UPDATE = "KANBAN_MODULE_DEPARTMENT_UPDATE";
    public static final String KANBAN_MODULE_DEPARTMENT_DELETE = "KANBAN_MODULE_DEPARTMENT_DELETE";
    public static final String KANBAN_MODULE_DEPARTMENT_EXPORT = "KANBAN_MODULE_DEPARTMENT_EXPORT";

    // Kanban Module Permission Constants - Workgroup Level
    public static final String KANBAN_MODULE_WORKGROUP_CREATE = "KANBAN_MODULE_WORKGROUP_CREATE";
    public static final String KANBAN_MODULE_WORKGROUP_READ = "KANBAN_MODULE_WORKGROUP_READ";
    public static final String KANBAN_MODULE_WORKGROUP_UPDATE = "KANBAN_MODULE_WORKGROUP_UPDATE";
    public static final String KANBAN_MODULE_WORKGROUP_DELETE = "KANBAN_MODULE_WORKGROUP_DELETE";
    public static final String KANBAN_MODULE_WORKGROUP_EXPORT = "KANBAN_MODULE_WORKGROUP_EXPORT";

    // Kanban Module Permission Constants - User Level
    public static final String KANBAN_MODULE_AGENT_CREATE = "KANBAN_MODULE_AGENT_CREATE";
    public static final String KANBAN_MODULE_AGENT_READ = "KANBAN_MODULE_AGENT_READ";
    public static final String KANBAN_MODULE_AGENT_UPDATE = "KANBAN_MODULE_AGENT_UPDATE";
    public static final String KANBAN_MODULE_AGENT_DELETE = "KANBAN_MODULE_AGENT_DELETE";
    public static final String KANBAN_MODULE_AGENT_EXPORT = "KANBAN_MODULE_AGENT_EXPORT";
    // 用户级权限
    public static final String KANBAN_MODULE_USER_READ = "KANBAN_MODULE_USER_READ";
    public static final String KANBAN_MODULE_USER_CREATE = "KANBAN_MODULE_USER_CREATE";
    public static final String KANBAN_MODULE_USER_UPDATE = "KANBAN_MODULE_USER_UPDATE";
    public static final String KANBAN_MODULE_USER_DELETE = "KANBAN_MODULE_USER_DELETE";
    public static final String KANBAN_MODULE_USER_EXPORT = "KANBAN_MODULE_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_KANBAN_MODULE_CREATE_ANY_LEVEL = "hasAnyAuthority('KANBAN_MODULE_PLATFORM_CREATE', 'KANBAN_MODULE_ORGANIZATION_CREATE', 'KANBAN_MODULE_DEPARTMENT_CREATE', 'KANBAN_MODULE_WORKGROUP_CREATE', 'KANBAN_MODULE_AGENT_CREATE', 'KANBAN_MODULE_USER_CREATE')";
    public static final String HAS_KANBAN_MODULE_READ_ANY_LEVEL = "hasAnyAuthority('KANBAN_MODULE_PLATFORM_READ', 'KANBAN_MODULE_ORGANIZATION_READ', 'KANBAN_MODULE_DEPARTMENT_READ', 'KANBAN_MODULE_WORKGROUP_READ', 'KANBAN_MODULE_AGENT_READ', 'KANBAN_MODULE_USER_READ')";
    public static final String HAS_KANBAN_MODULE_UPDATE_ANY_LEVEL = "hasAnyAuthority('KANBAN_MODULE_PLATFORM_UPDATE', 'KANBAN_MODULE_ORGANIZATION_UPDATE', 'KANBAN_MODULE_DEPARTMENT_UPDATE', 'KANBAN_MODULE_WORKGROUP_UPDATE', 'KANBAN_MODULE_AGENT_UPDATE', 'KANBAN_MODULE_USER_UPDATE')";
    public static final String HAS_KANBAN_MODULE_DELETE_ANY_LEVEL = "hasAnyAuthority('KANBAN_MODULE_PLATFORM_DELETE', 'KANBAN_MODULE_ORGANIZATION_DELETE', 'KANBAN_MODULE_DEPARTMENT_DELETE', 'KANBAN_MODULE_WORKGROUP_DELETE', 'KANBAN_MODULE_AGENT_DELETE', 'KANBAN_MODULE_USER_DELETE')";
    public static final String HAS_KANBAN_MODULE_EXPORT_ANY_LEVEL = "hasAnyAuthority('KANBAN_MODULE_PLATFORM_EXPORT', 'KANBAN_MODULE_ORGANIZATION_EXPORT', 'KANBAN_MODULE_DEPARTMENT_EXPORT', 'KANBAN_MODULE_WORKGROUP_EXPORT', 'KANBAN_MODULE_AGENT_EXPORT', 'KANBAN_MODULE_USER_EXPORT')";

}