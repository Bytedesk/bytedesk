package com.bytedesk.service.queue;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Lightweight facade for scheduling automatic queue assignments. Real dequeue logic
 * will be wired in later tasks; for now we centralize trigger bookkeeping so
 * routing strategies have a single place to notify when capacity becomes
 * available.
 */
@Slf4j
@Service
public class QueueAutoAssignService {

    /**
     * Signal that an agent has at least one free slot and we should try to pull
     * the next visitor waiting for them.
     */
    public void triggerAgentAutoAssign(String agentUid,
            QueueAutoAssignTriggerSource source,
            int slotsHint) {
        if (!StringUtils.hasText(agentUid)) {
            log.debug("Skip agent auto-assign trigger without agent uid (source={})", source);
            return;
        }
        int normalizedSlots = Math.max(1, slotsHint);
        log.debug("Queue auto-assign scheduled for agent {} (source={}, slotsHint={})",
                agentUid, source, normalizedSlots);
    }

    /**
     * Signal that a workgroup queue should try to move its head visitor to the
     * provided agent (if still eligible) when capacity allows.
     */
    public void triggerWorkgroupAutoAssign(String workgroupUid,
            String preferredAgentUid,
            QueueAutoAssignTriggerSource source,
            int slotsHint) {
        if (!StringUtils.hasText(workgroupUid)) {
            log.debug("Skip workgroup auto-assign trigger without workgroup uid (source={})", source);
            return;
        }
        int normalizedSlots = Math.max(1, slotsHint);
        log.debug("Queue auto-assign scheduled for workgroup {} (preferredAgent={}, source={}, slotsHint={})",
                workgroupUid, preferredAgentUid, source, normalizedSlots);
    }
}
