/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-12 12:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Builder;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TicketSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    /**
     * 是否设为组织默认配置（同一 org 仅允许一个默认，服务端在事务内保证唯一）
     */
    private Boolean isDefault;

    /**
     * 是否启用（默认为 true）
     */
    private Boolean enabled;

    // 草稿子配置（仅在更新草稿时传入；创建时也可传，后台将初始化发布+草稿）
    private TicketBasicSettingsRequest basicSettings;
    private TicketStatusFlowSettingsRequest statusFlowSettings;
    private TicketPrioritySettingsRequest prioritySettings;
    private TicketAssignmentSettingsRequest assignmentSettings;
    private TicketNotificationSettingsRequest notificationSettings;
    private TicketCustomFieldSettingsRequest customFieldSettings;

    private TicketCategorySettingsRequest categorySettings;

    private TicketCategorySettingsRequest draftCategorySettings;

}
