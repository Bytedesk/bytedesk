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

    /**
     * JSON: priorities:[]
     */
    @Builder.Default
    @Column(length = 2048)
    private String content = com.bytedesk.core.constant.BytedeskConsts.EMPTY_JSON_STRING;
}
