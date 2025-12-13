package com.bytedesk.ticket.ticket_settings_notification;

import com.bytedesk.core.base.BaseResponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 通知设置响应 DTO（结构化）。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketNotificationSettingsResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private Boolean emailEnabled;
    private List<String> emailEvents;
    private List<EmailTemplateDef> emailTemplates;
    private Boolean internalEnabled;
    private List<String> internalEvents;
    private Boolean webhookEnabled;
    private String webhookUrl;
    private List<String> webhookEvents;
}
