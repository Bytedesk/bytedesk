package com.bytedesk.core.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * 平台 org 成员跨组织只读查询放行（P0-9）验证：
 * - 平台成员显式传任意 orgUid → 放行且按该 orgUid 精确过滤；
 * - 平台成员未传 orgUid → 自动补 df_org_uid（默认行为不变）；
 * - 租户成员传他人 orgUid → 仍然拒绝；
 * - 超管行为不变（不校验 org 一致性）。
 */
class ThreadSpecificationPlatformMemberTest {

    private static final String PLATFORM_ORG = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
    private static final String TENANT_ORG = "tenant_org_uid";

    @SuppressWarnings("unchecked")
    private Root<ThreadEntity> mockRoot() {
        return mock(Root.class);
    }

    @SuppressWarnings("unchecked")
    private CriteriaQuery<ThreadEntity> mockQuery() {
        return mock(CriteriaQuery.class);
    }

    private AuthService mockAuth(UserEntity user) {
        AuthService authService = mock(AuthService.class);
        when(authService.getUser()).thenReturn(user);
        return authService;
    }

    private UserEntity userOf(String orgUid, boolean superUser) {
        UserEntity user = new UserEntity();
        user.setUid("user-" + orgUid);
        com.bytedesk.core.rbac.organization.OrganizationEntity org =
                new com.bytedesk.core.rbac.organization.OrganizationEntity();
        org.setUid(orgUid);
        user.setCurrentOrganization(org);
        user.setSuperUser(superUser);
        return user;
    }

    @Test
    @SuppressWarnings("unchecked")
    void platformMemberWithExplicitOrgUidShouldBeAllowedAndExactFiltered() {
        ThreadRequest request = ThreadRequest.builder()
                .orgUid(TENANT_ORG)
                .build();

        AuthService authService = mockAuth(userOf(PLATFORM_ORG, false));

        Root<ThreadEntity> root = mockRoot();
        CriteriaQuery<ThreadEntity> query = mockQuery();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<Object> deletedPath = mock(Path.class);
        Path<Object> orgUidPath = mock(Path.class);
        Path<Object> updatedAtPath = mock(Path.class);
        when(root.get("deleted")).thenReturn(deletedPath);
        when(root.get("orgUid")).thenReturn(orgUidPath);
        when(root.get("updatedAt")).thenReturn(updatedAtPath);

        Predicate deletedPredicate = mock(Predicate.class);
        Predicate orgPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);
        Order order = mock(Order.class);

        when(criteriaBuilder.equal(deletedPath, false)).thenReturn(deletedPredicate);
        when(criteriaBuilder.equal(orgUidPath, TENANT_ORG)).thenReturn(orgPredicate);
        when(criteriaBuilder.desc(updatedAtPath)).thenReturn(order);
        when(query.orderBy(order)).thenReturn(query);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        Specification<ThreadEntity> specification = ThreadSpecification.search(request, authService);
        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(combinedPredicate);
        // 关键断言：按显式传入的 orgUid 精确过滤（不做关键字模糊匹配）
        verify(criteriaBuilder).equal(orgUidPath, TENANT_ORG);
    }

    @Test
    @SuppressWarnings("unchecked")
    void platformMemberWithoutOrgUidShouldAutoFillPlatformOrg() {
        ThreadRequest request = ThreadRequest.builder().build();

        AuthService authService = mockAuth(userOf(PLATFORM_ORG, false));

        Root<ThreadEntity> root = mockRoot();
        CriteriaQuery<ThreadEntity> query = mockQuery();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<Object> deletedPath = mock(Path.class);
        Path<Object> orgUidPath = mock(Path.class);
        Path<Object> updatedAtPath = mock(Path.class);
        when(root.get("deleted")).thenReturn(deletedPath);
        when(root.get("orgUid")).thenReturn(orgUidPath);
        when(root.get("updatedAt")).thenReturn(updatedAtPath);

        Predicate deletedPredicate = mock(Predicate.class);
        Predicate orgPredicate = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);
        Order order = mock(Order.class);

        when(criteriaBuilder.equal(deletedPath, false)).thenReturn(deletedPredicate);
        when(criteriaBuilder.equal(orgUidPath, PLATFORM_ORG)).thenReturn(orgPredicate);
        when(criteriaBuilder.desc(updatedAtPath)).thenReturn(order);
        when(query.orderBy(order)).thenReturn(query);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        Specification<ThreadEntity> specification = ThreadSpecification.search(request, authService);
        specification.toPredicate(root, query, criteriaBuilder);

        // 未传 orgUid：自动补平台 org，默认行为不变
        assertThat(request.getOrgUid()).isEqualTo(PLATFORM_ORG);
        verify(criteriaBuilder).equal(orgUidPath, PLATFORM_ORG);
    }

    @Test
    void tenantMemberWithOtherOrgUidShouldBeDenied() {
        ThreadRequest request = ThreadRequest.builder()
                .orgUid(PLATFORM_ORG)
                .build();

        AuthService authService = mockAuth(userOf(TENANT_ORG, false));

        Root<ThreadEntity> root = mockRoot();
        CriteriaQuery<ThreadEntity> query = mockQuery();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Specification<ThreadEntity> specification = ThreadSpecification.search(request, authService);

        assertThatThrownBy(() -> specification.toPredicate(root, query, criteriaBuilder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tenantMemberForgedSuperUserFlagWithOtherOrgUidShouldBeDenied() {
        // 租户成员伪造 superUser=true：validateSuperUserPermission 会纠正为 false，随后 org 不一致仍拒绝
        ThreadRequest request = ThreadRequest.builder()
                .orgUid(PLATFORM_ORG)
                .superUser(true)
                .build();

        AuthService authService = mockAuth(userOf(TENANT_ORG, false));

        Root<ThreadEntity> root = mockRoot();
        CriteriaQuery<ThreadEntity> query = mockQuery();
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Specification<ThreadEntity> specification = ThreadSpecification.search(request, authService);

        assertThatThrownBy(() -> specification.toPredicate(root, query, criteriaBuilder))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
