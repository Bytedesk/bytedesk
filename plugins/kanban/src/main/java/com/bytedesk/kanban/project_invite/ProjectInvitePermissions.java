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
package com.bytedesk.kanban.project_invite;

import com.bytedesk.core.base.BasePermissions;

public class ProjectInvitePermissions extends BasePermissions {

    public static final String PROJECT_INVITE_PREFIX = "PROJECT_INVITE_";

    // ProjectInvite Permission Constants - Platform Level
    public static final String PROJECT_INVITE_PLATFORM_CREATE = "PROJECT_INVITE_PLATFORM_CREATE";
    public static final String PROJECT_INVITE_PLATFORM_READ = "PROJECT_INVITE_PLATFORM_READ";
    public static final String PROJECT_INVITE_PLATFORM_UPDATE = "PROJECT_INVITE_PLATFORM_UPDATE";
    public static final String PROJECT_INVITE_PLATFORM_DELETE = "PROJECT_INVITE_PLATFORM_DELETE";
    public static final String PROJECT_INVITE_PLATFORM_EXPORT = "PROJECT_INVITE_PLATFORM_EXPORT";

    // ProjectInvite Permission Constants - Organization Level
    public static final String PROJECT_INVITE_ORGANIZATION_CREATE = "PROJECT_INVITE_ORGANIZATION_CREATE";
    public static final String PROJECT_INVITE_ORGANIZATION_READ = "PROJECT_INVITE_ORGANIZATION_READ";
    public static final String PROJECT_INVITE_ORGANIZATION_UPDATE = "PROJECT_INVITE_ORGANIZATION_UPDATE";
    public static final String PROJECT_INVITE_ORGANIZATION_DELETE = "PROJECT_INVITE_ORGANIZATION_DELETE";
    public static final String PROJECT_INVITE_ORGANIZATION_EXPORT = "PROJECT_INVITE_ORGANIZATION_EXPORT";

    // ProjectInvite Permission Constants - Department Level
    public static final String PROJECT_INVITE_DEPARTMENT_CREATE = "PROJECT_INVITE_DEPARTMENT_CREATE";
    public static final String PROJECT_INVITE_DEPARTMENT_READ = "PROJECT_INVITE_DEPARTMENT_READ";
    public static final String PROJECT_INVITE_DEPARTMENT_UPDATE = "PROJECT_INVITE_DEPARTMENT_UPDATE";
    public static final String PROJECT_INVITE_DEPARTMENT_DELETE = "PROJECT_INVITE_DEPARTMENT_DELETE";
    public static final String PROJECT_INVITE_DEPARTMENT_EXPORT = "PROJECT_INVITE_DEPARTMENT_EXPORT";

    // ProjectInvite Permission Constants - Workgroup Level
    public static final String PROJECT_INVITE_WORKGROUP_CREATE = "PROJECT_INVITE_WORKGROUP_CREATE";
    public static final String PROJECT_INVITE_WORKGROUP_READ = "PROJECT_INVITE_WORKGROUP_READ";
    public static final String PROJECT_INVITE_WORKGROUP_UPDATE = "PROJECT_INVITE_WORKGROUP_UPDATE";
    public static final String PROJECT_INVITE_WORKGROUP_DELETE = "PROJECT_INVITE_WORKGROUP_DELETE";
    public static final String PROJECT_INVITE_WORKGROUP_EXPORT = "PROJECT_INVITE_WORKGROUP_EXPORT";

    // ProjectInvite Permission Constants - User Level
    public static final String PROJECT_INVITE_AGENT_CREATE = "PROJECT_INVITE_AGENT_CREATE";
    public static final String PROJECT_INVITE_AGENT_READ = "PROJECT_INVITE_AGENT_READ";
    public static final String PROJECT_INVITE_AGENT_UPDATE = "PROJECT_INVITE_AGENT_UPDATE";
    public static final String PROJECT_INVITE_AGENT_DELETE = "PROJECT_INVITE_AGENT_DELETE";
    public static final String PROJECT_INVITE_AGENT_EXPORT = "PROJECT_INVITE_AGENT_EXPORT";
    // 用户级权限
    public static final String PROJECT_INVITE_USER_READ = "PROJECT_INVITE_USER_READ";
    public static final String PROJECT_INVITE_USER_CREATE = "PROJECT_INVITE_USER_CREATE";
    public static final String PROJECT_INVITE_USER_UPDATE = "PROJECT_INVITE_USER_UPDATE";
    public static final String PROJECT_INVITE_USER_DELETE = "PROJECT_INVITE_USER_DELETE";
    public static final String PROJECT_INVITE_USER_EXPORT = "PROJECT_INVITE_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_PROJECT_INVITE_CREATE_ANY_LEVEL = "hasAnyAuthority('PROJECT_INVITE_PLATFORM_CREATE', 'PROJECT_INVITE_ORGANIZATION_CREATE', 'PROJECT_INVITE_DEPARTMENT_CREATE', 'PROJECT_INVITE_WORKGROUP_CREATE', 'PROJECT_INVITE_AGENT_CREATE', 'PROJECT_INVITE_USER_CREATE')";
    public static final String HAS_PROJECT_INVITE_READ_ANY_LEVEL = "hasAnyAuthority('PROJECT_INVITE_PLATFORM_READ', 'PROJECT_INVITE_ORGANIZATION_READ', 'PROJECT_INVITE_DEPARTMENT_READ', 'PROJECT_INVITE_WORKGROUP_READ', 'PROJECT_INVITE_AGENT_READ', 'PROJECT_INVITE_USER_READ')";
    public static final String HAS_PROJECT_INVITE_UPDATE_ANY_LEVEL = "hasAnyAuthority('PROJECT_INVITE_PLATFORM_UPDATE', 'PROJECT_INVITE_ORGANIZATION_UPDATE', 'PROJECT_INVITE_DEPARTMENT_UPDATE', 'PROJECT_INVITE_WORKGROUP_UPDATE', 'PROJECT_INVITE_AGENT_UPDATE', 'PROJECT_INVITE_USER_UPDATE')";
    public static final String HAS_PROJECT_INVITE_DELETE_ANY_LEVEL = "hasAnyAuthority('PROJECT_INVITE_PLATFORM_DELETE', 'PROJECT_INVITE_ORGANIZATION_DELETE', 'PROJECT_INVITE_DEPARTMENT_DELETE', 'PROJECT_INVITE_WORKGROUP_DELETE', 'PROJECT_INVITE_AGENT_DELETE', 'PROJECT_INVITE_USER_DELETE')";
    public static final String HAS_PROJECT_INVITE_EXPORT_ANY_LEVEL = "hasAnyAuthority('PROJECT_INVITE_PLATFORM_EXPORT', 'PROJECT_INVITE_ORGANIZATION_EXPORT', 'PROJECT_INVITE_DEPARTMENT_EXPORT', 'PROJECT_INVITE_WORKGROUP_EXPORT', 'PROJECT_INVITE_AGENT_EXPORT', 'PROJECT_INVITE_USER_EXPORT')";

}