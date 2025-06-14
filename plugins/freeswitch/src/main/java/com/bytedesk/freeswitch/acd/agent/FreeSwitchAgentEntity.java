package com.bytedesk.freeswitch.acd.agent;

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
 * FreeSwitch坐席实体类
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "freeswitch_acd_agents")
public class FreeSwitchAgentEntity extends BaseEntity {
    
    /**
     * 坐席唯一标识
     */
    @Column(unique = true, nullable = false)
    private String agentId;
    
    /**
     * 坐席名称
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * 坐席状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status;
    
    /**
     * 坐席模式
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentMode mode;
    
    /**
     * 坐席技能（JSON格式）
     */
    private String skills;
    
    /**
     * 最后状态变更时间
     */
    private LocalDateTime lastStatusChange;
    
    /**
     * 当前处理的呼叫UUID
     */
    private String currentCallUuid;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 坐席状态枚举
     */
    public enum AgentStatus {
        OFFLINE,    // 离线
        ONLINE,     // 在线
        READY,      // 就绪
        BUSY,       // 忙碌
        AWAY,       // 离开
        BREAK       // 休息
    }
    
    /**
     * 坐席模式枚举
     */
    public enum AgentMode {
        MANUAL,     // 手动模式
        AUTO        // 自动模式
    }
} 