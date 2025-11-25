/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 15:22:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 18:24:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_settings;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
@Builder
@Entity
@Table(
    name = "bytedesk_service_queue_settings",
    indexes = {
        @Index(name = "idx_queue_settings_uid", columnList = "uuid")
    }
)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueSettingsEntity extends BaseEntity {

    public static final int DEFAULT_MAX_WAITING = 10_000;

    public static final int DEFAULT_MAX_WAIT_TIME_SECONDS = 24 * 60 * 60;

    // 当排队人数超过指定值时，自动分配机器人
    // 仅适用于workgroup，对一对一人工客服无效。默认进入留言流程。
    // TODO: 暂不启用
    @Builder.Default
    private Boolean queueRobot = false;

    // 是否显示前面排队人数
    @Builder.Default
    private Boolean showQueuePosition = true;

    // 是否显示大概等待时间
    @Builder.Default
    private Boolean showEstimatedWaitTime = true;

    // 每个人大致等待时长(秒)，用于计算预估等待时间
    @Builder.Default
    private Integer avgWaitTimePerPerson = 60;

    // 设置允许同时排队等待的最大访客数量，超过该数量将进入留言流程
    @Builder.Default
    private Integer maxWaiting = DEFAULT_MAX_WAITING; // 最大等待人数

    // 设置访客最长排队等待时间（秒），超时后将进入留言流程
    @Builder.Default
    private Integer maxWaitTime = DEFAULT_MAX_WAIT_TIME_SECONDS; // 最大等待时间(秒)
    
    // 排队提示语模板，支持变量: {position}-排队位置, {queueSize}-队列总人数, {waitSeconds}-等待秒数, {waitMinutes}-等待分钟数, {waitTime}-格式化等待时间
    @NotBlank
    @Builder.Default
    private String queueTip = I18Consts.I18N_QUEUE_TIP_TEMPLATE;

    // 即将接入提示语（当排队位置为0时使用，即前面没有人排队，下一个就是该用户）
    @Builder.Default
    private String queueReadyTip = I18Consts.I18N_QUEUE_READY_TIP;

    // public static final int DEFAULT_QUEUE_NOTICE_BATCH_WINDOW_MS = 2000;

    /**
     * Batch window for queue notices, in milliseconds; keeps notices always on but rate-limited per agent.
     */
    // @Builder.Default
    // @Column(name = "queue_notice_batch_window_ms")
    // private Integer queueNoticeBatchWindowMs = DEFAULT_QUEUE_NOTICE_BATCH_WINDOW_MS;
    
    /**
     * 从 QueueSettingsRequest 创建 QueueSettings 实体
     * 如果 request 为 null，返回默认构建的实体
     * 
     * @param request QueueSettingsRequest 对象，可以为 null
     * @param modelMapper ModelMapper 实例用于对象映射
     * @return QueueSettings 实体，永远不为 null
     */
    public static QueueSettingsEntity fromRequest(QueueSettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return QueueSettingsEntity.builder().build();
        }
        
        return modelMapper.map(request, QueueSettingsEntity.class);
    }

    public int resolveMaxWaiting() {
        return maxWaiting != null && maxWaiting > 0 ? maxWaiting : DEFAULT_MAX_WAITING;
    }

    public int resolveMaxWaitTimeSeconds() {
        return maxWaitTime != null && maxWaitTime > 0 ? maxWaitTime : DEFAULT_MAX_WAIT_TIME_SECONDS;
    }

    /**
     * Returns a non-null batch window ensuring downstream services can rely on a default cadence.
     */
    // public int resolveQueueNoticeBatchWindowMs() {
    //     return queueNoticeBatchWindowMs != null ? queueNoticeBatchWindowMs : DEFAULT_QUEUE_NOTICE_BATCH_WINDOW_MS;
    // }
    
}

