/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.core.group.forum;

import com.bytedesk.core.base.BasePermissions;

public class GroupForumPermissions extends BasePermissions {

    // 模块前缀
    public static final String GROUP_FORUM_PREFIX = "GROUP_FORUM_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "GROUP_FORUM";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String GROUP_FORUM_READ = "GROUP_FORUM_READ";
    public static final String GROUP_FORUM_CREATE = "GROUP_FORUM_CREATE";
    public static final String GROUP_FORUM_UPDATE = "GROUP_FORUM_UPDATE";
    public static final String GROUP_FORUM_DELETE = "GROUP_FORUM_DELETE";
    public static final String GROUP_FORUM_EXPORT = "GROUP_FORUM_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_GROUP_FORUM_READ = "hasAuthority('" + GROUP_FORUM_READ + "')";
    public static final String HAS_GROUP_FORUM_CREATE = "hasAuthority('" + GROUP_FORUM_CREATE + "')";
    public static final String HAS_GROUP_FORUM_UPDATE = "hasAuthority('" + GROUP_FORUM_UPDATE + "')";
    public static final String HAS_GROUP_FORUM_DELETE = "hasAuthority('" + GROUP_FORUM_DELETE + "')";
    public static final String HAS_GROUP_FORUM_EXPORT = "hasAuthority('" + GROUP_FORUM_EXPORT + "')";

}
