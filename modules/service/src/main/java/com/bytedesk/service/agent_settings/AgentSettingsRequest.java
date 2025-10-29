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
package com.bytedesk.service.agent_settings;

import com.bytedesk.kbase.auto_reply.settings.AutoReplySettingsRequest;
import com.bytedesk.kbase.settings.BaseSettingsRequest;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsRequest;
import com.bytedesk.service.agent_status.settings.AgentStatusSettingRequest;
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
public class AgentSettingsRequest extends BaseSettingsRequest {

    private static final long serialVersionUID = 1L;

    /**
     * Maximum concurrent threads the agent can handle
     */
    private Integer maxThreadCount;

    /**
     * Whether timeout reminder is enabled for agent
     */
    private Boolean timeoutRemindEnabled;

    /**
     * Timeout reminder time in minutes
     */
    private Integer timeoutRemindTime;

    /**
     * Timeout reminder tip message
     */
    private String timeoutRemindTip;

    /**
     * Message leave settings (Agent-specific)
     */
    private MessageLeaveSettingsRequest messageLeaveSettings;

    /**
     * Auto-reply settings (Agent-specific)
     */
    private AutoReplySettingsRequest autoReplySettings;

    /**
     * Queue settings (Agent-specific)
     */
    private QueueSettingsRequest queueSettings;

    /**
     * Rating down settings (Agent-specific)
     */
    private RatedownSettingsRequest rateDownSettings;

    /**
     * Agent status settings (Agent-specific)
     */
    private AgentStatusSettingRequest agentStatusSettings;
}
