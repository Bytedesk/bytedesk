package com.bytedesk.service.worktime_settings;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.bytedesk.service.agent_settings.AgentSettingsEntity;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * Shared resolver for Agent/Workgroup draft-vs-published worktime settings.
 *
 * <p>Extracted from the duplicated logic in
 * {@code AgentThreadRoutingStrategy#resolveEffectiveWorktimeSettings} and
 * {@code WorkgroupThreadRoutingStrategy#resolveEffectiveWorktimeSettings}.</p>
 *
 * <p>Priority: draft (when the visitor request opts in) → published → {@code null}
 * (treated as always-in-service by {@link WorktimeService}).</p>
 */
@Slf4j
@Component
public class WorktimeSettingsResolver {

    @Nullable
    public WorktimeSettingEntity resolve(@Nullable VisitorRequest visitorRequest,
            @Nullable AgentSettingsEntity settings) {
        if (settings == null) {
            return null;
        }
        return resolveInternal(visitorRequest,
                settings.getWorktimeSettings(),
                settings.getDraftWorktimeSettings());
    }

    @Nullable
    public WorktimeSettingEntity resolve(@Nullable VisitorRequest visitorRequest,
            @Nullable WorkgroupSettingsEntity settings) {
        if (settings == null) {
            return null;
        }
        return resolveInternal(visitorRequest,
                settings.getWorktimeSettings(),
                settings.getDraftWorktimeSettings());
    }

    @Nullable
    private WorktimeSettingEntity resolveInternal(@Nullable VisitorRequest visitorRequest,
            @Nullable WorktimeSettingEntity published,
            @Nullable WorktimeSettingEntity draft) {
        boolean useDraft = visitorRequest != null && Boolean.TRUE.equals(visitorRequest.getDraft());
        if (useDraft && draft != null) {
            return draft;
        }
        return published;
    }
}
