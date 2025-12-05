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
package com.bytedesk.service.form_result;

import com.bytedesk.core.base.BasePermissions;

public class FormResultPermissions extends BasePermissions {

    // 模块前缀
    public static final String FORM_RESULT_PREFIX = "FORM_RESULT_";

    // 平台级权限
    public static final String FORM_RESULT_PLATFORM_READ = "FORM_RESULT_PLATFORM_READ";
    public static final String FORM_RESULT_PLATFORM_CREATE = "FORM_RESULT_PLATFORM_CREATE";
    public static final String FORM_RESULT_PLATFORM_UPDATE = "FORM_RESULT_PLATFORM_UPDATE";
    public static final String FORM_RESULT_PLATFORM_DELETE = "FORM_RESULT_PLATFORM_DELETE";
    public static final String FORM_RESULT_PLATFORM_EXPORT = "FORM_RESULT_PLATFORM_EXPORT";

    // 组织级权限
    public static final String FORM_RESULT_ORGANIZATION_READ = "FORM_RESULT_ORGANIZATION_READ";
    public static final String FORM_RESULT_ORGANIZATION_CREATE = "FORM_RESULT_ORGANIZATION_CREATE";
    public static final String FORM_RESULT_ORGANIZATION_UPDATE = "FORM_RESULT_ORGANIZATION_UPDATE";
    public static final String FORM_RESULT_ORGANIZATION_DELETE = "FORM_RESULT_ORGANIZATION_DELETE";
    public static final String FORM_RESULT_ORGANIZATION_EXPORT = "FORM_RESULT_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String FORM_RESULT_DEPARTMENT_READ = "FORM_RESULT_DEPARTMENT_READ";
    public static final String FORM_RESULT_DEPARTMENT_CREATE = "FORM_RESULT_DEPARTMENT_CREATE";
    public static final String FORM_RESULT_DEPARTMENT_UPDATE = "FORM_RESULT_DEPARTMENT_UPDATE";
    public static final String FORM_RESULT_DEPARTMENT_DELETE = "FORM_RESULT_DEPARTMENT_DELETE";
    public static final String FORM_RESULT_DEPARTMENT_EXPORT = "FORM_RESULT_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String FORM_RESULT_WORKGROUP_READ = "FORM_RESULT_WORKGROUP_READ";
    public static final String FORM_RESULT_WORKGROUP_CREATE = "FORM_RESULT_WORKGROUP_CREATE";
    public static final String FORM_RESULT_WORKGROUP_UPDATE = "FORM_RESULT_WORKGROUP_UPDATE";
    public static final String FORM_RESULT_WORKGROUP_DELETE = "FORM_RESULT_WORKGROUP_DELETE";
    public static final String FORM_RESULT_WORKGROUP_EXPORT = "FORM_RESULT_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String FORM_RESULT_AGENT_READ = "FORM_RESULT_AGENT_READ";
    public static final String FORM_RESULT_AGENT_CREATE = "FORM_RESULT_AGENT_CREATE";
    public static final String FORM_RESULT_AGENT_UPDATE = "FORM_RESULT_AGENT_UPDATE";
    public static final String FORM_RESULT_AGENT_DELETE = "FORM_RESULT_AGENT_DELETE";
    public static final String FORM_RESULT_AGENT_EXPORT = "FORM_RESULT_AGENT_EXPORT";
    // 用户级权限
    public static final String FORM_RESULT_USER_READ = "FORM_RESULT_USER_READ";
    public static final String FORM_RESULT_USER_CREATE = "FORM_RESULT_USER_CREATE";
    public static final String FORM_RESULT_USER_UPDATE = "FORM_RESULT_USER_UPDATE";
    public static final String FORM_RESULT_USER_DELETE = "FORM_RESULT_USER_DELETE";
    public static final String FORM_RESULT_USER_EXPORT = "FORM_RESULT_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_FORM_RESULT_READ_ANY_LEVEL = "hasAnyAuthority('FORM_RESULT_PLATFORM_READ', 'FORM_RESULT_ORGANIZATION_READ', 'FORM_RESULT_DEPARTMENT_READ', 'FORM_RESULT_WORKGROUP_READ', 'FORM_RESULT_AGENT_READ', 'FORM_RESULT_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_FORM_RESULT_CREATE_ANY_LEVEL = "hasAnyAuthority('FORM_RESULT_PLATFORM_CREATE', 'FORM_RESULT_ORGANIZATION_CREATE', 'FORM_RESULT_DEPARTMENT_CREATE', 'FORM_RESULT_WORKGROUP_CREATE', 'FORM_RESULT_AGENT_CREATE', 'FORM_RESULT_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_FORM_RESULT_UPDATE_ANY_LEVEL = "hasAnyAuthority('FORM_RESULT_PLATFORM_UPDATE', 'FORM_RESULT_ORGANIZATION_UPDATE', 'FORM_RESULT_DEPARTMENT_UPDATE', 'FORM_RESULT_WORKGROUP_UPDATE', 'FORM_RESULT_AGENT_UPDATE', 'FORM_RESULT_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_FORM_RESULT_DELETE_ANY_LEVEL = "hasAnyAuthority('FORM_RESULT_PLATFORM_DELETE', 'FORM_RESULT_ORGANIZATION_DELETE', 'FORM_RESULT_DEPARTMENT_DELETE', 'FORM_RESULT_WORKGROUP_DELETE', 'FORM_RESULT_AGENT_DELETE', 'FORM_RESULT_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_FORM_RESULT_EXPORT_ANY_LEVEL = "hasAnyAuthority('FORM_RESULT_PLATFORM_EXPORT', 'FORM_RESULT_ORGANIZATION_EXPORT', 'FORM_RESULT_DEPARTMENT_EXPORT', 'FORM_RESULT_WORKGROUP_EXPORT', 'FORM_RESULT_AGENT_EXPORT', 'FORM_RESULT_USER_EXPORT')";

}
