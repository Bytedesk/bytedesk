/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Structured result of ticket assignment resolution.
 *   Makes the assignment process traceable and auditable.
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

/**
 * Result of resolving a ticket assignee, capturing the outcome,
 * the source of the assignment, the strategy used, and a human-readable reason.
 *
 * @param assigneeUid           final assignee UID (null if unresolved)
 * @param source                which layer produced the result
 * @param strategy              allocation strategy name
 * @param reason                human-readable reason
 * @param candidatePoolDescription summary of candidate pool
 */
public record AssignmentResolutionResult(
        String assigneeUid,
        AssignmentSource source,
        String strategy,
        String reason,
        String candidatePoolDescription) {

    public static AssignmentResolutionResult resolved(String assigneeUid, AssignmentSource source,
                                                       String strategy, String reason,
                                                       String candidatePoolDescription) {
        return new AssignmentResolutionResult(assigneeUid, source, strategy, reason, candidatePoolDescription);
    }

    public static AssignmentResolutionResult unresolved(AssignmentSource source, String reason) {
        return new AssignmentResolutionResult(null, source, null, reason, null);
    }

    public boolean isResolved() {
        return assigneeUid != null && !assigneeUid.isBlank();
    }
}
