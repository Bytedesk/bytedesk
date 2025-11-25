package com.bytedesk.service.queue;

// import java.util.Optional;

// import org.springframework.orm.ObjectOptimisticLockingFailureException;
// import org.springframework.stereotype.Service;
// import org.springframework.util.StringUtils;

// import lombok.extern.slf4j.Slf4j;
// import lombok.AllArgsConstructor;

// /**
//  * Lightweight facade for scheduling automatic queue assignments. Real dequeue logic
//  * will be wired in later tasks; for now we centralize trigger bookkeeping so
//  * routing strategies have a single place to notify when capacity becomes
//  * available.
//  */
// @Slf4j
// @Service
// @AllArgsConstructor
// public class QueueAutoAssignService {

//     private static final int RETRY_MULTIPLIER = 3;

//     private final QueueService queueService;

//     /**
//      * Signal that an agent has at least one free slot and we should try to pull
//      * the next visitor waiting for them.
//      */
//     public void triggerAgentAutoAssign(String agentUid,
//             QueueAutoAssignTriggerSource source,
//             int slotsHint) {
//         if (!StringUtils.hasText(agentUid)) {
//             log.debug("Skip agent auto-assign trigger without agent uid (source={})", source);
//             return;
//         }
//         int normalizedSlots = Math.max(1, slotsHint);
//         log.debug("Queue auto-assign scheduled for agent {} (source={}, slotsHint={})",
//                 agentUid, source, normalizedSlots);

//         int assignments = 0;
//         int attempts = 0;
//         int maxAttempts = Math.max(normalizedSlots * RETRY_MULTIPLIER, RETRY_MULTIPLIER);

//         while (assignments < normalizedSlots && attempts < maxAttempts) {
//             attempts++;
//             try {
//                 Optional<QueueService.QueueAssignmentResult> result = queueService.assignNextAgentQueueMember(agentUid);
//                 if (!result.isPresent()) {
//                     if (attempts == 1) {
//                         log.trace("Agent {} trigger {} found no waiting visitors", agentUid, source);
//                     }
//                     break;
//                 }
//                 QueueService.QueueAssignmentResult assignment = result.get();
//                 log.info("Auto-assign success (source={}): agent={}, thread={}, queueMember={}",
//                         source, assignment.agentUid(), assignment.threadUid(), assignment.queueMemberUid());
//                 assignments++;
//             } catch (ObjectOptimisticLockingFailureException ex) {
//                 log.debug("Optimistic lock while auto-assigning agent {} (source={}, attempt={} of {}): {}",
//                         agentUid, source, attempts, maxAttempts, ex.getMessage());
//                 // Retry the same slot so do not increment assignments
//             }
//         }

//         if (assignments < normalizedSlots && attempts >= maxAttempts) {
//             log.warn("Aborted agent {} auto-assign after {} attempts (source={})", agentUid, attempts, source);
//         }
//     }

//     /**
//      * Signal that a workgroup queue should try to move its head visitor to the
//      * provided agent (if still eligible) when capacity allows.
//      */
//     public void triggerWorkgroupAutoAssign(String workgroupUid,
//             String preferredAgentUid,
//             QueueAutoAssignTriggerSource source,
//             int slotsHint) {
//         if (!StringUtils.hasText(workgroupUid)) {
//             log.debug("Skip workgroup auto-assign trigger without workgroup uid (source={})", source);
//             return;
//         }
//         int normalizedSlots = Math.max(1, slotsHint);
//         log.debug("Queue auto-assign scheduled for workgroup {} (preferredAgent={}, source={}, slotsHint={})",
//                 workgroupUid, preferredAgentUid, source, normalizedSlots);
//     }
// }
