/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Service configuration template entity for Agent/Workgroup/Robot
 *   Centralized configuration management to avoid duplication across entities
 */
package com.bytedesk.kbase.settings;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.kbase.settings_queue.QueueSettings;
import com.bytedesk.kbase.settings_messageleave.MessageLeaveSettings;
import com.bytedesk.kbase.settings_robot.RobotSettings;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Service configuration template for reusable settings across Agent/Workgroup/Robot
 * 
 * Purpose: 
 * - Eliminates duplicate @Embedded fields in AgentEntity/WorkgroupEntity/RobotEntity
 * - Enables sharing common configurations across multiple entities of the same type
 * - Simplifies configuration management and updates
 * 
 * Usage:
 * - Create templates for different scenarios (e.g., "Standard Agent", "VIP Workgroup", "FAQ Robot")
 * - Assign templates to entities via @ManyToOne reference
 * - Multiple agents/workgroups/robots can share the same template
 * 
 * Database Table: bytedesk_service_template
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_service_template",
    indexes = {
        @Index(name = "idx_template_type", columnList = "template_type"),
        @Index(name = "idx_template_org", columnList = "org_uid")
    }
)
public class ServiceTemplateEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template name for identification
     */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /**
     * Template description
     */
    @lombok.Builder.Default
    private String description = I18Consts.I18N_TEMPLATE_DESCRIPTION;

    /**
     * Template type: AGENT, WORKGROUP, ROBOT
     */
    @NotBlank
    @Column(name = "template_type", nullable = false)
    private String type;

    /**
     * Whether this is a system default template
     */
    @lombok.Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /**
     * Settings for handling offline messages
     */
    @Embedded
    @lombok.Builder.Default
    private MessageLeaveSettings messageLeaveSettings = new MessageLeaveSettings();

    /**
     * General service configuration settings
     */
    @Embedded
    @lombok.Builder.Default
    private ServiceSettings serviceSettings = new ServiceSettings();

    /**
     * Auto-reply settings (primarily for Agent)
     */
    @Embedded
    @lombok.Builder.Default
    private AutoReplySettings autoReplySettings = new AutoReplySettings();

    /**
     * Queue management settings
     */
    @Embedded
    @lombok.Builder.Default
    private QueueSettings queueSettings = new QueueSettings();

    /**
     * Robot service configuration settings (for Workgroup with robot)
     */
    @Embedded
    @lombok.Builder.Default
    private RobotSettings robotSettings = new RobotSettings();

    /**
     * Invitation settings
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private InviteSettingsEntity inviteSettings;

    /**
     * Rating down settings for feedback collection
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private RatedownSettingsEntity rateDownSettings;

    /**
     * Intention recognition settings
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private IntentionSettingsEntity intentionSettings;

    /**
     * Whether the template is enabled and can be used
     */
    @lombok.Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;
}
