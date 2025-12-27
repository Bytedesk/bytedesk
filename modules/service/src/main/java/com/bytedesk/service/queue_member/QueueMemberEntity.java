/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:23:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 13:49:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.ZonedDateTime;
import java.time.Duration;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.thread.enums.ThreadIntentionTypeEnum;
import com.bytedesk.core.thread.enums.ThreadInviteStatusEnum;
import com.bytedesk.core.thread.enums.ThreadSummaryStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.core.thread.enums.ThreadEmotionTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author Jackning &lt;270580156@qq.com&gt;
 * 
 * 这些统计数据将有助于客服质量监控和绩效评估，也可用于实时监控会话状态。
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

    // 作为工作组队列成员关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workgroup_queue_id")
    @JsonBackReference("workgroupQueueMembers")
    private QueueEntity workgroupQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_queue_id")
    @JsonBackReference("agentQueueMembers")
    private QueueEntity agentQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robot_queue_id")
    @JsonBackReference("robotQueueMembers")
    private QueueEntity robotQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_queue_id")
    @JsonBackReference("workflowQueueMembers")
    private QueueEntity workflowQueue;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "thread_id", referencedColumnName = "id")
    private ThreadEntity thread;

    @Builder.Default
    private Integer queueNumber = 0;  // 排队号码

    @Builder.Default
    private ZonedDateTime visitorEnqueueAt = BdDateUtils.now();  // 加入时间

    private ZonedDateTime lastNotifiedAt; // 最近一次通知时间

    /**
     * 访客消息统计：
     * 记录第一条访客消息的时间
     * 更新最后一条访客消息的时间
     * 统计访客消息总数
     */
    private ZonedDateTime visitorFirstMessageAt;  // 访客首次发送消息时间

    private ZonedDateTime visitorLastMessageAt;  // 访客最后发送消息时间

    @Builder.Default
    private Integer visitorMessageCount = 0;  // 访客消息数量

    private ZonedDateTime visitorLeavedAt;  // 离开时间

    @Builder.Default
    private Integer visitorPriority = 0;  // 优先级(0-100)

    /**
     * 客服消息统计：
     * 标记客服是否首次响应
     * 记录首次响应时间
     * 更新最后响应时间
     * 计算平均响应时间（累计平均算法）
     * 追踪最长响应时间
     * 统计客服消息总数
     */
    private String agentAcceptType ;  // 接入方式：自动、手动，不设置默认

    private ZonedDateTime agentAcceptedAt;  // 开始服务时间

    @Builder.Default
    private Boolean agentFirstResponse = false;  // 人工客服是否首次响应

    private ZonedDateTime agentFirstResponseAt;  // 首次响应时间

    private ZonedDateTime agentLastResponseAt;  // 最后响应时间

    private ZonedDateTime agentClosedAt;  // 结束时间

    @Builder.Default
    @Column(name = "is_agent_close")
    private Boolean agentClose = false;  // 是否客服手动结束

    /**
     * 响应时间计算：
     * 基于访客最后消息时间和客服响应时间计算响应时长
     * 动态更新平均响应时间和最大响应时间
     */
    @Builder.Default
    private Integer agentAvgResponseLength = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer agentMaxResponseLength = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer agentMessageCount = 0;  // 客服消息数量

    private ZonedDateTime agentTimeoutAt; // 人工对话超时时间

    @Builder.Default
    @Column(name = "is_agent_timeout")
    private Boolean agentTimeout = false; // 是否超时

    // agent timeout count
    @Builder.Default
    private Integer agentTimeoutCount = 0;  // 超时次数

    // 人工是否离线
    @Builder.Default
    @Column(name = "is_agent_offline")
    private Boolean agentOffline = false;

    /**
     * robot 
     * 响应时间计算：
     */
    private String robotAcceptType ;  // 接入方式：自动、手动，不设置默认

    private ZonedDateTime robotAcceptedAt;  // 开始服务时间
    
    @Builder.Default
    @Column(name = "is_robot_first_response")
    private Boolean robotFirstResponse = false;  // 机器人客服是否首次响应

    private ZonedDateTime robotFirstResponseAt;  // 首次响应时间

    private ZonedDateTime robotLastResponseAt;  // 最后响应时间

    private ZonedDateTime robotClosedAt;  // 结束时间

    @Builder.Default
    private Integer robotAvgResponseLength = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer robotMaxResponseLength = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer robotMessageCount = 0;  // 客服消息数量

    // 机器人对话超时时间
    private ZonedDateTime robotTimeoutAt;

    @Builder.Default
    @Column(name = "is_robot_timeout")
    private Boolean robotTimeout = false; // 是否超时

    //-------------------------------

    private ZonedDateTime systemFirstResponseAt;  // 系统首次响应时间

    private ZonedDateTime systemLastResponseAt;  // 系统最后响应时间

    private ZonedDateTime systemClosedAt;  // 系统结束时间，即：autoCloseTime

    @Builder.Default
    @Column(name = "is_system_close")
    private Boolean systemClose = false;  // 是否系统自动结束

    @Builder.Default
    private Integer systemMessageCount = 0;  // 系统消息数量

    // 直接在评价表里面根据threadUid查询是否已经评价
    // 是否被评价
    @Builder.Default
    @Column(name = "is_rated")
    private Boolean rated = false;

    private Integer rateScore;  // 评分

    // 评分时间
    private ZonedDateTime rateAt;  // 评分时间

    // 是否已解决
    @Builder.Default
    @Column(name = "is_resolved")
    private Boolean resolved = false;

    // 是否留言
    @Builder.Default
    @Column(name = "is_message_leave")
    private Boolean messageLeave = false;

    private ZonedDateTime messageLeaveAt;  // 留言时间

    // 直接在小结表里面根据threadUid查询是否已经小结
    // 是否已经小结
    @Builder.Default
    @Column(name = "is_summarized")
    private Boolean summarized = false;

    /**
     * 意图识别最近触发时间（用于冷却控制）
     */
    @Column(name = "intention_last_triggered_at")
    private ZonedDateTime intentionLastTriggeredAt;

    /**
     * 情绪识别最近触发时间（用于冷却控制）
     */
    @Column(name = "emotion_last_triggered_at")
    private ZonedDateTime emotionLastTriggeredAt;

    /**
     * 会话小结最近触发时间（用于冷却控制）
     */
    @Column(name = "summary_last_triggered_at")
    private ZonedDateTime summaryLastTriggeredAt;
    
    // resolved status
    @Builder.Default
    @Column(name = "thread_resolved_status")
    private String resolvedStatus = ThreadSummaryStatusEnum.PENDING.name();

    // 直接在质检表里面根据threadUid查询是否已经质检
    // 是否已经质检
    @Builder.Default
    @Column(name = "is_thread_quality_checked")
    private Boolean qualityChecked = false;

    // 质检结果
    @Column(name = "thread_quality_check_score")
    private Integer qualityCheckScore;

    // 质检时间
    @Column(name = "thread_quality_checked_at")
    private ZonedDateTime qualityCheckedAt;

    // 意图类型
    @Builder.Default
    @Column(name = "thread_intention_type")
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 意图识别原始结果（JSON/文本），便于回溯与二次处理
    @Builder.Default
    @Column(name = "thread_intention_result", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String threadIntentionResult = "";

    // 情绪类型
    @Builder.Default
    @Column(name = "thread_emotion_type")
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 情绪识别原始结果（JSON/文本），便于回溯与二次处理
    @Builder.Default
    @Column(name = "thread_emotion_result", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String threadEmotionResult = "";

    // 会话小结原始结果（JSON），便于后续人工审核/编辑
    @Builder.Default
    @Column(name = "thread_summary_result", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String threadSummaryResult = "";

    // 机器人转人工
    @Builder.Default
    @Column(name = "thread_robot_to_agent")
    private Boolean robotToAgent = false;

    // 机器人转人工时间
    private ZonedDateTime robotToAgentAt;  // 机器人转人工时间

    // 人工转人工
    @Builder.Default
    @Column(name = "thread_transfer_status")
    private String transferStatus = ThreadTransferStatusEnum.NONE.name();

    // TODO: 可能同时邀请多个人
    // invite status
    @Builder.Default
    @Column(name = "thread_invite_status")
    private String inviteStatus = ThreadInviteStatusEnum.NONE.name();

    
    public void manualAcceptThread() {
        this.agentAcceptType = QueueMemberAcceptTypeEnum.MANUAL.name();
        this.agentAcceptedAt = BdDateUtils.now();
    }

    public void agentAutoAcceptThread() {
        this.agentAcceptType = QueueMemberAcceptTypeEnum.AUTO.name();
        this.agentAcceptedAt = BdDateUtils.now();
    }

    public void robotAutoAcceptThread() {
        this.robotAcceptType = QueueMemberAcceptTypeEnum.AUTO.name();
        this.robotAcceptedAt = BdDateUtils.now();
    }

    public void workflowAutoAcceptThread() {
        // 工作流自动接受线程，复用机器人的接受逻辑
        this.robotAcceptType = QueueMemberAcceptTypeEnum.AUTO.name();
        this.robotAcceptedAt = BdDateUtils.now();
    }

    public void transferRobotToAgent() {
        this.robotToAgent = true;
        this.robotToAgentAt = BdDateUtils.now();
    }

    // 
    @Transient
    public Boolean isRoboting() {
        return this.thread != null && this.thread.isRoboting();
    }

    @Transient
    public Boolean isChatting() {
        return this.thread != null && this.thread.isChatting();
    }

    @Transient
    public Boolean isQueuing() {
        return this.thread != null && this.thread.isQueuing();
    }

    @Transient
    public Boolean isClosed() {
        return this.thread != null && this.thread.isClosed();
    }

    /**
     * 计算等待时间(秒)
     */
    @Transient
    public long getWaitLength() {
        if (visitorEnqueueAt == null) return 0;
        if (thread.isOffline() || agentOffline) return 0;
        // 首先判断robotAcceptTime是否为空，如果不为空，则使用robotAcceptTime作为结束时间
        if (robotAcceptedAt != null) {
            return Duration.between(visitorEnqueueAt, robotAcceptedAt).getSeconds();
        }
        ZonedDateTime endWaitLength = agentAcceptedAt != null ? agentAcceptedAt : BdDateUtils.now();
        return Duration.between(visitorEnqueueAt, endWaitLength).getSeconds();
    }

    /**
     * 计算首次响应时长(秒)
     * 从访客首次发送消息到客服首次响应的时间间隔
     */
    @Transient
    public Integer getAgentFirstResponseLength() {
        if (visitorFirstMessageAt == null || agentFirstResponseAt == null) {
            return null;
        }
        return (int) Duration.between(visitorFirstMessageAt, agentFirstResponseAt).getSeconds();
    }

    @Transient
    public String getStatus() {
        return thread != null ? thread.getStatus() : null;
    }

}