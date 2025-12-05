/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 17:37:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowPermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_PREFIX = "WORKFLOW_";

    // 平台级权限
    public static final String WORKFLOW_PLATFORM_READ = "WORKFLOW_PLATFORM_READ";
    public static final String WORKFLOW_PLATFORM_CREATE = "WORKFLOW_PLATFORM_CREATE";
    public static final String WORKFLOW_PLATFORM_UPDATE = "WORKFLOW_PLATFORM_UPDATE";
    public static final String WORKFLOW_PLATFORM_DELETE = "WORKFLOW_PLATFORM_DELETE";
    public static final String WORKFLOW_PLATFORM_EXPORT = "WORKFLOW_PLATFORM_EXPORT";

    // 组织级权限
    public static final String WORKFLOW_ORGANIZATION_READ = "WORKFLOW_ORGANIZATION_READ";
    public static final String WORKFLOW_ORGANIZATION_CREATE = "WORKFLOW_ORGANIZATION_CREATE";
    public static final String WORKFLOW_ORGANIZATION_UPDATE = "WORKFLOW_ORGANIZATION_UPDATE";
    public static final String WORKFLOW_ORGANIZATION_DELETE = "WORKFLOW_ORGANIZATION_DELETE";
    public static final String WORKFLOW_ORGANIZATION_EXPORT = "WORKFLOW_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String WORKFLOW_DEPARTMENT_READ = "WORKFLOW_DEPARTMENT_READ";
    public static final String WORKFLOW_DEPARTMENT_CREATE = "WORKFLOW_DEPARTMENT_CREATE";
    public static final String WORKFLOW_DEPARTMENT_UPDATE = "WORKFLOW_DEPARTMENT_UPDATE";
    public static final String WORKFLOW_DEPARTMENT_DELETE = "WORKFLOW_DEPARTMENT_DELETE";
    public static final String WORKFLOW_DEPARTMENT_EXPORT = "WORKFLOW_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String WORKFLOW_WORKGROUP_READ = "WORKFLOW_WORKGROUP_READ";
    public static final String WORKFLOW_WORKGROUP_CREATE = "WORKFLOW_WORKGROUP_CREATE";
    public static final String WORKFLOW_WORKGROUP_UPDATE = "WORKFLOW_WORKGROUP_UPDATE";
    public static final String WORKFLOW_WORKGROUP_DELETE = "WORKFLOW_WORKGROUP_DELETE";
    public static final String WORKFLOW_WORKGROUP_EXPORT = "WORKFLOW_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String WORKFLOW_AGENT_READ = "WORKFLOW_AGENT_READ";
    public static final String WORKFLOW_AGENT_CREATE = "WORKFLOW_AGENT_CREATE";
    public static final String WORKFLOW_AGENT_UPDATE = "WORKFLOW_AGENT_UPDATE";
    public static final String WORKFLOW_AGENT_DELETE = "WORKFLOW_AGENT_DELETE";
    public static final String WORKFLOW_AGENT_EXPORT = "WORKFLOW_AGENT_EXPORT";
    // 用户级权限
    public static final String WORKFLOW_USER_READ = "WORKFLOW_USER_READ";
    public static final String WORKFLOW_USER_CREATE = "WORKFLOW_USER_CREATE";
    public static final String WORKFLOW_USER_UPDATE = "WORKFLOW_USER_UPDATE";
    public static final String WORKFLOW_USER_DELETE = "WORKFLOW_USER_DELETE";
    public static final String WORKFLOW_USER_EXPORT = "WORKFLOW_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_WORKFLOW_READ_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_PLATFORM_READ', 'WORKFLOW_ORGANIZATION_READ', 'WORKFLOW_DEPARTMENT_READ', 'WORKFLOW_WORKGROUP_READ', 'WORKFLOW_AGENT_READ', 'WORKFLOW_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_WORKFLOW_CREATE_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_PLATFORM_CREATE', 'WORKFLOW_ORGANIZATION_CREATE', 'WORKFLOW_DEPARTMENT_CREATE', 'WORKFLOW_WORKGROUP_CREATE', 'WORKFLOW_AGENT_CREATE', 'WORKFLOW_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_WORKFLOW_UPDATE_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_PLATFORM_UPDATE', 'WORKFLOW_ORGANIZATION_UPDATE', 'WORKFLOW_DEPARTMENT_UPDATE', 'WORKFLOW_WORKGROUP_UPDATE', 'WORKFLOW_AGENT_UPDATE', 'WORKFLOW_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_WORKFLOW_DELETE_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_PLATFORM_DELETE', 'WORKFLOW_ORGANIZATION_DELETE', 'WORKFLOW_DEPARTMENT_DELETE', 'WORKFLOW_WORKGROUP_DELETE', 'WORKFLOW_AGENT_DELETE', 'WORKFLOW_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_WORKFLOW_EXPORT_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_PLATFORM_EXPORT', 'WORKFLOW_ORGANIZATION_EXPORT', 'WORKFLOW_DEPARTMENT_EXPORT', 'WORKFLOW_WORKGROUP_EXPORT', 'WORKFLOW_AGENT_EXPORT', 'WORKFLOW_USER_EXPORT')";

}