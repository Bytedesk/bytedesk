package com.bytedesk.ticket.ticket_settings.dto;

import com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 专用于按工作组维度保存草稿配置的请求体。
 * 路径参数提供 orgUid/workgroupUid，本体仅包含业务字段。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketSettingsByWorkgroupUpdateRequest {
    private String name;
    private String description;

    private TicketBasicSettingsRequest draftBasicSettings;
    private TicketStatusFlowSettingsRequest draftStatusFlowSettings;
    private TicketPrioritySettingsRequest draftPrioritySettings;
    private TicketAssignmentSettingsRequest draftAssignmentSettings;
    private TicketNotificationSettingsRequest draftNotificationSettings;
    private TicketCustomFieldSettingsRequest draftCustomFieldSettings;
    private TicketCategorySettingsRequest draftCategorySettings;
}
