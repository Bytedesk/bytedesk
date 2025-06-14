package com.bytedesk.freeswitch.acd.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FreeSwitch队列统计信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeSwitchQueueStatistics {
    
    /**
     * 队列名称
     */
    private String queueName;
    
    /**
     * 总呼叫数
     */
    private int totalCalls;
    
    /**
     * 等待中的呼叫数
     */
    private int waitingCalls;
    
    /**
     * 振铃中的呼叫数
     */
    private int ringingCalls;
    
    /**
     * 已接听的呼叫数
     */
    private int answeredCalls;
    
    /**
     * 平均等待时间（秒）
     */
    private int avgWaitTime;
    
    /**
     * 计算应答率
     */
    public double getAnswerRate() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) answeredCalls / totalCalls * 100;
    }
    
    /**
     * 计算放弃率
     */
    public double getAbandonRate() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) (totalCalls - answeredCalls - waitingCalls) / totalCalls * 100;
    }
    
    /**
     * 计算服务水平
     * 服务水平 = (已接听 + 振铃中) / 总呼叫数 * 100%
     */
    public double getServiceLevel() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) (answeredCalls + ringingCalls) / totalCalls * 100;
    }
} 