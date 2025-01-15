package com.bytedesk.ticket.agi.satisfaction;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_satisfaction")
@EqualsAndHashCode(callSuper = true)
public class TicketSatisfactionEntity extends BaseEntity {

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "rating", nullable = false)
    private Integer rating;  // 1-5星评分
    
    @Column(name = "comment", length = 1000)
    private String comment;  // 评价内容
    
    @Column(name = "response_time_satisfaction")
    private Integer responseTimeSatisfaction;  // 响应时间满意度 1-5
    
    @Column(name = "solution_satisfaction")
    private Integer solutionSatisfaction;      // 解决方案满意度 1-5
    
    @Column(name = "service_attitude_satisfaction")
    private Integer serviceAttitudeSatisfaction;  // 服务态度满意度 1-5
    
    @Column(name = "agent_id")
    private Long agentId;  // 客服ID
    
    @Column(name = "category_id")
    private Long categoryId;  // 工单分类ID
} 