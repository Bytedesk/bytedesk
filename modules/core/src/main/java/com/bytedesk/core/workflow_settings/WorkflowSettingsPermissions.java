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
package com.bytedesk.core.workflow_settings;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowSettingsPermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_SETTINGS_PREFIX = "WORKFLOW_SETTINGS_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "WORKFLOW_SETTINGS";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String WORKFLOW_SETTINGS_READ = "WORKFLOW_SETTINGS_READ";
    public static final String WORKFLOW_SETTINGS_CREATE = "WORKFLOW_SETTINGS_CREATE";
    public static final String WORKFLOW_SETTINGS_UPDATE = "WORKFLOW_SETTINGS_UPDATE";
    public static final String WORKFLOW_SETTINGS_DELETE = "WORKFLOW_SETTINGS_DELETE";
    public static final String WORKFLOW_SETTINGS_EXPORT = "WORKFLOW_SETTINGS_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_WORKFLOW_SETTINGS_READ = "hasAuthority('" + WORKFLOW_SETTINGS_READ + "')";
    public static final String HAS_WORKFLOW_SETTINGS_CREATE = "hasAuthority('" + WORKFLOW_SETTINGS_CREATE + "')";
    public static final String HAS_WORKFLOW_SETTINGS_UPDATE = "hasAuthority('" + WORKFLOW_SETTINGS_UPDATE + "')";
    public static final String HAS_WORKFLOW_SETTINGS_DELETE = "hasAuthority('" + WORKFLOW_SETTINGS_DELETE + "')";
    public static final String HAS_WORKFLOW_SETTINGS_EXPORT = "hasAuthority('" + WORKFLOW_SETTINGS_EXPORT + "')";

}
