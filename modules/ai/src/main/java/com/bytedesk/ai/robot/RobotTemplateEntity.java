/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Robot configuration template for reusable robot settings
 */
package com.bytedesk.ai.robot;

import com.bytedesk.kbase.settings.BaseTemplateEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Robot configuration template for reusable settings
 * 
 * Purpose:
 * - Store reusable robot configurations
 * - Enable sharing common settings across multiple robots
 * - Simplify robot configuration management
 * 
 * Usage:
 * - Create templates for different robot types (e.g., "FAQ Robot", "Customer Service Robot", "Sales Robot")
 * - Assign templates to robots via RobotEntity.template reference
 * - Multiple robots can share the same template
 * 
 * Common fields (inherited from BaseTemplateEntity):
 * - name, description, isDefault, enabled
 * - serviceSettings, inviteSettings, intentionSettings
 * 
 * Robot-specific fields:
 * - rateDownSettings
 * 
 * Database Table: bytedesk_ai_robot_template
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_ai_robot_template",
    indexes = {
        @Index(name = "idx_robot_template_org", columnList = "org_uid"),
        @Index(name = "idx_robot_template_default", columnList = "is_default")
    }
)
public class RobotTemplateEntity extends BaseTemplateEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Rating down settings
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private RatedownSettingsEntity rateDownSettings;
}
