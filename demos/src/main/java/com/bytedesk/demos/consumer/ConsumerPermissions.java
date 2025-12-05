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
package com.bytedesk.demos.consumer;

import com.bytedesk.core.base.BasePermissions;

public class ConsumerPermissions extends BasePermissions {

    public static final String CONSUMER_PREFIX = "CONSUMER_";

    // Consumer Permission Constants - Platform Level
    public static final String CONSUMER_PLATFORM_CREATE = "CONSUMER_PLATFORM_CREATE";
    public static final String CONSUMER_PLATFORM_READ = "CONSUMER_PLATFORM_READ";
    public static final String CONSUMER_PLATFORM_UPDATE = "CONSUMER_PLATFORM_UPDATE";
    public static final String CONSUMER_PLATFORM_DELETE = "CONSUMER_PLATFORM_DELETE";
    public static final String CONSUMER_PLATFORM_EXPORT = "CONSUMER_PLATFORM_EXPORT";

    // Consumer Permission Constants - Organization Level
    public static final String CONSUMER_ORGANIZATION_CREATE = "CONSUMER_ORGANIZATION_CREATE";
    public static final String CONSUMER_ORGANIZATION_READ = "CONSUMER_ORGANIZATION_READ";
    public static final String CONSUMER_ORGANIZATION_UPDATE = "CONSUMER_ORGANIZATION_UPDATE";
    public static final String CONSUMER_ORGANIZATION_DELETE = "CONSUMER_ORGANIZATION_DELETE";
    public static final String CONSUMER_ORGANIZATION_EXPORT = "CONSUMER_ORGANIZATION_EXPORT";

    // Consumer Permission Constants - Department Level
    public static final String CONSUMER_DEPARTMENT_CREATE = "CONSUMER_DEPARTMENT_CREATE";
    public static final String CONSUMER_DEPARTMENT_READ = "CONSUMER_DEPARTMENT_READ";
    public static final String CONSUMER_DEPARTMENT_UPDATE = "CONSUMER_DEPARTMENT_UPDATE";
    public static final String CONSUMER_DEPARTMENT_DELETE = "CONSUMER_DEPARTMENT_DELETE";
    public static final String CONSUMER_DEPARTMENT_EXPORT = "CONSUMER_DEPARTMENT_EXPORT";

    // Consumer Permission Constants - Workgroup Level
    public static final String CONSUMER_WORKGROUP_CREATE = "CONSUMER_WORKGROUP_CREATE";
    public static final String CONSUMER_WORKGROUP_READ = "CONSUMER_WORKGROUP_READ";
    public static final String CONSUMER_WORKGROUP_UPDATE = "CONSUMER_WORKGROUP_UPDATE";
    public static final String CONSUMER_WORKGROUP_DELETE = "CONSUMER_WORKGROUP_DELETE";
    public static final String CONSUMER_WORKGROUP_EXPORT = "CONSUMER_WORKGROUP_EXPORT";

    // Consumer Permission Constants - User Level
    public static final String CONSUMER_AGENT_CREATE = "CONSUMER_AGENT_CREATE";
    public static final String CONSUMER_AGENT_READ = "CONSUMER_AGENT_READ";
    public static final String CONSUMER_AGENT_UPDATE = "CONSUMER_AGENT_UPDATE";
    public static final String CONSUMER_AGENT_DELETE = "CONSUMER_AGENT_DELETE";
    public static final String CONSUMER_AGENT_EXPORT = "CONSUMER_AGENT_EXPORT";
    // 用户级权限
    public static final String CONSUMER_USER_READ = "CONSUMER_USER_READ";
    public static final String CONSUMER_USER_CREATE = "CONSUMER_USER_CREATE";
    public static final String CONSUMER_USER_UPDATE = "CONSUMER_USER_UPDATE";
    public static final String CONSUMER_USER_DELETE = "CONSUMER_USER_DELETE";
    public static final String CONSUMER_USER_EXPORT = "CONSUMER_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_CONSUMER_CREATE_ANY_LEVEL = "hasAnyAuthority('CONSUMER_PLATFORM_CREATE', 'CONSUMER_ORGANIZATION_CREATE', 'CONSUMER_DEPARTMENT_CREATE', 'CONSUMER_WORKGROUP_CREATE', 'CONSUMER_AGENT_CREATE', 'CONSUMER_USER_CREATE')";
    public static final String HAS_CONSUMER_READ_ANY_LEVEL = "hasAnyAuthority('CONSUMER_PLATFORM_READ', 'CONSUMER_ORGANIZATION_READ', 'CONSUMER_DEPARTMENT_READ', 'CONSUMER_WORKGROUP_READ', 'CONSUMER_AGENT_READ', 'CONSUMER_USER_READ')";
    public static final String HAS_CONSUMER_UPDATE_ANY_LEVEL = "hasAnyAuthority('CONSUMER_PLATFORM_UPDATE', 'CONSUMER_ORGANIZATION_UPDATE', 'CONSUMER_DEPARTMENT_UPDATE', 'CONSUMER_WORKGROUP_UPDATE', 'CONSUMER_AGENT_UPDATE', 'CONSUMER_USER_UPDATE')";
    public static final String HAS_CONSUMER_DELETE_ANY_LEVEL = "hasAnyAuthority('CONSUMER_PLATFORM_DELETE', 'CONSUMER_ORGANIZATION_DELETE', 'CONSUMER_DEPARTMENT_DELETE', 'CONSUMER_WORKGROUP_DELETE', 'CONSUMER_AGENT_DELETE', 'CONSUMER_USER_DELETE')";
    public static final String HAS_CONSUMER_EXPORT_ANY_LEVEL = "hasAnyAuthority('CONSUMER_PLATFORM_EXPORT', 'CONSUMER_ORGANIZATION_EXPORT', 'CONSUMER_DEPARTMENT_EXPORT', 'CONSUMER_WORKGROUP_EXPORT', 'CONSUMER_AGENT_EXPORT', 'CONSUMER_USER_EXPORT')";

}
