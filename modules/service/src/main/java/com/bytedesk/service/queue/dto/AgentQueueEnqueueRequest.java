package com.bytedesk.service.queue.dto;

import java.util.Map;

import com.bytedesk.service.visitor.VisitorRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for POST /api/v1/queue/agent/{agentUid}/members.
 * Carries the thread that should join the queue plus optional metadata
 * that future auditing/logging can persist.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentQueueEnqueueRequest {

    @NotBlank(message = "threadUid is required")
    private String threadUid;

    /**
     * Optional logical source identifier (workgroup, campaign, button, etc.).
     */
    private String source;

    /**
     * Optional visitor payload for downstream enrichment. Not currently
     * consumed by QueueService but retained for parity with the HTTP contract.
     */
    private VisitorRequest visitor;

    /**
     * Arbitrary metadata bag callers may include for auditing.
     */
    @Builder.Default
    private Map<String, String> metadata = java.util.Collections.emptyMap();
}
