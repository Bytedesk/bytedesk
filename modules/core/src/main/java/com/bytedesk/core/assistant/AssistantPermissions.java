/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 17:08:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

public class AssistantPermissions {

    public static final String ASSISTANT_PREFIX = "ASSISTANT_";
    // Assistant permissions
    public static final String ASSISTANT_CREATE = "hasAuthority('ASSISTANT_CREATE')";
    public static final String ASSISTANT_READ = "hasAuthority('ASSISTANT_READ')";
    public static final String ASSISTANT_UPDATE = "hasAuthority('ASSISTANT_UPDATE')";
    public static final String ASSISTANT_DELETE = "hasAuthority('ASSISTANT_DELETE')";
    public static final String ASSISTANT_EXPORT = "hasAuthority('ASSISTANT_EXPORT')";
}