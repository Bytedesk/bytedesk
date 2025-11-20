package com.bytedesk.service.queue.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for manually triggering the next queue assignment for a given agent.
 */
@Data
public class AgentQueueAssignmentRequest {

    /** Optional org hint to ensure callers target the correct tenant. */
    private String orgUid;

    /** Optional freeform audit reason for requesting the assignment. */
    @Size(max = 255)
    private String reason;

    /** Optional override for how many slots to try filling (default = 1). */
    private Integer slotsHint;
}
