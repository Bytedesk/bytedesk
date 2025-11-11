package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
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
    @Builder.Default
    @Column(length = 512)
    private String emailEvents = "[\"created\",\"assigned\",\"resolved\",\"closed\"]"; // JSON Array
    @Builder.Default
    @Column(length = 1024)
    private String emailTemplates = "{}"; // JSON Map

    @Builder.Default
    private Boolean internalEnabled = Boolean.TRUE;
    @Builder.Default
    @Column(length = 512)
    private String internalEvents = "[\"created\",\"assigned\",\"resolved\",\"closed\"]";

    @Builder.Default
    private Boolean webhookEnabled = Boolean.FALSE;
    private String webhookUrl; // 可为空
    @Builder.Default
    @Column(length = 512)
    private String webhookEvents = "[]";
}
