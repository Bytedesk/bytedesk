package com.bytedesk.ticket.ticket.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketAssignRequest {
    
    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;
    
    private String reason;           // 分配原因
    private Boolean notifyAssignee;  // 是否通知处理人
    private String note;             // 备注
} 