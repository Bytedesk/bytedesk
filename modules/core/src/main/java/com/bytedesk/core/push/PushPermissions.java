/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:08:01
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
package com.bytedesk.core.push;

import com.bytedesk.core.base.BasePermissions;

public class PushPermissions extends BasePermissions {

    // 模块前缀
    public static final String PUSH_PREFIX = "PUSH_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String PUSH_READ = "PUSH_READ";
    public static final String PUSH_CREATE = "PUSH_CREATE";
    public static final String PUSH_UPDATE = "PUSH_UPDATE";
    public static final String PUSH_DELETE = "PUSH_DELETE";
    public static final String PUSH_EXPORT = "PUSH_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_PUSH_READ = "hasAuthority('PUSH_READ')";
    public static final String HAS_PUSH_CREATE = "hasAuthority('PUSH_CREATE')";
    public static final String HAS_PUSH_UPDATE = "hasAuthority('PUSH_UPDATE')";
    public static final String HAS_PUSH_DELETE = "hasAuthority('PUSH_DELETE')";
    public static final String HAS_PUSH_EXPORT = "hasAuthority('PUSH_EXPORT')";

}