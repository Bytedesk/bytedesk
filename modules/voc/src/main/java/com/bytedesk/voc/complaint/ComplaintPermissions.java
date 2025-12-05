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
package com.bytedesk.voc.complaint;

import com.bytedesk.core.base.BasePermissions;

public class ComplaintPermissions extends BasePermissions {

    // 模块前缀
    public static final String COMPLAINT_PREFIX = "COMPLAINT_";

    // 平台级权限
    public static final String COMPLAINT_PLATFORM_READ = "COMPLAINT_PLATFORM_READ";
    public static final String COMPLAINT_PLATFORM_CREATE = "COMPLAINT_PLATFORM_CREATE";
    public static final String COMPLAINT_PLATFORM_UPDATE = "COMPLAINT_PLATFORM_UPDATE";
    public static final String COMPLAINT_PLATFORM_DELETE = "COMPLAINT_PLATFORM_DELETE";
    public static final String COMPLAINT_PLATFORM_EXPORT = "COMPLAINT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String COMPLAINT_ORGANIZATION_READ = "COMPLAINT_ORGANIZATION_READ";
    public static final String COMPLAINT_ORGANIZATION_CREATE = "COMPLAINT_ORGANIZATION_CREATE";
    public static final String COMPLAINT_ORGANIZATION_UPDATE = "COMPLAINT_ORGANIZATION_UPDATE";
    public static final String COMPLAINT_ORGANIZATION_DELETE = "COMPLAINT_ORGANIZATION_DELETE";
    public static final String COMPLAINT_ORGANIZATION_EXPORT = "COMPLAINT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String COMPLAINT_DEPARTMENT_READ = "COMPLAINT_DEPARTMENT_READ";
    public static final String COMPLAINT_DEPARTMENT_CREATE = "COMPLAINT_DEPARTMENT_CREATE";
    public static final String COMPLAINT_DEPARTMENT_UPDATE = "COMPLAINT_DEPARTMENT_UPDATE";
    public static final String COMPLAINT_DEPARTMENT_DELETE = "COMPLAINT_DEPARTMENT_DELETE";
    public static final String COMPLAINT_DEPARTMENT_EXPORT = "COMPLAINT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String COMPLAINT_WORKGROUP_READ = "COMPLAINT_WORKGROUP_READ";
    public static final String COMPLAINT_WORKGROUP_CREATE = "COMPLAINT_WORKGROUP_CREATE";
    public static final String COMPLAINT_WORKGROUP_UPDATE = "COMPLAINT_WORKGROUP_UPDATE";
    public static final String COMPLAINT_WORKGROUP_DELETE = "COMPLAINT_WORKGROUP_DELETE";
    public static final String COMPLAINT_WORKGROUP_EXPORT = "COMPLAINT_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String COMPLAINT_AGENT_READ = "COMPLAINT_AGENT_READ";
    public static final String COMPLAINT_AGENT_CREATE = "COMPLAINT_AGENT_CREATE";
    public static final String COMPLAINT_AGENT_UPDATE = "COMPLAINT_AGENT_UPDATE";
    public static final String COMPLAINT_AGENT_DELETE = "COMPLAINT_AGENT_DELETE";
    public static final String COMPLAINT_AGENT_EXPORT = "COMPLAINT_AGENT_EXPORT";
    // 用户级权限
    public static final String COMPLAINT_USER_READ = "COMPLAINT_USER_READ";
    public static final String COMPLAINT_USER_CREATE = "COMPLAINT_USER_CREATE";
    public static final String COMPLAINT_USER_UPDATE = "COMPLAINT_USER_UPDATE";
    public static final String COMPLAINT_USER_DELETE = "COMPLAINT_USER_DELETE";
    public static final String COMPLAINT_USER_EXPORT = "COMPLAINT_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_COMPLAINT_READ_ANY_LEVEL = "hasAnyAuthority('COMPLAINT_PLATFORM_READ', 'COMPLAINT_ORGANIZATION_READ', 'COMPLAINT_DEPARTMENT_READ', 'COMPLAINT_WORKGROUP_READ', 'COMPLAINT_AGENT_READ', 'COMPLAINT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_COMPLAINT_CREATE_ANY_LEVEL = "hasAnyAuthority('COMPLAINT_PLATFORM_CREATE', 'COMPLAINT_ORGANIZATION_CREATE', 'COMPLAINT_DEPARTMENT_CREATE', 'COMPLAINT_WORKGROUP_CREATE', 'COMPLAINT_AGENT_CREATE', 'COMPLAINT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_COMPLAINT_UPDATE_ANY_LEVEL = "hasAnyAuthority('COMPLAINT_PLATFORM_UPDATE', 'COMPLAINT_ORGANIZATION_UPDATE', 'COMPLAINT_DEPARTMENT_UPDATE', 'COMPLAINT_WORKGROUP_UPDATE', 'COMPLAINT_AGENT_UPDATE', 'COMPLAINT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_COMPLAINT_DELETE_ANY_LEVEL = "hasAnyAuthority('COMPLAINT_PLATFORM_DELETE', 'COMPLAINT_ORGANIZATION_DELETE', 'COMPLAINT_DEPARTMENT_DELETE', 'COMPLAINT_WORKGROUP_DELETE', 'COMPLAINT_AGENT_DELETE', 'COMPLAINT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_COMPLAINT_EXPORT_ANY_LEVEL = "hasAnyAuthority('COMPLAINT_PLATFORM_EXPORT', 'COMPLAINT_ORGANIZATION_EXPORT', 'COMPLAINT_DEPARTMENT_EXPORT', 'COMPLAINT_WORKGROUP_EXPORT', 'COMPLAINT_AGENT_EXPORT', 'COMPLAINT_USER_EXPORT')";

}
