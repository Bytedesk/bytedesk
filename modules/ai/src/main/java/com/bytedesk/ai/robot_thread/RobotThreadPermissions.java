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
package com.bytedesk.ai.robot_thread;

import com.bytedesk.core.base.BasePermissions;

/**
 * Robot Thread权限控制
 */
public class RobotThreadPermissions extends BasePermissions {

    public static final String ROBOTTHREAD_PREFIX = "ROBOTTHREAD_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String ROBOTTHREAD_READ = "ROBOTTHREAD_READ";
    public static final String ROBOTTHREAD_CREATE = "ROBOTTHREAD_CREATE";
    public static final String ROBOTTHREAD_UPDATE = "ROBOTTHREAD_UPDATE";
    public static final String ROBOTTHREAD_DELETE = "ROBOTTHREAD_DELETE";
    public static final String ROBOTTHREAD_EXPORT = "ROBOTTHREAD_EXPORT";

    // PreAuthorize 注解使用的 SpEL 表达式
    public static final String HAS_ROBOTTHREAD_READ = "hasAuthority('ROBOTTHREAD_READ')";
    public static final String HAS_ROBOTTHREAD_CREATE = "hasAuthority('ROBOTTHREAD_CREATE')";
    public static final String HAS_ROBOTTHREAD_UPDATE = "hasAuthority('ROBOTTHREAD_UPDATE')";
    public static final String HAS_ROBOTTHREAD_DELETE = "hasAuthority('ROBOTTHREAD_DELETE')";
    public static final String HAS_ROBOTTHREAD_EXPORT = "hasAuthority('ROBOTTHREAD_EXPORT')";
}