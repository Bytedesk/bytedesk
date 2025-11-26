package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueNotification extends BaseContent {

    private static final long serialVersionUID = 1L;
    
    /** High level channel that consumers use to route the message (notice/update/timeout/etc). */
    // QueueNotificationType type;

    /** Fine grained delta describing what changed for this queue member. */
    // QueueNotificationDelta delta;

    /** Queue member that triggered the update (null only for legacy fallbacks). */
    String queueMemberUid;

    /** Associated conversation thread uid so clients can correlate with chat sessions. */
    String threadUid;

    String threadTopic;

    /** Agent currently responsible for the visitor, falls back to provided agent when unknown. */
    // String agentUid;

    /** Member position in the agent queue; null when no longer waiting (e.g. assigned). */
    Integer position;

    /** Current queue size exposed for realtime dashboards, may be null when queue missing. */
    Integer queueSize;

    /** Estimated milliseconds until assignment using the same heuristic shown in Builder. */
    Long estimatedWaitMs;

    /** Populated only for QUEUE_UPDATE payloads so clients can refresh multiple members at once. */
    // List<QueueNotificationSnapshot> snapshot;

    /** Server side epoch millis when the payload was produced. */
    long serverTimestamp;

    // UserProtobuf访客json信息 
    String user;

    // @Value
    // @Builder
    // public static class QueueNotificationSnapshot {
    //     /** Queue member represented by this snapshot entry. */
    //     String queueMemberUid;

    //     /** Human friendly visitor name rendered on the queue list. */
    //     String displayName;

    //     /** Position for this member at the time the snapshot was generated. */
    //     Integer position;
    // }
}
