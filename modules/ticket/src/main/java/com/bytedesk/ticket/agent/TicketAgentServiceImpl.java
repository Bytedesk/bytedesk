/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 15:47:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 16:40:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytedesk.ticket.satisfaction.TicketSatisfactionRepository;
// import com.bytedesk.ticket.ticket.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketAgentServiceImpl implements TicketAgentService {
    
    // @Autowired
    // private TicketRepository ticketRepository;
    
    @Autowired
    private TicketSatisfactionRepository satisfactionRepository;

    @Override
    public List<Long> getAvailableAgents() {
        // return coreAgentService.getAvailableAgents();
        return null;
    }
    
    @Override
    public List<Long> getAgentsBySkills(List<String> skills) {
        // return coreAgentService.getAgentsBySkills(skills);
        return null;
    }
    
    @Override
    public List<Long> getOnlineAgents() {
        // return coreAgentService.getOnlineAgents();
        return null;
    }
    
    @Override
    public boolean isAgentOnline(Long agentId) {
        // return coreAgentService.isAgentOnline(agentId);
        return false;
    }
    
    @Override
    public String getAgentStatus(Long agentId) {
        // return coreAgentService.getAgentStatus(agentId);
        return null;
    }
    
    @Override
    public String getAgentName(Long agentId) {
        // return coreAgentService.getAgentName(agentId);
        return null;
    }
    
    @Override
    public List<String> getAgentSkills(Long agentId) {
        // return coreAgentService.getAgentSkills(agentId);
        return null;
    }
    
    @Override
    public long getAverageResponseTime(Long agentId) {
        // return ticketRepository.calculateAverageResponseTime(agentId);
        return 0;
    }
    
    @Override
    public Double getAverageSatisfactionRating(Long agentId) {
        return satisfactionRepository.getAverageRating(
            agentId, 
            LocalDateTime.now().minusMonths(3),
            LocalDateTime.now()
        );
    }
    
    @Override
    public List<Long> getTeamAgents(Long teamId) {
        // return coreAgentService.getTeamAgents(teamId);
        return null;
    }
} 