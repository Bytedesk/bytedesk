/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 11:26:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:34:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.queue;

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
 * Call队列实体类
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "bytedesk_call_queue")
public class CallQueueEntity extends BaseEntity {

    /**
     * 队列名称
     */
    @Column(unique = true, nullable = false)
    private String name;
    
    /**
     * 队列类型
     */
    @Enumerated(EnumType.STRING)
    
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