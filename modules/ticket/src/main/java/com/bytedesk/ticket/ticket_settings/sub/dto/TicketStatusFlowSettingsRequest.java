package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 状态流转设置草稿请求 DTO：content 为 JSON {statuses:[],transitions:[]}。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TicketStatusFlowSettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
}
