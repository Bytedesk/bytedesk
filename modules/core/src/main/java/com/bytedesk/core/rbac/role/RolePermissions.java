/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 21:13:47
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
package com.bytedesk.core.rbac.role;

import com.bytedesk.core.base.BasePermissions;

public class RolePermissions extends BasePermissions {

    // 模块前缀
    public static final String ROLE_PREFIX = "ROLE_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String ROLE_READ = "ROLE_READ";
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_UPDATE = "ROLE_UPDATE";
    public static final String ROLE_DELETE = "ROLE_DELETE";
    public static final String ROLE_EXPORT = "ROLE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_ROLE_READ = "hasAuthority('ROLE_READ')";
    public static final String HAS_ROLE_CREATE = "hasAuthority('ROLE_CREATE')";
    public static final String HAS_ROLE_UPDATE = "hasAuthority('ROLE_UPDATE')";
    public static final String HAS_ROLE_DELETE = "hasAuthority('ROLE_DELETE')";
    public static final String HAS_ROLE_EXPORT = "hasAuthority('ROLE_EXPORT')";

    // 角色检查常量
    public static final String ROLE_SUPER = "hasRole('SUPER')";
    public static final String ROLE_ADMIN = "hasAnyRole('SUPER', 'ADMIN')";
    public static final String ROLE_AGENT = "hasAnyRole('SUPER', 'ADMIN', 'AGENT')";

}
