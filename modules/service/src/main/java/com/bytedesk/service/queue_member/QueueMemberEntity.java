/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:23:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-06 21:42:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.LocalDateTime;
import java.time.Duration;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.thread.ThreadIntentionTypeEnum;
import com.bytedesk.core.thread.ThreadQualityCheckResultEnum;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.core.thread.ThreadSummaryStatusEnum;
import com.bytedesk.core.thread.ThreadEmotionTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author Jackning <270580156@qq.com>
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ QueueMemberEntityListener.class })
@Table(name = "bytedesk_service_queue_member")
public class QueueMemberEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 多个queueMember对应一个queue
    @ManyToOne(fetch = FetchType.LAZY)
    private QueueEntity queue;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "thread_id", referencedColumnName = "id")
    private ThreadEntity thread;

    @Builder.Default
    private int queueNumber = 0;  // 排队号码

    @Builder.Default
    private LocalDateTime enqueueTime = LocalDateTime.now();  // 加入时间

    private LocalDateTime firstMessageTime;  // 访客首次发送消息时间

    private LocalDateTime lastMessageTime;  // 访客最后发送消息时间

    private LocalDateTime leaveTime;  // 离开时间

    private String acceptType ;  // 接入方式：自动、手动，不设置默认

    private LocalDateTime acceptTime;  // 开始服务时间

    @Builder.Default
    private boolean firstResponse = false;  // 人工客服是否首次响应

    private LocalDateTime firstResponseTime;  // 首次响应时间

    private LocalDateTime lastResponseTime;  // 最后响应时间

    private LocalDateTime closeTime;  // 结束时间

    @Builder.Default
    private int avgResponseTime = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private int maxResponseTime = 0;  // 最长响应时间(秒)

    @Builder.Default
    private int agentMessageCount = 0;  // 客服消息数量

    @Builder.Default
    private int visitorMessageCount = 0;  // 访客消息数量

    @Builder.Default
    @Column(name = "is_timeout")
    private boolean timeout = false; // 是否超时

    // 机器人对话超时时间
    private LocalDateTime robotTimeoutAt;

    // 人工对话超时时间
    private LocalDateTime humanTimeoutAt;

    @Builder.Default
    private int priority = 0;  // 优先级(0-100)

    // 直接在评价表里面根据threadUid查询是否已经评价
    // 是否被评价
    @Builder.Default
    @Column(name = "is_rated")
    private boolean rated = false;

    @Builder.Default
    private int rateLevel = 0;  // 评分等级

    // 直接在小结表里面根据threadUid查询是否已经小结
    // 是否已经小结
    @Builder.Default
    @Column(name = "is_summarized")
    private boolean summarized = false;

    // 是否已解决
    @Builder.Default
    @Column(name = "is_resolved")
    private boolean resolved = false;

    // 直接在质检表里面根据threadUid查询是否已经质检
    // 是否已经质检
    @Builder.Default
    @Column(name = "is_quality_checked")
    private boolean qualityChecked = false;

    // @Builder.Default
    // @Column(name = "was_offline")
    // private boolean wasOffline = false;  // 标记该会话是否曾处于离线状态
    
    // 重构到相应的表里面
    // 意图类型
    @Builder.Default
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 情绪类型
    @Builder.Default
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 质检结果
    @Builder.Default
    private String qualityCheckResult = ThreadQualityCheckResultEnum.OTHER.name();

    // 处理状态（待处理、已处理、已关闭等）
    @Builder.Default
    private String summaryStatus = ThreadSummaryStatusEnum.PENDING.name();
    
    // 访客来源渠道
    // private String client;  // 客户来源渠道

    // 排队访客信息
    // @Builder.Default
    // @Column(name = "queue_visitor", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    // private String visitor = BytedeskConsts.EMPTY_JSON_STRING;

    // 接待客服信息
    // @Builder.Default
    // @Column(name = "queue_agent", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    // private String agent = BytedeskConsts.EMPTY_JSON_STRING;
    
    // 接待工作组信息
    // @Builder.Default
    // @Column(name = "queue_workgroup", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    // private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 计算等待时间(秒)
     */
    public long getWaitTime() {
        if (enqueueTime == null) return 0;
        if (thread.isOffline()) return 0;
        LocalDateTime endWaitTime = acceptTime != null ? acceptTime : LocalDateTime.now();
        return Duration.between(enqueueTime, endWaitTime).getSeconds();
    }

    public void acceptThread() {
        // this.status = QueueMemberStatusEnum.SERVING.name();
        this.acceptType = QueueMemberAcceptTypeEnum.MANUAL.name();
        this.acceptTime = LocalDateTime.now();
        // 计算等待时间
        // this.waitTime = (int) Duration.between(enqueueTime, acceptTime).getSeconds();
    }

    /**
     * 标记为离线状态
     */
    // public void markAsOffline() {
    //     this.wasOffline = true;
    // }

    /**
     * 检查是否曾经是离线状态
     */
    // public boolean wasOffline() {
    //     // return this.wasOffline || (thread != null && thread.isOffline());
    //     return this.wasOffline || (thread != null && thread.isOffline());
    // }
}