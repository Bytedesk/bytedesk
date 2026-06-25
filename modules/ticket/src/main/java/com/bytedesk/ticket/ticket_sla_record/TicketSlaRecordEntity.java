package com.bytedesk.ticket.ticket_sla_record;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
@Table(name = "bytedesk_ticket_sla_record", indexes = {
        @Index(name = "idx_ticket_sla_record_ticket_type", columnList = "ticket_uid,sla_type"),
        @Index(name = "idx_ticket_sla_record_due_at", columnList = "due_at"),
        @Index(name = "idx_ticket_sla_record_status", columnList = "status")
})
public class TicketSlaRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // Associated ticket UID / 关联工单UID
    @Column(name = "ticket_uid")
    private String ticketUid;

    // Flowable process instance ID / 流程实例ID
    @Column(name = "process_instance_id")
    private String processInstanceId;

    // SLA type (CLAIM/FIRST_RESPONSE/RESOLUTION/CUSTOMER_VERIFY) / SLA类型
    @Column(name = "sla_type", length = 32)
    private String slaType;
    // Record status (running/paused/completed/breached) / 记录状态
    @Column(length = 32)
    private String status;
    // Priority level / 优先级
    private String priority;
    // Category UID / 分类UID
    private String categoryUid;
    // SLA duration in minutes / SLA时限（分钟）
    private Long durationMinutes;
    // SLA start timestamp / SLA开始时间
    private ZonedDateTime startedAt;
    // SLA due timestamp / SLA到期时间
    @Column(name = "due_at")
    private ZonedDateTime dueAt;
    // Pause timestamp / 暂停时间
    private ZonedDateTime pausedAt;
    // Total paused duration in seconds / 累计暂停时长（秒）
    @Builder.Default
    private Long pausedDurationSeconds = 0L;
    // Completion timestamp / 完成时间
    private ZonedDateTime completedAt;
    // Breach timestamp / 超时时间
    private ZonedDateTime breachedAt;
    // Breach reason / 超时原因
    private String breachReason;
    // Completed by user / 完成人
    private String completedBy;
    // Whether SLA was breached / 是否已超时
    @Builder.Default
    private Boolean breached = Boolean.FALSE;
}