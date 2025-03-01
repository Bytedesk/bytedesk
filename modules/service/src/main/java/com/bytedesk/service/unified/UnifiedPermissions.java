/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 13:40:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.unified;

public class UnifiedPermissions {

    public static final String UNIFIED_PREFIX = "UNIFIED_";
    // Unified permissions
    public static final String UNIFIED_CREATE = "hasAuthority('UNIFIED_CREATE')";
    public static final String UNIFIED_READ = "hasAuthority('UNIFIED_READ')";
    public static final String UNIFIED_UPDATE = "hasAuthority('UNIFIED_UPDATE')";
    public static final String UNIFIED_DELETE = "hasAuthority('UNIFIED_DELETE')";
    public static final String UNIFIED_EXPORT = "hasAuthority('UNIFIED_EXPORT')";

    // 
    
    
}