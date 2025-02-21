/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 07:37:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.menu;

public class MenuPermissions {

    public static final String MENU_PREFIX = "MENU_";
    // Menu permissions
    public static final String MENU_CREATE = "hasAuthority('MENU_CREATE')";
    public static final String MENU_READ = "hasAuthority('MENU_READ')";
    public static final String MENU_UPDATE = "hasAuthority('MENU_UPDATE')";
    public static final String MENU_DELETE = "hasAuthority('MENU_DELETE')";
    public static final String MENU_EXPORT = "hasAuthority('MENU_EXPORT')";

    // 
    
    
}