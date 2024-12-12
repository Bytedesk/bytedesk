package com.bytedesk.ticket.ticket.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketSearchRequest {
    private String keyword;              // 搜索关键词
    private List<Long> categoryIds;      // 分类ID列表
    private List<String> statuses;       // 状态列表
    private List<String> priorities;     // 优先级列表
    private Long assignedTo;             // 处理人ID
    private Long userId;                 // 提交人ID
    private LocalDateTime startTime;     // 开始时间
    private LocalDateTime endTime;       // 结束时间
    private List<String> tags;           // 标签列表
    private Boolean overdue;             // 是否逾期
    private Boolean unassigned;          // 是否未分配
} 