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
package com.bytedesk.voc.feedback;

import com.bytedesk.core.base.BasePermissions;

public class FeedbackPermissions extends BasePermissions {

    // 模块前缀
    public static final String FEEDBACK_PREFIX = "FEEDBACK_";

    // 平台级权限
    public static final String FEEDBACK_PLATFORM_READ = "FEEDBACK_PLATFORM_READ";
    public static final String FEEDBACK_PLATFORM_CREATE = "FEEDBACK_PLATFORM_CREATE";
    public static final String FEEDBACK_PLATFORM_UPDATE = "FEEDBACK_PLATFORM_UPDATE";
    public static final String FEEDBACK_PLATFORM_DELETE = "FEEDBACK_PLATFORM_DELETE";
    public static final String FEEDBACK_PLATFORM_EXPORT = "FEEDBACK_PLATFORM_EXPORT";

    // 组织级权限
    public static final String FEEDBACK_ORGANIZATION_READ = "FEEDBACK_ORGANIZATION_READ";
    public static final String FEEDBACK_ORGANIZATION_CREATE = "FEEDBACK_ORGANIZATION_CREATE";
    public static final String FEEDBACK_ORGANIZATION_UPDATE = "FEEDBACK_ORGANIZATION_UPDATE";
    public static final String FEEDBACK_ORGANIZATION_DELETE = "FEEDBACK_ORGANIZATION_DELETE";
    public static final String FEEDBACK_ORGANIZATION_EXPORT = "FEEDBACK_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String FEEDBACK_DEPARTMENT_READ = "FEEDBACK_DEPARTMENT_READ";
    public static final String FEEDBACK_DEPARTMENT_CREATE = "FEEDBACK_DEPARTMENT_CREATE";
    public static final String FEEDBACK_DEPARTMENT_UPDATE = "FEEDBACK_DEPARTMENT_UPDATE";
    public static final String FEEDBACK_DEPARTMENT_DELETE = "FEEDBACK_DEPARTMENT_DELETE";
    public static final String FEEDBACK_DEPARTMENT_EXPORT = "FEEDBACK_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String FEEDBACK_WORKGROUP_READ = "FEEDBACK_WORKGROUP_READ";
    public static final String FEEDBACK_WORKGROUP_CREATE = "FEEDBACK_WORKGROUP_CREATE";
    public static final String FEEDBACK_WORKGROUP_UPDATE = "FEEDBACK_WORKGROUP_UPDATE";
    public static final String FEEDBACK_WORKGROUP_DELETE = "FEEDBACK_WORKGROUP_DELETE";
    public static final String FEEDBACK_WORKGROUP_EXPORT = "FEEDBACK_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String FEEDBACK_AGENT_READ = "FEEDBACK_AGENT_READ";
    public static final String FEEDBACK_AGENT_CREATE = "FEEDBACK_AGENT_CREATE";
    public static final String FEEDBACK_AGENT_UPDATE = "FEEDBACK_AGENT_UPDATE";
    public static final String FEEDBACK_AGENT_DELETE = "FEEDBACK_AGENT_DELETE";
    public static final String FEEDBACK_AGENT_EXPORT = "FEEDBACK_AGENT_EXPORT";
    // 用户级权限
    public static final String FEEDBACK_USER_READ = "FEEDBACK_USER_READ";
    public static final String FEEDBACK_USER_CREATE = "FEEDBACK_USER_CREATE";
    public static final String FEEDBACK_USER_UPDATE = "FEEDBACK_USER_UPDATE";
    public static final String FEEDBACK_USER_DELETE = "FEEDBACK_USER_DELETE";
    public static final String FEEDBACK_USER_EXPORT = "FEEDBACK_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_FEEDBACK_READ_ANY_LEVEL = "hasAnyAuthority('FEEDBACK_PLATFORM_READ', 'FEEDBACK_ORGANIZATION_READ', 'FEEDBACK_DEPARTMENT_READ', 'FEEDBACK_WORKGROUP_READ', 'FEEDBACK_AGENT_READ', 'FEEDBACK_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_FEEDBACK_CREATE_ANY_LEVEL = "hasAnyAuthority('FEEDBACK_PLATFORM_CREATE', 'FEEDBACK_ORGANIZATION_CREATE', 'FEEDBACK_DEPARTMENT_CREATE', 'FEEDBACK_WORKGROUP_CREATE', 'FEEDBACK_AGENT_CREATE', 'FEEDBACK_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_FEEDBACK_UPDATE_ANY_LEVEL = "hasAnyAuthority('FEEDBACK_PLATFORM_UPDATE', 'FEEDBACK_ORGANIZATION_UPDATE', 'FEEDBACK_DEPARTMENT_UPDATE', 'FEEDBACK_WORKGROUP_UPDATE', 'FEEDBACK_AGENT_UPDATE', 'FEEDBACK_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_FEEDBACK_DELETE_ANY_LEVEL = "hasAnyAuthority('FEEDBACK_PLATFORM_DELETE', 'FEEDBACK_ORGANIZATION_DELETE', 'FEEDBACK_DEPARTMENT_DELETE', 'FEEDBACK_WORKGROUP_DELETE', 'FEEDBACK_AGENT_DELETE', 'FEEDBACK_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_FEEDBACK_EXPORT_ANY_LEVEL = "hasAnyAuthority('FEEDBACK_PLATFORM_EXPORT', 'FEEDBACK_ORGANIZATION_EXPORT', 'FEEDBACK_DEPARTMENT_EXPORT', 'FEEDBACK_WORKGROUP_EXPORT', 'FEEDBACK_AGENT_EXPORT', 'FEEDBACK_USER_EXPORT')";

}
