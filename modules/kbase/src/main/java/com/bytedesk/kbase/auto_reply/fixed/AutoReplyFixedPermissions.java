/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:42:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import com.bytedesk.core.base.BasePermissions;

public class AutoReplyFixedPermissions extends BasePermissions {

    // 模块前缀
    public static final String AUTO_REPLY_FIXED_PREFIX = "AUTO_REPLY_FIXED_";

    // 统一权限（不区分层级）
    public static final String AUTO_REPLY_FIXED_READ = "AUTO_REPLY_FIXED_READ";
    public static final String AUTO_REPLY_FIXED_CREATE = "AUTO_REPLY_FIXED_CREATE";
    public static final String AUTO_REPLY_FIXED_UPDATE = "AUTO_REPLY_FIXED_UPDATE";
    public static final String AUTO_REPLY_FIXED_DELETE = "AUTO_REPLY_FIXED_DELETE";
    public static final String AUTO_REPLY_FIXED_EXPORT = "AUTO_REPLY_FIXED_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_AUTO_REPLY_FIXED_READ = "hasAuthority('AUTO_REPLY_FIXED_READ')";
    public static final String HAS_AUTO_REPLY_FIXED_CREATE = "hasAuthority('AUTO_REPLY_FIXED_CREATE')";
    public static final String HAS_AUTO_REPLY_FIXED_UPDATE = "hasAuthority('AUTO_REPLY_FIXED_UPDATE')";
    public static final String HAS_AUTO_REPLY_FIXED_DELETE = "hasAuthority('AUTO_REPLY_FIXED_DELETE')";
    public static final String HAS_AUTO_REPLY_FIXED_EXPORT = "hasAuthority('AUTO_REPLY_FIXED_EXPORT')";

}