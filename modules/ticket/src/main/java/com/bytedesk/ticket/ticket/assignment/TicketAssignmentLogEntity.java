/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Audit log for ticket assignment events (auto-assign, claim, assign, transfer).
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Records every assignment change for a ticket, including auto-assignment,
 * manual claim, manual assign, and transfer operations.
 */
@Entity
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_assignment_log")
public class TicketAssignmentLogEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Ticket UID */
    private String ticketUid;

    /** Flowable process instance ID */
    private String processInstanceId;

    /** Current task definition key (BPMN node ID) */
    private String taskDefinitionKey;

    /** Previous assignee JSON (UserProtobuf), null for initial assignment */
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String fromAssignee;

    /** New assignee JSON (UserProtobuf) */
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String toAssignee;

    /**
     * Type of assignment operation:
     * AUTO_WORKFLOW — auto-assigned from node config
     * AUTO_STRATEGY — auto-assigned from global strategy fallback
     * MANUAL_CLAIM — claimed by a member
     * MANUAL_ASSIGN — assigned by a supervisor
     * MANUAL_TRANSFER — transferred to another member
     */
    @Column(length = 32)
    private String assignmentType;

    /** Allocation strategy name (ROUND_ROBIN, LEAST_ACTIVE, RANDOM, MANUAL, etc.) */
    @Column(length = 64)
    private String strategy;

    /** Human-readable reason for this assignment */
    @Column(length = 512)
    private String reason;

    /** Summary of the candidate pool considered */
    @Column(length = 256)
    @Builder.Default
    private String candidatePoolDescription = "";
}
