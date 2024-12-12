package com.bytedesk.ticket.escalation;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_escalation_history")
@EqualsAndHashCode(callSuper = true)
public class EscalationHistoryEntity extends BaseEntity {

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;
    
    @Column(name = "from_user_id")
    private Long fromUserId;
    
    @Column(name = "to_user_id")
    private Long toUserId;
    
    private String reason;
    
    @Column(name = "notified_users")
    private String notifiedUsers;  // 已通知用户列表
} 