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
package com.bytedesk.kanban.module;

public class ModulePermissions {

    public static final String MODULE_PREFIX = "MODULE_";
    // Module permissions
    public static final String MODULE_CREATE = "hasAuthority('MODULE_CREATE')";
    public static final String MODULE_READ = "hasAuthority('MODULE_READ')";
    public static final String MODULE_UPDATE = "hasAuthority('MODULE_UPDATE')";
    public static final String MODULE_DELETE = "hasAuthority('MODULE_DELETE')";
    public static final String MODULE_EXPORT = "hasAuthority('MODULE_EXPORT')";

    // 
    public static final String MODULE_ANY = "hasAnyAuthority('MODULE_CREATE', 'MODULE_READ', 'MODULE_UPDATE', 'MODULE_EXPORT', 'MODULE_DELETE')";
    
}