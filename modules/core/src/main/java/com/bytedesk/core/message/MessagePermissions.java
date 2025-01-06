/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:57:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 16:57:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

public class MessagePermissions {

    public static final String MESSAGE_PREFIX = "MESSAGE_";
    // Message permissions
    public static final String MESSAGE_CREATE = "hasAuthority('MESSAGE_CREATE')";
    public static final String MESSAGE_READ = "hasAuthority('MESSAGE_READ')";
    public static final String MESSAGE_UPDATE = "hasAuthority('MESSAGE_UPDATE')";
    public static final String MESSAGE_DELETE = "hasAuthority('MESSAGE_DELETE')";
    public static final String MESSAGE_EXPORT = "hasAuthority('MESSAGE_EXPORT')";

}