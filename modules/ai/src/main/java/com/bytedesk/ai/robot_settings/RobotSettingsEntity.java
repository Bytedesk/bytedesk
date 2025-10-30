/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Robot configuration settings for reusable robot settings
 */
package com.bytedesk.ai.robot_settings;

import com.bytedesk.kbase.settings.BaseSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Robot configuration settings for reusable settings
 * 
 * Purpose:
 * - Store reusable robot configurations
 * - Enable sharing common settings across multiple robots
 * - Simplify robot configuration management
 * 
 * Usage:
 * - Create settings for different robot types (e.g., "FAQ Robot", "Customer Service Robot", "Sales Robot")
 * - Assign settings to robots via RobotEntity.settings reference
 * - Multiple robots can share the same settings
 * 
 * Common fields (inherited from BaseSettingsEntity):
 * - name, description, isDefault, enabled
 * - serviceSettings, inviteSettings, intentionSettings
 * 
 * Robot-specific fields:
 * - rateDownSettings
 * 
 * Database Table: bytedesk_ai_robot_settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_ai_robot_settings",
    indexes = {
        @Index(name = "idx_robot_settings_org", columnList = "org_uid"),
        @Index(name = "idx_robot_settings_default", columnList = "is_default")
    }
)
public class RobotSettingsEntity extends BaseSettingsEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 是否显示答案匹配知识库来源
     */
    @Builder.Default
    @Column(name = "is_kb_source_enabled")
    private Boolean kbSourceEnabled = true;

    /**
     * Rating down settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private RatedownSettingsEntity rateDownSettings;

    /**
     * Draft Rating down settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private RatedownSettingsEntity draftRateDownSettings;

    /**
     * 是否启用知识库问答
     */
    @Builder.Default
    @Column(name = "is_kb_enabled")
    private Boolean kbEnabled = false;

    /**
     * 关联的知识库UID
     */
    @Column(name = "kb_uid")
    private String kbUid;

    /**
     * LLM 配置（已从 RobotEntity 迁移到 Settings）
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private RobotLlmEntity llm;

    /**
     * 草稿版 LLM 配置（发布时覆盖线上）
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private RobotLlmEntity draftLlm;
}
