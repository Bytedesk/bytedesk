/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-22 12:12:04
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

    public static final String ROBOT_PREFIX = "ROBOT_";
    //
    public static final String ROBOT_CREATE = formatAuthority(ROBOT_PREFIX + CREATE);
    public static final String ROBOT_READ = formatAuthority(ROBOT_PREFIX + READ);
    public static final String ROBOT_UPDATE = formatAuthority(ROBOT_PREFIX + UPDATE);
    public static final String ROBOT_DELETE = formatAuthority(ROBOT_PREFIX + DELETE);
    public static final String ROBOT_EXPORT = formatAuthority(ROBOT_PREFIX + EXPORT);
    // 
    public static final String ROBOT_ANY = formatAnyAuthority(ROBOT_PREFIX + CREATE, ROBOT_PREFIX + READ, ROBOT_PREFIX + UPDATE, ROBOT_PREFIX + EXPORT, ROBOT_PREFIX + DELETE);

}
