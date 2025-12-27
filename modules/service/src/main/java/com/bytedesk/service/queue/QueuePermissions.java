/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
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
package com.bytedesk.service.queue;

import com.bytedesk.core.base.BasePermissions;

public class QueuePermissions extends BasePermissions {

    // 模块前缀
    public static final String QUEUE_PREFIX = "QUEUE_";

    // 统一权限（不区分层级）
    public static final String QUEUE_READ = "QUEUE_READ";
    public static final String QUEUE_CREATE = "QUEUE_CREATE";
    public static final String QUEUE_UPDATE = "QUEUE_UPDATE";
    public static final String QUEUE_DELETE = "QUEUE_DELETE";
    public static final String QUEUE_EXPORT = "QUEUE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_QUEUE_READ = "hasAuthority('QUEUE_READ')";
    public static final String HAS_QUEUE_CREATE = "hasAuthority('QUEUE_CREATE')";
    public static final String HAS_QUEUE_UPDATE = "hasAuthority('QUEUE_UPDATE')";
    public static final String HAS_QUEUE_DELETE = "hasAuthority('QUEUE_DELETE')";
    public static final String HAS_QUEUE_EXPORT = "hasAuthority('QUEUE_EXPORT')";

}