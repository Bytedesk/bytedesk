/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 16:01:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import com.bytedesk.core.base.BasePermissions;

public class VisitorThreadPermissions extends BasePermissions {

    // 模块前缀
    public static final String VISITOR_THREAD_PREFIX = "VISITOR_THREAD_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String VISITOR_THREAD_READ = "VISITOR_THREAD_READ";
    public static final String VISITOR_THREAD_CREATE = "VISITOR_THREAD_CREATE";
    public static final String VISITOR_THREAD_UPDATE = "VISITOR_THREAD_UPDATE";
    public static final String VISITOR_THREAD_DELETE = "VISITOR_THREAD_DELETE";
    public static final String VISITOR_THREAD_EXPORT = "VISITOR_THREAD_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_VISITOR_THREAD_READ = "hasAuthority('VISITOR_THREAD_READ')";
    public static final String HAS_VISITOR_THREAD_CREATE = "hasAuthority('VISITOR_THREAD_CREATE')";
    public static final String HAS_VISITOR_THREAD_UPDATE = "hasAuthority('VISITOR_THREAD_UPDATE')";
    public static final String HAS_VISITOR_THREAD_DELETE = "hasAuthority('VISITOR_THREAD_DELETE')";
    public static final String HAS_VISITOR_THREAD_EXPORT = "hasAuthority('VISITOR_THREAD_EXPORT')";

}