/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 11:25:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 11:52:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.agent;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
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
@Table(name = "freeswitch_agent")
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
    private ZonedDateTime lastStatusChange;
    
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