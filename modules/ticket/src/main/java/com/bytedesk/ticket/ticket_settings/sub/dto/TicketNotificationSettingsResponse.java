package com.bytedesk.ticket.ticket_settings.sub.dto;

import com.bytedesk.ticket.ticket_settings.sub.model.EmailTemplateDef;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 通知设置响应 DTO（结构化）。
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketNotificationSettingsResponse implements Serializable {
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
