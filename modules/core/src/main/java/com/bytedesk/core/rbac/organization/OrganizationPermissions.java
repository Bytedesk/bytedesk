/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:57:29
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
package com.bytedesk.core.rbac.organization;

import com.bytedesk.core.base.BasePermissions;

public class OrganizationPermissions extends BasePermissions {

    // 模块前缀
    public static final String ORGANIZATION_PREFIX = "ORGANIZATION_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String ORGANIZATION_READ = "ORGANIZATION_READ";
    public static final String ORGANIZATION_CREATE = "ORGANIZATION_CREATE";
    public static final String ORGANIZATION_UPDATE = "ORGANIZATION_UPDATE";
    public static final String ORGANIZATION_DELETE = "ORGANIZATION_DELETE";
    public static final String ORGANIZATION_EXPORT = "ORGANIZATION_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_ORGANIZATION_READ = "hasAuthority('ORGANIZATION_READ')";
    public static final String HAS_ORGANIZATION_CREATE = "hasAuthority('ORGANIZATION_CREATE')";
    public static final String HAS_ORGANIZATION_UPDATE = "hasAuthority('ORGANIZATION_UPDATE')";
    public static final String HAS_ORGANIZATION_DELETE = "hasAuthority('ORGANIZATION_DELETE')";
    public static final String HAS_ORGANIZATION_EXPORT = "hasAuthority('ORGANIZATION_EXPORT')";

}