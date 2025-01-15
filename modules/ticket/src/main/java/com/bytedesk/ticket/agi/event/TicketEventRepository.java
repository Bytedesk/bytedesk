package com.bytedesk.ticket.agi.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketEventRepository extends JpaRepository<TicketEventEntity, Long> {
    
    Page<TicketEventEntity> findByTicketId(Long ticketId, Pageable pageable);
    
    Page<TicketEventEntity> findByUserId(Long userId, Pageable pageable);
    
    Page<TicketEventEntity> findByTicketIdAndEvent(Long ticketId, String event, Pageable pageable);
} 