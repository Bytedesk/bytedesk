package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.model.PrioritySettingsData;
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

/**
 * 优先级设置集合。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_priority_settings")
public class TicketPrioritySettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 优先级集合结构化表示（持久化为 JSON） */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.PrioritySettingsConverter.class)
    @Column(length = 2048)
    private com.bytedesk.ticket.ticket_settings.sub.model.PrioritySettingsData content = com.bytedesk.ticket.ticket_settings.sub.model.PrioritySettingsData.builder().build();

    public static TicketPrioritySettingsEntity fromRequest(TicketPrioritySettingsRequest req) {
        TicketPrioritySettingsEntity entity = new TicketPrioritySettingsEntity();
        if (req == null || req.getContent() == null || req.getContent().isEmpty()) {
            entity.setContent(PrioritySettingsData.builder().build());
            return entity;
        }
        try {
            ObjectMapper om = new ObjectMapper();
            PrioritySettingsData data = om.readValue(req.getContent(), PrioritySettingsData.class);
            entity.setContent(data == null ? PrioritySettingsData.builder().build() : data);
        } catch (Exception e) {
            entity.setContent(PrioritySettingsData.builder().build());
        }
        return entity;
    }
}
