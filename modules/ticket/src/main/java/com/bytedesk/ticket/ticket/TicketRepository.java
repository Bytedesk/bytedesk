package com.bytedesk.ticket.ticket;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketRepository extends JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    Optional<TicketEntity> findByUid(String uid);

    Optional<TicketEntity> findByProcessInstanceId(String processInstanceId);
    
    long countByStatus(String status);
    long countByStatusNot(String status);
} 