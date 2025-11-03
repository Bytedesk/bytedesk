/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-18 12:56:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 15:41:25
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

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AgentStatusEventListener {

    private final AgentStatusRestService agentStatusService;
   
    @EventListener
    public void onAgentUpdateStatusEvent(AgentUpdateStatusEvent event) {
        AgentEntity agent = event.getAgent();
        log.info("agent status update: {}", agent.getUid());
        UserProtobuf userProtobuf = UserProtobuf.builder()
                .uid(agent.getUid())
                .nickname(agent.getNickname())
                .avatar(agent.getAvatar())
                .type(UserTypeEnum.AGENT.name())
                .extra(agent.getExtra())
                .build();
        // 客服切换接待状态
        AgentStatusRequest agentStatusRequest = AgentStatusRequest.builder()
                .status(agent.getStatus())
                .agent(userProtobuf.toJson())
                // .userUid(agent.getMember().getUser().getUid()) // 懒加载可能导致报错
                .orgUid(agent.getOrgUid())
                .build();
        agentStatusService.create(agentStatusRequest);
    }
    
}
