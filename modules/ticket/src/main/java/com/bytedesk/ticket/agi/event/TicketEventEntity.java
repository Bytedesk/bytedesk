package com.bytedesk.ticket.agi.event;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_event")
@EqualsAndHashCode(callSuper = true)
public class TicketEventEntity extends BaseEntity {

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String event;  // created, assigned, status_changed, priority_changed, commented, etc.

    private String oldValue;
    private String newValue;

    private String description;  // 事件描述
} 