/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:50:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import com.bytedesk.core.base.BasePermissions;

public class AgentPermissions extends BasePermissions {

    public static final String AGENT_PREFIX = "AGENT_";
    // Agent permissions
    public static final String AGENT_CREATE = formatAuthority(AGENT_PREFIX + "CREATE");
    public static final String AGENT_READ = formatAuthority(AGENT_PREFIX + "READ");
    public static final String AGENT_UPDATE = formatAuthority(AGENT_PREFIX + "UPDATE");
    public static final String AGENT_DELETE = formatAuthority(AGENT_PREFIX + "DELETE");
    public static final String AGENT_EXPORT = formatAuthority(AGENT_PREFIX + "EXPORT");

    // 
    public static final String AGENT_ANY = formatAnyAuthority(AGENT_PREFIX + "CREATE", AGENT_PREFIX + "READ", AGENT_PREFIX + "UPDATE", AGENT_PREFIX + "EXPORT", AGENT_PREFIX + "DELETE");
    
}