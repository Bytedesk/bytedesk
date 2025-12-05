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
package com.bytedesk.kanban.report;

import com.bytedesk.core.base.BasePermissions;

public class ReportPermissions extends BasePermissions {

    public static final String REPORT_PREFIX = "REPORT_";

    // Report Permission Constants - Platform Level
    public static final String REPORT_PLATFORM_CREATE = "REPORT_PLATFORM_CREATE";
    public static final String REPORT_PLATFORM_READ = "REPORT_PLATFORM_READ";
    public static final String REPORT_PLATFORM_UPDATE = "REPORT_PLATFORM_UPDATE";
    public static final String REPORT_PLATFORM_DELETE = "REPORT_PLATFORM_DELETE";
    public static final String REPORT_PLATFORM_EXPORT = "REPORT_PLATFORM_EXPORT";

    // Report Permission Constants - Organization Level
    public static final String REPORT_ORGANIZATION_CREATE = "REPORT_ORGANIZATION_CREATE";
    public static final String REPORT_ORGANIZATION_READ = "REPORT_ORGANIZATION_READ";
    public static final String REPORT_ORGANIZATION_UPDATE = "REPORT_ORGANIZATION_UPDATE";
    public static final String REPORT_ORGANIZATION_DELETE = "REPORT_ORGANIZATION_DELETE";
    public static final String REPORT_ORGANIZATION_EXPORT = "REPORT_ORGANIZATION_EXPORT";

    // Report Permission Constants - Department Level
    public static final String REPORT_DEPARTMENT_CREATE = "REPORT_DEPARTMENT_CREATE";
    public static final String REPORT_DEPARTMENT_READ = "REPORT_DEPARTMENT_READ";
    public static final String REPORT_DEPARTMENT_UPDATE = "REPORT_DEPARTMENT_UPDATE";
    public static final String REPORT_DEPARTMENT_DELETE = "REPORT_DEPARTMENT_DELETE";
    public static final String REPORT_DEPARTMENT_EXPORT = "REPORT_DEPARTMENT_EXPORT";

    // Report Permission Constants - Workgroup Level
    public static final String REPORT_WORKGROUP_CREATE = "REPORT_WORKGROUP_CREATE";
    public static final String REPORT_WORKGROUP_READ = "REPORT_WORKGROUP_READ";
    public static final String REPORT_WORKGROUP_UPDATE = "REPORT_WORKGROUP_UPDATE";
    public static final String REPORT_WORKGROUP_DELETE = "REPORT_WORKGROUP_DELETE";
    public static final String REPORT_WORKGROUP_EXPORT = "REPORT_WORKGROUP_EXPORT";

    // Report Permission Constants - User Level
    public static final String REPORT_AGENT_CREATE = "REPORT_AGENT_CREATE";
    public static final String REPORT_AGENT_READ = "REPORT_AGENT_READ";
    public static final String REPORT_AGENT_UPDATE = "REPORT_AGENT_UPDATE";
    public static final String REPORT_AGENT_DELETE = "REPORT_AGENT_DELETE";
    public static final String REPORT_AGENT_EXPORT = "REPORT_AGENT_EXPORT";
    // 用户级权限
    public static final String REPORT_USER_READ = "REPORT_USER_READ";
    public static final String REPORT_USER_CREATE = "REPORT_USER_CREATE";
    public static final String REPORT_USER_UPDATE = "REPORT_USER_UPDATE";
    public static final String REPORT_USER_DELETE = "REPORT_USER_DELETE";
    public static final String REPORT_USER_EXPORT = "REPORT_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_REPORT_CREATE_ANY_LEVEL = "hasAnyAuthority('REPORT_PLATFORM_CREATE', 'REPORT_ORGANIZATION_CREATE', 'REPORT_DEPARTMENT_CREATE', 'REPORT_WORKGROUP_CREATE', 'REPORT_AGENT_CREATE', 'REPORT_USER_CREATE')";
    public static final String HAS_REPORT_READ_ANY_LEVEL = "hasAnyAuthority('REPORT_PLATFORM_READ', 'REPORT_ORGANIZATION_READ', 'REPORT_DEPARTMENT_READ', 'REPORT_WORKGROUP_READ', 'REPORT_AGENT_READ', 'REPORT_USER_READ')";
    public static final String HAS_REPORT_UPDATE_ANY_LEVEL = "hasAnyAuthority('REPORT_PLATFORM_UPDATE', 'REPORT_ORGANIZATION_UPDATE', 'REPORT_DEPARTMENT_UPDATE', 'REPORT_WORKGROUP_UPDATE', 'REPORT_AGENT_UPDATE', 'REPORT_USER_UPDATE')";
    public static final String HAS_REPORT_DELETE_ANY_LEVEL = "hasAnyAuthority('REPORT_PLATFORM_DELETE', 'REPORT_ORGANIZATION_DELETE', 'REPORT_DEPARTMENT_DELETE', 'REPORT_WORKGROUP_DELETE', 'REPORT_AGENT_DELETE', 'REPORT_USER_DELETE')";
    public static final String HAS_REPORT_EXPORT_ANY_LEVEL = "hasAnyAuthority('REPORT_PLATFORM_EXPORT', 'REPORT_ORGANIZATION_EXPORT', 'REPORT_DEPARTMENT_EXPORT', 'REPORT_WORKGROUP_EXPORT', 'REPORT_AGENT_EXPORT', 'REPORT_USER_EXPORT')";

}