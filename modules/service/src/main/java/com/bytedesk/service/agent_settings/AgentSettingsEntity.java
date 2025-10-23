/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Agent configuration settings for reusable agent settings
 */
package com.bytedesk.service.agent_settings;

import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.BaseSettingsEntity;
import com.bytedesk.kbase.settings_ratedown.RatedownSettingsEntity;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;

import jakarta.persistence.Embedded;
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
     * Message leave settings
     */
    @Embedded
    @lombok.Builder.Default
    private MessageLeaveSettings messageLeaveSettings = new MessageLeaveSettings();

    /**
     * Auto-reply settings
     */
    @Embedded
    @lombok.Builder.Default
    private AutoReplySettings autoReplySettings = new AutoReplySettings();

    /**
     * Queue settings
     */
    @Embedded
    @lombok.Builder.Default
    private QueueSettings queueSettings = new QueueSettings();

    /**
     * Rating down settings
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private RatedownSettingsEntity rateDownSettings;
}
