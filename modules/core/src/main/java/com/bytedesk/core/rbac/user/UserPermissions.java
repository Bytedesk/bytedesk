/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:56:40
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
package com.bytedesk.core.rbac.user;

import com.bytedesk.core.base.BasePermissions;

public class UserPermissions extends BasePermissions {

    // 模块前缀
    public static final String USER_PREFIX = "USER_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String USER_READ = "USER_READ";
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_EXPORT = "USER_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_USER_READ = "hasAuthority('USER_READ')";
    public static final String HAS_USER_CREATE = "hasAuthority('USER_CREATE')";
    public static final String HAS_USER_UPDATE = "hasAuthority('USER_UPDATE')";
    public static final String HAS_USER_DELETE = "hasAuthority('USER_DELETE')";
    public static final String HAS_USER_EXPORT = "hasAuthority('USER_EXPORT')";

}