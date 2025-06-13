/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:23:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import com.bytedesk.core.base.BasePermissions;

public class MemberPermissions extends BasePermissions {

    public static final String MEMBER_PREFIX = "MEMBER_";
    //
    public static final String MEMBER_CREATE = formatAuthority(MEMBER_PREFIX + CREATE);
    public static final String MEMBER_READ = formatAuthority(MEMBER_PREFIX + READ);
    public static final String MEMBER_UPDATE = formatAuthority(MEMBER_PREFIX + UPDATE);
    public static final String MEMBER_DELETE = formatAuthority(MEMBER_PREFIX + DELETE);
    public static final String MEMBER_EXPORT = formatAuthority(MEMBER_PREFIX + EXPORT);
    // 
    public static final String MEMBER_ANY = formatAnyAuthority(MEMBER_PREFIX + CREATE, MEMBER_PREFIX + READ, MEMBER_PREFIX + UPDATE, MEMBER_PREFIX + EXPORT, MEMBER_PREFIX + DELETE);
}
