package com.bytedesk.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bytedesk.ticket.model.TicketAttachment;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {
} 