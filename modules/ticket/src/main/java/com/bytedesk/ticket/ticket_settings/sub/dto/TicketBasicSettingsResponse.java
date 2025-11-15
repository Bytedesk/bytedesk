package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 基础设置发布/草稿响应 DTO（与请求字段一致，预留后续只读属性扩展）。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketBasicSettingsResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private String numberPrefix;
    private Integer numberLength;
    private String defaultPriority;
    private Integer validityDays;
    private Integer autoCloseHours;
    private Boolean enableAutoClose;
}
