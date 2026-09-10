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

        assertThat(predicates).isEmpty();
        verify(context.departmentPath, times(0)).in((java.util.Collection<?>) any());
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

        when(root.get("userUid")).thenReturn(userUidPath);
        when(root.get("assignee")).thenReturn(assigneePath);
        when(root.get("departmentUid")).thenReturn(departmentPath);
        when(root.get("categoryUid")).thenReturn(categoryPath);

        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(any(), any(String.class), eq('\\'))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.isNull(any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.disjunction()).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.not(any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class), any(Predicate.class)))
                .thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(Predicate[].class))).thenReturn(mock(Predicate.class));

        return new CriteriaContext(root, criteriaBuilder, departmentPath, categoryPath);
    }

    private record CriteriaContext(
            Root<TicketEntity> root,
            CriteriaBuilder criteriaBuilder,
            Path<?> departmentPath,
            Path<?> categoryPath) {
    }
}