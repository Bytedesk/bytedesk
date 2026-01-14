/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:49:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unreplied;

import com.bytedesk.core.base.BasePermissions;

public class MessageUnrepliedPermissions extends BasePermissions {

    // 模块前缀
    public static final String MESSAGE_UNANSWERED_PREFIX = "MESSAGE_UNANSWERED_";

    // 统一权限（不区分层级）
    public static final String MESSAGE_UNANSWERED_READ = "MESSAGE_UNANSWERED_READ";
    public static final String MESSAGE_UNANSWERED_CREATE = "MESSAGE_UNANSWERED_CREATE";
    public static final String MESSAGE_UNANSWERED_UPDATE = "MESSAGE_UNANSWERED_UPDATE";
    public static final String MESSAGE_UNANSWERED_DELETE = "MESSAGE_UNANSWERED_DELETE";
    public static final String MESSAGE_UNANSWERED_EXPORT = "MESSAGE_UNANSWERED_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_MESSAGE_UNANSWERED_READ = "hasAuthority('MESSAGE_UNANSWERED_READ')";
    public static final String HAS_MESSAGE_UNANSWERED_CREATE = "hasAuthority('MESSAGE_UNANSWERED_CREATE')";
    public static final String HAS_MESSAGE_UNANSWERED_UPDATE = "hasAuthority('MESSAGE_UNANSWERED_UPDATE')";
    public static final String HAS_MESSAGE_UNANSWERED_DELETE = "hasAuthority('MESSAGE_UNANSWERED_DELETE')";
    public static final String HAS_MESSAGE_UNANSWERED_EXPORT = "hasAuthority('MESSAGE_UNANSWERED_EXPORT')";

}
