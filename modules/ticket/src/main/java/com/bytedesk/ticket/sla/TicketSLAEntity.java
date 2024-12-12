package com.bytedesk.ticket.sla;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_sla")
@EqualsAndHashCode(callSuper = true)
public class TicketSLAEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "first_response_time")
    private Integer firstResponseTime;  // 首次响应时限(分钟)

    @Column(name = "resolution_time")
    private Integer resolutionTime;  // 解决时限(分钟)

    private String priority;  // 适用的优先级: urgent, high, normal, low

    @Column(name = "category_id")
    private Long categoryId;  // 适用的分类

    @Column(name = "business_hours_only")
    private Boolean businessHoursOnly = true;  // 是否仅在工作时间内计算

    @Column(name = "escalation_time")
    private Integer escalationTime;  // 升级时限(分钟)

    @Column(name = "escalation_user_id")
    private Long escalationUserId;  // 升级后分配给谁
} 