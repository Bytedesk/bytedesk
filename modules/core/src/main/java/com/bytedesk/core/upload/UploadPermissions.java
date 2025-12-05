/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.bytedesk.core.base.BasePermissions;

public class UploadPermissions extends BasePermissions {

    // 模块前缀
    public static final String UPLOAD_PREFIX = "UPLOAD_";

    // 平台级权限
    public static final String UPLOAD_PLATFORM_READ = "UPLOAD_PLATFORM_READ";
    public static final String UPLOAD_PLATFORM_CREATE = "UPLOAD_PLATFORM_CREATE";
    public static final String UPLOAD_PLATFORM_UPDATE = "UPLOAD_PLATFORM_UPDATE";
    public static final String UPLOAD_PLATFORM_DELETE = "UPLOAD_PLATFORM_DELETE";
    public static final String UPLOAD_PLATFORM_EXPORT = "UPLOAD_PLATFORM_EXPORT";

    // 组织级权限
    public static final String UPLOAD_ORGANIZATION_READ = "UPLOAD_ORGANIZATION_READ";
    public static final String UPLOAD_ORGANIZATION_CREATE = "UPLOAD_ORGANIZATION_CREATE";
    public static final String UPLOAD_ORGANIZATION_UPDATE = "UPLOAD_ORGANIZATION_UPDATE";
    public static final String UPLOAD_ORGANIZATION_DELETE = "UPLOAD_ORGANIZATION_DELETE";
    public static final String UPLOAD_ORGANIZATION_EXPORT = "UPLOAD_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String UPLOAD_DEPARTMENT_READ = "UPLOAD_DEPARTMENT_READ";
    public static final String UPLOAD_DEPARTMENT_CREATE = "UPLOAD_DEPARTMENT_CREATE";
    public static final String UPLOAD_DEPARTMENT_UPDATE = "UPLOAD_DEPARTMENT_UPDATE";
    public static final String UPLOAD_DEPARTMENT_DELETE = "UPLOAD_DEPARTMENT_DELETE";
    public static final String UPLOAD_DEPARTMENT_EXPORT = "UPLOAD_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String UPLOAD_WORKGROUP_READ = "UPLOAD_WORKGROUP_READ";
    public static final String UPLOAD_WORKGROUP_CREATE = "UPLOAD_WORKGROUP_CREATE";
    public static final String UPLOAD_WORKGROUP_UPDATE = "UPLOAD_WORKGROUP_UPDATE";
    public static final String UPLOAD_WORKGROUP_DELETE = "UPLOAD_WORKGROUP_DELETE";
    public static final String UPLOAD_WORKGROUP_EXPORT = "UPLOAD_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String UPLOAD_AGENT_READ = "UPLOAD_AGENT_READ";
    public static final String UPLOAD_AGENT_CREATE = "UPLOAD_AGENT_CREATE";
    public static final String UPLOAD_AGENT_UPDATE = "UPLOAD_AGENT_UPDATE";
    public static final String UPLOAD_AGENT_DELETE = "UPLOAD_AGENT_DELETE";
    public static final String UPLOAD_AGENT_EXPORT = "UPLOAD_AGENT_EXPORT";
    // 用户级权限
    public static final String UPLOAD_USER_READ = "UPLOAD_USER_READ";
    public static final String UPLOAD_USER_CREATE = "UPLOAD_USER_CREATE";
    public static final String UPLOAD_USER_UPDATE = "UPLOAD_USER_UPDATE";
    public static final String UPLOAD_USER_DELETE = "UPLOAD_USER_DELETE";
    public static final String UPLOAD_USER_EXPORT = "UPLOAD_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_UPLOAD_READ_ANY_LEVEL = "hasAnyAuthority('UPLOAD_PLATFORM_READ', 'UPLOAD_ORGANIZATION_READ', 'UPLOAD_DEPARTMENT_READ', 'UPLOAD_WORKGROUP_READ', 'UPLOAD_AGENT_READ', 'UPLOAD_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_UPLOAD_CREATE_ANY_LEVEL = "hasAnyAuthority('UPLOAD_PLATFORM_CREATE', 'UPLOAD_ORGANIZATION_CREATE', 'UPLOAD_DEPARTMENT_CREATE', 'UPLOAD_WORKGROUP_CREATE', 'UPLOAD_AGENT_CREATE', 'UPLOAD_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_UPLOAD_UPDATE_ANY_LEVEL = "hasAnyAuthority('UPLOAD_PLATFORM_UPDATE', 'UPLOAD_ORGANIZATION_UPDATE', 'UPLOAD_DEPARTMENT_UPDATE', 'UPLOAD_WORKGROUP_UPDATE', 'UPLOAD_AGENT_UPDATE', 'UPLOAD_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_UPLOAD_DELETE_ANY_LEVEL = "hasAnyAuthority('UPLOAD_PLATFORM_DELETE', 'UPLOAD_ORGANIZATION_DELETE', 'UPLOAD_DEPARTMENT_DELETE', 'UPLOAD_WORKGROUP_DELETE', 'UPLOAD_AGENT_DELETE', 'UPLOAD_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_UPLOAD_EXPORT_ANY_LEVEL = "hasAnyAuthority('UPLOAD_PLATFORM_EXPORT', 'UPLOAD_ORGANIZATION_EXPORT', 'UPLOAD_DEPARTMENT_EXPORT', 'UPLOAD_WORKGROUP_EXPORT', 'UPLOAD_AGENT_EXPORT', 'UPLOAD_USER_EXPORT')";

}