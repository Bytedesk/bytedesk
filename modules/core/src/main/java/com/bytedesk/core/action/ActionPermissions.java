/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:08:27
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
package com.bytedesk.core.action;

import com.bytedesk.core.base.BasePermissions;

public class ActionPermissions extends BasePermissions {
    
    // 模块前缀
    public static final String ACTION_PREFIX = "ACTION_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String ACTION_READ = "ACTION_READ";
    public static final String ACTION_CREATE = "ACTION_CREATE";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_EXPORT = "ACTION_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_ACTION_READ = "hasAuthority('ACTION_READ')";
    public static final String HAS_ACTION_CREATE = "hasAuthority('ACTION_CREATE')";
    public static final String HAS_ACTION_UPDATE = "hasAuthority('ACTION_UPDATE')";
    public static final String HAS_ACTION_DELETE = "hasAuthority('ACTION_DELETE')";
    public static final String HAS_ACTION_EXPORT = "hasAuthority('ACTION_EXPORT')";

}
