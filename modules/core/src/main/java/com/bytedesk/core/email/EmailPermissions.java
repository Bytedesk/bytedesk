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
package com.bytedesk.core.email;

import com.bytedesk.core.base.BasePermissions;

public class EmailPermissions extends BasePermissions {

    // 模块前缀
    public static final String EMAIL_PREFIX = "EMAIL_";

    // 统一权限（不区分层级）
    public static final String EMAIL_READ = "EMAIL_READ";
    public static final String EMAIL_CREATE = "EMAIL_CREATE";
    public static final String EMAIL_UPDATE = "EMAIL_UPDATE";
    public static final String EMAIL_DELETE = "EMAIL_DELETE";
    public static final String EMAIL_EXPORT = "EMAIL_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_EMAIL_READ = "hasAuthority('EMAIL_READ')";
    public static final String HAS_EMAIL_CREATE = "hasAuthority('EMAIL_CREATE')";
    public static final String HAS_EMAIL_UPDATE = "hasAuthority('EMAIL_UPDATE')";
    public static final String HAS_EMAIL_DELETE = "hasAuthority('EMAIL_DELETE')";
    public static final String HAS_EMAIL_EXPORT = "hasAuthority('EMAIL_EXPORT')";

}
