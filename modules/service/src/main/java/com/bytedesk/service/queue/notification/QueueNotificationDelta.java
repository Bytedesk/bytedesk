package com.bytedesk.service.queue.notification;

/**
 * 代表队列事件发生的增量类型。
 */
public enum QueueNotificationDelta {
    JOINED,
    LEFT,
    TIMEOUT,
    ASSIGNED,
    BULK_CLEANUP
}
