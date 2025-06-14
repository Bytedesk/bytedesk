package com.bytedesk.freeswitch.acd.call;

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
 * FreeSwitch呼叫实体类
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "freeswitch_acd_calls")
public class FreeSwitchCallEntity extends BaseEntity {

    /**
     * 呼叫UUID
     */
    @Column(unique = true, nullable = false)
    private String callUuid;
    
    /**
     * 呼叫类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallType type;
    
    /**
     * 主叫号码
     */
    private String callerNumber;
    
    /**
     * 被叫号码
     */
    private String calleeNumber;
    
    /**
     * 呼叫状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;
    
    /**
     * 呼叫开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 呼叫结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 呼叫时长（秒）
     */
    private Integer duration;
    
    /**
     * 等待时长（秒）
     */
    private Integer waitTime;
    
    /**
     * 队列ID
     */
    private Long queueId;
    
    /**
     * 坐席ID
     */
    private Long agentId;
    
    /**
     * 呼叫技能（JSON格式）
     */
    private String skills;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 呼叫类型枚举
     */
    public enum CallType {
        INBOUND,    // 呼入
        OUTBOUND,   // 呼出
        INTERNAL    // 内部
    }
    
    /**
     * 呼叫状态枚举
     */
    public enum CallStatus {
        QUEUED,     // 排队中
        RINGING,    // 振铃中
        IN_PROGRESS,// 通话中
        COMPLETED,  // 已完成
        FAILED,     // 失败
        ABANDONED   // 放弃
    }
} 