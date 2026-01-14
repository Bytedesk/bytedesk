/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-13
 * @Description: Queue meta info for ThreadResponse
 */
package com.bytedesk.core.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Queue-related meta info attached to {@link ThreadResponse}.
 *
 * Notes:
 * - This lives in core module on purpose (no dependency on service module).
 * - Service module can populate these fields from QueueMemberEntity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueMeta {

    /** QueueMember uid */
    private String queueMemberUid;

    /** Position in queue (optional) */
    private Integer position;

    /** Queue size (optional) */
    private Integer queueSize;

    /** Estimated wait time (optional) */
    private Long estimatedWaitMs;

    /** Server timestamp when meta is generated (epoch millis) */
    private Long serverTimestamp;

    /** Display name (optional) */
    private String displayName;

    /** Visitor enqueue time (epoch millis) */
    private Long enqueuedAt;

    /** Current waiting time (epoch millis diff, optional) */
    private Long waitingMs;
}
