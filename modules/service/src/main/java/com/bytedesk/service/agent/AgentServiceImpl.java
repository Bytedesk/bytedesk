/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:30:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 15:56:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.service.rating.RatingRepository;
import com.bytedesk.service.thread.ThreadAgentRepository; // 修改为ThreadAgentRepository

import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private ThreadAgentRepository threadAgentRepository;
    
    @Autowired
    private RatingRepository ratingRepository;

    // ... 其他代码保持不变
    @Override
    public double getAgentRating(String agentUid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAgentRating'");
    }

    @Override
    public List<AgentEntity> getAvailableAgents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableAgents'");
    }

    @Override
    public List<String> getAgentsBySkills(List<String> skills) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAgentsBySkills'");
    }

    @Override
    public List<String> getOnlineAgents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOnlineAgents'");
    }

    @Override
    public int getAgentWorkload(String agentUid) {
        return threadAgentRepository.countActiveThreadsByAgent(agentUid);
    }

    @Override
    public long getAverageResponseTime(String agentUid) {
        Long avgTime = 0L;//threadAgentRepository.getAverageResponseTime(agentUid);
        return avgTime != null ? avgTime : 0L;
    }

    @Override
    @Transactional
    public void updateAgentStatus(String agentUid, String status) {
        agentRepository.updateStatus(agentUid, status);
        
        if ("offline".equals(status)) {
            List<String> activeThreads = threadAgentRepository.findActiveThreadsByAgent(agentUid);
            activeThreads.forEach(this::unassignThread);
        }
    }

    @Override
    @Transactional
    public void assignThread(String threadUid, String agentUid) {
        threadAgentRepository.updateAssignedAgent(threadUid, agentUid);
        // agentRepository.incrementWorkload(agentUid);
        log.info("Thread {} assigned to agent {}", threadUid, agentUid);
    }

    @Override
    @Transactional
    public void unassignThread(String threadUid) {
        String currentAgent = threadAgentRepository.getAssignedAgent(threadUid);
        if (currentAgent != null) {
            // agentRepository.decrementWorkload(currentAgent);
        }
        threadAgentRepository.updateAssignedAgent(threadUid, null);
        log.info("Thread {} unassigned from agent {}", threadUid, currentAgent);
    }

    @Override
    public Map<String, Object> getAgentStats(String agentUid) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeThreads", getAgentWorkload(agentUid));
        stats.put("rating", getAgentRating(agentUid));
        stats.put("avgResponseTime", getAverageResponseTime(agentUid));
        stats.put("totalThreads", threadAgentRepository.countTotalThreadsByAgent(agentUid));
        stats.put("resolvedThreads", threadAgentRepository.countResolvedThreadsByAgent(agentUid));
        stats.put("satisfactionRate", ratingRepository.getSatisfactionRating(agentUid));
        return stats;
    }

    
}