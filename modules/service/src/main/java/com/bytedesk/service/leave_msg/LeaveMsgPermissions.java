/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:55:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg;

public class LeaveMsgPermissions {

    public static final String LEAVEMSG_PREFIX = "LEAVEMSG_";
    // LeaveMsg permissions
    public static final String LEAVEMSG_CREATE = "hasAuthority('LEAVEMSG_CREATE')";
    public static final String LEAVEMSG_READ = "hasAuthority('LEAVEMSG_READ')";
    public static final String LEAVEMSG_UPDATE = "hasAuthority('LEAVEMSG_UPDATE')";
    public static final String LEAVEMSG_DELETE = "hasAuthority('LEAVEMSG_DELETE')";
    public static final String LEAVEMSG_EXPORT = "hasAuthority('LEAVEMSG_EXPORT')";
}