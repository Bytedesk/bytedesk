/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Agent configuration settings for reusable agent settings
 */
package com.bytedesk.service.agent_settings;

import com.bytedesk.kbase.auto_reply.settings.AutoReplySettingsEntity;
import com.bytedesk.kbase.settings.BaseSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettingsEntity;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;

import jakarta.persistence.CascadeType;
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
 * Agent configuration settings for reusable settings
 * 
 * Purpose:
 * - Store reusable agent configurations
 * - Enable sharing common settings across multiple agents
 * - Simplify agent configuration management
 * 
 * Usage:
 * - Create settings for different agent roles (e.g., "Junior Support", "Senior Support", "VIP Agent")
 * - Assign settings to agents via AgentEntity.settings reference
 * - Multiple agents can share the same settings
 * 
 * Common fields (inherited from BaseSettingsEntity):
 * - name, description, isDefault, enabled
 * - serviceSettings, inviteSettings, intentionSettings
 * 
 * Agent-specific fields:
 * - messageLeaveSettings, autoReplySettings, queueSettings, rateDownSettings
 * 
 * Database Table: bytedesk_service_agent_settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_service_agent_settings",
    indexes = {
        @Index(name = "idx_agent_settings_org", columnList = "org_uid"),
        @Index(name = "idx_agent_settings_default", columnList = "is_default")
    }
)
public class AgentSettingsEntity extends BaseSettingsEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Maximum concurrent threads the agent can handle
     * Moved from AgentEntity to centralize configuration
     */
    @lombok.Builder.Default
    private Integer maxThreadCount = 10;

    /**
     * Whether timeout reminder is enabled for agent
     */
    @lombok.Builder.Default
    private Boolean timeoutRemindEnabled = false;

    /**
     * Timeout reminder time in minutes
     */
    @lombok.Builder.Default
    private Integer timeoutRemindTime = 5;

    /**
     * Timeout reminder tip message
     */
    @lombok.Builder.Default
    private String timeoutRemindTip = com.bytedesk.core.constant.I18Consts.I18N_AGENT_TIMEOUT_TIP;

    /**
     * Message leave settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private MessageLeaveSettingsEntity messageLeaveSettings;

    /**
     * Draft Message leave settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private MessageLeaveSettingsEntity draftMessageLeaveSettings;

    /**
     * Auto-reply settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private AutoReplySettingsEntity autoReplySettings;

    /**
     * Draft Auto-reply settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private AutoReplySettingsEntity draftAutoReplySettings;

    /**
     * Queue settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private QueueSettingsEntity queueSettings;

    /**
     * Draft Queue settings
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE, jakarta.persistence.CascadeType.REMOVE })
    // @NotFound(action = NotFoundAction.IGNORE)
    private QueueSettingsEntity draftQueueSettings;

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
}
