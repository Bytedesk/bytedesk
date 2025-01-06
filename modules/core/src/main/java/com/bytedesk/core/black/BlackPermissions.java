/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 17:09:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

public class BlackPermissions {

    public static final String BLACK_PREFIX = "BLACK_";
    // Black permissions
    public static final String BLACK_CREATE = "hasAuthority('BLACK_CREATE')";
    public static final String BLACK_READ = "hasAuthority('BLACK_READ')";
    public static final String BLACK_UPDATE = "hasAuthority('BLACK_UPDATE')";
    public static final String BLACK_DELETE = "hasAuthority('BLACK_DELETE')";
    public static final String BLACK_EXPORT = "hasAuthority('BLACK_EXPORT')";
}