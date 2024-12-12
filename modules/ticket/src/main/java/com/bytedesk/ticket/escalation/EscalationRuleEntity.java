package com.bytedesk.ticket.escalation;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_escalation_rule")
@EqualsAndHashCode(callSuper = true)
public class EscalationRuleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "category_id")
    private Long categoryId;  // 适用的分类
    
    private String priority;  // 适用的优先级
    
    @Column(name = "condition_type")
    @Enumerated(EnumType.STRING)
    private EscalationConditionType conditionType;  // 升级条件类型
    
    @Column(name = "condition_value")
    private Integer conditionValue;  // 条件值(分钟)
    
    @Column(name = "target_user_id")
    private Long targetUserId;  // 升级目标用户
    
    @Column(name = "target_group_id")
    private Long targetGroupId;  // 升级目标组
    
    @Column(name = "notify_users")
    private String notifyUsers;  // 通知用户列表，逗号分隔
    
    private Boolean enabled = true;
}

enum EscalationConditionType {
    NO_RESPONSE,          // 未响应
    NO_UPDATE,           // 无更新
    OVERDUE,            // 超时
    SLA_BREACH         // SLA违反
} 