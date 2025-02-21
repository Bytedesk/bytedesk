/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 16:02:03
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-21 14:43:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_message;

import java.util.Date;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 记录客服回复访客消息时间戳
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic_message")
public class StatisticMessageEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 所属会话uid
    private String threadUid;

    // 消息类型
    private String messageType;

    // 消息状态
    private String status;

    // 访客消息uid
    private String visitorMessageUid;

    // 访客消息创建时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "message_at")
    private Date messageAt;
 
    // 客服回复消息uid
    private String responseMessageUid;

    // 客服回复消息回复时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "response_at")
    private Date responseAt;

    // 计算客服响应时长
    @Builder.Default
    @Column(name = "response_duration")
    private long responseDuration = 0;


    // 消息归属客服
    private String agentUid;

    // 消息归属工作组
    private String workgroupUid;

    // 消息归属机器人
    private String robotUid;

    // 消息归属组织, 在baseEntity中
    // private String orgUid;

    // 消息创建时间，继承自BaseEntity
    // @Temporal(TemporalType.TIMESTAMP)
    // @Column(name = "created_at", updatable = false)


    
}
