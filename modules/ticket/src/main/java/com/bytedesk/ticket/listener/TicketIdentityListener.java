/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-30 14:02:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 14:10:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.event.AgentUpdateEvent;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.event.WorkgroupUpdateEvent;
import com.bytedesk.ticket.service.TicketIdentityService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketIdentityListener {

    private final TicketIdentityService identityService;

    /**
     * 监听用户创建/更新事件
     */
    @EventListener
    public void onAgentUpdated(AgentUpdateEvent event) {
        AgentEntity agent = event.getAgent();
        // 同步用户到Flowable
        identityService.syncUser(agent);
    }

    /**
     * 监听工作组变更事件
     */
    @EventListener
    public void onWorkgroupUpdated(WorkgroupUpdateEvent event) {
        WorkgroupEntity workgroup = event.getWorkgroup();
        // 同步工作组到Flowable
        identityService.syncWorkgroup(workgroup);
    }

    /**
     * 监听用户加入工作组事件
     */
    // @EventListener
    // public void onAgentJoinWorkgroup(AgentJoinWorkgroupEvent event) {
    //     // 同步用户和工作组关系
    //     identityService.syncMembership(event.getAgentId(), event.getWorkgroupId());
    // }
} 