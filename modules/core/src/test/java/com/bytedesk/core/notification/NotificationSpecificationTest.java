package com.bytedesk.core.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class NotificationSpecificationTest {

    @Test
    void searchShouldFilterByUserUidForUserScopedQueries() {
        NotificationRequest request = NotificationRequest.builder()
                .userUid("user-1")
                .superUser(true)
                .build();

        AuthService authService = mock(AuthService.class);
        UserEntity currentUser = new UserEntity();
        currentUser.setUid("user-1");
        currentUser.setSuperUser(true);
        when(authService.getUser()).thenReturn(currentUser);

        @SuppressWarnings("unchecked")
        Root<NotificationEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<NotificationEntity> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        @SuppressWarnings("unchecked")
        Path<Object> deletedPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<Object> userUidPath = mock(Path.class);

        Predicate deletedPredicate = mock(Predicate.class);
        Predicate userUidPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(root.get("deleted")).thenReturn(deletedPath);
        when(root.get("userUid")).thenReturn(userUidPath);

        when(criteriaBuilder.equal(deletedPath, false)).thenReturn(deletedPredicate);
        when(criteriaBuilder.equal(userUidPath, "user-1")).thenReturn(userUidPredicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        Specification<NotificationEntity> specification = NotificationSpecification.search(request, authService);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(combinedPredicate);
        verify(criteriaBuilder).equal(userUidPath, "user-1");
    }
}