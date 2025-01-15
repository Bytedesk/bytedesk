package com.bytedesk.ticket.agi.assign;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_assignment_config")
@EqualsAndHashCode(callSuper = true)
public class AssignmentConfigEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStrategy strategy;

    @Column(name = "category_id")
    private Long categoryId;  // 适用的分类

    private String priority;  // 适用的优先级

    @Column(name = "agent_ids")
    private String agentIds;  // 可分配的客服ID列表，逗号分隔

    @Column(name = "max_active_tickets")
    private Integer maxActiveTickets = 10;  // 每个客服最大活跃工单数

    private Boolean enabled = true;  // 是否启用
} 