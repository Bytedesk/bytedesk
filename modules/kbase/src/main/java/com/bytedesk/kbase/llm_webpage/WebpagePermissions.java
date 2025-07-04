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
package com.bytedesk.kbase.llm_webpage;

public class WebpagePermissions {

    public static final String WEBSITE_PREFIX = "WEBSITE_";
    // Webpage permissions
    public static final String WEBSITE_CREATE = "hasAuthority('WEBSITE_CREATE')";
    public static final String WEBSITE_READ = "hasAuthority('WEBSITE_READ')";
    public static final String WEBSITE_UPDATE = "hasAuthority('WEBSITE_UPDATE')";
    public static final String WEBSITE_DELETE = "hasAuthority('WEBSITE_DELETE')";
    public static final String WEBSITE_EXPORT = "hasAuthority('WEBSITE_EXPORT')";

    // 
    
    
}