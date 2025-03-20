/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:50:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import com.bytedesk.core.base.BasePermissions;

public class WorkgroupPermissions extends BasePermissions {

    public static final String WORKGROUP_PREFIX = "WORKGROUP_";
    //
    public static final String WORKGROUP_CREATE = formatAuthority(WORKGROUP_PREFIX + CREATE);
    public static final String WORKGROUP_READ = formatAuthority(WORKGROUP_PREFIX + READ);
    public static final String WORKGROUP_UPDATE = formatAuthority(WORKGROUP_PREFIX + UPDATE);
    public static final String WORKGROUP_DELETE = formatAuthority(WORKGROUP_PREFIX + DELETE);
    public static final String WORKGROUP_EXPORT = formatAuthority(WORKGROUP_PREFIX + EXPORT);
    // 
    public static final String WORKGROUP_ANY = formatAnyAuthority(WORKGROUP_PREFIX + CREATE, WORKGROUP_PREFIX + READ, WORKGROUP_PREFIX + UPDATE, WORKGROUP_PREFIX + EXPORT, WORKGROUP_PREFIX + DELETE);
}
