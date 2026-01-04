/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_routing;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.socket.connection.ConnectionEntity;
import com.bytedesk.core.socket.connection.event.ConnectionCreateEvent;
import com.bytedesk.core.socket.connection.event.ConnectionDeleteEvent;
import com.bytedesk.core.socket.connection.event.ConnectionUpdateEvent;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.service.workgroup.event.WorkgroupCreateEvent;
import com.bytedesk.service.workgroup.event.WorkgroupUpdateEvent;
import com.bytedesk.service.workgroup_routing.event.WorkgroupRoutingAdvanceEvent;
import com.bytedesk.service.workgroup_routing.event.WorkgroupRoutingRefreshEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class WorkgroupRoutingEventListener {

    private final WorkgroupRepository workgroupRepository;
    private final AgentRepository agentRepository;
    private final WorkgroupRoutingService workgroupRoutingService;

    /**
     * 分配完成后异步推进 cursor 并刷新 nextAgent。
     */
    @Async
    @EventListener
    public void onWorkgroupRoutingAdvanceEvent(WorkgroupRoutingAdvanceEvent event) {
        if (event == null || event.getWorkgroupUid() == null) {
            return;
        }
        try {
            workgroupRoutingService.advanceAfterAssignment(event.getWorkgroupUid(), event.getAssignedAgentUid());
        } catch (Exception e) {
            log.warn("routing advance failed: workgroupUid={}, err={}", event.getWorkgroupUid(), e.getMessage());
        }
    }

    /**
     * presence/配置变化/兜底任务触发：刷新 nextAgent（不推进 cursor）。
     */
    @Async
    @EventListener
    public void onWorkgroupRoutingRefreshEvent(WorkgroupRoutingRefreshEvent event) {
        if (event == null || event.getWorkgroupUid() == null) {
            return;
        }
        try {
            workgroupRoutingService.refreshRoutingStateByWorkgroupUid(event.getWorkgroupUid());
        } catch (Exception e) {
            log.warn("routing refresh failed: workgroupUid={}, reason={}, err={}",
                    event.getWorkgroupUid(), event.getReason(), e.getMessage());
        }
    }

    /**
     * 坐席接待状态变化，会影响 availableAgents -> 刷新关联工作组 nextAgent。
     */
    @Async
    @EventListener
    public void onAgentUpdateStatusEvent(AgentUpdateStatusEvent event) {
        if (event == null || event.getAgent() == null || event.getAgent().getUid() == null) {
            return;
        }
        refreshWorkgroupsByAgentUid(event.getAgent().getUid(), "agent_status");
    }

    /**
     * 连接在线/离线变化属于真正的 presence 变化：
     * ConnectionEntity 携带 userUid，可映射到 AgentEntity，再刷新其关联工作组。
     */
    @Async
    @EventListener
    public void onConnectionCreateEvent(ConnectionCreateEvent event) {
        handleConnectionPresenceChange(event != null ? event.getConnection() : null, "connection_create");
    }

    @Async
    @EventListener
    public void onConnectionDeleteEvent(ConnectionDeleteEvent event) {
        handleConnectionPresenceChange(event != null ? event.getConnection() : null, "connection_delete");
    }

    @Async
    @EventListener
    public void onConnectionUpdateEvent(ConnectionUpdateEvent event) {
        handleConnectionPresenceChange(event != null ? event.getConnection() : null, "connection_update");
    }

    @Async
    @EventListener
    public void onWorkgroupCreateEvent(WorkgroupCreateEvent event) {
        if (event == null || event.getWorkgroupUid() == null) {
            return;
        }
        workgroupRoutingService.refreshRoutingStateByWorkgroupUid(event.getWorkgroupUid());
    }

    @Async
    @EventListener
    public void onWorkgroupUpdateEvent(WorkgroupUpdateEvent event) {
        if (event == null || event.getWorkgroupUid() == null) {
            return;
        }
        workgroupRoutingService.refreshRoutingStateByWorkgroupUid(event.getWorkgroupUid());
    }

    /**
     * 监听 Quartz 5分钟事件：兜底刷新 routing 状态，替换 @Scheduled。
     */
    @Async
    @EventListener
    public void onQuartzFiveMinEvent(QuartzFiveMinEvent event) {
        List<WorkgroupEntity> workgroups = workgroupRepository.findByDeletedFalse();
        if (workgroups == null || workgroups.isEmpty()) {
            return;
        }

        // int refreshed = 0;
        for (WorkgroupEntity workgroup : workgroups) {
            try {
                if (workgroup == null || workgroup.getUid() == null) {
                    continue;
                }
                if (workgroup.getAgents() == null || workgroup.getAgents().isEmpty()) {
                    continue;
                }
                workgroupRoutingService.refreshRoutingState(workgroup);
                // refreshed++;
            } catch (Exception e) {
                log.debug("quartz routing refresh failed: workgroupUid={}, err={}",
                        workgroup != null ? workgroup.getUid() : "null", e.getMessage());
            }
        }
        // log.debug("quartz routing refresh done: {} workgroups", refreshed);
    }

    private void handleConnectionPresenceChange(ConnectionEntity connection, String reason) {
        if (connection == null || connection.getUserUid() == null) {
            return;
        }
        try {
            agentRepository.findByUserUid(connection.getUserUid()).ifPresent(agent -> {
                if (agent == null || agent.getUid() == null) {
                    return;
                }
                refreshWorkgroupsByAgentUid(agent.getUid(), reason);
            });
        } catch (Exception e) {
            log.debug("routing refresh on {} failed: userUid={}, err={}", reason, connection.getUserUid(), e.getMessage());
        }
    }

    private void refreshWorkgroupsByAgentUid(String agentUid, String reason) {
        if (agentUid == null) {
            return;
        }
        List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agentUid);
        if (workgroups == null || workgroups.isEmpty()) {
            return;
        }
        for (WorkgroupEntity workgroup : workgroups) {
            try {
                workgroupRoutingService.refreshRoutingStateByWorkgroupUid(workgroup.getUid());
            } catch (Exception e) {
                log.debug("routing refresh on {} failed: workgroupUid={}, agentUid={}, err={}",
                        reason, workgroup.getUid(), agentUid, e.getMessage());
            }
        }
    }
}

