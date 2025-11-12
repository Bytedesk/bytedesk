package com.bytedesk.ticket.ticket_settings.sub.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 通知设置响应 DTO。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketNotificationSettingsResponse implements Serializable {
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
