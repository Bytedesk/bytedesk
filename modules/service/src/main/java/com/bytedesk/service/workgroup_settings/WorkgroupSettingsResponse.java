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

import com.bytedesk.ai.robot.settings.RobotRoutingSettingsResponse;
import com.bytedesk.kbase.settings.BaseSettingsResponse;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsResponse;
import com.bytedesk.service.queue_settings.QueueSettingsResponse;

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
@EqualsAndHashCode(callSuper = true)
public class WorkgroupSettingsResponse extends BaseSettingsResponse {

    private static final long serialVersionUID = 1L;

    /**
     * Customer routing mode (ROUND_ROBIN, LEAST_BUSY, etc.)
     */
    private String routingMode;

    /**
     * Message leave settings (Workgroup-specific)
     */
    private MessageLeaveSettingsResponse messageLeaveSettings;
    /**
     * Draft message leave settings (Workgroup-specific)
     */
    private MessageLeaveSettingsResponse draftMessageLeaveSettings;

    /**
     * Robot routing settings (Workgroup-specific)
     */
    private RobotRoutingSettingsResponse robotRoutingSettings;
    /**
     * Draft robot routing settings (Workgroup-specific)
     */
    private RobotRoutingSettingsResponse draftRobotRoutingSettings;

    /**
     * Queue settings (Workgroup-specific)
     */
    private QueueSettingsResponse queueSettings;
    /**
     * Draft queue settings (Workgroup-specific)
     */
    private QueueSettingsResponse draftQueueSettings;
}
