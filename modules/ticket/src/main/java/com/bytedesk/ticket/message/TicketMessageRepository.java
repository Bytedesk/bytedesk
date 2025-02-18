package com.bytedesk.ticket.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketMessageRepository extends JpaRepository<TicketMessageEntity, Long> {
    List<TicketMessageEntity> findByStatusAndRetryCountLessThan(TicketMessageStatusEnum status, int retryCount);
}
