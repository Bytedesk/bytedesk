package com.bytedesk.ticket.ticket_settings_basic;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 基础设置草稿请求 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketBasicSettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    private String numberPrefix;
    private Integer numberLength;
    private String defaultPriority;
    private Integer validityDays;
    private Integer autoCloseHours;
    private Boolean enableAutoClose;
    private Boolean requireLogin;
    private String assignmentMode;
}
