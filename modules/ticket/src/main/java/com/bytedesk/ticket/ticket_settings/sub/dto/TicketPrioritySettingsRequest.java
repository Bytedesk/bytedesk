package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 优先级设置草稿请求 DTO：content 为 JSON priorities 数组。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TicketPrioritySettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
}
