/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:30:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 16:07:47
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

import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private AgentThreadRepository agentThreadRepository;
    
    @Autowired
    private RatingRepository ratingRepository;    

    //  获取可用客服列表
    public List<AgentEntity> getAvailableAgents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableAgents'");
    }

    // 根据技能获取客服
    public List<String> getAgentsBySkills(List<String> skills) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAgentsBySkills'");
    }

    //  获取在线客服列表
    public List<String> getOnlineAgents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOnlineAgents'");
    }

    // 获取客服工作负载
    public int getAgentWorkload(String agentUid) {
        return agentThreadRepository.countActiveThreadsByAgent(agentUid);
    }

    // 获取客服评分
    public double getAgentRating(String agentUid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAgentRating'");
    }

    // 获取客服平均响应时间
    public long getAverageResponseTime(String agentUid) {
        Long avgTime = 0L;//threadAgentRepository.getAverageResponseTime(agentUid);
        return avgTime != null ? avgTime : 0L;
    }

    // 更新客服状态
    @Transactional
    public void updateAgentStatus(String agentUid, String status) {
        agentRepository.updateStatus(agentUid, status);
        
        if ("offline".equals(status)) {
            List<String> activeThreads = agentThreadRepository.findActiveThreadsByAgent(agentUid);
            activeThreads.forEach(this::unassignThread);
        }
    }

    // 分配会话给客服
    @Transactional
    public void assignThread(String threadUid, String agentUid) {
        agentThreadRepository.updateAssignedAgent(threadUid, agentUid);
        // agentRepository.incrementWorkload(agentUid);
        log.info("Thread {} assigned to agent {}", threadUid, agentUid);
    }

    // 取消分配会话
    @Transactional
    public void unassignThread(String threadUid) {
        String currentAgent = agentThreadRepository.getAssignedAgent(threadUid);
        if (currentAgent != null) {
            // agentRepository.decrementWorkload(currentAgent);
        }
        agentThreadRepository.updateAssignedAgent(threadUid, null);
        log.info("Thread {} unassigned from agent {}", threadUid, currentAgent);
    }

    // 获取客服统计信息
    public Map<String, Object> getAgentStats(String agentUid) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeThreads", getAgentWorkload(agentUid));
        stats.put("rating", getAgentRating(agentUid));
        stats.put("avgResponseTime", getAverageResponseTime(agentUid));
        stats.put("totalThreads", agentThreadRepository.countTotalThreadsByAgent(agentUid));
        stats.put("resolvedThreads", agentThreadRepository.countResolvedThreadsByAgent(agentUid));
        stats.put("satisfactionRate", ratingRepository.getSatisfactionRating(agentUid));
        return stats;
    }

    
}