/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-02 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 08:39:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.presence.PresenceFacadeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 监听客服状态变更事件，清除相关工作组缓存
 */
@Slf4j
@Component
@AllArgsConstructor
public class WorkgroupCacheEvictListener {

    private final WorkgroupRepository workgroupRepository;
    private final CacheManager cacheManager;
    private final PresenceFacadeService presenceFacadeService;

    /**
     * 处理客服状态更新事件，清除关联的工作组缓存
     * 使用 @Async 注解，避免阻塞主流程
     */
    @Async
    @EventListener
    public void handleAgentUpdateStatusEvent(AgentUpdateStatusEvent event) {
        AgentEntity agent = event.getAgent();
        String agentUid = agent.getUid();
    boolean presenceOnline = presenceFacadeService.isAgentOnline(agent);
    log.info("监测到客服状态变更，准备清除关联工作组缓存: {}, status: {}, presenceOnline: {}", 
        agentUid, agent.getStatus(), presenceOnline);

        // 查找包含该客服的所有工作组
        List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agentUid);
        
        if (workgroups != null && !workgroups.isEmpty()) {
            log.info("客服 {} 关联工作组数量: {}", agentUid, workgroups.size());
            for (WorkgroupEntity workgroup : workgroups) {
                log.info("清除工作组缓存: {}", workgroup.getUid());
                // 从缓存中清除工作组
                cacheManager.getCache("workgroup").evict(workgroup.getUid());
            }
        } else {
            log.info("客服 {} 没有关联的工作组", agentUid);
        }
    }
}
