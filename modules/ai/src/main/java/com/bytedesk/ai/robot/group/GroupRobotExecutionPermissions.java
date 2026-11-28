/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.ai.robot.group;

import com.bytedesk.core.base.BasePermissions;

public class GroupRobotExecutionPermissions extends BasePermissions {

    // 模块前缀
    public static final String GROUP_ROBOT_EXECUTION_PREFIX = "GROUP_ROBOT_EXECUTION_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "GROUP_ROBOT_EXECUTION";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String GROUP_ROBOT_EXECUTION_READ = "GROUP_ROBOT_EXECUTION_READ";
    public static final String GROUP_ROBOT_EXECUTION_CREATE = "GROUP_ROBOT_EXECUTION_CREATE";
    public static final String GROUP_ROBOT_EXECUTION_UPDATE = "GROUP_ROBOT_EXECUTION_UPDATE";
    public static final String GROUP_ROBOT_EXECUTION_DELETE = "GROUP_ROBOT_EXECUTION_DELETE";
    public static final String GROUP_ROBOT_EXECUTION_EXPORT = "GROUP_ROBOT_EXECUTION_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_GROUP_ROBOT_EXECUTION_READ = "hasAuthority('" + GROUP_ROBOT_EXECUTION_READ + "')";
    public static final String HAS_GROUP_ROBOT_EXECUTION_CREATE = "hasAuthority('" + GROUP_ROBOT_EXECUTION_CREATE + "')";
    public static final String HAS_GROUP_ROBOT_EXECUTION_UPDATE = "hasAuthority('" + GROUP_ROBOT_EXECUTION_UPDATE + "')";
    public static final String HAS_GROUP_ROBOT_EXECUTION_DELETE = "hasAuthority('" + GROUP_ROBOT_EXECUTION_DELETE + "')";
    public static final String HAS_GROUP_ROBOT_EXECUTION_EXPORT = "hasAuthority('" + GROUP_ROBOT_EXECUTION_EXPORT + "')";

}
