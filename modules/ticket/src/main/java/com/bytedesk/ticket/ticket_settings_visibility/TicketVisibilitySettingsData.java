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
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TicketVisibilitySettingsData implements Serializable {

    private static final String LEGACY_DEPARTMENT_ONLY = "DEPARTMENT_ONLY";

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String mode = TicketVisibilityModeEnum.ORG_WIDE.name();

    @Builder.Default
    private List<String> departmentUids = new ArrayList<>();

    @Builder.Default
    private List<TicketVisibilityCategoryRuleData> categoryRules = new ArrayList<>();

    public void normalize() {
        mode = resolveMode(mode).name();
        departmentUids = normalizeDepartmentUids(departmentUids);
        if (TicketVisibilityModeEnum.DEPARTMENT_BASED.name().equals(mode)
                && departmentUids.isEmpty()) {
            log.warn("ticket visibility settings degraded to ORG_WIDE: departmentUids is empty, mode={}", mode);
            mode = TicketVisibilityModeEnum.ORG_WIDE.name();
        }
        if (categoryRules == null) {
            categoryRules = new ArrayList<>();
        } else {
            Map<String, TicketVisibilityCategoryRuleData> deduplicated = new LinkedHashMap<>();
            for (TicketVisibilityCategoryRuleData rule : categoryRules) {
                if (rule == null || !StringUtils.hasText(rule.getCategoryUid())) {
                    continue;
                }
                String ruleVisibility = resolveRuleVisibility(rule.getVisibility());
                List<String> departmentUids = normalizeDepartmentUids(rule.getDepartmentUids());
                if (TicketVisibilityModeEnum.DEPARTMENT_BASED.name().equals(ruleVisibility)
                        && departmentUids.isEmpty()) {
                    // 选择部门但未指定任何部门，退化为公司内部可见。
                    // 该告警用于识别发布/保存链路中丢失 departmentUids 的异常数据
                    //（如历史版本 copyVisibilitySettingsData 丢字段导致的退化），便于运营定位受影响配置并重新发布。
                    log.warn("ticket visibility category rule degraded to ORG_WIDE: departmentUids is empty, "
                            + "categoryUid={}, ruleVisibility={}", rule.getCategoryUid(), ruleVisibility);
                    ruleVisibility = TicketVisibilityModeEnum.ORG_WIDE.name();
                }
                TicketVisibilityCategoryRuleData normalized = TicketVisibilityCategoryRuleData.builder()
                        .categoryUid(rule.getCategoryUid().trim())
                        .visibility(ruleVisibility)
                        .departmentUids(departmentUids)
                        .build();
                deduplicated.put(normalized.getCategoryUid(), normalized);
            }
            categoryRules = new ArrayList<>(deduplicated.values());
        }

        if (!TicketVisibilityModeEnum.CATEGORY_BASED.name().equals(mode)) {
            categoryRules = new ArrayList<>();
        }
        if (!TicketVisibilityModeEnum.DEPARTMENT_BASED.name().equals(mode)) {
            departmentUids = new ArrayList<>();
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

    public List<String> resolveCategoryDepartmentUids(String categoryUid) {
        if (!StringUtils.hasText(categoryUid) || categoryRules == null) {
            return new ArrayList<>();
        }
        for (TicketVisibilityCategoryRuleData rule : categoryRules) {
            if (rule != null && categoryUid.equals(rule.getCategoryUid())
                    && TicketVisibilityModeEnum.DEPARTMENT_BASED.name().equals(rule.getVisibility())) {
                return rule.getDepartmentUids() == null ? new ArrayList<>() : rule.getDepartmentUids();
            }
        }
        return new ArrayList<>();
    }

    private List<String> normalizeDepartmentUids(List<String> departmentUids) {
        if (departmentUids == null) {
            return new ArrayList<>();
        }
        List<String> normalized = new ArrayList<>();
        for (String departmentUid : departmentUids) {
            if (StringUtils.hasText(departmentUid)) {
                String trimmed = departmentUid.trim();
                if (!normalized.contains(trimmed)) {
                    normalized.add(trimmed);
                }
            }
        }
        return normalized;
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