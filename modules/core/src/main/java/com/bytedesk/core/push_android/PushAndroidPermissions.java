/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

import com.bytedesk.core.base.BasePermissions;

public class PushAndroidPermissions extends BasePermissions {

    // 模块前缀
    public static final String PUSH_ANDROID_PREFIX = "PUSH_ANDROID_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "PUSH_ANDROID";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String PUSH_ANDROID_READ = "PUSH_ANDROID_READ";
    public static final String PUSH_ANDROID_CREATE = "PUSH_ANDROID_CREATE";
    public static final String PUSH_ANDROID_UPDATE = "PUSH_ANDROID_UPDATE";
    public static final String PUSH_ANDROID_DELETE = "PUSH_ANDROID_DELETE";
    public static final String PUSH_ANDROID_EXPORT = "PUSH_ANDROID_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_PUSH_ANDROID_READ = "hasAuthority('" + PUSH_ANDROID_READ + "')";
    public static final String HAS_PUSH_ANDROID_CREATE = "hasAuthority('" + PUSH_ANDROID_CREATE + "')";
    public static final String HAS_PUSH_ANDROID_UPDATE = "hasAuthority('" + PUSH_ANDROID_UPDATE + "')";
    public static final String HAS_PUSH_ANDROID_DELETE = "hasAuthority('" + PUSH_ANDROID_DELETE + "')";
    public static final String HAS_PUSH_ANDROID_EXPORT = "hasAuthority('" + PUSH_ANDROID_EXPORT + "')";

}
