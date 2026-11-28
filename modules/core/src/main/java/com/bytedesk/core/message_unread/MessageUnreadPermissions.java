/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import com.bytedesk.core.base.BasePermissions;

public class MessageUnreadPermissions extends BasePermissions {

    // 模块前缀
    public static final String MESSAGE_UNREAD_PREFIX = "MESSAGE_UNREAD_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "MESSAGE_UNREAD";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String MESSAGE_UNREAD_READ = "MESSAGE_UNREAD_READ";
    public static final String MESSAGE_UNREAD_CREATE = "MESSAGE_UNREAD_CREATE";
    public static final String MESSAGE_UNREAD_UPDATE = "MESSAGE_UNREAD_UPDATE";
    public static final String MESSAGE_UNREAD_DELETE = "MESSAGE_UNREAD_DELETE";
    public static final String MESSAGE_UNREAD_EXPORT = "MESSAGE_UNREAD_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_MESSAGE_UNREAD_READ = "hasAuthority('" + MESSAGE_UNREAD_READ + "')";
    public static final String HAS_MESSAGE_UNREAD_CREATE = "hasAuthority('" + MESSAGE_UNREAD_CREATE + "')";
    public static final String HAS_MESSAGE_UNREAD_UPDATE = "hasAuthority('" + MESSAGE_UNREAD_UPDATE + "')";
    public static final String HAS_MESSAGE_UNREAD_DELETE = "hasAuthority('" + MESSAGE_UNREAD_DELETE + "')";
    public static final String HAS_MESSAGE_UNREAD_EXPORT = "hasAuthority('" + MESSAGE_UNREAD_EXPORT + "')";

}
