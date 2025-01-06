/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:56:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 10:15:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

public class UserPermissions {

    public static final String USER_PREFIX = "USER_";
    // User permissions
    public static final String USER_CREATE = "hasAuthority('USER_CREATE')";
    public static final String USER_READ = "hasAuthority('USER_READ')";
    public static final String USER_UPDATE = "hasAuthority('USER_UPDATE')";
    public static final String USER_DELETE = "hasAuthority('USER_DELETE')";
    public static final String USER_EXPORT = "hasAuthority('USER_EXPORT')";
    // 
    public static final String USER_ANY = "hasAnyAuthority('USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_EXPORT', 'USER_DELETE')";


    
}