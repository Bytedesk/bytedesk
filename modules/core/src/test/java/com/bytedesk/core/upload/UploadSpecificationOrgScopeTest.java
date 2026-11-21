package com.bytedesk.core.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class UploadSpecificationOrgScopeTest {

    @Test
    void nonSuperUserWithoutOrgUidIsScopedToOwnOrg() {
        AuthService authService = mockAuthService("org-1", false);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Root<UploadEntity> root = mockRoot();
        stubDeletedPredicate(root, criteriaBuilder);
        Path<Object> orgUidPath = path();
        when(root.get("orgUid")).thenReturn(orgUidPath);
        Predicate combined = mock(Predicate.class);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(combined);

        UploadRequest request = UploadRequest.builder().type("CHAT").build();
        Specification<UploadEntity> specification = UploadSpecification.search(request, authService);
        Predicate predicate = specification.toPredicate(root, mockQuery(), criteriaBuilder);

        assertThat(predicate).isSameAs(combined);
        verify(criteriaBuilder).equal(orgUidPath, "org-1");
    }

    @Test
    void nonSuperUserCannotQueryAnotherOrg() {
        AuthService authService = mockAuthService("org-1", false);
        UploadRequest request = UploadRequest.builder().orgUid("org-2").build();

        Specification<UploadEntity> specification = UploadSpecification.search(request, authService);

        assertThatThrownBy(() -> specification.toPredicate(mockRoot(), mockQuery(), mock(CriteriaBuilder.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(I18Consts.I18N_ORGANIZATION_ACCESS_DENIED);
    }

    @Test
    void platformOrgMemberCanQuerySpecifiedOrg() {
        AuthService authService = mockAuthService(BytedeskConsts.DEFAULT_ORGANIZATION_UID, false);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Root<UploadEntity> root = mockRoot();
        stubDeletedPredicate(root, criteriaBuilder);
        Path<Object> orgUidPath = path();
        when(root.get("orgUid")).thenReturn(orgUidPath);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));

        UploadRequest request = UploadRequest.builder().orgUid("org-2").build();
        Specification<UploadEntity> specification = UploadSpecification.search(request, authService);
        specification.toPredicate(root, mockQuery(), criteriaBuilder);

        verify(criteriaBuilder).equal(orgUidPath, "org-2");
    }

    @Test
    void superUserWithoutOrgUidIsNotOrgFiltered() {
        UserEntity superUser = new UserEntity();
        superUser.setUid("admin-1");
        superUser.setSuperUser(true);
        AuthService authService = mock(AuthService.class);
        when(authService.getUser()).thenReturn(superUser);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Root<UploadEntity> root = mockRoot();
        stubDeletedPredicate(root, criteriaBuilder);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));

        UploadRequest request = UploadRequest.builder().superUser(true).build();
        Specification<UploadEntity> specification = UploadSpecification.search(request, authService);
        specification.toPredicate(root, mockQuery(), criteriaBuilder);

        verify(root, never()).get("orgUid");
    }

    private static AuthService mockAuthService(String orgUid, boolean superUser) {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        user.setCurrentOrganization(organization(orgUid));
        user.setSuperUser(superUser);
        AuthService authService = mock(AuthService.class);
        when(authService.getUser()).thenReturn(user);
        return authService;
    }

    private static OrganizationEntity organization(String uid) {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid(uid);
        return organization;
    }

    private static void stubDeletedPredicate(Root<UploadEntity> root, CriteriaBuilder criteriaBuilder) {
        Path<Object> deletedPath = path();
        when(root.get("deleted")).thenReturn(deletedPath);
        when(criteriaBuilder.equal(deletedPath, false)).thenReturn(mock(Predicate.class));
    }

    private static Root<UploadEntity> mockRoot() {
        @SuppressWarnings("unchecked")
        Root<UploadEntity> root = mock(Root.class);
        return root;
    }

    private static CriteriaQuery<UploadEntity> mockQuery() {
        @SuppressWarnings("unchecked")
        CriteriaQuery<UploadEntity> query = mock(CriteriaQuery.class);
        return query;
    }

    private static Path<Object> path() {
        @SuppressWarnings("unchecked")
        Path<Object> path = mock(Path.class);
        return path;
    }
}
