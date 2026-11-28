/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import com.bytedesk.core.base.BasePermissions;

public class ThreadProcessPermissions extends BasePermissions {

    // 模块前缀
    public static final String THREAD_PROCESS_PREFIX = "THREAD_PROCESS_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "THREAD_PROCESS";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String THREAD_PROCESS_READ = "THREAD_PROCESS_READ";
    public static final String THREAD_PROCESS_CREATE = "THREAD_PROCESS_CREATE";
    public static final String THREAD_PROCESS_UPDATE = "THREAD_PROCESS_UPDATE";
    public static final String THREAD_PROCESS_DELETE = "THREAD_PROCESS_DELETE";
    public static final String THREAD_PROCESS_EXPORT = "THREAD_PROCESS_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_THREAD_PROCESS_READ = "hasAuthority('" + THREAD_PROCESS_READ + "')";
    public static final String HAS_THREAD_PROCESS_CREATE = "hasAuthority('" + THREAD_PROCESS_CREATE + "')";
    public static final String HAS_THREAD_PROCESS_UPDATE = "hasAuthority('" + THREAD_PROCESS_UPDATE + "')";
    public static final String HAS_THREAD_PROCESS_DELETE = "hasAuthority('" + THREAD_PROCESS_DELETE + "')";
    public static final String HAS_THREAD_PROCESS_EXPORT = "hasAuthority('" + THREAD_PROCESS_EXPORT + "')";

}
