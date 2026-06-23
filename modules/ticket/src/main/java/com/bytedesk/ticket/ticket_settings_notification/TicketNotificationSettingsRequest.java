package com.bytedesk.ticket.ticket_settings_notification;

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
    private String emailProviderUid;
    private String emailEvents;
    private String emailTemplates;
    private Boolean internalEnabled;
    private String internalEvents;
    private Boolean webhookEnabled;
    private String webhookUrl;
    private String webhookEvents;
    // SMS notification fields
    private Boolean smsEnabled;
    private String smsProviderUid;
    private String smsEvents;
    private String smsTemplateIds;

    /** 用户在线时是否仍发送邮件通知 */
    private Boolean emailNotifyWhenOnline;

    /** 用户在线时是否仍发送短信通知 */
    private Boolean smsNotifyWhenOnline;
}
