package com.bytedesk.ticket.ticket_sla_rule;

import java.util.function.Supplier;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_sla_rule")
public class TicketSlaRuleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // SLA type (CLAIM/FIRST_RESPONSE/RESOLUTION/CUSTOMER_VERIFY) / SLA类型
    @Column(name = "sla_type", length = 32)
    private String slaType;

    // Priority level (CRITICAL/URGENT/HIGH/MEDIUM/LOW) / 优先级
    @Column(length = 32)
    private String priority;

    // Category UID for scoped rules / 分类UID（用于按分类区分规则）
    @Column(name = "category_uid")
    private String categoryUid;

    // Duration in minutes / 时限（分钟）
    @Builder.Default
    private Long durationMinutes = 240L;

    /**
     * Warning threshold in minutes / 预警阈值（分钟）
     * 
     * 规则级别的预警时间，优先级高于全局 warningPercent。
     * 当已消耗时间达到此值时触发预警通知。
     * 为空时回退使用全局 warningPercent × durationMinutes 计算预警时间。
     */
    private Long warningMinutes;

    // Enable this rule / 启用此规则
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = Boolean.TRUE;

    // Sort order / 排序序号
    @Builder.Default
    private Integer orderIndex = 0;

    public static TicketSlaRuleEntity fromRequest(TicketSlaRuleRequest request, Supplier<String> uidSupplier, String orgUid) {
        TicketSlaRuleEntity entity = new TicketSlaRuleEntity();
        entity.setUid(uidSupplier.get());
        entity.setOrgUid(orgUid);
        if (request == null) {
            return entity;
        }
        entity.setSlaType(request.getSlaType());
        entity.setPriority(request.getPriority());
        entity.setCategoryUid(request.getCategoryUid());
        if (request.getDurationMinutes() != null) entity.setDurationMinutes(request.getDurationMinutes());
        entity.setWarningMinutes(request.getWarningMinutes());
        if (request.getEnabled() != null) entity.setEnabled(request.getEnabled());
        if (request.getOrderIndex() != null) entity.setOrderIndex(request.getOrderIndex());
        return entity;
    }
}