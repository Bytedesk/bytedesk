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
package com.bytedesk.kbase.llm_faq;

import com.bytedesk.core.base.BasePermissions;

public class FaqPermissions extends BasePermissions {

    // 模块前缀
    public static final String FAQ_PREFIX = "FAQ_";

    // 统一权限（不区分层级）
    public static final String FAQ_READ = "FAQ_READ";
    public static final String FAQ_CREATE = "FAQ_CREATE";
    public static final String FAQ_UPDATE = "FAQ_UPDATE";
    public static final String FAQ_DELETE = "FAQ_DELETE";
    public static final String FAQ_EXPORT = "FAQ_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_FAQ_READ = "hasAuthority('FAQ_READ')";
    public static final String HAS_FAQ_CREATE = "hasAuthority('FAQ_CREATE')";
    public static final String HAS_FAQ_UPDATE = "hasAuthority('FAQ_UPDATE')";
    public static final String HAS_FAQ_DELETE = "hasAuthority('FAQ_DELETE')";
    public static final String HAS_FAQ_EXPORT = "hasAuthority('FAQ_EXPORT')";

}