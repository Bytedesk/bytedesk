/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:53:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

public class AgentPermissions {

    public static final String AGENT_PREFIX = "AGENT_";
    // Agent permissions
    public static final String AGENT_CREATE = "hasAuthority('AGENT_CREATE')";
    public static final String AGENT_READ = "hasAuthority('AGENT_READ')";
    public static final String AGENT_UPDATE = "hasAuthority('AGENT_UPDATE')";
    public static final String AGENT_DELETE = "hasAuthority('AGENT_DELETE')";
    public static final String AGENT_EXPORT = "hasAuthority('AGENT_EXPORT')";
}