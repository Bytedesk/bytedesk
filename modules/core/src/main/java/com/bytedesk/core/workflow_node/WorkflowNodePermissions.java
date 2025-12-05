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
package com.bytedesk.core.workflow_node;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowNodePermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_NODE_PREFIX = "WORKFLOW_NODE_";

    // 平台级权限
    public static final String WORKFLOW_NODE_PLATFORM_READ = "WORKFLOW_NODE_PLATFORM_READ";
    public static final String WORKFLOW_NODE_PLATFORM_CREATE = "WORKFLOW_NODE_PLATFORM_CREATE";
    public static final String WORKFLOW_NODE_PLATFORM_UPDATE = "WORKFLOW_NODE_PLATFORM_UPDATE";
    public static final String WORKFLOW_NODE_PLATFORM_DELETE = "WORKFLOW_NODE_PLATFORM_DELETE";
    public static final String WORKFLOW_NODE_PLATFORM_EXPORT = "WORKFLOW_NODE_PLATFORM_EXPORT";

    // 组织级权限
    public static final String WORKFLOW_NODE_ORGANIZATION_READ = "WORKFLOW_NODE_ORGANIZATION_READ";
    public static final String WORKFLOW_NODE_ORGANIZATION_CREATE = "WORKFLOW_NODE_ORGANIZATION_CREATE";
    public static final String WORKFLOW_NODE_ORGANIZATION_UPDATE = "WORKFLOW_NODE_ORGANIZATION_UPDATE";
    public static final String WORKFLOW_NODE_ORGANIZATION_DELETE = "WORKFLOW_NODE_ORGANIZATION_DELETE";
    public static final String WORKFLOW_NODE_ORGANIZATION_EXPORT = "WORKFLOW_NODE_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String WORKFLOW_NODE_DEPARTMENT_READ = "WORKFLOW_NODE_DEPARTMENT_READ";
    public static final String WORKFLOW_NODE_DEPARTMENT_CREATE = "WORKFLOW_NODE_DEPARTMENT_CREATE";
    public static final String WORKFLOW_NODE_DEPARTMENT_UPDATE = "WORKFLOW_NODE_DEPARTMENT_UPDATE";
    public static final String WORKFLOW_NODE_DEPARTMENT_DELETE = "WORKFLOW_NODE_DEPARTMENT_DELETE";
    public static final String WORKFLOW_NODE_DEPARTMENT_EXPORT = "WORKFLOW_NODE_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String WORKFLOW_NODE_WORKGROUP_READ = "WORKFLOW_NODE_WORKGROUP_READ";
    public static final String WORKFLOW_NODE_WORKGROUP_CREATE = "WORKFLOW_NODE_WORKGROUP_CREATE";
    public static final String WORKFLOW_NODE_WORKGROUP_UPDATE = "WORKFLOW_NODE_WORKGROUP_UPDATE";
    public static final String WORKFLOW_NODE_WORKGROUP_DELETE = "WORKFLOW_NODE_WORKGROUP_DELETE";
    public static final String WORKFLOW_NODE_WORKGROUP_EXPORT = "WORKFLOW_NODE_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String WORKFLOW_NODE_AGENT_READ = "WORKFLOW_NODE_AGENT_READ";
    public static final String WORKFLOW_NODE_AGENT_CREATE = "WORKFLOW_NODE_AGENT_CREATE";
    public static final String WORKFLOW_NODE_AGENT_UPDATE = "WORKFLOW_NODE_AGENT_UPDATE";
    public static final String WORKFLOW_NODE_AGENT_DELETE = "WORKFLOW_NODE_AGENT_DELETE";
    public static final String WORKFLOW_NODE_AGENT_EXPORT = "WORKFLOW_NODE_AGENT_EXPORT";
    // 用户级权限
    public static final String WORKFLOW_NODE_USER_READ = "WORKFLOW_NODE_USER_READ";
    public static final String WORKFLOW_NODE_USER_CREATE = "WORKFLOW_NODE_USER_CREATE";
    public static final String WORKFLOW_NODE_USER_UPDATE = "WORKFLOW_NODE_USER_UPDATE";
    public static final String WORKFLOW_NODE_USER_DELETE = "WORKFLOW_NODE_USER_DELETE";
    public static final String WORKFLOW_NODE_USER_EXPORT = "WORKFLOW_NODE_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_WORKFLOW_NODE_READ_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_NODE_PLATFORM_READ', 'WORKFLOW_NODE_ORGANIZATION_READ', 'WORKFLOW_NODE_DEPARTMENT_READ', 'WORKFLOW_NODE_WORKGROUP_READ', 'WORKFLOW_NODE_AGENT_READ', 'WORKFLOW_NODE_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_WORKFLOW_NODE_CREATE_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_NODE_PLATFORM_CREATE', 'WORKFLOW_NODE_ORGANIZATION_CREATE', 'WORKFLOW_NODE_DEPARTMENT_CREATE', 'WORKFLOW_NODE_WORKGROUP_CREATE', 'WORKFLOW_NODE_AGENT_CREATE', 'WORKFLOW_NODE_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_WORKFLOW_NODE_UPDATE_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_NODE_PLATFORM_UPDATE', 'WORKFLOW_NODE_ORGANIZATION_UPDATE', 'WORKFLOW_NODE_DEPARTMENT_UPDATE', 'WORKFLOW_NODE_WORKGROUP_UPDATE', 'WORKFLOW_NODE_AGENT_UPDATE', 'WORKFLOW_NODE_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_WORKFLOW_NODE_DELETE_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_NODE_PLATFORM_DELETE', 'WORKFLOW_NODE_ORGANIZATION_DELETE', 'WORKFLOW_NODE_DEPARTMENT_DELETE', 'WORKFLOW_NODE_WORKGROUP_DELETE', 'WORKFLOW_NODE_AGENT_DELETE', 'WORKFLOW_NODE_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_WORKFLOW_NODE_EXPORT_ANY_LEVEL = "hasAnyAuthority('WORKFLOW_NODE_PLATFORM_EXPORT', 'WORKFLOW_NODE_ORGANIZATION_EXPORT', 'WORKFLOW_NODE_DEPARTMENT_EXPORT', 'WORKFLOW_NODE_WORKGROUP_EXPORT', 'WORKFLOW_NODE_AGENT_EXPORT', 'WORKFLOW_NODE_USER_EXPORT')";

}
