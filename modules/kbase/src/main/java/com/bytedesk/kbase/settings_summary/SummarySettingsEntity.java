/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: Conversation summary settings for reusable summary settings
 */
package com.bytedesk.kbase.settings_summary;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.kbase.settings_trigger.ExecutionTimingConsts;
import com.bytedesk.kbase.settings_trigger.TriggerScopeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bytedesk_kbase_settings_summary")
public class SummarySettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设置名称，用于区分不同的会话小结设置模板
     */
    @Column(name = "settings_name")
    private String name;

    /**
     * 设置描述
     */
    @Column(name = "settings_description")
    private String description;

    /**
     * 关联 robot entity uid，用于调用 LLM 进行会话小结
     */
    @Column(name = "robot_uid")
    private String robotUid;

    /**
     * 是否为默认设置模板
     */
    @Builder.Default
    @Column(name = "is_default_template")
    private Boolean defaultTemplate = false;

    /**
     * 是否启用会话小结
     */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = false;

    /**
     * 执行时机
     * - ON_MESSAGE: 收到新消息时
     * - THREAD_END: 会话结束时
     */
    @Builder.Default
    @Column(name = "execution_timing")
    private String executionTiming = ExecutionTimingConsts.THREAD_END;

    /**
     * 触发范围（当 executionTiming=ON_MESSAGE 时生效）
     * - VISITOR_ONLY: 仅访客消息
     * - AGENT_ONLY: 仅客服消息
     * - ALL: 所有非系统消息
     */
    @Builder.Default
    @Column(name = "trigger_scope")
    private String triggerScope = TriggerScopeConsts.VISITOR_ONLY;

    /**
     * 触发频率（当 executionTiming=ON_MESSAGE 时生效）：每隔 N 条消息触发一次
     */
    @Builder.Default
    @Column(name = "trigger_every_n_messages")
    private Integer triggerEveryNMessages = 1;

    /**
     * 触发冷却时间（秒）（当 executionTiming=ON_MESSAGE 时生效）
     */
    @Builder.Default
    @Column(name = "trigger_cooldown_seconds")
    private Integer triggerCooldownSeconds = 0;

    /**
     * 是否仅按时间冷却触发（忽略 triggerEveryNMessages）
     */
    @Builder.Default
    @Column(name = "is_trigger_cooldown_only")
    private Boolean triggerCooldownOnly = false;

    /**
     * 从 SummarySettingsRequest 创建实体
     */
    public static SummarySettingsEntity fromRequest(SummarySettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return SummarySettingsEntity.builder().build();
        }
        return modelMapper.map(request, SummarySettingsEntity.class);
    }
}
