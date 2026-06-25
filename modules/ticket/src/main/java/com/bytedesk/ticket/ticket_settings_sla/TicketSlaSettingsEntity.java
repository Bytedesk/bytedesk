package com.bytedesk.ticket.ticket_settings_sla;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.ticket.ticket_sla.TicketSlaTypeEnum;
import com.bytedesk.ticket.ticket_sla_rule.TicketSlaRuleEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table(name = "bytedesk_ticket_sla_settings")
public class TicketSlaSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // Enable SLA / 启用SLA
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = Boolean.TRUE;

    // Only count working hours / 仅工作时间计时
    @Builder.Default
    private Boolean businessHoursEnabled = Boolean.FALSE;

    // Business hours start time / 工作开始时间
    @Builder.Default
    private String businessHoursStartTime = "09:00";

    // Business hours end time / 工作结束时间
    @Builder.Default
    private String businessHoursEndTime = "18:00";

    // Business hours timezone / 工作时区
    @Builder.Default
    private String businessHoursTimezone = "Asia/Shanghai";

    // Holiday calendar country code / 节假日口径（国家代码）
    @Builder.Default
    private String businessHoursCountryCode = "CN";

    // Pause SLA when ticket is on hold / 挂起时暂停SLA计时
    @Builder.Default
    private Boolean pauseOnHold = Boolean.FALSE;

    // Notify when SLA is breached / 超时时发送通知
    @Builder.Default
    private Boolean notifyOnBreach = Boolean.TRUE;

    // Auto escalate on SLA breach / 超时自动升级转派
    @Builder.Default
    private Boolean autoEscalateEnabled = Boolean.FALSE;

    // Target assignee UID for escalation / 升级转派目标处理人UID
    @Column(name = "escalate_assignee_uid")
    private String escalateAssigneeUid;

    // Auto close ticket when customer pending times out / 客户确认超时自动关闭
    @Builder.Default
    private Boolean autoCloseCustomerPendingEnabled = Boolean.FALSE;

    // Wait hours before auto-close for customer verification / 客户确认自动关闭等待小时数
    @Builder.Default
    private Integer customerVerifyAutoCloseHours = 168;

    /**
     * Warning threshold percentage / 预警百分比阈值
     * 
     * 当 SLA 规则的已消耗时间达到其时限的该百分比时，触发预警通知。
     * 默认 80%：例如 30 分钟的时限，在 24 分钟时预警。
     * 
     * 与规则级 warningMinutes 的关系：
     * - 规则级 warningMinutes 优先，为空时回退使用此全局百分比计算预警时间
     */
    @Builder.Default
    private Integer warningPercent = 80;

    // SLA rules list / SLA规则列表
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sla_settings_uid", referencedColumnName = "uuid")
    private List<TicketSlaRuleEntity> rules = new ArrayList<>();

    public static TicketSlaSettingsEntity fromRequest(TicketSlaSettingsRequest request, Supplier<String> uidSupplier, String orgUid) {
        TicketSlaSettingsEntity entity = new TicketSlaSettingsEntity();
        entity.setUid(uidSupplier.get());
        if (request != null) {
            applyRequest(entity, request, uidSupplier, orgUid);
        }
        if (entity.getRules() == null || entity.getRules().isEmpty()) {
            entity.setRules(defaultRules(uidSupplier, orgUid));
        }
        return entity;
    }

    public static void applyRequest(TicketSlaSettingsEntity entity, TicketSlaSettingsRequest request,
            Supplier<String> uidSupplier, String orgUid) {
        if (entity == null || request == null) return;
        if (request.getEnabled() != null) entity.setEnabled(request.getEnabled());
        if (request.getBusinessHoursEnabled() != null) entity.setBusinessHoursEnabled(request.getBusinessHoursEnabled());
        if (request.getBusinessHoursStartTime() != null) entity.setBusinessHoursStartTime(request.getBusinessHoursStartTime());
        if (request.getBusinessHoursEndTime() != null) entity.setBusinessHoursEndTime(request.getBusinessHoursEndTime());
        if (request.getBusinessHoursTimezone() != null) entity.setBusinessHoursTimezone(request.getBusinessHoursTimezone());
        if (request.getBusinessHoursCountryCode() != null) entity.setBusinessHoursCountryCode(request.getBusinessHoursCountryCode());
        if (request.getPauseOnHold() != null) entity.setPauseOnHold(request.getPauseOnHold());
        if (request.getNotifyOnBreach() != null) entity.setNotifyOnBreach(request.getNotifyOnBreach());
        if (request.getAutoEscalateEnabled() != null) entity.setAutoEscalateEnabled(request.getAutoEscalateEnabled());
        if (request.getEscalateAssigneeUid() != null) entity.setEscalateAssigneeUid(request.getEscalateAssigneeUid());
        if (request.getAutoCloseCustomerPendingEnabled() != null) entity.setAutoCloseCustomerPendingEnabled(request.getAutoCloseCustomerPendingEnabled());
        if (request.getCustomerVerifyAutoCloseHours() != null) entity.setCustomerVerifyAutoCloseHours(request.getCustomerVerifyAutoCloseHours());
        if (request.getWarningPercent() != null) entity.setWarningPercent(request.getWarningPercent());
        if (request.getRules() != null && !request.getRules().isEmpty()) {
            entity.getRules().clear();
            request.getRules().forEach(ruleRequest -> entity.getRules().add(TicketSlaRuleEntity.fromRequest(ruleRequest, uidSupplier, orgUid)));
        }
    }

    public static List<TicketSlaRuleEntity> defaultRules(Supplier<String> uidSupplier, String orgUid) {
        List<TicketSlaRuleEntity> rules = new ArrayList<>();
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CLAIM, "CRITICAL", 30L, 24L, 10);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CLAIM, "URGENT", 60L, 48L, 20);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CLAIM, "HIGH", 120L, 96L, 30);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CLAIM, "MEDIUM", 240L, 192L, 40);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CLAIM, "LOW", 480L, 384L, 50);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.RESOLUTION, "CRITICAL", 60L, 48L, 110);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.RESOLUTION, "URGENT", 120L, 96L, 120);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.RESOLUTION, "HIGH", 240L, 192L, 130);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.RESOLUTION, "MEDIUM", 480L, 384L, 140);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.RESOLUTION, "LOW", 1440L, 1152L, 150);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.FIRST_RESPONSE, "CRITICAL", 30L, 24L, 210);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.FIRST_RESPONSE, "URGENT", 60L, 48L, 220);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.FIRST_RESPONSE, "HIGH", 120L, 96L, 230);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.FIRST_RESPONSE, "MEDIUM", 240L, 192L, 240);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.FIRST_RESPONSE, "LOW", 480L, 384L, 250);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CUSTOMER_VERIFY, "CRITICAL", 1440L, 1152L, 310);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CUSTOMER_VERIFY, "URGENT", 1440L, 1152L, 320);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CUSTOMER_VERIFY, "HIGH", 1440L, 1152L, 330);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CUSTOMER_VERIFY, "MEDIUM", 1440L, 1152L, 340);
        addRule(rules, uidSupplier, orgUid, TicketSlaTypeEnum.CUSTOMER_VERIFY, "LOW", 1440L, 1152L, 350);
        return rules;
    }

    private static void addRule(List<TicketSlaRuleEntity> rules, Supplier<String> uidSupplier, String orgUid, TicketSlaTypeEnum type,
            String priority, Long durationMinutes, Long warningMinutes, int orderIndex) {
        rules.add(TicketSlaRuleEntity.builder()
                .uid(uidSupplier.get())
                .orgUid(orgUid)
                .slaType(type.name())
                .priority(priority)
                .durationMinutes(durationMinutes)
                .warningMinutes(warningMinutes)
                .enabled(Boolean.TRUE)
                .orderIndex(orderIndex)
                .build());
    }
}