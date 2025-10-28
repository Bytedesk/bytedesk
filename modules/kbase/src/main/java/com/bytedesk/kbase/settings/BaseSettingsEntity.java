/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Base settings entity with common configuration fields
 */
package com.bytedesk.kbase.settings;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_invite.InviteSettingsEntity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Base settings entity with common configuration fields
 * 
 * Purpose:
 * - Provide common fields for all settings entities
 * - Reduce code duplication across Agent/Workgroup/Robot settings
 * - Ensure consistent settings structure
 * 
 * Common Fields:
 * - name: Settings name (required)
 * - description: Settings description
 * - isDefault: Whether this is a default settings template for new entities
 * - enabled: Whether the settings is enabled
 * - serviceSettings: Common service settings
 * - inviteSettings: Invitation settings
 * - intentionSettings: Intention recognition settings
 * 
 * Usage:
 * - Extend this class in AgentSettingsEntity, WorkgroupSettingsEntity,
 * RobotSettingsEntity
 * - Add specific settings fields in subclasses
 */
@MappedSuperclass
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Settings name (required)
     */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /**
     * Settings description
     */
    @lombok.Builder.Default
    private String description = "配置模板";

    /**
     * Whether this is a default settings template for new entities
     * Only one settings per organization should have isDefault=true
     */
    @lombok.Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /**
     * Service settings (published)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private ServiceSettingsEntity serviceSettings;

    /**
     * Invitation settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private InviteSettingsEntity inviteSettings;

    /**
     * Intention recognition settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private IntentionSettingsEntity intentionSettings;

    /**
     * Whether the settings is enabled
     */
    @lombok.Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    /**
     * Draft service settings (used in admin editing/testing, not affecting online
     * runtime until published)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private ServiceSettingsEntity draftServiceSettings;

    /**
     * Draft invitation settings (admin editing/testing only)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private InviteSettingsEntity draftInviteSettings;

    /**
     * Draft intention recognition settings (admin editing/testing only)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private IntentionSettingsEntity draftIntentionSettings;

    /**
     * Whether there are unpublished changes in draft
     */
    @lombok.Builder.Default
    private Boolean hasUnpublishedChanges = false;

    /**
     * Last published time
     */
    private ZonedDateTime publishedAt;
}
