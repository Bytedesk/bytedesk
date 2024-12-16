package com.bytedesk.service.queue;

import lombok.Data;

/**
 * 队列统计信息
 */
@Data
class QueueStats {
    private int totalThreads;          // 总会话数
    private int waitingThreads;        // 等待中会话数
    private int processingThreads;     // 处理中会话数
    private int avgWaitTime;          // 平均等待时间(分钟)
    private int maxWaitTime;          // 最长等待时间(分钟)
    private double timeoutRate;        // 超时率
    private double cancelRate;         // 取消率
}