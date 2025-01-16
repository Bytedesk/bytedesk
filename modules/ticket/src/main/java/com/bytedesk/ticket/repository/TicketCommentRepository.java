package com.bytedesk.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bytedesk.ticket.model.TicketComment;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
} 