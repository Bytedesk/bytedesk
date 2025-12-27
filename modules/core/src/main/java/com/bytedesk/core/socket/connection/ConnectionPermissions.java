/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
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
package com.bytedesk.core.socket.connection;

import com.bytedesk.core.base.BasePermissions;

public class ConnectionPermissions extends BasePermissions {

    // 模块前缀
    public static final String CONNECTION_PREFIX = "CONNECTION_";

    // 统一权限（不区分层级）
    public static final String CONNECTION_READ = "CONNECTION_READ";
    public static final String CONNECTION_CREATE = "CONNECTION_CREATE";
    public static final String CONNECTION_UPDATE = "CONNECTION_UPDATE";
    public static final String CONNECTION_DELETE = "CONNECTION_DELETE";
    public static final String CONNECTION_EXPORT = "CONNECTION_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_CONNECTION_READ = "hasAuthority('CONNECTION_READ')";
    public static final String HAS_CONNECTION_CREATE = "hasAuthority('CONNECTION_CREATE')";
    public static final String HAS_CONNECTION_UPDATE = "hasAuthority('CONNECTION_UPDATE')";
    public static final String HAS_CONNECTION_DELETE = "hasAuthority('CONNECTION_DELETE')";
    public static final String HAS_CONNECTION_EXPORT = "hasAuthority('CONNECTION_EXPORT')";

}
