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
package com.bytedesk.ai.robot;

import com.bytedesk.core.base.BasePermissions;

public class RobotPermissions extends BasePermissions {

    // 模块前缀
    public static final String ROBOT_PREFIX = "ROBOT_";

    // 统一权限（不区分层级）
    public static final String ROBOT_READ = "ROBOT_READ";
    public static final String ROBOT_CREATE = "ROBOT_CREATE";
    public static final String ROBOT_UPDATE = "ROBOT_UPDATE";
    public static final String ROBOT_DELETE = "ROBOT_DELETE";
    public static final String ROBOT_EXPORT = "ROBOT_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_ROBOT_READ = "hasAuthority('ROBOT_READ')";
    public static final String HAS_ROBOT_CREATE = "hasAuthority('ROBOT_CREATE')";
    public static final String HAS_ROBOT_UPDATE = "hasAuthority('ROBOT_UPDATE')";
    public static final String HAS_ROBOT_DELETE = "hasAuthority('ROBOT_DELETE')";
    public static final String HAS_ROBOT_EXPORT = "hasAuthority('ROBOT_EXPORT')";

}
