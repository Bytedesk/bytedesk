package com.bytedesk.ticket.agent;

import java.util.List;

public interface TicketAgentService {
    
    // 获取可用客服列表
    List<Long> getAvailableAgents();
    
    // 根据技能获取客服
    List<Long> getAgentsBySkills(List<String> skills);
    
    // 获取在线客服
    List<Long> getOnlineAgents();
    
    // 检查客服是否在线
    boolean isAgentOnline(Long agentId);
    
    // 获取客服状态
    String getAgentStatus(Long agentId);
    
    // 获取客服名称
    String getAgentName(Long agentId);
    
    // 获取客服技能列表
    List<String> getAgentSkills(Long agentId);
    
    // 获取客服平均响应时间(毫秒)
    long getAverageResponseTime(Long agentId);
    
    // 获取客服平均满意度评分(1-5)
    Double getAverageSatisfactionRating(Long agentId);
    
    // 获取团队成员
    List<Long> getTeamAgents(Long teamId);
} 