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
package com.bytedesk.core.group;

import com.bytedesk.core.base.BasePermissions;

public class GroupPermissions extends BasePermissions {

    // 模块前缀
    public static final String GROUP_PREFIX = "GROUP_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String GROUP_READ = "GROUP_READ";
    public static final String GROUP_CREATE = "GROUP_CREATE";
    public static final String GROUP_UPDATE = "GROUP_UPDATE";
    public static final String GROUP_DELETE = "GROUP_DELETE";
    public static final String GROUP_EXPORT = "GROUP_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_GROUP_READ = "hasAuthority('GROUP_READ')";
    public static final String HAS_GROUP_CREATE = "hasAuthority('GROUP_CREATE')";
    public static final String HAS_GROUP_UPDATE = "hasAuthority('GROUP_UPDATE')";
    public static final String HAS_GROUP_DELETE = "hasAuthority('GROUP_DELETE')";
    public static final String HAS_GROUP_EXPORT = "hasAuthority('GROUP_EXPORT')";

}