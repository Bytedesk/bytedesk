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

    /** JSON 数组: [{ key,name,type,required,options?,defaultValue?,order,isActive }] */
    @Builder.Default
    @Column(length = 4096)
    private String content = com.bytedesk.core.constant.BytedeskConsts.EMPTY_JSON_STRING;
}
