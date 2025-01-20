package com.bytedesk.ticket.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    long countByStatus(String status);
    long countByStatusNot(String status);
} 