package com.bytedesk.ticket.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketEventService {
    
    TicketEventEntity recordEvent(Long ticketId, Long userId, String event, String oldValue, String newValue, String description);
    
    Page<TicketEventEntity> getEventsByTicket(Long ticketId, Pageable pageable);
    
    Page<TicketEventEntity> getEventsByUser(Long userId, Pageable pageable);
    
    Page<TicketEventEntity> getEventsByTicketAndType(Long ticketId, String event, Pageable pageable);
    
    TicketEventEntity getEvent(Long eventId);
} 