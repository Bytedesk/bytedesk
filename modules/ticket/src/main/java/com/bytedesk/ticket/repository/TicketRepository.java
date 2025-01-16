package com.bytedesk.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bytedesk.ticket.model.TicketEntity;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    long countByStatus(String status);
    long countByStatusNot(String status);
} 