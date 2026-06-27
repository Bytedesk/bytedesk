package com.bytedesk.ticket.ticket_settings_notification;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 通知设置：邮件/内部/Webhook。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_notification_settings")
@Slf4j
public class TicketNotificationSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Boolean emailEnabled = Boolean.TRUE;

    /** 邮件供应商 UID（关联 EmailProviderEntity），为空则自动匹配 org 下首个 TICKET 类型供应商 */
    @Column(name = "email_provider_uid")
    private String emailProviderUid;
    
    /** 邮件事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.core.converter.JsonStringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> emailEvents = new java.util.ArrayList<>();

    /** 邮件模板集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.utils.EmailTemplateListConverter.class)
    @Column(length = 1024)
    private java.util.List<com.bytedesk.ticket.ticket_settings_notification.EmailTemplateDef> emailTemplates = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean internalEnabled = Boolean.TRUE;

    /** 内部事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.core.converter.JsonStringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> internalEvents = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean webhookEnabled = Boolean.FALSE;

    private String webhookUrl; // 可为空

    /** webhook事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.core.converter.JsonStringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> webhookEvents = new java.util.ArrayList<>();

    // ===== 短信通知设置 =====
    @Builder.Default
    private Boolean smsEnabled = Boolean.FALSE;

    /** 短信服务商 UID（关联 SmsProviderEntity） */
    @Column(name = "sms_provider_uid")
    private String smsProviderUid;

    /** 短信事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.core.converter.JsonStringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> smsEvents = new java.util.ArrayList<>();

    /** 短信模板配置映射 (event -> {"tc":"模板编码","sn":"签名"})，每个事件独立携带模板和签名 */
    @Builder.Default
    @Convert(converter = com.bytedesk.core.converter.JsonStringMapConverter.class)
    @Column(length = 2048)
    private java.util.Map<String, String> smsTemplateIds = new java.util.HashMap<>();

    /** 用户在线时是否仍发送邮件通知，默认否（在线时跳过邮件） */
    @Builder.Default
    private Boolean emailNotifyWhenOnline = Boolean.FALSE;

    /** 用户在线时是否仍发送短信通知，默认否（在线时跳过短信） */
    @Builder.Default
    private Boolean smsNotifyWhenOnline = Boolean.FALSE;

    /** 是否开启 iOS APNs 推送，默认关闭 */
    @Builder.Default
    private Boolean apnsEnabled = Boolean.FALSE;

    /** 是否开启微信服务号推送，默认关闭（开发中暂未上线） */
    @Builder.Default
    private Boolean wechatEnabled = Boolean.FALSE;

    public static TicketNotificationSettingsEntity fromRequest(TicketNotificationSettingsRequest req) {
        
        TicketNotificationSettingsEntity entity = new TicketNotificationSettingsEntity();
        if (req == null) {
            return entity;
        }
        ObjectMapper om = new ObjectMapper();
        if (req.getEmailEnabled() != null) entity.setEmailEnabled(req.getEmailEnabled());
        if (req.getEmailEvents() != null && !req.getEmailEvents().isEmpty()) {
            try {
                entity.setEmailEvents(java.util.Arrays.asList(om.readValue(req.getEmailEvents(), String[].class)));
            } catch (Exception ex) {
                log.warn("Invalid emailEvents JSON, keep default value", ex);
            }
        }
        if (req.getEmailTemplates() != null && !req.getEmailTemplates().isEmpty()) {
            try {
                entity.setEmailTemplates(java.util.Arrays.asList(om.readValue(req.getEmailTemplates(), EmailTemplateDef[].class)));
            } catch (Exception ex) {
                log.warn("Invalid emailTemplates JSON, keep default value", ex);
            }
        }
        if (req.getInternalEnabled() != null) entity.setInternalEnabled(req.getInternalEnabled());
        if (req.getInternalEvents() != null && !req.getInternalEvents().isEmpty()) {
            try {
                entity.setInternalEvents(java.util.Arrays.asList(om.readValue(req.getInternalEvents(), String[].class)));
            } catch (Exception ex) {
                log.warn("Invalid internalEvents JSON, keep default value", ex);
            }
        }
        if (req.getWebhookEnabled() != null) entity.setWebhookEnabled(req.getWebhookEnabled());
        if (req.getWebhookUrl() != null && !req.getWebhookUrl().isEmpty()) entity.setWebhookUrl(req.getWebhookUrl());
        if (req.getWebhookEvents() != null && !req.getWebhookEvents().isEmpty()) {
            try {
                entity.setWebhookEvents(java.util.Arrays.asList(om.readValue(req.getWebhookEvents(), String[].class)));
            } catch (Exception ex) {
                log.warn("Invalid webhookEvents JSON, keep default value", ex);
            }
        }
        // Email provider
        if (req.getEmailProviderUid() != null && !req.getEmailProviderUid().isEmpty()) entity.setEmailProviderUid(req.getEmailProviderUid());
        // SMS fields
        if (req.getSmsEnabled() != null) entity.setSmsEnabled(req.getSmsEnabled());
        if (req.getSmsProviderUid() != null && !req.getSmsProviderUid().isEmpty()) entity.setSmsProviderUid(req.getSmsProviderUid());
        if (req.getSmsEvents() != null && !req.getSmsEvents().isEmpty()) {
            try {
                entity.setSmsEvents(java.util.Arrays.asList(om.readValue(req.getSmsEvents(), String[].class)));
            } catch (Exception ex) {
                log.warn("Invalid smsEvents JSON, keep default value", ex);
            }
        }
        if (req.getSmsTemplateIds() != null && !req.getSmsTemplateIds().isEmpty()) {
            try {
                entity.setSmsTemplateIds(om.readValue(req.getSmsTemplateIds(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, String>>() {}));
            } catch (Exception ex) {
                log.warn("Invalid smsTemplateIds JSON, keep default value", ex);
            }
        }
        if (req.getEmailNotifyWhenOnline() != null) entity.setEmailNotifyWhenOnline(req.getEmailNotifyWhenOnline());
        if (req.getSmsNotifyWhenOnline() != null) entity.setSmsNotifyWhenOnline(req.getSmsNotifyWhenOnline());
        if (req.getApnsEnabled() != null) entity.setApnsEnabled(req.getApnsEnabled());
        if (req.getWechatEnabled() != null) entity.setWechatEnabled(req.getWechatEnabled());
        return entity;
    }
}
