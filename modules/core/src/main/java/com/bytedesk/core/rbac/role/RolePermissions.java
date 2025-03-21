/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 21:13:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 17:16:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import com.bytedesk.core.base.BasePermissions;

public class RolePermissions extends BasePermissions {

    public static final String ROLE_PREFIX = "ROLE_";
    //
    public static final String ROLE_CREATE = formatAuthority(ROLE_PREFIX + CREATE);
    public static final String ROLE_READ = formatAuthority(ROLE_PREFIX + READ);
    public static final String ROLE_UPDATE = formatAuthority(ROLE_PREFIX + UPDATE);
    public static final String ROLE_DELETE = formatAuthority(ROLE_PREFIX + DELETE);
    public static final String ROLE_EXPORT = formatAuthority(ROLE_PREFIX + EXPORT);
    // 
    public static final String ROLE_ANY = formatAnyAuthority(
        ROLE_PREFIX + CREATE, 
        ROLE_PREFIX + READ, 
        ROLE_PREFIX + UPDATE, 
        ROLE_PREFIX + EXPORT, 
        ROLE_PREFIX + DELETE
    );

    // Add more permissions for other entities as needed
    public static final String ROLE_SUPER = "hasRole('SUPER')";
    public static final String ROLE_ADMIN = "hasAnyRole('SUPER', 'ADMIN')";
    public static final String ROLE_MEMBER = "hasAnyRole('SUPER', 'ADMIN', 'MEMBER')";
    public static final String ROLE_AGENT = "hasAnyRole('SUPER', 'ADMIN', 'AGENT')";

}
