package com.bytedesk.service.queue;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.Data;

/**
 * 会话排队服务接口
 */
public interface QueueService {

    /**
     * 添加会话到队列
     * @param threadUid 会话UID
     * @param priority 优先级(1-10,数字越大优先级越高)
     * @return 队列位置
     */
    int enqueue(String threadUid, int priority);
    
    /**
     * 从队列中移除会话
     * @param threadUid 会话UID
     * @param status 结束状态
     */
    void dequeue(String threadUid, QueueStatusEnum status);
    
    /**
     * 获取队列中的会话
     * @param status 状态过滤
     * @return 会话UID列表
     */
    List<String> getQueuedThreads(QueueStatusEnum status);
    
    /**
     * 获取会话在队列中的位置
     * @param threadUid 会话UID
     * @return 队列位置,如果不在队列中返回-1
     */
    int getQueuePosition(String threadUid);
    
    /**
     * 更新会话排队状态
     * @param threadUid 会话UID
     * @param status 新状态
     */
    void updateStatus(String threadUid, QueueStatusEnum status);
    
    /**
     * 获取预计等待时间(分钟)
     * @param threadUid 会话UID
     * @return 预计等待时间,无法估算时返回-1
     */
    int getEstimatedWaitTime(String threadUid);
    
    /**
     * 检查并处理超时的会话
     */
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    void checkQueueTimeout();
    
    /**
     * 处理排队队列
     */
    @Scheduled(fixedRate = 5000) // 每5秒处理一次
    void processQueue();
    
    /**
     * 获取队列统计信息
     * @return 统计信息
     */
    QueueStats getQueueStats();
}

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