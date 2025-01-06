/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:55:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

public class WorktimePermissions {

    public static final String WORKTIME_PREFIX = "WORKTIME_";
    // Worktime permissions
    public static final String WORKTIME_CREATE = "hasAuthority('WORKTIME_CREATE')";
    public static final String WORKTIME_READ = "hasAuthority('WORKTIME_READ')";
    public static final String WORKTIME_UPDATE = "hasAuthority('WORKTIME_UPDATE')";
    public static final String WORKTIME_DELETE = "hasAuthority('WORKTIME_DELETE')";
    public static final String WORKTIME_EXPORT = "hasAuthority('WORKTIME_EXPORT')";
}