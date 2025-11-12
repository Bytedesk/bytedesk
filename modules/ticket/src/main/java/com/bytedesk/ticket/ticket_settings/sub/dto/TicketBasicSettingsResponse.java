package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 基础设置发布/草稿响应 DTO（与请求字段一致，预留后续只读属性扩展）。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketBasicSettingsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numberPrefix;
    private Integer numberLength;
    private String defaultPriority;
    private Integer validityDays;
    private Integer autoCloseHours;
    private Boolean enableAutoClose;
}
