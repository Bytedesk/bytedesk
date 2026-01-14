/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup_routing.WorkgroupRoutingService;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentCapacityService {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;

    private final QueueService queueService;
    private final PresenceFacadeService presenceFacadeService;
    private final WorkgroupRoutingService workgroupRoutingService;

    /**
     * Select an available agent with capacity.
     *
     * <p>
     * Priority:
     * 1) Use routing strategy selection (workgroup routing mode) if agent is online+available and has capacity.
     * 2) Fallback to the first agent in availableAgents that has capacity.
     */
    public AgentEntity findAvailableAgentWithCapacity(WorkgroupEntity workgroup, ThreadEntity thread,
            List<AgentEntity> availableAgents) {
        if (CollectionUtils.isEmpty(availableAgents)) {
            return null;
        }

        AgentEntity routedAgent = workgroupRoutingService.selectAgent(workgroup, thread);
        if (routedAgent != null && presenceFacadeService.isAgentOnlineAndAvailable(routedAgent) && hasCapacity(routedAgent)) {
            log.debug("Routing selected agent has capacity - agentUid: {}", routedAgent.getUid());
            return routedAgent;
        }

        for (AgentEntity agent : availableAgents) {
            if (hasCapacity(agent)) {
                log.debug("Found agent with capacity - agentUid: {}", agent.getUid());
                return agent;
            }
        }

        log.debug("All agents at max capacity");
        return null;
    }

    /**
     * Check if agent has capacity for more threads.
     */
    public boolean hasCapacity(AgentEntity agent) {
        if (agent == null) {
            return false;
        }
        String today = LocalDate.now().format(ISO_DATE);
        String queueTopic = TopicUtils.getQueueTopicFromUid(agent.getUid());
        Optional<QueueEntity> queueEntityOpt = queueService.findByTopicAndDay(queueTopic, today);
        int currentChattingCount = queueEntityOpt.map(QueueEntity::getChattingCount).orElse(0);
        int maxThreadCount = agent.getMaxThreadCount();
        boolean hasCapacity = currentChattingCount < maxThreadCount;
        log.debug("Agent capacity check - agentUid: {}, current: {}, max: {}, hasCapacity: {}",
                agent.getUid(), currentChattingCount, maxThreadCount, hasCapacity);
        return hasCapacity;
    }
}
