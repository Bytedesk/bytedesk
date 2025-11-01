/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23 14:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-23 14:40:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_settings;

import com.bytedesk.ai.robot.settings.RobotRoutingSettingsRequest;
import com.bytedesk.kbase.settings.BaseSettingsRequest;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsRequest;
import com.bytedesk.service.queue_settings.QueueSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WorkgroupSettingsRequest extends BaseSettingsRequest {

    private static final long serialVersionUID = 1L;

    /**
     * Customer routing mode (ROUND_ROBIN, LEAST_BUSY, etc.)
     */
    private String routingMode;

    /**
     * Message leave settings (Workgroup-specific)
     */
    private MessageLeaveSettingsRequest messageLeaveSettings;

    /**
     * Robot routing settings (Workgroup-specific)
     */
    private RobotRoutingSettingsRequest robotRoutingSettings;

    /**
     * Queue settings (Workgroup-specific)
     */
    private QueueSettingsRequest queueSettings;
}
