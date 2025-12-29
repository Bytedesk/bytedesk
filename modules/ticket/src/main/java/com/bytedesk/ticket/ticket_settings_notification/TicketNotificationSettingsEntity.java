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
    /** 邮件事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings_notification.StringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> emailEvents = new java.util.ArrayList<>();
    /** 邮件模板集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings_notification.EmailTemplateListConverter.class)
    @Column(length = 1024)
    private java.util.List<com.bytedesk.ticket.ticket_settings_notification.EmailTemplateDef> emailTemplates = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean internalEnabled = Boolean.TRUE;
    /** 内部事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings_notification.StringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> internalEvents = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean webhookEnabled = Boolean.FALSE;
    private String webhookUrl; // 可为空
    /** webhook事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings_notification.StringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> webhookEvents = new java.util.ArrayList<>();

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
        return entity;
    }
}
