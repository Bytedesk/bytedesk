package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.Builder;

/**
 * 基础设置草稿请求 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketBasicSettingsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numberPrefix;
    private Integer numberLength;
    private String defaultPriority;
    private Integer validityDays;
    private Integer autoCloseHours;
    private Boolean enableAutoClose;
}
