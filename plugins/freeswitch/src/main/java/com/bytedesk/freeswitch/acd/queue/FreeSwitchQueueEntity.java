package com.bytedesk.freeswitch.acd.queue;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * FreeSwitch队列实体类
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "freeswitch_acd_queues")
public class FreeSwitchQueueEntity extends BaseEntity {

    /**
     * 队列名称
     */
    @Column(unique = true, nullable = false)
    private String name;
    
    /**
     * 队列类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueType type;
    
    /**
     * 队列技能（JSON格式）
     */
    private String skills;
    
    /**
     * 最大等待时间（秒）
     */
    private Integer maxWaitTime;
    
    /**
     * 最大队列长度
     */
    private Integer maxLength;
    
    /**
     * 队列权重
     */
    private Integer weight;
    
    /**
     * 队列状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 队列类型枚举
     */
    public enum QueueType {
        INBOUND,    // 呼入队列
        OUTBOUND,   // 呼出队列
        INTERNAL    // 内部队列
    }
    
    /**
     * 队列状态枚举
     */
    public enum QueueStatus {
        ACTIVE,     // 激活
        INACTIVE,   // 未激活
        PAUSED      // 暂停
    }
} 