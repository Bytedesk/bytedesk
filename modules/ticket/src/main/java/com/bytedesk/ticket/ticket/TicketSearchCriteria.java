package com.bytedesk.ticket.ticket;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketSearchCriteria {
    private String keyword;
    private Long userId;
    private Long assignedTo;
    private Long categoryId;
    private String status;
    private String priority;
    private String source;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isOverdue;
} 