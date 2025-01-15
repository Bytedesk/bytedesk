package com.bytedesk.ticket.agi.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, Long> {
    
    List<TicketCommentEntity> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
    
    List<TicketCommentEntity> findByParentIdOrderByCreatedAtAsc(Long parentId);
    
    @Query("SELECT c FROM TicketCommentEntity c WHERE c.ticketId = ?1 AND c.internal = false " +
           "ORDER BY c.createdAt ASC")
    List<TicketCommentEntity> findPublicComments(Long ticketId);
    
    @Query("SELECT c FROM TicketCommentEntity c WHERE c.ticketId = ?1 AND c.internal = true " +
           "ORDER BY c.createdAt ASC")
    List<TicketCommentEntity> findInternalComments(Long ticketId);
} 