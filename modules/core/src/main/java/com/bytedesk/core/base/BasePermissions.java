/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:45:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:24:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

public class BasePermissions {

    protected static final String HAS_AUTHORITY = "hasAuthority('%s')";
    protected static final String HAS_ANY_AUTHORITY = "hasAnyAuthority(%s)";
    protected static final String HAS_ROLE = "hasRole('%s')";
    protected static final String HAS_ANY_ROLE = "hasAnyRole(%s)";
    protected static final String READ = "READ";
    protected static final String CREATE = "CREATE";
    protected static final String UPDATE = "UPDATE";
    protected static final String DELETE = "DELETE";
    protected static final String EXPORT = "EXPORT";
    protected static final String ANY = "ANY";

    protected static String formatAuthority(String authority) {
        return String.format(HAS_AUTHORITY, authority);
    }

    protected static String formatAnyAuthority(String... authorities) {
        return String.format(HAS_ANY_AUTHORITY, String.join(", ", authorities));
    }
}