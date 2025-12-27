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
package com.bytedesk.service.message_leave;

import com.bytedesk.core.base.BasePermissions;

public class MessageLeavePermissions extends BasePermissions {

    // 模块前缀
    public static final String MESSAGE_LEAVE_PREFIX = "MESSAGE_LEAVE_";

    // 统一权限（不区分层级）
    public static final String MESSAGE_LEAVE_READ = "MESSAGE_LEAVE_READ";
    public static final String MESSAGE_LEAVE_CREATE = "MESSAGE_LEAVE_CREATE";
    public static final String MESSAGE_LEAVE_UPDATE = "MESSAGE_LEAVE_UPDATE";
    public static final String MESSAGE_LEAVE_DELETE = "MESSAGE_LEAVE_DELETE";
    public static final String MESSAGE_LEAVE_EXPORT = "MESSAGE_LEAVE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_MESSAGE_LEAVE_READ = "hasAuthority('MESSAGE_LEAVE_READ')";
    public static final String HAS_MESSAGE_LEAVE_CREATE = "hasAuthority('MESSAGE_LEAVE_CREATE')";
    public static final String HAS_MESSAGE_LEAVE_UPDATE = "hasAuthority('MESSAGE_LEAVE_UPDATE')";
    public static final String HAS_MESSAGE_LEAVE_DELETE = "hasAuthority('MESSAGE_LEAVE_DELETE')";
    public static final String HAS_MESSAGE_LEAVE_EXPORT = "hasAuthority('MESSAGE_LEAVE_EXPORT')";

}