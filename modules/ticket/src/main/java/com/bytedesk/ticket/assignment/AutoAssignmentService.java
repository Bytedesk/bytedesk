package com.bytedesk.ticket.assignment;

import java.util.List;

import com.bytedesk.ticket.ticket.TicketEntity;

public interface AutoAssignmentService {
    // 自动分配工单
    void autoAssignTicket(TicketEntity ticket);
    
    // 负载均衡分配
    Long selectAgentByWorkload();
    
    // 技能匹配分配
    Long selectAgentBySkills(List<String> requiredSkills);
    
    // 在线状态分配
    Long selectOnlineAgent();
    
    // 优先级分配
    Long selectAgentByPriority(String priority);
} 