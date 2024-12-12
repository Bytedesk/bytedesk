package com.bytedesk.ticket.escalation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface EscalationHistoryRepository extends JpaRepository<EscalationHistoryEntity, Long> {
    
    Optional<EscalationHistoryEntity> findTopByTicketIdOrderByCreatedAtDesc(Long ticketId);
    
    List<EscalationHistoryEntity> findByTicketId(Long ticketId);
    
    @Query("SELECT h FROM EscalationHistoryEntity h WHERE h.ticketId = ?1 AND " +
           "h.createdAt BETWEEN ?2 AND ?3 ORDER BY h.createdAt DESC")
    List<EscalationHistoryEntity> findTicketEscalations(Long ticketId, 
            LocalDateTime startTime, LocalDateTime endTime);
}
