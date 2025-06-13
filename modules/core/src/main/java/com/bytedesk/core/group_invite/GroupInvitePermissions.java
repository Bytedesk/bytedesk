/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-03-08 10:32:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group_invite;

public class GroupInvitePermissions {

    public static final String GROUP_INVITE_PREFIX = "GROUP_INVITE_";
    // GroupInvite permissions
    public static final String GROUP_INVITE_CREATE = "hasAuthority('GROUP_INVITE_CREATE')";
    public static final String GROUP_INVITE_READ = "hasAuthority('GROUP_INVITE_READ')";
    public static final String GROUP_INVITE_UPDATE = "hasAuthority('GROUP_INVITE_UPDATE')";
    public static final String GROUP_INVITE_DELETE = "hasAuthority('GROUP_INVITE_DELETE')";
    public static final String GROUP_INVITE_EXPORT = "hasAuthority('GROUP_INVITE_EXPORT')";

    // 
    public static final String GROUP_INVITE_ANY = "hasAnyAuthority('GROUP_INVITE_CREATE', 'GROUP_INVITE_READ', 'GROUP_INVITE_UPDATE', 'GROUP_INVITE_EXPORT', 'GROUP_INVITE_DELETE')";
    
}