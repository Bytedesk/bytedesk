/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

import com.bytedesk.core.base.BasePermissions;

public class JobTitlePermissions extends BasePermissions {

    // 模块前缀
    public static final String JOBTITLE_PREFIX = "JOBTITLE_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "JOBTITLE";

    // 统一权限（不区分层级）
    public static final String JOBTITLE_READ = "JOBTITLE_READ";
    public static final String JOBTITLE_CREATE = "JOBTITLE_CREATE";
    public static final String JOBTITLE_UPDATE = "JOBTITLE_UPDATE";
    public static final String JOBTITLE_DELETE = "JOBTITLE_DELETE";

    public static final String HAS_JOBTITLE_READ = "hasAuthority('" + JOBTITLE_READ + "')";
    public static final String HAS_JOBTITLE_CREATE = "hasAuthority('" + JOBTITLE_CREATE + "')";
    public static final String HAS_JOBTITLE_UPDATE = "hasAuthority('" + JOBTITLE_UPDATE + "')";
    public static final String HAS_JOBTITLE_DELETE = "hasAuthority('" + JOBTITLE_DELETE + "')";
}
