/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:48:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import com.bytedesk.core.base.BasePermissions;

public class KbasePermissions extends BasePermissions {

    public static final String KBASE_PREFIX = "KBASE_";
    //
    public static final String KBASE_CREATE = formatAuthority(KBASE_PREFIX + "CREATE");
    public static final String KBASE_READ = formatAuthority(KBASE_PREFIX + "READ");
    public static final String KBASE_UPDATE = formatAuthority(KBASE_PREFIX + "UPDATE");
    public static final String KBASE_DELETE = formatAuthority(KBASE_PREFIX + "DELETE");
    public static final String KBASE_EXPORT = formatAuthority(KBASE_PREFIX + "EXPORT");
    // 
    public static final String KBASE_ANY = formatAnyAuthority(KBASE_PREFIX + "CREATE", KBASE_PREFIX + "READ", KBASE_PREFIX + "UPDATE", KBASE_PREFIX + "EXPORT", KBASE_PREFIX + "DELETE");
}
