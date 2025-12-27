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
package com.bytedesk.kbase.quick_reply;

import com.bytedesk.core.base.BasePermissions;

public class QuickReplyPermissions extends BasePermissions {

    // 模块前缀
    public static final String QUICK_REPLY_PREFIX = "QUICK_REPLY_";

    // 统一权限（不区分层级）
    public static final String QUICK_REPLY_READ = "QUICK_REPLY_READ";
    public static final String QUICK_REPLY_CREATE = "QUICK_REPLY_CREATE";
    public static final String QUICK_REPLY_UPDATE = "QUICK_REPLY_UPDATE";
    public static final String QUICK_REPLY_DELETE = "QUICK_REPLY_DELETE";
    public static final String QUICK_REPLY_EXPORT = "QUICK_REPLY_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_QUICK_REPLY_READ = "hasAuthority('QUICK_REPLY_READ')";
    public static final String HAS_QUICK_REPLY_CREATE = "hasAuthority('QUICK_REPLY_CREATE')";
    public static final String HAS_QUICK_REPLY_UPDATE = "hasAuthority('QUICK_REPLY_UPDATE')";
    public static final String HAS_QUICK_REPLY_DELETE = "hasAuthority('QUICK_REPLY_DELETE')";
    public static final String HAS_QUICK_REPLY_EXPORT = "hasAuthority('QUICK_REPLY_EXPORT')";

}