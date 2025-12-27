/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: Trigger settings entity (Quartz-based triggers)
 */
package com.bytedesk.kbase.settings_trigger;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.kbase.trigger.TriggerEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Trigger settings (v1): focus on simplest Quartz-driven triggers.
 *
 * Current supported use-case:
 * - "long time no response" timeout reminder (checked by Quartz periodic job)
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_kbase_trigger_settings",
    indexes = {
        @Index(name = "idx_trigger_settings_uid", columnList = "uuid")
    }
)
public class TriggerSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 触发器列表：仅存放 TriggerEntity 引用。
     * 具体参数与配置存放在 TriggerEntity.config 中。
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "bytedesk_kbase_trigger_settings_triggers",
        joinColumns = @JoinColumn(name = "trigger_settings_id"),
        inverseJoinColumns = @JoinColumn(name = "trigger_id")
    )
    private List<TriggerEntity> triggers = new ArrayList<>();

    /**
     * 从 TriggerSettingsRequest 创建 TriggerSettingsEntity。
     * request/modelMapper 任一为 null 时返回默认值实体。
     */
    public static TriggerSettingsEntity fromRequest(TriggerSettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return TriggerSettingsEntity.builder().build();
        }
        // 触发器关联由 TriggerSettingsHelper 在 service 层处理
        return TriggerSettingsEntity.builder().build();
    }
}
