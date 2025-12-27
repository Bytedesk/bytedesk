/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-25
 * @Description: Base settings request with common configuration fields
 */
package com.bytedesk.kbase.settings;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.kbase.settings_emotion.EmotionSettingRequest;
import com.bytedesk.kbase.settings_intention.IntentionSettingsRequest;
import com.bytedesk.kbase.settings_invite.InviteSettingsRequest;
import com.bytedesk.kbase.settings_service.ServiceSettingsRequest;
import com.bytedesk.kbase.settings_summary.SummarySettingsRequest;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Base settings request with common configuration fields
 * 
 * Purpose:
 * - Provide common fields for all settings request DTOs
 * - Reduce code duplication across Agent/Workgroup/Robot settings requests
 * - Ensure consistent request structure
 * 
 * Common Fields:
 * - name: Settings name (required)
 * - description: Settings description
 * - isDefault: Whether this is a default settings template
 * - enabled: Whether the settings is enabled
 * - serviceSettings: Common service settings
 * - inviteSettings: Invitation settings
 * - intentionSettings: Intention recognition settings
 * 
 * Usage:
 * - Extend this class in AgentSettingsRequest, WorkgroupSettingsRequest, RobotSettingsRequest
 * - Add specific settings fields in subclasses
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class BaseSettingsRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * Settings name (required)
     */
    private String name;

    /**
     * Settings description
     */
    private String description;

    /**
     * Whether this is a default settings template for new entities
     * Only one settings per organization should have isDefault=true
     */
    private Boolean isDefault;

    /**
     * Whether the settings is enabled
     */
    private Boolean enabled;

    /**
     * Service settings (common to all settings)
     */
    private ServiceSettingsRequest serviceSettings;

    /**
     * Trigger settings (common to all settings)
     */
    private TriggerSettingsRequest triggerSettings;

    /**
     * Invitation settings (common to all settings)
     */
    private InviteSettingsRequest inviteSettings;

    /**
     * Intention recognition settings (common to all settings)
     */
    private IntentionSettingsRequest intentionSettings;

    /**
     * Emotion recognition settings (common to all settings)
     */
    private EmotionSettingRequest emotionSettings;

    /**
     * Conversation summary settings (common to all settings)
     */
    private SummarySettingsRequest summarySettings;
}
