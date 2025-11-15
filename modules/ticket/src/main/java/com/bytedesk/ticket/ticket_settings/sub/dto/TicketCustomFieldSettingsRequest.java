package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 自定义字段草稿设置 DTO：content 为字段数组 JSON。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TicketCustomFieldSettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
}
