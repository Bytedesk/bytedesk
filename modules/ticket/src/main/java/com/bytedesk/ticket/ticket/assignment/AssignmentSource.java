/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Source of ticket assignment resolution.
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

/**
 * Identifies which layer produced the final assignee decision.
 */
public enum AssignmentSource {
    /** Explicit configuration on the workflow node (assigneeType + assigneeUids / role / department) */
    NODE_CONFIG,
    /** Global fallback strategy from TicketBasicSettings.assignmentMode */
    GLOBAL_STRATEGY,
    /** Manual assign/claim/transfer operation by a human */
    MANUAL,
    /** Filled from the ticket reporter */
    REPORTER,
    /** System automatic resolution (e.g., after task completion) */
    AUTOMATIC
}
