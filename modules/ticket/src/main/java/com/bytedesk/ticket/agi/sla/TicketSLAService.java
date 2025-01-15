package com.bytedesk.ticket.agi.sla;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketSLAService {
    
    TicketSLAEntity createSLA(TicketSLAEntity sla);
    
    TicketSLAEntity updateSLA(Long slaId, TicketSLAEntity sla);
    
    void deleteSLA(Long slaId);
    
    TicketSLAEntity getSLA(Long slaId);
    
    List<TicketSLAEntity> getAllSLAs();
    
    TicketSLAEntity getMatchingSLA(String priority, Long categoryId);
    
    LocalDateTime calculateDueDate(TicketSLAEntity sla, LocalDateTime startTime);
    
    boolean isWithinBusinessHours(LocalDateTime dateTime);
    
    long calculateElapsedTime(LocalDateTime startTime, LocalDateTime endTime, boolean businessHoursOnly);
    
    void checkAndEscalateTickets();
} 