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
package com.bytedesk.voc.opinion;

import com.bytedesk.core.base.BasePermissions;

public class OpinionPermissions extends BasePermissions {

    // 模块前缀
    public static final String OPINION_PREFIX = "OPINION_";

    // 平台级权限
    public static final String OPINION_PLATFORM_READ = "OPINION_PLATFORM_READ";
    public static final String OPINION_PLATFORM_CREATE = "OPINION_PLATFORM_CREATE";
    public static final String OPINION_PLATFORM_UPDATE = "OPINION_PLATFORM_UPDATE";
    public static final String OPINION_PLATFORM_DELETE = "OPINION_PLATFORM_DELETE";
    public static final String OPINION_PLATFORM_EXPORT = "OPINION_PLATFORM_EXPORT";

    // 组织级权限
    public static final String OPINION_ORGANIZATION_READ = "OPINION_ORGANIZATION_READ";
    public static final String OPINION_ORGANIZATION_CREATE = "OPINION_ORGANIZATION_CREATE";
    public static final String OPINION_ORGANIZATION_UPDATE = "OPINION_ORGANIZATION_UPDATE";
    public static final String OPINION_ORGANIZATION_DELETE = "OPINION_ORGANIZATION_DELETE";
    public static final String OPINION_ORGANIZATION_EXPORT = "OPINION_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String OPINION_DEPARTMENT_READ = "OPINION_DEPARTMENT_READ";
    public static final String OPINION_DEPARTMENT_CREATE = "OPINION_DEPARTMENT_CREATE";
    public static final String OPINION_DEPARTMENT_UPDATE = "OPINION_DEPARTMENT_UPDATE";
    public static final String OPINION_DEPARTMENT_DELETE = "OPINION_DEPARTMENT_DELETE";
    public static final String OPINION_DEPARTMENT_EXPORT = "OPINION_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String OPINION_WORKGROUP_READ = "OPINION_WORKGROUP_READ";
    public static final String OPINION_WORKGROUP_CREATE = "OPINION_WORKGROUP_CREATE";
    public static final String OPINION_WORKGROUP_UPDATE = "OPINION_WORKGROUP_UPDATE";
    public static final String OPINION_WORKGROUP_DELETE = "OPINION_WORKGROUP_DELETE";
    public static final String OPINION_WORKGROUP_EXPORT = "OPINION_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String OPINION_AGENT_READ = "OPINION_AGENT_READ";
    public static final String OPINION_AGENT_CREATE = "OPINION_AGENT_CREATE";
    public static final String OPINION_AGENT_UPDATE = "OPINION_AGENT_UPDATE";
    public static final String OPINION_AGENT_DELETE = "OPINION_AGENT_DELETE";
    public static final String OPINION_AGENT_EXPORT = "OPINION_AGENT_EXPORT";
    // 用户级权限
    public static final String OPINION_USER_READ = "OPINION_USER_READ";
    public static final String OPINION_USER_CREATE = "OPINION_USER_CREATE";
    public static final String OPINION_USER_UPDATE = "OPINION_USER_UPDATE";
    public static final String OPINION_USER_DELETE = "OPINION_USER_DELETE";
    public static final String OPINION_USER_EXPORT = "OPINION_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_OPINION_READ_ANY_LEVEL = "hasAnyAuthority('OPINION_PLATFORM_READ', 'OPINION_ORGANIZATION_READ', 'OPINION_DEPARTMENT_READ', 'OPINION_WORKGROUP_READ', 'OPINION_AGENT_READ', 'OPINION_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_OPINION_CREATE_ANY_LEVEL = "hasAnyAuthority('OPINION_PLATFORM_CREATE', 'OPINION_ORGANIZATION_CREATE', 'OPINION_DEPARTMENT_CREATE', 'OPINION_WORKGROUP_CREATE', 'OPINION_AGENT_CREATE', 'OPINION_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_OPINION_UPDATE_ANY_LEVEL = "hasAnyAuthority('OPINION_PLATFORM_UPDATE', 'OPINION_ORGANIZATION_UPDATE', 'OPINION_DEPARTMENT_UPDATE', 'OPINION_WORKGROUP_UPDATE', 'OPINION_AGENT_UPDATE', 'OPINION_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_OPINION_DELETE_ANY_LEVEL = "hasAnyAuthority('OPINION_PLATFORM_DELETE', 'OPINION_ORGANIZATION_DELETE', 'OPINION_DEPARTMENT_DELETE', 'OPINION_WORKGROUP_DELETE', 'OPINION_AGENT_DELETE', 'OPINION_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_OPINION_EXPORT_ANY_LEVEL = "hasAnyAuthority('OPINION_PLATFORM_EXPORT', 'OPINION_ORGANIZATION_EXPORT', 'OPINION_DEPARTMENT_EXPORT', 'OPINION_WORKGROUP_EXPORT', 'OPINION_AGENT_EXPORT', 'OPINION_USER_EXPORT')";

}
