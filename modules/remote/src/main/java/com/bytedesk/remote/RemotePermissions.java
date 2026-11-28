/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.remote;

import com.bytedesk.core.base.BasePermissions;

public class RemotePermissions extends BasePermissions {

    // 模块前缀
    public static final String REMOTE_PREFIX = "REMOTE_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "REMOTE";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String REMOTE_READ = "REMOTE_READ";
    public static final String REMOTE_CREATE = "REMOTE_CREATE";
    public static final String REMOTE_UPDATE = "REMOTE_UPDATE";
    public static final String REMOTE_DELETE = "REMOTE_DELETE";
    public static final String REMOTE_EXPORT = "REMOTE_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_REMOTE_READ = "hasAuthority('" + REMOTE_READ + "')";
    public static final String HAS_REMOTE_CREATE = "hasAuthority('" + REMOTE_CREATE + "')";
    public static final String HAS_REMOTE_UPDATE = "hasAuthority('" + REMOTE_UPDATE + "')";
    public static final String HAS_REMOTE_DELETE = "hasAuthority('" + REMOTE_DELETE + "')";
    public static final String HAS_REMOTE_EXPORT = "hasAuthority('" + REMOTE_EXPORT + "')";

}
