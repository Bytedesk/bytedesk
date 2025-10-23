/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Workgroup configuration settings for reusable workgroup settings
 */
package com.bytedesk.service.workgroup_settings;

import com.bytedesk.kbase.settings.BaseSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
import com.bytedesk.service.settings.RobotSettings;

import jakarta.persistence.Embedded;
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
     * Message leave settings
     */
    @Embedded
    @lombok.Builder.Default
    private MessageLeaveSettings messageLeaveSettings = new MessageLeaveSettings();

    /**
     * Robot settings
     */
    @Embedded
    @lombok.Builder.Default
    private RobotSettings robotSettings = new RobotSettings();

    /**
     * Queue settings
     */
    @Embedded
    @lombok.Builder.Default
    private QueueSettings queueSettings = new QueueSettings();
}
