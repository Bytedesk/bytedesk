package com.bytedesk.kbase.llm_faq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class FaqSpecificationTest {

    @Test
    void searchShouldFilterByCategoryUidCollection() {
        FaqRequest request = FaqRequest.builder()
                .superUser(true)
                .categoryUids(List.of("parent", "child"))
                .build();

        AuthService authService = mock(AuthService.class);
        UserEntity currentUser = new UserEntity();
        currentUser.setUid("user-1");
        currentUser.setSuperUser(true);
        when(authService.getUser()).thenReturn(currentUser);

        @SuppressWarnings("unchecked")
        Root<FaqEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<FaqEntity> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        @SuppressWarnings("unchecked")
        Path<Object> deletedPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> categoryUidPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Expression<Object> categoryUidExpression = (Expression<Object>) categoryUidPath;

        Predicate deletedPredicate = mock(Predicate.class);
        Predicate categoryPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(root.get("deleted")).thenReturn(deletedPath);
        when(root.get("categoryUid")).thenReturn(categoryUidPath);
        when(criteriaBuilder.equal(deletedPath, false)).thenReturn(deletedPredicate);
        when(categoryUidExpression.in(request.getCategoryUids())).thenReturn(categoryPredicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        Specification<FaqEntity> specification = FaqSpecification.search(request, authService);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(combinedPredicate);
        verify(categoryUidExpression).in(request.getCategoryUids());
    }
}