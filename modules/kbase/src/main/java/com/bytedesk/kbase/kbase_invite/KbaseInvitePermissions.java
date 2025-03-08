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
package com.bytedesk.kbase.kbase_invite;

public class KbaseInvitePermissions {

    public static final String TAG_PREFIX = "TAG_";
    // KbaseInvite permissions
    public static final String TAG_CREATE = "hasAuthority('TAG_CREATE')";
    public static final String TAG_READ = "hasAuthority('TAG_READ')";
    public static final String TAG_UPDATE = "hasAuthority('TAG_UPDATE')";
    public static final String TAG_DELETE = "hasAuthority('TAG_DELETE')";
    public static final String TAG_EXPORT = "hasAuthority('TAG_EXPORT')";

    // 
    public static final String TAG_ANY = "hasAnyAuthority('TAG_CREATE', 'TAG_READ', 'TAG_UPDATE', 'TAG_EXPORT', 'TAG_DELETE')";
    
}