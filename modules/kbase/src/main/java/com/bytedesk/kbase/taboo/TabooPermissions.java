/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:42:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import com.bytedesk.core.base.BasePermissions;

public class TabooPermissions extends BasePermissions {

    // 模块前缀
    public static final String TABOO_PREFIX = "TABOO_";

    // 统一权限（不区分层级）
    public static final String TABOO_READ = "TABOO_READ";
    public static final String TABOO_CREATE = "TABOO_CREATE";
    public static final String TABOO_UPDATE = "TABOO_UPDATE";
    public static final String TABOO_DELETE = "TABOO_DELETE";
    public static final String TABOO_EXPORT = "TABOO_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_TABOO_READ = "hasAuthority('TABOO_READ')";
    public static final String HAS_TABOO_CREATE = "hasAuthority('TABOO_CREATE')";
    public static final String HAS_TABOO_UPDATE = "hasAuthority('TABOO_UPDATE')";
    public static final String HAS_TABOO_DELETE = "hasAuthority('TABOO_DELETE')";
    public static final String HAS_TABOO_EXPORT = "hasAuthority('TABOO_EXPORT')";

}