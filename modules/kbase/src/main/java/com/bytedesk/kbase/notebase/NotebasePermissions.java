/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 13:20:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.notebase;

public class NotebasePermissions {

    public static final String NOTEBASE_PREFIX = "NOTEBASE_";
    // Notebase permissions
    public static final String NOTEBASE_CREATE = "hasAuthority('NOTEBASE_CREATE')";
    public static final String NOTEBASE_READ = "hasAuthority('NOTEBASE_READ')";
    public static final String NOTEBASE_UPDATE = "hasAuthority('NOTEBASE_UPDATE')";
    public static final String NOTEBASE_DELETE = "hasAuthority('NOTEBASE_DELETE')";
    public static final String NOTEBASE_EXPORT = "hasAuthority('NOTEBASE_EXPORT')";
}
