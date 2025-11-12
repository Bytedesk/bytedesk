/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:36:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;


import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String workgroupUid;

    // ===== 发布版本（前端展示用） =====
    private TicketBasicSettingsResponse basicSettings;
    private TicketStatusFlowSettingsResponse statusFlowSettings;
    private TicketPrioritySettingsResponse prioritySettings;
    private TicketAssignmentSettingsResponse assignmentSettings;
    private TicketNotificationSettingsResponse notificationSettings;
    private TicketCustomFieldSettingsResponse customFieldSettings;

    // ===== 草稿版本（编辑态用，使用同一响应结构便于统一渲染） =====
    private TicketBasicSettingsResponse draftBasicSettings;
    private TicketStatusFlowSettingsResponse draftStatusFlowSettings;
    private TicketPrioritySettingsResponse draftPrioritySettings;
    private TicketAssignmentSettingsResponse draftAssignmentSettings;
    private TicketNotificationSettingsResponse draftNotificationSettings;
    private TicketCustomFieldSettingsResponse draftCustomFieldSettings;

}
