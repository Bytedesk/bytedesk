package com.bytedesk.ticket.ticket_settings_basic;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.modelmapper.ModelMapper;

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

    // 是否需要登录才能创建工单/查询工单
    @Builder.Default
    private Boolean requireLogin = Boolean.FALSE;

    /**
     * 工单分配方式
     */
    @Builder.Default
    @Column(name = "assignment_mode", length = 64)
    private String assignmentMode = TicketAssignmentModeEnum.ROUND_ROBIN.name();

    // ============ 联系方式字段显示配置 ============

    /**
     * 是否显示联系人姓名字段
     */
    @Builder.Default
    @Column(name = "show_contact_name")
    private Boolean showContactName = Boolean.TRUE;

    /**
     * 联系人姓名是否必填
     */
    @Builder.Default
    @Column(name = "require_contact_name")
    private Boolean requireContactName = Boolean.TRUE;

    /**
     * 是否显示邮箱字段
     */
    @Builder.Default
    @Column(name = "show_email")
    private Boolean showEmail = Boolean.TRUE;

    /**
     * 邮箱是否必填
     */
    @Builder.Default
    @Column(name = "require_email")
    private Boolean requireEmail = Boolean.FALSE;

    /**
     * 是否显示手机字段
     */
    @Builder.Default
    @Column(name = "show_phone")
    private Boolean showPhone = Boolean.TRUE;

    /**
     * 手机是否必填
     */
    @Builder.Default
    @Column(name = "require_phone")
    private Boolean requirePhone = Boolean.TRUE;

    /**
     * 是否显示微信字段
     */
    @Builder.Default
    @Column(name = "show_wechat")
    private Boolean showWechat = Boolean.FALSE;

    /**
     * 微信是否必填
     */
    @Builder.Default
    @Column(name = "require_wechat")
    private Boolean requireWechat = Boolean.FALSE;

    /**
     * 静态工厂：根据请求DTO与可选ModelMapper构建实体。
     * 为空时返回默认配置；非空字段才覆盖默认值。
     */
    public static TicketBasicSettingsEntity fromRequest(TicketBasicSettingsRequest request, ModelMapper mapper) {
        if (request == null || mapper == null) {
            return TicketBasicSettingsEntity.builder().build(); // 返回默认
        }
        // 若提供 mapper，直接 map 后再补默认值（避免为 null 覆盖默认）
        TicketBasicSettingsEntity entity = mapper.map(request, TicketBasicSettingsEntity.class);
        entity.setAssignmentMode(request.getAssignmentMode());
        return entity;
    }

    public TicketBasicSettingsEntity setAssignmentMode(String assignmentMode) {
        this.assignmentMode = TicketAssignmentModeEnum.normalize(assignmentMode);
        return this;
    }
}
