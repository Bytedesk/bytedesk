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
package com.bytedesk.core.calendar;

import com.bytedesk.core.base.BasePermissions;

public class CalendarPermissions extends BasePermissions {

    // 模块前缀
    public static final String CALENDAR_PREFIX = "CALENDAR_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "CALENDAR";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String CALENDAR_READ = "CALENDAR_READ";
    public static final String CALENDAR_CREATE = "CALENDAR_CREATE";
    public static final String CALENDAR_UPDATE = "CALENDAR_UPDATE";
    public static final String CALENDAR_DELETE = "CALENDAR_DELETE";
    public static final String CALENDAR_EXPORT = "CALENDAR_EXPORT";

    // 新 PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_CALENDAR_READ = "hasAuthority('" + CALENDAR_READ + "')";
    public static final String HAS_CALENDAR_CREATE = "hasAuthority('" + CALENDAR_CREATE + "')";
    public static final String HAS_CALENDAR_UPDATE = "hasAuthority('" + CALENDAR_UPDATE + "')";
    public static final String HAS_CALENDAR_DELETE = "hasAuthority('" + CALENDAR_DELETE + "')";
    public static final String HAS_CALENDAR_EXPORT = "hasAuthority('" + CALENDAR_EXPORT + "')";

}
