package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
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
 * 基础工单设置：编号、优先级默认值、有效期与自动关闭。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_basic_settings")
public class TicketBasicSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String numberPrefix = "TK";

    @Builder.Default
    private Integer numberLength = 8;

    @Builder.Default
    private String defaultPriority = "medium";

    @Builder.Default
    private Integer validityDays = 30; // 工单默认有效天数

    @Builder.Default
    private Integer autoCloseHours = 72; // 自动关闭小时数

    @Builder.Default
    private Boolean enableAutoClose = Boolean.TRUE;
}
