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
package com.bytedesk.service.queue.settings;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

import com.bytedesk.core.constant.I18Consts;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    // 当排队人数超过指定值时，自动分配机器人
    // 仅适用于workgroup，对一对一人工客服无效
    @Builder.Default
    private Boolean queueRobot = false;

    @Builder.Default
    private Integer maxWaiting = 10000; // 最大等待人数

    @Builder.Default
    private Integer maxWaitTime = 24 * 60 * 60; // 最大等待时间(秒)
    
    @NotBlank
    @Builder.Default
    private String queueTip = I18Consts.I18N_QUEUE_TIP;
    
}

