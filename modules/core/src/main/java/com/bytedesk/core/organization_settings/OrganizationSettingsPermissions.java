/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.organization_settings;

import com.bytedesk.core.base.BasePermissions;

public class OrganizationSettingsPermissions extends BasePermissions {

    // 模块前缀
    public static final String ORGANIZATION_SETTINGS_PREFIX = "ORGANIZATION_SETTINGS_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "ORGANIZATION_SETTINGS";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String ORGANIZATION_SETTINGS_READ = "ORGANIZATION_SETTINGS_READ";
    public static final String ORGANIZATION_SETTINGS_CREATE = "ORGANIZATION_SETTINGS_CREATE";
    public static final String ORGANIZATION_SETTINGS_UPDATE = "ORGANIZATION_SETTINGS_UPDATE";
    public static final String ORGANIZATION_SETTINGS_DELETE = "ORGANIZATION_SETTINGS_DELETE";
    public static final String ORGANIZATION_SETTINGS_EXPORT = "ORGANIZATION_SETTINGS_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_ORGANIZATION_SETTINGS_READ = "hasAuthority('" + ORGANIZATION_SETTINGS_READ + "')";
    public static final String HAS_ORGANIZATION_SETTINGS_CREATE = "hasAuthority('" + ORGANIZATION_SETTINGS_CREATE + "')";
    public static final String HAS_ORGANIZATION_SETTINGS_UPDATE = "hasAuthority('" + ORGANIZATION_SETTINGS_UPDATE + "')";
    public static final String HAS_ORGANIZATION_SETTINGS_DELETE = "hasAuthority('" + ORGANIZATION_SETTINGS_DELETE + "')";
    public static final String HAS_ORGANIZATION_SETTINGS_EXPORT = "hasAuthority('" + ORGANIZATION_SETTINGS_EXPORT + "')";

}
