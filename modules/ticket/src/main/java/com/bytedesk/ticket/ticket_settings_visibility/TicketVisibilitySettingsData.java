package com.bytedesk.ticket.ticket_settings_visibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVisibilitySettingsData implements Serializable {

    private static final String LEGACY_DEPARTMENT_ONLY = "DEPARTMENT_ONLY";

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String mode = TicketVisibilityModeEnum.ORG_WIDE.name();

    @Builder.Default
    private List<TicketVisibilityCategoryRuleData> categoryRules = new ArrayList<>();

    public void normalize() {
        mode = resolveMode(mode).name();
        if (categoryRules == null) {
            categoryRules = new ArrayList<>();
        } else {
            Map<String, TicketVisibilityCategoryRuleData> deduplicated = new LinkedHashMap<>();
            for (TicketVisibilityCategoryRuleData rule : categoryRules) {
                if (rule == null || !StringUtils.hasText(rule.getCategoryUid())) {
                    continue;
                }
                TicketVisibilityCategoryRuleData normalized = TicketVisibilityCategoryRuleData.builder()
                        .categoryUid(rule.getCategoryUid().trim())
                        .visibility(resolveRuleVisibility(rule.getVisibility()))
                        .build();
                deduplicated.put(normalized.getCategoryUid(), normalized);
            }
            categoryRules = new ArrayList<>(deduplicated.values());
        }

        if (!TicketVisibilityModeEnum.CATEGORY_BASED.name().equals(mode)) {
            categoryRules = new ArrayList<>();
        }
    }

    public String resolveCategoryVisibility(String categoryUid) {
        if (!StringUtils.hasText(categoryUid) || categoryRules == null) {
            return TicketVisibilityModeEnum.ORG_WIDE.name();
        }
        for (TicketVisibilityCategoryRuleData rule : categoryRules) {
            if (rule != null && categoryUid.equals(rule.getCategoryUid())) {
                return resolveRuleVisibility(rule.getVisibility());
            }
        }
        return TicketVisibilityModeEnum.ORG_WIDE.name();
    }

    private TicketVisibilityModeEnum resolveMode(String rawMode) {
        if (!StringUtils.hasText(rawMode)) {
            return TicketVisibilityModeEnum.ORG_WIDE;
        }
        String normalizedMode = rawMode.trim().toUpperCase();
        if (LEGACY_DEPARTMENT_ONLY.equals(normalizedMode)) {
            return TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED;
        }
        try {
            return TicketVisibilityModeEnum.valueOf(normalizedMode);
        } catch (IllegalArgumentException ex) {
            return TicketVisibilityModeEnum.ORG_WIDE;
        }
    }

    private String resolveRuleVisibility(String rawVisibility) {
        TicketVisibilityModeEnum visibility = resolveMode(rawVisibility);
        if (TicketVisibilityModeEnum.CATEGORY_BASED.equals(visibility)) {
            return TicketVisibilityModeEnum.ORG_WIDE.name();
        }
        return visibility.name();
    }
}