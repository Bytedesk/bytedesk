/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BasePermissions;

public class MessagePermissions extends BasePermissions {

    // 模块前缀
    public static final String MESSAGE_PREFIX = "MESSAGE_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String MESSAGE_READ = "MESSAGE_READ";
    public static final String MESSAGE_CREATE = "MESSAGE_CREATE";
    public static final String MESSAGE_UPDATE = "MESSAGE_UPDATE";
    public static final String MESSAGE_DELETE = "MESSAGE_DELETE";
    public static final String MESSAGE_EXPORT = "MESSAGE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_MESSAGE_READ = "hasAuthority('MESSAGE_READ')";
    public static final String HAS_MESSAGE_CREATE = "hasAuthority('MESSAGE_CREATE')";
    public static final String HAS_MESSAGE_UPDATE = "hasAuthority('MESSAGE_UPDATE')";
    public static final String HAS_MESSAGE_DELETE = "hasAuthority('MESSAGE_DELETE')";
    public static final String HAS_MESSAGE_EXPORT = "hasAuthority('MESSAGE_EXPORT')";

}
