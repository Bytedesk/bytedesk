package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.ticket.ticket_settings.sub.model.CustomFieldSettingsData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 自定义字段设置响应 DTO（结构化）。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketCustomFieldSettingsResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private CustomFieldSettingsData content;
}
