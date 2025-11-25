/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-23 15:24:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 15:24:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_settings;

import com.bytedesk.core.base.BaseResponse;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
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
public class QueueSettingsResponse extends BaseResponse {
    
    private static final long serialVersionUID = 1L;

    private Boolean showQueuePosition; // 是否显示前面排队人数

    private Boolean showEstimatedWaitTime; // 是否显示大概等待时间

    private Integer avgWaitTimePerPerson; // 每个人大致等待时长(秒)

    private Integer maxWaiting; // 最大等待人数

    private Integer maxWaitTime; // 最大等待时间(秒)
    
    private String queueTip; // 排队提示

    private Integer queueNoticeBatchWindowMs; // 排队通知批处理窗口(毫秒)
    
    /**
     * 从 QueueSettings 实体创建 QueueSettingsResponse
     * @param settings QueueSettings 实体
     * @return QueueSettingsResponse 对象,如果 settings 为 null 则返回 null
     */
    public static QueueSettingsResponse fromEntity(QueueSettingsEntity settings) {
        if (settings == null) {
            return null;
        }
        return QueueSettingsResponse.builder()
                .showQueuePosition(settings.getShowQueuePosition())
                .showEstimatedWaitTime(settings.getShowEstimatedWaitTime())
                .avgWaitTimePerPerson(settings.getAvgWaitTimePerPerson())
                .maxWaiting(settings.getMaxWaiting())
                .maxWaitTime(settings.getMaxWaitTime())
                .queueTip(settings.getQueueTip())
                .queueNoticeBatchWindowMs(settings.getQueueNoticeBatchWindowMs())
                .build();
    }
    
}
