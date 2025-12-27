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
package com.bytedesk.core.quartz_task;

import com.bytedesk.core.base.BasePermissions;

public class QuartzTaskPermissions extends BasePermissions {

    // 模块前缀
    public static final String QUARTZ_TASK_PREFIX = "QUARTZ_TASK_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String QUARTZ_TASK_READ = "QUARTZ_TASK_READ";
    public static final String QUARTZ_TASK_CREATE = "QUARTZ_TASK_CREATE";
    public static final String QUARTZ_TASK_UPDATE = "QUARTZ_TASK_UPDATE";
    public static final String QUARTZ_TASK_DELETE = "QUARTZ_TASK_DELETE";
    public static final String QUARTZ_TASK_EXPORT = "QUARTZ_TASK_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_QUARTZ_TASK_READ = "hasAuthority('QUARTZ_TASK_READ')";
    public static final String HAS_QUARTZ_TASK_CREATE = "hasAuthority('QUARTZ_TASK_CREATE')";
    public static final String HAS_QUARTZ_TASK_UPDATE = "hasAuthority('QUARTZ_TASK_UPDATE')";
    public static final String HAS_QUARTZ_TASK_DELETE = "hasAuthority('QUARTZ_TASK_DELETE')";
    public static final String HAS_QUARTZ_TASK_EXPORT = "hasAuthority('QUARTZ_TASK_EXPORT')";

}
