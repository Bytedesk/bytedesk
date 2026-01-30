/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-03-08 10:32:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group_notice;

import com.bytedesk.core.base.BasePermissions;

public class GroupNoticePermissions extends BasePermissions {

    public static final String GROUP_NOTICE_PREFIX = "GROUP_NOTICE_";

    public static final String MODULE_NAME = "GROUP_NOTICE";

    // 统一权限（不区分层级）
    public static final String GROUP_NOTICE_READ = "GROUP_NOTICE_READ";
    public static final String GROUP_NOTICE_CREATE = "GROUP_NOTICE_CREATE";
    public static final String GROUP_NOTICE_UPDATE = "GROUP_NOTICE_UPDATE";
    public static final String GROUP_NOTICE_DELETE = "GROUP_NOTICE_DELETE";
    public static final String GROUP_NOTICE_EXPORT = "GROUP_NOTICE_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_GROUP_NOTICE_READ = "hasAuthority('" + GROUP_NOTICE_READ + "')";
    public static final String HAS_GROUP_NOTICE_CREATE = "hasAuthority('" + GROUP_NOTICE_CREATE + "')";
    public static final String HAS_GROUP_NOTICE_UPDATE = "hasAuthority('" + GROUP_NOTICE_UPDATE + "')";
    public static final String HAS_GROUP_NOTICE_DELETE = "hasAuthority('" + GROUP_NOTICE_DELETE + "')";
    public static final String HAS_GROUP_NOTICE_EXPORT = "hasAuthority('" + GROUP_NOTICE_EXPORT + "')";
    
}