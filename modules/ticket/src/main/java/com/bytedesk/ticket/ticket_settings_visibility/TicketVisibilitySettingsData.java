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

    /**
     * 普通成员（非报告人/处理人/管理员豁免对象）的部门维度可见性判定。
     * <p>
     * 供详情判定与通知收件人过滤共用，保证「列表可见」与「通知可见」同口径；调用方需先放行
     * 报告人/处理人/管理员等豁免身份。须在 {@link #normalize()} 之后调用。
     *
     * @param categoryUid        工单分类 uid（可为空）
     * @param ticketDepartmentUid 工单自身部门 uid（可为空）
     * @param viewerDeptUid      查看成员部门 uid（可为空，表示无部门）
     * @return 该成员按本设置是否可见
     */
    public boolean canViewByDepartment(String categoryUid, String ticketDepartmentUid, String viewerDeptUid) {
        if (TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED.name().equals(mode)) {
            // 工单未分配部门：视为公司内部可见（与 canViewTicket 现状一致）
            if (!StringUtils.hasText(ticketDepartmentUid)) {
                return true;
            }
            return StringUtils.hasText(viewerDeptUid) && ticketDepartmentUid.equals(viewerDeptUid);
        }
        if (TicketVisibilityModeEnum.DEPARTMENT_BASED.name().equals(mode)) {
            List<String> allowed = departmentUids == null ? new ArrayList<>() : departmentUids;
            return StringUtils.hasText(viewerDeptUid) && allowed.contains(viewerDeptUid);
        }
        if (TicketVisibilityModeEnum.CATEGORY_BASED.name().equals(mode)) {
            // 未分类工单无法套用分类规则：存在受限规则时收紧为仅豁免身份可见
            if (!StringUtils.hasText(categoryUid) && hasRestrictingCategoryRule()) {
                return false;
            }
            String categoryVisibility = resolveCategoryVisibility(categoryUid);
            if (TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED.name().equals(categoryVisibility)) {
                if (!StringUtils.hasText(ticketDepartmentUid)) {
                    return true;
                }
                return StringUtils.hasText(viewerDeptUid) && ticketDepartmentUid.equals(viewerDeptUid);
            }
            if (TicketVisibilityModeEnum.DEPARTMENT_BASED.name().equals(categoryVisibility)) {
                List<String> allowed = resolveCategoryDepartmentUids(categoryUid);
                return StringUtils.hasText(viewerDeptUid) && allowed.contains(viewerDeptUid);
            }
        }
        // ORG_WIDE / 命中公开分类 / 无匹配规则：公司内部可见
        return true;
    }

    /**
     * 是否存在任一非 ORG_WIDE 的分类规则（CATEGORY_BASED 下未分类工单收紧用）。
     */
    public boolean hasRestrictingCategoryRule() {
        if (categoryRules == null) {
            return false;
        }
        for (TicketVisibilityCategoryRuleData rule : categoryRules) {
            if (rule != null && !TicketVisibilityModeEnum.ORG_WIDE.name().equals(rule.getVisibility())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回规范化深拷贝：不改动自身（含嵌套分类规则），供通知等只读链路安全使用，
     * 避免在事务内修改 JPA 管理的 content 引起脏写。
     */
    public TicketVisibilitySettingsData normalizedCopy() {
        TicketVisibilitySettingsData copy = TicketVisibilitySettingsData.builder()
                .mode(mode)
                .departmentUids(departmentUids == null ? new ArrayList<>() : new ArrayList<>(departmentUids))
                .categoryRules(categoryRules == null ? new ArrayList<>()
                        : categoryRules.stream()
                                .filter(java.util.Objects::nonNull)
                                .map(rule -> TicketVisibilityCategoryRuleData.builder()
                                        .categoryUid(rule.getCategoryUid())
                                        .visibility(rule.getVisibility())
                                        .departmentUids(rule.getDepartmentUids() == null ? new ArrayList<>()
                                                : new ArrayList<>(rule.getDepartmentUids()))
                                        .build())
                                .collect(java.util.stream.Collectors.toList()))
                .build();
        copy.normalize();
        return copy;
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