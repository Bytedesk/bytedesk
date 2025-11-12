package com.bytedesk.ticket.ticket_settings.binding;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 绑定表：一个工作组(workgroupUid)只能绑定一套 TicketSettings；
 * 同一套 TicketSettings 可以被多个工作组绑定。
 * 不直接依赖 service 模块实体，避免循环依赖。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_ticket_settings_binding",
    indexes = {
        @Index(name = "idx_binding_settings_uid", columnList = "ticketSettingsUid"),
        @Index(name = "idx_binding_workgroup_uid", columnList = "workgroupUid")
    }
)
public class TicketSettingsBindingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 被绑定的工单设置 UID
     */
    @Column(nullable = false)
    private String ticketSettingsUid;

    /**
     * 工作组 UID（字符串，不做 JPA 关联，避免依赖 service 模块）
     */
    @Column(nullable = false)
    private String workgroupUid;
}
