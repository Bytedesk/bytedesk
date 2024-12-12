package com.bytedesk.ticket.assignment;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_assignment_rule")
@EqualsAndHashCode(callSuper = true)
public class AssignmentRuleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "category_id")
    private Long categoryId;  // 适用的工单分类
    
    @Column(name = "priority_level")
    private String priorityLevel;  // 适用的优先级
    
    @Column(name = "required_skills")
    private String requiredSkills;  // 所需技能，逗号分隔
    
    @Column(name = "max_active_tickets")
    private Integer maxActiveTickets = 10;  // 最大活跃工单数
    
    @Column(name = "consider_workload")
    private Boolean considerWorkload = true;  // 是否考虑工作量
    
    @Column(name = "consider_online_status")
    private Boolean considerOnlineStatus = true;  // 是否考虑在线状态
    
    @Column(name = "weight")
    private Double weight = 1.0;  // 规则权重
    
    private Boolean enabled = true;
} 