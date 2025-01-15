package com.bytedesk.ticket.agi.assign;

import java.util.List;

import com.bytedesk.ticket.ticket.TicketEntity;

public interface TicketAssignmentService {
    
    Long assignTicket(TicketEntity ticket);
    
    AssignmentConfigEntity createConfig(AssignmentConfigEntity config);
    
    AssignmentConfigEntity updateConfig(Long configId, AssignmentConfigEntity config);
    
    void deleteConfig(Long configId);
    
    AssignmentConfigEntity getConfig(Long configId);
    
    List<AssignmentConfigEntity> getAllConfigs();
    
    AssignmentConfigEntity getMatchingConfig(Long categoryId, String priority);
    
    void redistributeTickets();  // 重新分配未分配的工单
} 