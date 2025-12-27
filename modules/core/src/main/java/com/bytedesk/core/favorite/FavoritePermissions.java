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
package com.bytedesk.core.favorite;

import com.bytedesk.core.base.BasePermissions;

public class FavoritePermissions extends BasePermissions {

    // 模块前缀
    public static final String FAVORITE_PREFIX = "FAVORITE_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String FAVORITE_READ = "FAVORITE_READ";
    public static final String FAVORITE_CREATE = "FAVORITE_CREATE";
    public static final String FAVORITE_UPDATE = "FAVORITE_UPDATE";
    public static final String FAVORITE_DELETE = "FAVORITE_DELETE";
    public static final String FAVORITE_EXPORT = "FAVORITE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_FAVORITE_READ = "hasAuthority('FAVORITE_READ')";
    public static final String HAS_FAVORITE_CREATE = "hasAuthority('FAVORITE_CREATE')";
    public static final String HAS_FAVORITE_UPDATE = "hasAuthority('FAVORITE_UPDATE')";
    public static final String HAS_FAVORITE_DELETE = "hasAuthority('FAVORITE_DELETE')";
    public static final String HAS_FAVORITE_EXPORT = "hasAuthority('FAVORITE_EXPORT')";

}