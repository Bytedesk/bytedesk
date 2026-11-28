/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.forum.post;

import com.bytedesk.core.base.BasePermissions;

public class PostPermissions extends BasePermissions {

    // 模块前缀
    public static final String POST_PREFIX = "POST_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "POST";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String POST_READ = "POST_READ";
    public static final String POST_CREATE = "POST_CREATE";
    public static final String POST_UPDATE = "POST_UPDATE";
    public static final String POST_DELETE = "POST_DELETE";
    public static final String POST_EXPORT = "POST_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_POST_READ = "hasAuthority('" + POST_READ + "')";
    public static final String HAS_POST_CREATE = "hasAuthority('" + POST_CREATE + "')";
    public static final String HAS_POST_UPDATE = "hasAuthority('" + POST_UPDATE + "')";
    public static final String HAS_POST_DELETE = "hasAuthority('" + POST_DELETE + "')";
    public static final String HAS_POST_EXPORT = "hasAuthority('" + POST_EXPORT + "')";

}
