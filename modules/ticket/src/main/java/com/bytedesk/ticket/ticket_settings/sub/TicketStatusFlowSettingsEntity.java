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
 * 状态流程设置：状态集合与迁移规则。
 * 这里用 JSON 字段承载当前版本的集合，后续可拆分子表。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_status_flow_settings")
public class TicketStatusFlowSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 结构化状态流转定义（持久化为 JSON） */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.StatusFlowSettingsConverter.class)
    @Column(length = 4096)
    private com.bytedesk.ticket.ticket_settings.sub.model.StatusFlowSettingsData content = com.bytedesk.ticket.ticket_settings.sub.model.StatusFlowSettingsData.builder().build();
}
