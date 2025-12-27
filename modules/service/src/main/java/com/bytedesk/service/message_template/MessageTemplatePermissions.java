/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_template;

import com.bytedesk.core.base.BasePermissions;

public class MessageTemplatePermissions extends BasePermissions {

    // 模块前缀
    public static final String MESSAGE_TEMPLATE_PREFIX = "MESSAGE_TEMPLATE_";

    // 统一权限（不区分层级）
    public static final String MESSAGE_TEMPLATE_READ = "MESSAGE_TEMPLATE_READ";
    public static final String MESSAGE_TEMPLATE_CREATE = "MESSAGE_TEMPLATE_CREATE";
    public static final String MESSAGE_TEMPLATE_UPDATE = "MESSAGE_TEMPLATE_UPDATE";
    public static final String MESSAGE_TEMPLATE_DELETE = "MESSAGE_TEMPLATE_DELETE";
    public static final String MESSAGE_TEMPLATE_EXPORT = "MESSAGE_TEMPLATE_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_MESSAGE_TEMPLATE_READ = "hasAuthority('MESSAGE_TEMPLATE_READ')";
    public static final String HAS_MESSAGE_TEMPLATE_CREATE = "hasAuthority('MESSAGE_TEMPLATE_CREATE')";
    public static final String HAS_MESSAGE_TEMPLATE_UPDATE = "hasAuthority('MESSAGE_TEMPLATE_UPDATE')";
    public static final String HAS_MESSAGE_TEMPLATE_DELETE = "hasAuthority('MESSAGE_TEMPLATE_DELETE')";
    public static final String HAS_MESSAGE_TEMPLATE_EXPORT = "hasAuthority('MESSAGE_TEMPLATE_EXPORT')";

}
