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
 * 自定义字段集合设置。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_custom_field_settings")
public class TicketCustomFieldSettingsEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 结构化自定义字段定义集合（持久化 JSON） */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.CustomFieldSettingsConverter.class)
    @Column(length = 4096)
    private com.bytedesk.ticket.ticket_settings.sub.model.CustomFieldSettingsData content = com.bytedesk.ticket.ticket_settings.sub.model.CustomFieldSettingsData.builder().build();
}
