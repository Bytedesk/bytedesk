/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 21:13:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 13:41:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

public class AuthorityPermissions {
    
    public static final String AUTHORITY_PREFIX = "AUTHORITY_";
    // Authority permissions
    public static final String AUTHORITY_CREATE = "hasAuthority('AUTHORITY_CREATE')";
    public static final String AUTHORITY_READ = "hasAuthority('AUTHORITY_READ')";
    public static final String AUTHORITY_UPDATE = "hasAuthority('AUTHORITY_UPDATE')";
    public static final String AUTHORITY_EXPORT = "hasAuthority('AUTHORITY_EXPORT')";
    public static final String AUTHORITY_DELETE = "hasAuthority('AUTHORITY_DELETE')";
    // 
    public static final String AUTHORITY_ANY = "hasAnyAuthority('AUTHORITY_CREATE', 'AUTHORITY_READ', 'AUTHORITY_UPDATE', 'AUTHORITY_EXPORT', 'AUTHORITY_DELETE')";
    // public static final String AUTHORITY_ALL = "isFullyAuthenticated()";


}
