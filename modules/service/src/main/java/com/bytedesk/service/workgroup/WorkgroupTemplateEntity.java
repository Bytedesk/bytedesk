/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Workgroup configuration template for reusable workgroup settings
 */
package com.bytedesk.service.workgroup;

import com.bytedesk.kbase.settings.BaseTemplateEntity;
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
 * Workgroup configuration template for reusable settings
 * 
 * Purpose:
 * - Store reusable workgroup configurations
 * - Enable sharing common settings across multiple workgroups
 * - Simplify workgroup configuration management
 * 
 * Usage:
 * - Create templates for different workgroup types (e.g., "Standard Support", "VIP Support", "Technical Support")
 * - Assign templates to workgroups via WorkgroupEntity.template reference
 * - Multiple workgroups can share the same template
 * 
 * Common fields (inherited from BaseTemplateEntity):
 * - name, description, isDefault, enabled
 * - serviceSettings, inviteSettings, intentionSettings
 * 
 * Workgroup-specific fields:
 * - messageLeaveSettings, robotSettings, queueSettings
 * 
 * Database Table: bytedesk_service_workgroup_template
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_service_workgroup_template",
    indexes = {
        @Index(name = "idx_workgroup_template_org", columnList = "org_uid"),
        @Index(name = "idx_workgroup_template_default", columnList = "is_default")
    }
)
public class WorkgroupTemplateEntity extends BaseTemplateEntity {

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
