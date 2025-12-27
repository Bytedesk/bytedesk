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
package com.bytedesk.core.task;

import com.bytedesk.core.base.BasePermissions;

public class TaskPermissions extends BasePermissions {

    // 模块前缀
    public static final String TASK_PREFIX = "TASK_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String TASK_READ = "TASK_READ";
    public static final String TASK_CREATE = "TASK_CREATE";
    public static final String TASK_UPDATE = "TASK_UPDATE";
    public static final String TASK_DELETE = "TASK_DELETE";
    public static final String TASK_EXPORT = "TASK_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_TASK_READ = "hasAuthority('TASK_READ')";
    public static final String HAS_TASK_CREATE = "hasAuthority('TASK_CREATE')";
    public static final String HAS_TASK_UPDATE = "hasAuthority('TASK_UPDATE')";
    public static final String HAS_TASK_DELETE = "hasAuthority('TASK_DELETE')";
    public static final String HAS_TASK_EXPORT = "hasAuthority('TASK_EXPORT')";

}
