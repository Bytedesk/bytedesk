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
package com.bytedesk.demos.booking;

import com.bytedesk.core.base.BasePermissions;

public class BookingPermissions extends BasePermissions {

    public static final String BOOKING_PREFIX = "BOOKING_";

    // Booking Permission Constants - Platform Level
    public static final String BOOKING_PLATFORM_CREATE = "BOOKING_PLATFORM_CREATE";
    public static final String BOOKING_PLATFORM_READ = "BOOKING_PLATFORM_READ";
    public static final String BOOKING_PLATFORM_UPDATE = "BOOKING_PLATFORM_UPDATE";
    public static final String BOOKING_PLATFORM_DELETE = "BOOKING_PLATFORM_DELETE";
    public static final String BOOKING_PLATFORM_EXPORT = "BOOKING_PLATFORM_EXPORT";

    // Booking Permission Constants - Organization Level
    public static final String BOOKING_ORGANIZATION_CREATE = "BOOKING_ORGANIZATION_CREATE";
    public static final String BOOKING_ORGANIZATION_READ = "BOOKING_ORGANIZATION_READ";
    public static final String BOOKING_ORGANIZATION_UPDATE = "BOOKING_ORGANIZATION_UPDATE";
    public static final String BOOKING_ORGANIZATION_DELETE = "BOOKING_ORGANIZATION_DELETE";
    public static final String BOOKING_ORGANIZATION_EXPORT = "BOOKING_ORGANIZATION_EXPORT";

    // Booking Permission Constants - Department Level
    public static final String BOOKING_DEPARTMENT_CREATE = "BOOKING_DEPARTMENT_CREATE";
    public static final String BOOKING_DEPARTMENT_READ = "BOOKING_DEPARTMENT_READ";
    public static final String BOOKING_DEPARTMENT_UPDATE = "BOOKING_DEPARTMENT_UPDATE";
    public static final String BOOKING_DEPARTMENT_DELETE = "BOOKING_DEPARTMENT_DELETE";
    public static final String BOOKING_DEPARTMENT_EXPORT = "BOOKING_DEPARTMENT_EXPORT";

    // Booking Permission Constants - Workgroup Level
    public static final String BOOKING_WORKGROUP_CREATE = "BOOKING_WORKGROUP_CREATE";
    public static final String BOOKING_WORKGROUP_READ = "BOOKING_WORKGROUP_READ";
    public static final String BOOKING_WORKGROUP_UPDATE = "BOOKING_WORKGROUP_UPDATE";
    public static final String BOOKING_WORKGROUP_DELETE = "BOOKING_WORKGROUP_DELETE";
    public static final String BOOKING_WORKGROUP_EXPORT = "BOOKING_WORKGROUP_EXPORT";

    // Booking Permission Constants - User Level
    public static final String BOOKING_AGENT_CREATE = "BOOKING_AGENT_CREATE";
    public static final String BOOKING_AGENT_READ = "BOOKING_AGENT_READ";
    public static final String BOOKING_AGENT_UPDATE = "BOOKING_AGENT_UPDATE";
    public static final String BOOKING_AGENT_DELETE = "BOOKING_AGENT_DELETE";
    public static final String BOOKING_AGENT_EXPORT = "BOOKING_AGENT_EXPORT";
    // 用户级权限
    public static final String BOOKING_USER_READ = "BOOKING_USER_READ";
    public static final String BOOKING_USER_CREATE = "BOOKING_USER_CREATE";
    public static final String BOOKING_USER_UPDATE = "BOOKING_USER_UPDATE";
    public static final String BOOKING_USER_DELETE = "BOOKING_USER_DELETE";
    public static final String BOOKING_USER_EXPORT = "BOOKING_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_BOOKING_CREATE_ANY_LEVEL = "hasAnyAuthority('BOOKING_PLATFORM_CREATE', 'BOOKING_ORGANIZATION_CREATE', 'BOOKING_DEPARTMENT_CREATE', 'BOOKING_WORKGROUP_CREATE', 'BOOKING_AGENT_CREATE', 'BOOKING_USER_CREATE')";
    public static final String HAS_BOOKING_READ_ANY_LEVEL = "hasAnyAuthority('BOOKING_PLATFORM_READ', 'BOOKING_ORGANIZATION_READ', 'BOOKING_DEPARTMENT_READ', 'BOOKING_WORKGROUP_READ', 'BOOKING_AGENT_READ', 'BOOKING_USER_READ')";
    public static final String HAS_BOOKING_UPDATE_ANY_LEVEL = "hasAnyAuthority('BOOKING_PLATFORM_UPDATE', 'BOOKING_ORGANIZATION_UPDATE', 'BOOKING_DEPARTMENT_UPDATE', 'BOOKING_WORKGROUP_UPDATE', 'BOOKING_AGENT_UPDATE', 'BOOKING_USER_UPDATE')";
    public static final String HAS_BOOKING_DELETE_ANY_LEVEL = "hasAnyAuthority('BOOKING_PLATFORM_DELETE', 'BOOKING_ORGANIZATION_DELETE', 'BOOKING_DEPARTMENT_DELETE', 'BOOKING_WORKGROUP_DELETE', 'BOOKING_AGENT_DELETE', 'BOOKING_USER_DELETE')";
    public static final String HAS_BOOKING_EXPORT_ANY_LEVEL = "hasAnyAuthority('BOOKING_PLATFORM_EXPORT', 'BOOKING_ORGANIZATION_EXPORT', 'BOOKING_DEPARTMENT_EXPORT', 'BOOKING_WORKGROUP_EXPORT', 'BOOKING_AGENT_EXPORT', 'BOOKING_USER_EXPORT')";

}
