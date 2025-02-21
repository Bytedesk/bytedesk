/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-18 12:56:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:03:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentUpdateTypeEnum;
import com.bytedesk.service.agent.event.AgentUpdateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AgentStatusLogEventListener {

    private final AgentStatusLogService agentStatusService;
   
    @EventListener
    public void onAgentUpdateEvent(AgentUpdateEvent event) {
        // AgentUpdateEvent agentUpdateEvent = (AgentUpdateEvent) event.getObject();
        if (event.getUpdateType().equals(AgentUpdateTypeEnum.STATUS.name())) {
            AgentEntity agent = event.getAgent();
            log.info("agent status update: {} type {}", agent.getUid(), event.getUpdateType());
            // 客服切换接待状态
            AgentStatusLogRequest agentStatusRequest = AgentStatusLogRequest.builder()
                    .status(agent.getStatus())
                    .agentUid(agent.getUid())
                    .build();
            agentStatusService.create(agentStatusRequest);
        }
    }
    
}
