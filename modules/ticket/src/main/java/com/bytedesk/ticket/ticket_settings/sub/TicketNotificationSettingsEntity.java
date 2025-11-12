package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

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
public class TicketNotificationSettingsEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Boolean emailEnabled = Boolean.TRUE;
    /** 邮件事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.StringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> emailEvents = new java.util.ArrayList<>();
    /** 邮件模板集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.EmailTemplateListConverter.class)
    @Column(length = 1024)
    private java.util.List<com.bytedesk.ticket.ticket_settings.sub.model.EmailTemplateDef> emailTemplates = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean internalEnabled = Boolean.TRUE;
    /** 内部事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.StringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> internalEvents = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean webhookEnabled = Boolean.FALSE;
    private String webhookUrl; // 可为空
    /** webhook事件集合 */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.StringListConverter.class)
    @Column(length = 512)
    private java.util.List<String> webhookEvents = new java.util.ArrayList<>();
}
