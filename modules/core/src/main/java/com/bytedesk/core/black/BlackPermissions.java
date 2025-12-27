/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:16
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
package com.bytedesk.core.black;

import com.bytedesk.core.base.BasePermissions;

public class BlackPermissions extends BasePermissions {

    // 模块前缀
    public static final String BLACK_PREFIX = "BLACK_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String BLACK_READ = "BLACK_READ";
    public static final String BLACK_CREATE = "BLACK_CREATE";
    public static final String BLACK_UPDATE = "BLACK_UPDATE";
    public static final String BLACK_DELETE = "BLACK_DELETE";
    public static final String BLACK_EXPORT = "BLACK_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_BLACK_READ = "hasAuthority('BLACK_READ')";
    public static final String HAS_BLACK_CREATE = "hasAuthority('BLACK_CREATE')";
    public static final String HAS_BLACK_UPDATE = "hasAuthority('BLACK_UPDATE')";
    public static final String HAS_BLACK_DELETE = "hasAuthority('BLACK_DELETE')";
    public static final String HAS_BLACK_EXPORT = "hasAuthority('BLACK_EXPORT')";

}