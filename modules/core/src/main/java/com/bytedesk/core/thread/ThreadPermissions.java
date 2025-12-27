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
package com.bytedesk.core.thread;

import com.bytedesk.core.base.BasePermissions;

public class ThreadPermissions extends BasePermissions {

    // 模块前缀
    public static final String THREAD_PREFIX = "THREAD_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String THREAD_READ = "THREAD_READ";
    public static final String THREAD_CREATE = "THREAD_CREATE";
    public static final String THREAD_UPDATE = "THREAD_UPDATE";
    public static final String THREAD_DELETE = "THREAD_DELETE";
    public static final String THREAD_EXPORT = "THREAD_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_THREAD_READ = "hasAuthority('THREAD_READ')";
    public static final String HAS_THREAD_CREATE = "hasAuthority('THREAD_CREATE')";
    public static final String HAS_THREAD_UPDATE = "hasAuthority('THREAD_UPDATE')";
    public static final String HAS_THREAD_DELETE = "hasAuthority('THREAD_DELETE')";
    public static final String HAS_THREAD_EXPORT = "hasAuthority('THREAD_EXPORT')";

}
