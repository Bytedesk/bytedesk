/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 09:47:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.url;

import com.bytedesk.core.base.BasePermissions;

public class UrlPermissions extends BasePermissions {

    public static final String URL_PREFIX = "URL_";

    public static final String MODULE_NAME = "URL";

    // 统一权限（不区分层级）
    public static final String URL_READ = "URL_READ";
    public static final String URL_CREATE = "URL_CREATE";
    public static final String URL_UPDATE = "URL_UPDATE";
    public static final String URL_DELETE = "URL_DELETE";
    public static final String URL_EXPORT = "URL_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_URL_READ = "hasAuthority('" + URL_READ + "')";
    public static final String HAS_URL_CREATE = "hasAuthority('" + URL_CREATE + "')";
    public static final String HAS_URL_UPDATE = "hasAuthority('" + URL_UPDATE + "')";
    public static final String HAS_URL_DELETE = "hasAuthority('" + URL_DELETE + "')";
    public static final String HAS_URL_EXPORT = "hasAuthority('" + URL_EXPORT + "')";
    
}