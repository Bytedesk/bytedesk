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
package com.bytedesk.core.sms;

import com.bytedesk.core.base.BasePermissions;

/**
 * 短信权限控制 - 五级权限体系
 */
public class SmsPermissions extends BasePermissions {

    public static final String SMS_PREFIX = "SMS_";

    // 平台级别权限
    public static final String SMS_PLATFORM_CREATE = "SMS_PLATFORM_CREATE";
    public static final String SMS_PLATFORM_READ = "SMS_PLATFORM_READ";
    public static final String SMS_PLATFORM_UPDATE = "SMS_PLATFORM_UPDATE";
    public static final String SMS_PLATFORM_DELETE = "SMS_PLATFORM_DELETE";
    public static final String SMS_PLATFORM_EXPORT = "SMS_PLATFORM_EXPORT";

    // 组织级别权限
    public static final String SMS_ORGANIZATION_CREATE = "SMS_ORGANIZATION_CREATE";
    public static final String SMS_ORGANIZATION_READ = "SMS_ORGANIZATION_READ";
    public static final String SMS_ORGANIZATION_UPDATE = "SMS_ORGANIZATION_UPDATE";
    public static final String SMS_ORGANIZATION_DELETE = "SMS_ORGANIZATION_DELETE";
    public static final String SMS_ORGANIZATION_EXPORT = "SMS_ORGANIZATION_EXPORT";

    // 部门级别权限
    public static final String SMS_DEPARTMENT_CREATE = "SMS_DEPARTMENT_CREATE";
    public static final String SMS_DEPARTMENT_READ = "SMS_DEPARTMENT_READ";
    public static final String SMS_DEPARTMENT_UPDATE = "SMS_DEPARTMENT_UPDATE";
    public static final String SMS_DEPARTMENT_DELETE = "SMS_DEPARTMENT_DELETE";
    public static final String SMS_DEPARTMENT_EXPORT = "SMS_DEPARTMENT_EXPORT";

    // 工作组级别权限
    public static final String SMS_WORKGROUP_CREATE = "SMS_WORKGROUP_CREATE";
    public static final String SMS_WORKGROUP_READ = "SMS_WORKGROUP_READ";
    public static final String SMS_WORKGROUP_UPDATE = "SMS_WORKGROUP_UPDATE";
    public static final String SMS_WORKGROUP_DELETE = "SMS_WORKGROUP_DELETE";
    public static final String SMS_WORKGROUP_EXPORT = "SMS_WORKGROUP_EXPORT";

    // 客服级别权限
    public static final String SMS_AGENT_CREATE = "SMS_AGENT_CREATE";
    public static final String SMS_AGENT_READ = "SMS_AGENT_READ";
    public static final String SMS_AGENT_UPDATE = "SMS_AGENT_UPDATE";
    public static final String SMS_AGENT_DELETE = "SMS_AGENT_DELETE";
    public static final String SMS_AGENT_EXPORT = "SMS_AGENT_EXPORT";
    // 用户级权限
    public static final String SMS_USER_READ = "SMS_USER_READ";
    public static final String SMS_USER_CREATE = "SMS_USER_CREATE";
    public static final String SMS_USER_UPDATE = "SMS_USER_UPDATE";
    public static final String SMS_USER_DELETE = "SMS_USER_DELETE";
    public static final String SMS_USER_EXPORT = "SMS_USER_EXPORT";


    // PreAuthorize注解使用的SpEL表达式 - 任意级别权限检查
    public static final String HAS_SMS_CREATE_ANY_LEVEL = "hasAnyAuthority('SMS_PLATFORM_CREATE', 'SMS_ORGANIZATION_CREATE', 'SMS_DEPARTMENT_CREATE', 'SMS_WORKGROUP_CREATE', 'SMS_AGENT_CREATE', 'SMS_USER_CREATE')";
    public static final String HAS_SMS_READ_ANY_LEVEL = "hasAnyAuthority('SMS_PLATFORM_READ', 'SMS_ORGANIZATION_READ', 'SMS_DEPARTMENT_READ', 'SMS_WORKGROUP_READ', 'SMS_AGENT_READ', 'SMS_USER_READ')";
    public static final String HAS_SMS_UPDATE_ANY_LEVEL = "hasAnyAuthority('SMS_PLATFORM_UPDATE', 'SMS_ORGANIZATION_UPDATE', 'SMS_DEPARTMENT_UPDATE', 'SMS_WORKGROUP_UPDATE', 'SMS_AGENT_UPDATE', 'SMS_USER_UPDATE')";
    public static final String HAS_SMS_DELETE_ANY_LEVEL = "hasAnyAuthority('SMS_PLATFORM_DELETE', 'SMS_ORGANIZATION_DELETE', 'SMS_DEPARTMENT_DELETE', 'SMS_WORKGROUP_DELETE', 'SMS_AGENT_DELETE', 'SMS_USER_DELETE')";
    public static final String HAS_SMS_EXPORT_ANY_LEVEL = "hasAnyAuthority('SMS_PLATFORM_EXPORT', 'SMS_ORGANIZATION_EXPORT', 'SMS_DEPARTMENT_EXPORT', 'SMS_WORKGROUP_EXPORT', 'SMS_AGENT_EXPORT', 'SMS_USER_EXPORT')";
}
