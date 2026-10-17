package com.bytedesk.ticket.ticket_settings_visibility;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 共享部门可见性判定的规则矩阵测试（通知收件人过滤与详情判定共用，规划 8.1）。
 */
class TicketVisibilitySettingsDataTest {

        // ==================== 顶层模式 ====================

        @Test
        void orgWideAllowsEveryone() {
                TicketVisibilitySettingsData data = normalized("ORG_WIDE", List.of());

                assertThat(data.canViewByDepartment("cat-1", "dept-a", null)).isTrue();
                assertThat(data.canViewByDepartment(null, "dept-a", "dept-b")).isTrue();
        }

        @Test
        void departmentRestrictedMatchesTicketDepartmentOnly() {
                TicketVisibilitySettingsData data = normalized("DEPARTMENT_RESTRICTED", List.of());

                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-a")).isTrue();
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-b")).isFalse();
                // 查看者无部门 → 不可见
                assertThat(data.canViewByDepartment("cat-1", "dept-a", null)).isFalse();
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "")).isFalse();
        }

        @Test
        void departmentRestrictedWithoutTicketDepartmentDegradesOrgWide() {
                // 工单未分配部门 → 公司内部可见（沿用现状语义，D3）
                TicketVisibilitySettingsData data = normalized("DEPARTMENT_RESTRICTED", List.of());

                assertThat(data.canViewByDepartment("cat-1", null, "dept-b")).isTrue();
                assertThat(data.canViewByDepartment("cat-1", "", null)).isTrue();
        }

        @Test
        void departmentBasedMatchesAllowedDepartmentsOnly() {
                TicketVisibilitySettingsData data = normalized("DEPARTMENT_BASED", List.of("dept-a", "dept-c"));

                assertThat(data.canViewByDepartment("cat-1", "dept-x", "dept-a")).isTrue();
                assertThat(data.canViewByDepartment("cat-1", "dept-x", "dept-c")).isTrue();
                assertThat(data.canViewByDepartment("cat-1", "dept-x", "dept-b")).isFalse();
                assertThat(data.canViewByDepartment("cat-1", "dept-x", null)).isFalse();
                // 忽略工单自身部门：白名单为准
                assertThat(data.canViewByDepartment("cat-1", "dept-b", "dept-a")).isTrue();
        }

        @Test
        void departmentBasedWithEmptyAllowedListDegradesOrgWideAfterNormalize() {
                TicketVisibilitySettingsData data = normalized("DEPARTMENT_BASED", List.of());

                assertThat(data.getMode()).isEqualTo("ORG_WIDE");
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-b")).isTrue();
        }

        @Test
        void legacyDepartmentOnlyModeMapsToRestricted() {
                TicketVisibilitySettingsData data = TicketVisibilitySettingsData.builder()
                                .mode("DEPARTMENT_ONLY").build();
                data.normalize();

                assertThat(data.getMode()).isEqualTo("DEPARTMENT_RESTRICTED");
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-a")).isTrue();
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-b")).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "NOT_A_MODE"})
        void invalidModeDegradesOrgWide(String rawMode) {
                TicketVisibilitySettingsData data = TicketVisibilitySettingsData.builder()
                                .mode(rawMode).build();
                data.normalize();

                assertThat(data.getMode()).isEqualTo("ORG_WIDE");
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-b")).isTrue();
        }

        @Test
        void nullModeDegradesOrgWide() {
                TicketVisibilitySettingsData data = TicketVisibilitySettingsData.builder().mode(null).build();
                data.normalize();

                assertThat(data.getMode()).isEqualTo("ORG_WIDE");
                assertThat(data.canViewByDepartment("cat-1", "dept-a", "dept-b")).isTrue();
        }

        // ==================== CATEGORY_BASED ====================

        @Test
        void categoryBasedRestrictedCategoryMatchesTicketDepartment() {
                TicketVisibilitySettingsData data = categoryBased(rule("cat-secret", "DEPARTMENT_RESTRICTED", List.of()));

                assertThat(data.canViewByDepartment("cat-secret", "dept-a", "dept-a")).isTrue();
                assertThat(data.canViewByDepartment("cat-secret", "dept-a", "dept-b")).isFalse();
                // 未命中规则的分类 → 公开回退
                assertThat(data.canViewByDepartment("cat-public", "dept-a", "dept-b")).isTrue();
        }

        @Test
        void categoryBasedDepartmentRuleMatchesAllowedList() {
                TicketVisibilitySettingsData data = categoryBased(rule("cat-kefu", "DEPARTMENT_BASED", List.of("dept-kefu")));

                assertThat(data.canViewByDepartment("cat-kefu", "dept-x", "dept-kefu")).isTrue();
                assertThat(data.canViewByDepartment("cat-kefu", "dept-x", "dept-other")).isFalse();
        }

        @Test
        void categoryBasedUncategorizedTicketTightenedWhenRestrictingRuleExists() {
                TicketVisibilitySettingsData data = categoryBased(rule("cat-secret", "DEPARTMENT_RESTRICTED", List.of()));

                // 无分类 + 存在受限规则 → 普通成员不可见（豁免身份由调用方放行）
                assertThat(data.canViewByDepartment(null, "dept-a", "dept-a")).isFalse();
                assertThat(data.canViewByDepartment("", "dept-a", "dept-a")).isFalse();
                assertThat(data.canViewByDepartment(" ", "dept-a", "dept-a")).isFalse();
        }

        @Test
        void categoryBasedUncategorizedTicketVisibleWhenNoRestrictingRule() {
                TicketVisibilitySettingsData data = categoryBased(rule("cat-public", "ORG_WIDE", List.of()));

                assertThat(data.canViewByDepartment(null, "dept-a", "dept-b")).isTrue();
        }

        @Test
        void categoryBasedRestrictedCategoryWithoutTicketDepartmentDegradesOrgWide() {
                TicketVisibilitySettingsData data = categoryBased(rule("cat-secret", "DEPARTMENT_RESTRICTED", List.of()));

                assertThat(data.canViewByDepartment("cat-secret", null, "dept-b")).isTrue();
        }

        @Test
        void categoryBasedRuleWithEmptyAllowedListDegradesOrgWide() {
                // 分类内 DEPARTMENT_BASED 空部门列表 → 降级 ORG_WIDE（沿用现状，D3）
                TicketVisibilitySettingsData data = categoryBased(rule("cat-empty", "DEPARTMENT_BASED", List.of()));

                assertThat(data.resolveCategoryVisibility("cat-empty")).isEqualTo("ORG_WIDE");
                assertThat(data.canViewByDepartment("cat-empty", "dept-a", "dept-b")).isTrue();
        }

        // ==================== normalizedCopy 无脏写 ====================

        @Test
        void normalizedCopyDoesNotMutateSource() {
                // CATEGORY_BASED 模式保留分类规则，可同时验证顶层部门与嵌套规则的深拷贝
                TicketVisibilitySettingsData source = TicketVisibilitySettingsData.builder()
                                .mode("CATEGORY_BASED")
                                .departmentUids(new ArrayList<>(List.of("dept-a", "dept-a", " ")))
                                .categoryRules(List.of(rule("cat-kefu", "DEPARTMENT_BASED",
                                                new ArrayList<>(List.of("dept-kefu", "dept-kefu")))))
                                .build();
                // 故意不调用 normalize：源保持原始脏数据
                TicketVisibilitySettingsData copy = source.normalizedCopy();

                // 拷贝已规范化：嵌套规则去重保留；顶层非 DEPARTMENT_BASED 模式清空部门列表
                assertThat(copy.getCategoryRules()).hasSize(1);
                assertThat(copy.getCategoryRules().get(0).getDepartmentUids()).containsExactly("dept-kefu");
                assertThat(copy.getDepartmentUids()).isEmpty();
                // 源不变：仍含重复
                assertThat(source.getDepartmentUids()).hasSize(3);
                assertThat(source.getCategoryRules().get(0).getDepartmentUids()).hasSize(2);
                // 二次拷贝结果一致（幂等）
                assertThat(source.normalizedCopy()).isEqualTo(copy);
        }

        @Test
        void hasRestrictingCategoryRuleDetectsAnyNonOrgWideRule() {
                assertThat(categoryBased(rule("cat-public", "ORG_WIDE", List.of()))
                                .hasRestrictingCategoryRule()).isFalse();
                assertThat(categoryBased(rule("cat-secret", "DEPARTMENT_RESTRICTED", List.of()))
                                .hasRestrictingCategoryRule()).isTrue();
                assertThat(normalized("ORG_WIDE", List.of()).hasRestrictingCategoryRule()).isFalse();
                // mode 非 CATEGORY_BASED 时 normalize 会清空 rules
                assertThat(normalized("DEPARTMENT_BASED", List.of()).hasRestrictingCategoryRule()).isFalse();
        }

        // ==================== fixtures ====================

        private static TicketVisibilitySettingsData normalized(String mode, List<String> departmentUids) {
                TicketVisibilitySettingsData data = TicketVisibilitySettingsData.builder()
                                .mode(mode).departmentUids(departmentUids).build();
                data.normalize();
                return data;
        }

        private static TicketVisibilitySettingsData categoryBased(TicketVisibilityCategoryRuleData... rules) {
                TicketVisibilitySettingsData data = TicketVisibilitySettingsData.builder()
                                .mode("CATEGORY_BASED")
                                .categoryRules(List.of(rules))
                                .build();
                data.normalize();
                return data;
        }

        private static TicketVisibilityCategoryRuleData rule(String categoryUid, String visibility,
                        List<String> departmentUids) {
                return TicketVisibilityCategoryRuleData.builder()
                                .categoryUid(categoryUid).visibility(visibility)
                                .departmentUids(departmentUids).build();
        }
}
