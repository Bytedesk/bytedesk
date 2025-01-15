package com.bytedesk.ticket.agi.skill;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_agent_skill")
@EqualsAndHashCode(callSuper = true)
public class AgentSkillEntity extends BaseEntity {

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    private Integer proficiency = 1;  // 熟练度 1-5

    @Column(name = "max_tickets")
    private Integer maxTickets = 10;  // 该分类最大工单数

    private Boolean enabled = true;
} 