/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 17:37:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

public class WorkflowPermissions {

    public static final String FLOW_PREFIX = "FLOW_";
    // Workflow permissions
    public static final String FLOW_CREATE = "hasAuthority('FLOW_CREATE')";
    public static final String FLOW_READ = "hasAuthority('FLOW_READ')";
    public static final String FLOW_UPDATE = "hasAuthority('FLOW_UPDATE')";
    public static final String FLOW_DELETE = "hasAuthority('FLOW_DELETE')";
    public static final String FLOW_EXPORT = "hasAuthority('FLOW_EXPORT')";

    // 
    
    
}