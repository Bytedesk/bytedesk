package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bytedesk_ticket")
@EqualsAndHashCode(callSuper = true)
public class TicketEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // 提交工单的用户

    @Column(name = "assigned_to")
    private Long assignedTo;  // 处理人ID

    @Column(name = "category_id")
    private Long categoryId;  // 工单分类

    private String priority = "normal";  // urgent, high, normal, low

    private String status = "open";  // open, processing, resolved, closed, reopened

    private String source = "web";  // web, app, email, phone, chat

    @Column(name = "due_date")
    private LocalDateTime dueDate;  // 截止日期

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;  // 解决时间

    @Column(name = "closed_at")
    private LocalDateTime closedAt;  // 关闭时间

    @Column(name = "first_response_time")
    private Long firstResponseTime;  // 首次响应时间(分钟)

    @Column(name = "resolution_time")
    private Long resolutionTime;  // 解决用时(分钟)

    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating;  // 满意度评分(1-5)

    @Column(name = "satisfaction_comment")
    private String satisfactionComment;  // 满意度评价
} 