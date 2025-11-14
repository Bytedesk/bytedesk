package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.modelmapper.ModelMapper;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsRequest;
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

    // 是否需要登录才能创建工单
    @Builder.Default
    private Boolean requireLoginToCreate = Boolean.FALSE;

    /**
     * 静态工厂：根据请求DTO与可选ModelMapper构建实体。
     * 为空时返回默认配置；非空字段才覆盖默认值。
     */
    public static TicketBasicSettingsEntity fromRequest(TicketBasicSettingsRequest request, ModelMapper mapper) {
        if (request == null || mapper == null) {
            return TicketBasicSettingsEntity.builder().build(); // 返回默认
        }
        // 若提供 mapper，直接 map 后再补默认值（避免为 null 覆盖默认）
        return mapper.map(request, TicketBasicSettingsEntity.class);
    }
}
