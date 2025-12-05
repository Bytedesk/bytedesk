/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 13:40:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import com.bytedesk.core.base.BasePermissions;

public class ChunkPermissions extends BasePermissions {

    // 模块前缀
    public static final String CHUNK_PREFIX = "CHUNK_";

    // 平台级权限
    public static final String CHUNK_PLATFORM_READ = "CHUNK_PLATFORM_READ";
    public static final String CHUNK_PLATFORM_CREATE = "CHUNK_PLATFORM_CREATE";
    public static final String CHUNK_PLATFORM_UPDATE = "CHUNK_PLATFORM_UPDATE";
    public static final String CHUNK_PLATFORM_DELETE = "CHUNK_PLATFORM_DELETE";
    public static final String CHUNK_PLATFORM_EXPORT = "CHUNK_PLATFORM_EXPORT";

    // 组织级权限
    public static final String CHUNK_ORGANIZATION_READ = "CHUNK_ORGANIZATION_READ";
    public static final String CHUNK_ORGANIZATION_CREATE = "CHUNK_ORGANIZATION_CREATE";
    public static final String CHUNK_ORGANIZATION_UPDATE = "CHUNK_ORGANIZATION_UPDATE";
    public static final String CHUNK_ORGANIZATION_DELETE = "CHUNK_ORGANIZATION_DELETE";
    public static final String CHUNK_ORGANIZATION_EXPORT = "CHUNK_ORGANIZATION_EXPORT";

    // 部门级权限
    public static final String CHUNK_DEPARTMENT_READ = "CHUNK_DEPARTMENT_READ";
    public static final String CHUNK_DEPARTMENT_CREATE = "CHUNK_DEPARTMENT_CREATE";
    public static final String CHUNK_DEPARTMENT_UPDATE = "CHUNK_DEPARTMENT_UPDATE";
    public static final String CHUNK_DEPARTMENT_DELETE = "CHUNK_DEPARTMENT_DELETE";
    public static final String CHUNK_DEPARTMENT_EXPORT = "CHUNK_DEPARTMENT_EXPORT";

    // 工作组级权限
    public static final String CHUNK_WORKGROUP_READ = "CHUNK_WORKGROUP_READ";
    public static final String CHUNK_WORKGROUP_CREATE = "CHUNK_WORKGROUP_CREATE";
    public static final String CHUNK_WORKGROUP_UPDATE = "CHUNK_WORKGROUP_UPDATE";
    public static final String CHUNK_WORKGROUP_DELETE = "CHUNK_WORKGROUP_DELETE";
    public static final String CHUNK_WORKGROUP_EXPORT = "CHUNK_WORKGROUP_EXPORT";

    // 客服级权限
    public static final String CHUNK_AGENT_READ = "CHUNK_AGENT_READ";
    public static final String CHUNK_AGENT_CREATE = "CHUNK_AGENT_CREATE";
    public static final String CHUNK_AGENT_UPDATE = "CHUNK_AGENT_UPDATE";
    public static final String CHUNK_AGENT_DELETE = "CHUNK_AGENT_DELETE";
    public static final String CHUNK_AGENT_EXPORT = "CHUNK_AGENT_EXPORT";
    // 用户级权限
    public static final String CHUNK_USER_READ = "CHUNK_USER_READ";
    public static final String CHUNK_USER_CREATE = "CHUNK_USER_CREATE";
    public static final String CHUNK_USER_UPDATE = "CHUNK_USER_UPDATE";
    public static final String CHUNK_USER_DELETE = "CHUNK_USER_DELETE";
    public static final String CHUNK_USER_EXPORT = "CHUNK_USER_EXPORT";


    // PreAuthorize 表达式 - 读取权限（允许多层级访问）
    public static final String HAS_CHUNK_READ_ANY_LEVEL = "hasAnyAuthority('CHUNK_PLATFORM_READ', 'CHUNK_ORGANIZATION_READ', 'CHUNK_DEPARTMENT_READ', 'CHUNK_WORKGROUP_READ', 'CHUNK_AGENT_READ', 'CHUNK_USER_READ')";
    
    // PreAuthorize 表达式 - 创建权限（允许多层级访问）
    public static final String HAS_CHUNK_CREATE_ANY_LEVEL = "hasAnyAuthority('CHUNK_PLATFORM_CREATE', 'CHUNK_ORGANIZATION_CREATE', 'CHUNK_DEPARTMENT_CREATE', 'CHUNK_WORKGROUP_CREATE', 'CHUNK_AGENT_CREATE', 'CHUNK_USER_CREATE')";
    
    // PreAuthorize 表达式 - 更新权限（允许多层级访问）
    public static final String HAS_CHUNK_UPDATE_ANY_LEVEL = "hasAnyAuthority('CHUNK_PLATFORM_UPDATE', 'CHUNK_ORGANIZATION_UPDATE', 'CHUNK_DEPARTMENT_UPDATE', 'CHUNK_WORKGROUP_UPDATE', 'CHUNK_AGENT_UPDATE', 'CHUNK_USER_UPDATE')";
    
    // PreAuthorize 表达式 - 删除权限（允许多层级访问）
    public static final String HAS_CHUNK_DELETE_ANY_LEVEL = "hasAnyAuthority('CHUNK_PLATFORM_DELETE', 'CHUNK_ORGANIZATION_DELETE', 'CHUNK_DEPARTMENT_DELETE', 'CHUNK_WORKGROUP_DELETE', 'CHUNK_AGENT_DELETE', 'CHUNK_USER_DELETE')";
    
    // PreAuthorize 表达式 - 导出权限（允许多层级访问）
    public static final String HAS_CHUNK_EXPORT_ANY_LEVEL = "hasAnyAuthority('CHUNK_PLATFORM_EXPORT', 'CHUNK_ORGANIZATION_EXPORT', 'CHUNK_DEPARTMENT_EXPORT', 'CHUNK_WORKGROUP_EXPORT', 'CHUNK_AGENT_EXPORT', 'CHUNK_USER_EXPORT')";

}