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
    private String emailProviderUid;
    private List<String> emailEvents;
    private List<EmailTemplateDef> emailTemplates;
    private Boolean internalEnabled;
    private List<String> internalEvents;
    private Boolean webhookEnabled;
    private String webhookUrl;
    private List<String> webhookEvents;
    // SMS notification fields
    private Boolean smsEnabled;
    private String smsProviderUid;
    private List<String> smsEvents;
    private java.util.Map<String, String> smsTemplateIds;

    /** 用户在线时是否仍发送邮件通知 */
    private Boolean emailNotifyWhenOnline;

    /** 用户在线时是否仍发送短信通知 */
    private Boolean smsNotifyWhenOnline;

    /** 是否开启 iOS APNs 推送 */
    private Boolean apnsEnabled;

    /** 是否开启微信服务号推送（开发中暂未上线） */
    private Boolean wechatEnabled;
}
