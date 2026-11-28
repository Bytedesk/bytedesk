/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.kbase.translation;

import com.bytedesk.core.base.BasePermissions;

public class KbaseTranslationPermissions extends BasePermissions {

    // 模块前缀
    public static final String KBASE_TRANSLATION_PREFIX = "KBASE_TRANSLATION_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "KBASE_TRANSLATION";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String KBASE_TRANSLATION_READ = "KBASE_TRANSLATION_READ";
    public static final String KBASE_TRANSLATION_CREATE = "KBASE_TRANSLATION_CREATE";
    public static final String KBASE_TRANSLATION_UPDATE = "KBASE_TRANSLATION_UPDATE";
    public static final String KBASE_TRANSLATION_DELETE = "KBASE_TRANSLATION_DELETE";
    public static final String KBASE_TRANSLATION_EXPORT = "KBASE_TRANSLATION_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_KBASE_TRANSLATION_READ = "hasAuthority('" + KBASE_TRANSLATION_READ + "')";
    public static final String HAS_KBASE_TRANSLATION_CREATE = "hasAuthority('" + KBASE_TRANSLATION_CREATE + "')";
    public static final String HAS_KBASE_TRANSLATION_UPDATE = "hasAuthority('" + KBASE_TRANSLATION_UPDATE + "')";
    public static final String HAS_KBASE_TRANSLATION_DELETE = "hasAuthority('" + KBASE_TRANSLATION_DELETE + "')";
    public static final String HAS_KBASE_TRANSLATION_EXPORT = "hasAuthority('" + KBASE_TRANSLATION_EXPORT + "')";

}
