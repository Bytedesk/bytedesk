/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-25
 * @Description: Base settings response with common configuration fields
 */
package com.bytedesk.kbase.settings;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.kbase.settings_emotion.EmotionSettingResponse;
import com.bytedesk.kbase.settings_intention.IntentionSettingsResponse;
import com.bytedesk.kbase.settings_invite.InviteSettingsResponse;
import com.bytedesk.kbase.settings_service.ServiceSettingsResponse;
import com.bytedesk.kbase.settings_summary.SummarySettingsResponse;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsResponse;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Base settings response with common configuration fields
 * 
 * Purpose:
 * - Provide common fields for all settings response DTOs
 * - Reduce code duplication across Agent/Workgroup/Robot settings responses
 * - Ensure consistent response structure
 * 
 * Common Fields:
 * - name: Settings name
 * - description: Settings description
 * - isDefault: Whether this is a default settings template
 * - enabled: Whether the settings is enabled
 * - serviceSettings: Common service settings
 * - inviteSettings: Invitation settings
 * - intentionSettings: Intention recognition settings
 * - draftServiceSettings/invite/intention: Draft versions for admin editing
 * - hasUnpublishedChanges/publishedAt: Draft publish status metadata
 * 
 * Usage:
 * - Extend this class in AgentSettingsResponse, WorkgroupSettingsResponse, RobotSettingsResponse
 * - Add specific settings fields in subclasses
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * Settings name
     */
    private String name;

    /**
     * Settings description
     */
    private String description;

    /**
     * Whether this is a default settings template for new entities
     */
    private Boolean isDefault;

    /**
     * Whether the settings is enabled
     */
    private Boolean enabled;

    /**
     * Service settings (common to all settings)
     */
    private ServiceSettingsResponse serviceSettings;

    /**
     * Trigger settings (common to all settings)
     */
    private TriggerSettingsResponse triggerSettings;

    /**
     * Invitation settings (common to all settings)
     */
    private InviteSettingsResponse inviteSettings;

    /**
     * Intention recognition settings (common to all settings)
     */
    private IntentionSettingsResponse intentionSettings;

    /**
     * Emotion recognition settings (common to all settings)
     */
    private EmotionSettingResponse emotionSettings;

    /**
     * Conversation summary settings (common to all settings)
     */
    private SummarySettingsResponse summarySettings;

    /**
     * Draft service settings (admin editing/testing only)
     */
    private ServiceSettingsResponse draftServiceSettings;

    /**
     * Draft trigger settings (admin editing/testing only)
     */
    private TriggerSettingsResponse draftTriggerSettings;

    /**
     * Draft invitation settings (admin editing/testing only)
     */
    private InviteSettingsResponse draftInviteSettings;

    /**
     * Draft intention recognition settings (admin editing/testing only)
     */
    private IntentionSettingsResponse draftIntentionSettings;

    /**
     * Draft emotion recognition settings (admin editing/testing only)
     */
    private EmotionSettingResponse draftEmotionSettings;

    /**
     * Draft conversation summary settings (admin editing/testing only)
     */
    private SummarySettingsResponse draftSummarySettings;

    /**
     * Whether there are unpublished changes in draft
     */
    private Boolean hasUnpublishedChanges;

    /**
     * Last published time
     */
    private ZonedDateTime publishedAt;
}
