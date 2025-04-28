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
package com.bytedesk.kbase.llm.chunk;

public class SplitPermissions {

    public static final String SPLIT_PREFIX = "SPLIT_";
    // Split permissions
    public static final String SPLIT_CREATE = "hasAuthority('SPLIT_CREATE')";
    public static final String SPLIT_READ = "hasAuthority('SPLIT_READ')";
    public static final String SPLIT_UPDATE = "hasAuthority('SPLIT_UPDATE')";
    public static final String SPLIT_DELETE = "hasAuthority('SPLIT_DELETE')";
    public static final String SPLIT_EXPORT = "hasAuthority('SPLIT_EXPORT')";

    // 
    
    
}