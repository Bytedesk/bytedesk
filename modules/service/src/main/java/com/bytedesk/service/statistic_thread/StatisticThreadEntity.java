/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 09:16:05
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-21 14:40:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_thread;

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
 * 跟踪记录thread更新链路
 * 记录会话处理链路，从访客进入，接入客服，转接客服，结束会话、监控会话等，全部记录到数据库中
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic_thread")
public class StatisticThreadEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String threadUid;

    private String threadTopic;

    private String status;

    // 是否有排队
    @Builder.Default
    @Column(name = "is_queued")
    private boolean isQueued = false;

    // 排队接入客服时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "queue_accepted_at")
    private Date queueAcceptedAt;
    
    // 计算排队时长
    @Builder.Default
    @Column(name = "queue_duration")
    private long queueDuration = 0;

    // 客服首次响应时间，客服发送第一条消息时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "first_response_at")
    private Date firstResponseAt;

    // 计算响应时长
    @Builder.Default
    @Column(name = "response_duration")
    private long responseDuration = 0;

    // 是否机器人接待
    @Builder.Default
    @Column(name = "is_robot")
    private boolean isRobot = false;

    // 是否已解决
    @Builder.Default
    @Column(name = "is_solved")
    private boolean isSolved = false;

    // 会话问题解决时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "solved_at")
    private Date solvedAt;

    // 是否已经评价
    @Builder.Default
    @Column(name = "is_rated")
    private boolean isRated = false;

    // 会话评价时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rated_at")
    private Date ratedAt;

    // 是否已关闭
    @Builder.Default
    @Column(name = "is_closed")
    private boolean isClosed = false;

    // 会话关闭时间
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closed_at")
    private Date closedAt;
    // 计算会话时长
    @Builder.Default
    @Column(name = "duration")
    private long duration = 0;

    // 会话类型
    @Column(name = "thread_type")
    private String type;

    // 会话归属客服
    private String agentUid;

    // 会话归属工作组
    private String workgroupUid;

    // 会话归属机器人
    private String robotUid;

    // 会话归属组织, 在baseEntity中
    // private String orgUid;

    // 会话创建时间，继承自BaseEntity
    // @Temporal(TemporalType.TIMESTAMP)
    // @Column(name = "created_at", updatable = false)
}
