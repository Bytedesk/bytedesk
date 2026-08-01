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
package com.bytedesk.core.menu;

import com.bytedesk.core.base.BasePermissions;

/**
 * 菜单权限控制
 */
public class MenuPermissions extends BasePermissions {

    public static final String MENU_PREFIX = "MENU_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String MENU_READ = "MENU_READ";
    public static final String MENU_CREATE = "MENU_CREATE";
    public static final String MENU_UPDATE = "MENU_UPDATE";
    public static final String MENU_DELETE = "MENU_DELETE";
    public static final String MENU_EXPORT = "MENU_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_MENU_READ = "hasAuthority('MENU_READ')";
    public static final String HAS_MENU_CREATE = "hasAuthority('MENU_CREATE')";
    public static final String HAS_MENU_UPDATE = "hasAuthority('MENU_UPDATE')";
    public static final String HAS_MENU_DELETE = "hasAuthority('MENU_DELETE')";
    public static final String HAS_MENU_EXPORT = "hasAuthority('MENU_EXPORT')";
}
