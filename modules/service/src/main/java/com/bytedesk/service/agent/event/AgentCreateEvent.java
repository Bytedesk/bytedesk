/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 15:39:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 11:34:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent.event;

import com.bytedesk.service.agent.AgentEntity;

public class AgentCreateEvent extends AbstractAgentEvent {

    private static final long serialVersionUID = 1L;

    public AgentCreateEvent(Object source, AgentEntity agent) {
        super(source, agent);
    }
}
