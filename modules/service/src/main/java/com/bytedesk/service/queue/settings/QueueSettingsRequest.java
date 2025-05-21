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
package com.bytedesk.service.queue.settings;

import java.io.Serializable;

import com.bytedesk.core.constant.I18Consts;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueSettingsRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Integer maxWaiting; // 最大等待人数

    private Integer maxWaitTime; // 最大等待时间(秒)
    
    @NotBlank
    @Builder.Default
    private String queueTip = I18Consts.I18N_QUEUE_TIP;
    
}
