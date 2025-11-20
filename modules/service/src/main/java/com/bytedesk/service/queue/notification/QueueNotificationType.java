package com.bytedesk.service.queue.notification;

/**
 * 消息类型，与 message 枚举保持一致。
 */
public enum QueueNotificationType {
    QUEUE_NOTICE,
    QUEUE_UPDATE,
    QUEUE_TIMEOUT,
    QUEUE_ACCEPT
}
