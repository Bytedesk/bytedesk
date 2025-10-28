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

import com.bytedesk.kbase.auto_reply.settings.AutoReplySettingsResponse;
import com.bytedesk.kbase.settings.BaseSettingsResponse;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsResponse;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsResponse;
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
public class AgentSettingsResponse extends BaseSettingsResponse {

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
    private MessageLeaveSettingsResponse messageLeaveSettings;
    /**
     * Draft message leave settings (Agent-specific)
     */
    private MessageLeaveSettingsResponse draftMessageLeaveSettings;

    /**
     * Auto-reply settings (Agent-specific)
     */
    private AutoReplySettingsResponse autoReplySettings;
    /**
     * Draft auto-reply settings (Agent-specific)
     */
    private AutoReplySettingsResponse draftAutoReplySettings;

    /**
     * Queue settings (Agent-specific)
     */
    private QueueSettingsResponse queueSettings;
    /**
     * Draft queue settings (Agent-specific)
     */
    private QueueSettingsResponse draftQueueSettings;

    /**
     * Rating down settings (Agent-specific)
     */
    private RatedownSettingsResponse rateDownSettings;
    /**
     * Draft rating down settings (Agent-specific)
     */
    private RatedownSettingsResponse draftRateDownSettings;
}
