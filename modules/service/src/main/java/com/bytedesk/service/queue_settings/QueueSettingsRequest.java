/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 15:23:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 12:14:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_settings;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QueueSettingsRequest extends BaseRequest {
    
    private static final long serialVersionUID = 1L;

    // 是否显示前面排队人数
    @Builder.Default
    private Boolean showQueuePosition = true;

    // 是否显示大概等待时间
    @Builder.Default
    private Boolean showEstimatedWaitTime = true;

    // 每个人大致等待时长(秒)，用于计算预估等待时间
    @Builder.Default
    private Integer avgWaitTimePerPerson = 60;

    private Integer maxWaiting; // 最大等待人数

    private Integer maxWaitTime; // 最大等待时间(秒)
    
    // 排队提示语模板，支持变量: {position}-排队位置, {queueSize}-队列总人数, {waitSeconds}-等待秒数, {waitMinutes}-等待分钟数, {waitTime}-格式化等待时间
    @NotBlank
    @Builder.Default
    private String queueTip = I18Consts.I18N_QUEUE_TIP_TEMPLATE;

    // @Builder.Default
    // private Integer queueNoticeBatchWindowMs = QueueSettingsEntity.DEFAULT_QUEUE_NOTICE_BATCH_WINDOW_MS; // 排队通知批处理窗口(毫秒)
    
}
