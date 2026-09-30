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
package com.bytedesk.kanban.module;

import com.bytedesk.core.base.BasePermissions;

public class ModulePermissions extends BasePermissions {

    public static final String KANBAN_MODULE_PREFIX = "KANBAN_MODULE_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String KANBAN_MODULE_READ = "KANBAN_MODULE_READ";
    public static final String KANBAN_MODULE_CREATE = "KANBAN_MODULE_CREATE";
    public static final String KANBAN_MODULE_UPDATE = "KANBAN_MODULE_UPDATE";
    public static final String KANBAN_MODULE_DELETE = "KANBAN_MODULE_DELETE";
    public static final String KANBAN_MODULE_EXPORT = "KANBAN_MODULE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_KANBAN_MODULE_READ = "hasAuthority('KANBAN_MODULE_READ')";
    public static final String HAS_KANBAN_MODULE_CREATE = "hasAuthority('KANBAN_MODULE_CREATE')";
    public static final String HAS_KANBAN_MODULE_UPDATE = "hasAuthority('KANBAN_MODULE_UPDATE')";
    public static final String HAS_KANBAN_MODULE_DELETE = "hasAuthority('KANBAN_MODULE_DELETE')";
    public static final String HAS_KANBAN_MODULE_EXPORT = "hasAuthority('KANBAN_MODULE_EXPORT')";

}