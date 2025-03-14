/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-03 17:09:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

public class NoticePermissions {

    public static final String NOTICE_PREFIX = "NOTICE_";
    // Notice permissions
    public static final String NOTICE_CREATE = "hasAuthority('NOTICE_CREATE')";
    public static final String NOTICE_READ = "hasAuthority('NOTICE_READ')";
    public static final String NOTICE_UPDATE = "hasAuthority('NOTICE_UPDATE')";
    public static final String NOTICE_DELETE = "hasAuthority('NOTICE_DELETE')";
    public static final String NOTICE_EXPORT = "hasAuthority('NOTICE_EXPORT')";
}
