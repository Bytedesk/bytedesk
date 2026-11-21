package com.bytedesk.core.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

/**
 * 公开群搜索 spec 回归（规划 §4.2/D9）：
 * publicOnly + userUid 同时存在时不得叠加成员 join 过滤，
 * 否则 "userUid=本人" 与 "NOT EXISTS 本人成员" 互斥导致公开列表恒为空。
 */
class GroupSpecificationPublicOnlyTest {

    @SuppressWarnings("unchecked")
    private Root<GroupEntity> mockRoot() {
        return mock(Root.class);
    }

    @SuppressWarnings("unchecked")
    private CriteriaQuery<GroupEntity> mockQuery() {
        return mock(CriteriaQuery.class);
    }

    private AuthService mockAuth(String userUid, String orgUid) {
        AuthService authService = mock(AuthService.class);
        UserEntity user = new UserEntity();
        user.setUid(userUid);
        com.bytedesk.core.rbac.organization.OrganizationEntity org =
                new com.bytedesk.core.rbac.organization.OrganizationEntity();
        org.setUid(orgUid);
        user.setCurrentOrganization(org);
        when(authService.getUser()).thenReturn(user);
        return authService;
    }

    @Test
    @SuppressWarnings("unchecked")
    void publicOnlyShouldNotFilterByUserMembership() {
        GroupRequest request = GroupRequest.builder()
                .orgUid("org-1")
                .userUid("user-1")
                .publicOnly(true)
                .build();

        AuthService authService = mockAuth("user-1", "org-1");

        Root<GroupEntity> root = mockRoot();
        CriteriaQuery<GroupEntity> query = mockQuery();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<Object> deletedPath = mock(Path.class);
        Path<Object> orgUidPath = mock(Path.class);
        Path<Object> visibilityPath = mock(Path.class);
        Path<Object> statusPath = mock(Path.class);
        when(root.get("deleted")).thenReturn(deletedPath);
        when(root.get("orgUid")).thenReturn(orgUidPath);
        when(root.get("visibility")).thenReturn(visibilityPath);
        when(root.get("status")).thenReturn(statusPath);

        Predicate deletedPredicate = mock(Predicate.class);
        Predicate orgPredicate = mock(Predicate.class);
        Predicate visibilityPredicate = mock(Predicate.class);
        Predicate statusPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        when(criteriaBuilder.equal(deletedPath, false)).thenReturn(deletedPredicate);
        when(criteriaBuilder.equal(orgUidPath, "org-1")).thenReturn(orgPredicate);
        when(criteriaBuilder.equal(visibilityPath, "PUBLIC")).thenReturn(visibilityPredicate);
        when(criteriaBuilder.equal(statusPath, "NORMAL")).thenReturn(statusPredicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        Specification<GroupEntity> specification = GroupSpecification.search(request, authService);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(combinedPredicate);
        // 公开群过滤生效
        verify(criteriaBuilder).equal(visibilityPath, "PUBLIC");
        verify(criteriaBuilder).equal(statusPath, "NORMAL");
        // 关键回归断言：不得按 userUid 做成员 join，也不得用子查询排除已加入群
        verify(root, never()).join(any(String.class));
        verify(query, never()).subquery(any(Class.class));
    }
}
