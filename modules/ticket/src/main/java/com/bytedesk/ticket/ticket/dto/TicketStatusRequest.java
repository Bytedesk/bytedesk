package com.bytedesk.ticket.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketStatusRequest {
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String reason;       // 状态变更原因
    private String resolution;   // 解决方案(仅用于resolved状态)
    private Integer rating;      // 满意度评分(仅用于closed状态)
    private String comment;      // 评价内容(仅用于closed状态)
} 