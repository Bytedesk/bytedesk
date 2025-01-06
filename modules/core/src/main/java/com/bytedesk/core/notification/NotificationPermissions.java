/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-03 17:09:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

public class NotificationPermissions {

    public static final String NOTIFICATION_PREFIX = "NOTIFICATION_";
    // Notification permissions
    public static final String NOTIFICATION_CREATE = "hasAuthority('NOTIFICATION_CREATE')";
    public static final String NOTIFICATION_READ = "hasAuthority('NOTIFICATION_READ')";
    public static final String NOTIFICATION_UPDATE = "hasAuthority('NOTIFICATION_UPDATE')";
    public static final String NOTIFICATION_DELETE = "hasAuthority('NOTIFICATION_DELETE')";
    public static final String NOTIFICATION_EXPORT = "hasAuthority('NOTIFICATION_EXPORT')";
}
