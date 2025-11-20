package com.bytedesk.service.queue;

/**
 * Enumerates the known sources that can kick off an automatic queue assignment.
 */
public enum QueueAutoAssignTriggerSource {
    /** Triggered when an agent status/presence change frees up capacity. */
    AGENT_STATUS_EVENT,
    /** Triggered when workgroup capacity signals are processed. */
    WORKGROUP_STATUS_EVENT,
    /** Triggered manually via REST/RPC endpoints once implemented. */
    MANUAL_ENDPOINT
}
