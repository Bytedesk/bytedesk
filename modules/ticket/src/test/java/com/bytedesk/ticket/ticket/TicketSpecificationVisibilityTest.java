package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class TicketSpecificationVisibilityTest {

        @Test
        void appendVisibilityPredicatesDoesNotRestrictTopLevelDepartmentBasedWhenCurrentDepartmentAllowed() {
                TicketRequest request = TicketRequest.builder()
                                .visibilityRestricted(true)
                                .visibilityMode("DEPARTMENT_BASED")
                                .visibilityCurrentUserUid("user-1")
                                .visibilityCurrentUserDepartmentUid("dept-b")
                                .visibilityAllowedDepartmentUids(List.of("dept-a", "dept-b"))
                                .build();

                CriteriaContext context = newCriteriaContext();
                List<Predicate> predicates = new ArrayList<>();

                invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

                assertThat(predicates).isEmpty();
        }

        @Test
        void appendVisibilityPredicatesKeepsOnlyReporterAndAssigneeVisibleForTopLevelDepartmentBasedWhenNotAllowed() {
                TicketRequest request = TicketRequest.builder()
                                .visibilityRestricted(true)
                                .visibilityMode("DEPARTMENT_BASED")
                                .visibilityCurrentUserUid("user-1")
                                .visibilityCurrentUserMemberUid("member-1")
                                .visibilityCurrentUserDepartmentUid("dept-z")
                                .visibilityAllowedDepartmentUids(List.of("dept-a", "dept-b"))
                                .build();

                CriteriaContext context = newCriteriaContext();
                List<Predicate> predicates = new ArrayList<>();

                invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

                assertThat(predicates).hasSize(1);
                verify(context.criteriaBuilder).equal(context.userUidPath, "user-1");
                verify(context.criteriaBuilder).equal(context.userUidPath, "member-1");
        }

        @Test
        void appendVisibilityPredicatesCombinesTopLevelDepartmentBasedPerTypeContextWhenTypeAbsent() {
                TicketRequest request = TicketRequest.builder()
                                .visibilityRestricted(true)
                                .visibilityCurrentUserUid("user-1")
                                .visibilityCurrentUserMemberUid("member-1")
                                .visibilityCurrentUserDepartmentUid("dept-z")
                                .visibilityInternalContext(TicketVisibilityQueryContext.builder()
                                                .mode("ORG_WIDE")
                                                .restricted(false)
                                                .build())
                                .visibilityExternalContext(TicketVisibilityQueryContext.builder()
                                                .mode("DEPARTMENT_BASED")
                                                .restricted(true)
                                                .allowedDepartmentUids(List.of("dept-a", "dept-b"))
                                                .build())
                                .build();

                CriteriaContext context = newCriteriaContext();
                List<Predicate> predicates = new ArrayList<>();

                invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

                assertThat(predicates).hasSize(1);
                verify(context.criteriaBuilder).equal(context.typePath, "INTERNAL");
                verify(context.criteriaBuilder).equal(context.typePath, "EXTERNAL");
                verify(context.criteriaBuilder).disjunction();
        }

    @Test
    void appendVisibilityPredicatesDoesNotAddRestrictionWhenCurrentDepartmentIsAllowed() {
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityMode("CATEGORY_BASED")
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserDepartmentUid("dept-b")
                .visibilityRestrictedCategoryDepartmentUids(Map.of("cat-1", List.of("dept-b")))
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        // 新语义：viewer 在允许部门内时，分类规则本身不限制（无需 dept in 查询），
        // 但未分类工单仍被收紧为仅创建人/受理人可见（1 条谓词）
        assertThat(predicates).hasSize(1);
        verify(context.departmentPath, times(0)).in((java.util.Collection<?>) any());
        verify(context.criteriaBuilder).isNull(context.categoryPath);
    }

    @Test
    void appendVisibilityPredicatesAddsRestrictionWhenCurrentDepartmentIsNotAllowed() {
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityMode("CATEGORY_BASED")
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserDepartmentUid("dept-z")
                .visibilityRestrictedCategoryDepartmentUids(Map.of("cat-1", List.of("dept-b")))
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        assertThat(predicates).hasSize(1);
        verify(context.criteriaBuilder).equal(context.categoryPath, "cat-1");
        verify(context.departmentPath, times(0)).in((java.util.Collection<?>) any());
    }

    @Test
    void appendVisibilityPredicatesRestrictsTicketWithoutCategoryWhenCategoryRulesExist() {
        // viewer 在允许部门内（旧版本此时不会深加任何谓词），但未分类工单仍应被收紧为仅创建人/受理人可见
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityMode("CATEGORY_BASED")
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserDepartmentUid("dept-b")
                .visibilityRestrictedCategoryDepartmentUids(Map.of("cat-1", List.of("dept-b")))
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        assertThat(predicates).hasSize(1);
        verify(context.criteriaBuilder).isNull(context.categoryPath);
        verify(context.criteriaBuilder).equal(context.categoryPath, "");
    }

    @Test
    void appendVisibilityPredicatesDoesNotRestrictWhenNoRestrictingCategoryRulesConfigured() {
        // 无任何受限分类规则（规则全为 ORG_WIDE/退化后为空）时不加限制，保持兼容
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityMode("CATEGORY_BASED")
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserDepartmentUid("dept-b")
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        assertThat(predicates).isEmpty();
    }

    @Test
    void appendVisibilityPredicatesKeepsDepartmentRestrictedCategoryUsingCurrentDepartmentComparison() {
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityMode("CATEGORY_BASED")
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserDepartmentUid("dept-a")
                .visibilityRestrictedCategoryUids(List.of("cat-2"))
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        assertThat(predicates).hasSize(1);
        verify(context.criteriaBuilder).equal(context.departmentPath, "dept-a");
        verify(context.criteriaBuilder).equal(context.categoryPath, "cat-2");
    }

    @Test
    void appendVisibilityPredicatesKeepsAssigneeVisibleByMemberUidWhenCategoryRestrictedAndUncategorized() {
        // G8：自动分配的外部工单 assignee JSON 存 member uid（与 user uid 不同），
        // 且自动创建工单无 categoryUid——受限规则存在时也必须让处理人始终可见
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityMode("CATEGORY_BASED")
                .visibilityCurrentUserUid("user-9")
                .visibilityCurrentUserMemberUid("member-9")
                .visibilityCurrentUserDepartmentUid("dept-design")
                .visibilityRestrictedCategoryDepartmentUids(Map.of("cat-1", List.of("dept-kefu")))
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        // 1 条谓词：alwaysVisible OR NOT(restrictedAndNotVisible)
        assertThat(predicates).hasSize(1);
        // reporterSelf 双口径：userUid = user-9 OR userUid = member-9
        verify(context.criteriaBuilder).equal(context.userUidPath, "user-9");
        verify(context.criteriaBuilder).equal(context.userUidPath, "member-9");
        // assigneeSelf 双口径：like assignee %"uid":"user-9"% / %"uid":"member-9"%
        org.mockito.ArgumentCaptor<String> patternCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(context.criteriaBuilder, times(2)).like(any(), patternCaptor.capture(), eq('\\'));
        assertThat(patternCaptor.getAllValues())
                .containsExactlyInAnyOrder("%\"uid\":\"user-9\"%", "%\"uid\":\"member-9\"%");
    }

    @Test
    void appendVisibilityPredicatesCombinesPerTypeContextsWhenTypeAbsent() {
        // G2：无 type 混合列表——INTERNAL=DEPARTMENT_RESTRICTED、EXTERNAL=CATEGORY_BASED（分类不允许），
        // 组合谓词 = reporter/assignee OR (type=INTERNAL AND deptPred) OR (type=EXTERNAL AND categoryPred)
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserMemberUid("member-1")
                .visibilityCurrentUserDepartmentUid("dept-a")
                .visibilityInternalContext(TicketVisibilityQueryContext.builder()
                        .mode("DEPARTMENT_RESTRICTED")
                        .restricted(true)
                        .build())
                .visibilityExternalContext(TicketVisibilityQueryContext.builder()
                        .mode("CATEGORY_BASED")
                        .restricted(true)
                        .restrictedCategoryDepartmentUids(Map.of("cat-1", List.of("dept-kefu")))
                        .build())
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        assertThat(predicates).hasSize(1);
        // 按类型分别判定
        verify(context.criteriaBuilder).equal(context.typePath, "INTERNAL");
        verify(context.criteriaBuilder).equal(context.typePath, "EXTERNAL");
        // INTERNAL 分支：or(noDept, sameDept)
        verify(context.criteriaBuilder).equal(context.departmentPath, "dept-a");
        // EXTERNAL 分支：分类 cat-1 因部门不允许而受限
        verify(context.criteriaBuilder).equal(context.categoryPath, "cat-1");
    }

    @Test
    void appendVisibilityPredicatesTreatsUnrestrictedTypeContextAsAlwaysVisible() {
        // G2：某类型设置 ORG_WIDE（restricted=false）→ 该类型工单全部可见（conjunction）
        TicketRequest request = TicketRequest.builder()
                .visibilityRestricted(true)
                .visibilityCurrentUserUid("user-1")
                .visibilityCurrentUserDepartmentUid("dept-a")
                .visibilityInternalContext(TicketVisibilityQueryContext.builder()
                        .mode("ORG_WIDE")
                        .restricted(false)
                        .build())
                .visibilityExternalContext(TicketVisibilityQueryContext.builder()
                        .mode("CATEGORY_BASED")
                        .restricted(true)
                        .restrictedCategoryDepartmentUids(Map.of("cat-1", List.of("dept-kefu")))
                        .build())
                .build();

        CriteriaContext context = newCriteriaContext();
        List<Predicate> predicates = new ArrayList<>();

        invokeAppendVisibilityPredicates(request, context.root, context.criteriaBuilder, predicates);

        assertThat(predicates).hasSize(1);
        // ORG_WIDE 上下文走 conjunction 恒真
        verify(context.criteriaBuilder).conjunction();
    }

    private static void invokeAppendVisibilityPredicates(TicketRequest request,
            Root<TicketEntity> root,
            CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates) {
        try {
            Method method = TicketSpecification.class.getDeclaredMethod(
                    "appendVisibilityPredicates",
                    TicketRequest.class,
                    Root.class,
                    CriteriaBuilder.class,
                    List.class);
            method.setAccessible(true);
            method.invoke(null, request, root, criteriaBuilder, predicates);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static CriteriaContext newCriteriaContext() {
        Root<TicketEntity> root = mock(Root.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path userUidPath = mock(Path.class);
        Path assigneePath = mock(Path.class);
        Path departmentPath = mock(Path.class);
        Path categoryPath = mock(Path.class);
        Path typePath = mock(Path.class);

        when(root.get("userUid")).thenReturn(userUidPath);
        when(root.get("assignee")).thenReturn(assigneePath);
        when(root.get("departmentUid")).thenReturn(departmentPath);
        when(root.get("categoryUid")).thenReturn(categoryPath);
        when(root.get("type")).thenReturn(typePath);

        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(any(), any(String.class), eq('\\'))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.isNull(any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.disjunction()).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.not(any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class)))
                .thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate[].class))).thenReturn(mock(Predicate.class));

        return new CriteriaContext(root, criteriaBuilder, departmentPath, categoryPath, userUidPath, assigneePath, typePath);
    }

    private record CriteriaContext(
            Root<TicketEntity> root,
            CriteriaBuilder criteriaBuilder,
            Path<?> departmentPath,
            Path<?> categoryPath,
            Path<?> userUidPath,
            Path<?> assigneePath,
            Path<?> typePath) {
    }
}