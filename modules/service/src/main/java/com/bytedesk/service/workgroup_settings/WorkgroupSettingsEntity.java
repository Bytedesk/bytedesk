/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Workgroup configuration settings for reusable workgroup settings
 */
package com.bytedesk.service.workgroup_settings;

import com.bytedesk.ai.robot.settings.RobotRoutingSettingsEntity;
import com.bytedesk.kbase.settings.BaseSettingsEntity;
import com.bytedesk.service.message_leave_settings.MessageLeaveSettingsEntity;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.worktime_settings.WorktimeSettingEntity;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
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
 * Workgroup configuration settings for reusable settings
 * 
 * Purpose:
 * - Store reusable workgroup configurations
 * - Enable sharing common settings across multiple workgroups
 * - Simplify workgroup configuration management
 * 
 * Usage:
 * - Create settings for different workgroup types (e.g., "Standard Support", "VIP Support", "Technical Support")
 * - Assign settings to workgroups via WorkgroupEntity.settings reference
 * - Multiple workgroups can share the same settings
 * 
 * Common fields (inherited from BaseSettingsEntity):
 * - name, description, isDefault, enabled
 * - serviceSettings, inviteSettings, intentionSettings
 * 
 * Workgroup-specific fields:
 * - messageLeaveSettings, robotSettings, queueSettings
 * 
 * Database Table: bytedesk_service_workgroup_settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_service_workgroup_settings",
    indexes = {
        @Index(name = "idx_workgroup_settings_org", columnList = "org_uid"),
        @Index(name = "idx_workgroup_settings_default", columnList = "is_default")
    }
)
public class WorkgroupSettingsEntity extends BaseSettingsEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Customer routing mode (ROUND_ROBIN, LEAST_BUSY, etc.)
     * Moved from WorkgroupEntity to centralize configuration
     */
    @lombok.Builder.Default
    private String routingMode = com.bytedesk.service.workgroup.WorkgroupRoutingModeEnum.ROUND_ROBIN.name();

    /**
     * Message leave settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private MessageLeaveSettingsEntity messageLeaveSettings;

    /**
     * Draft Message leave settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private MessageLeaveSettingsEntity draftMessageLeaveSettings;

    /**
     * Worktime settings reference (shared template)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private WorktimeSettingEntity worktimeSettings;

    /**
     * Draft worktime settings reference
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private WorktimeSettingEntity draftWorktimeSettings;

    /**
     * Robot routing settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private RobotRoutingSettingsEntity robotSettings;

    /**
     * Draft Robot routing settings
     * Note: Override columns and association to avoid conflicts with published embedded fields
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private RobotRoutingSettingsEntity draftRobotSettings;

    /**
     * Queue settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private QueueSettingsEntity queueSettings;

    /**
     * Draft Queue settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private QueueSettingsEntity draftQueueSettings;
}
