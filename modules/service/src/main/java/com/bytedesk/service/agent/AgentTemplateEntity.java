/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Agent configuration template for reusable agent settings
 */
package com.bytedesk.service.agent;

import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.BaseTemplateEntity;
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
 * Agent configuration template for reusable settings
 * 
 * Purpose:
 * - Store reusable agent configurations
 * - Enable sharing common settings across multiple agents
 * - Simplify agent configuration management
 * 
 * Usage:
 * - Create templates for different agent roles (e.g., "Junior Support", "Senior Support", "VIP Agent")
 * - Assign templates to agents via AgentEntity.template reference
 * - Multiple agents can share the same template
 * 
 * Common fields (inherited from BaseTemplateEntity):
 * - name, description, isDefault, enabled
 * - serviceSettings, inviteSettings, intentionSettings
 * 
 * Agent-specific fields:
 * - messageLeaveSettings, autoReplySettings, queueSettings, rateDownSettings
 * 
 * Database Table: bytedesk_service_agent_template
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "bytedesk_service_agent_template",
    indexes = {
        @Index(name = "idx_agent_template_org", columnList = "org_uid"),
        @Index(name = "idx_agent_template_default", columnList = "is_default")
    }
)
public class AgentTemplateEntity extends BaseTemplateEntity {

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
