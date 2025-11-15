package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 通知设置草稿请求 DTO。
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketNotificationSettingsRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;
    private Boolean emailEnabled;
    private String emailEvents;
    private String emailTemplates;
    private Boolean internalEnabled;
    private String internalEvents;
    private Boolean webhookEnabled;
    private String webhookUrl;
    private String webhookEvents;
}
