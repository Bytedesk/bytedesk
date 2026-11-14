package com.bytedesk.core.department;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class DepartmentRestServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache memberCache;

    @Mock
    private Cache departmentCache;

    @Mock
    private AuthService authService;

    @Test
    void deleteSoftDeletesActiveDescendantsAndClearsTheirMembersAfterCommit() {
        DepartmentEntity root = department("root", "org-1", false);
        DepartmentEntity deletedIntermediate = department("intermediate", "org-1", true);
        DepartmentEntity leaf = department("leaf", "org-1", false);
        root.addChild(deletedIntermediate);
        deletedIntermediate.addChild(leaf);

        MemberEntity member = new MemberEntity();
        member.setUid("member-1");
        member.setOrgUid("org-1");
        member.setDeptUid("leaf");

        UserEntity user = new UserEntity();
        user.setCurrentOrganization(organization("org-1"));
        when(authService.getUser()).thenReturn(user);
        when(departmentRepository.findByUid("root")).thenReturn(Optional.of(root));
        when(memberRepository.findByDeptUidInAndOrgUidAndDeletedFalse(
                Set.of("root", "leaf"), "org-1")).thenReturn(List.of(member));
        when(cacheManager.getCache("member")).thenReturn(memberCache);
        when(cacheManager.getCache("department")).thenReturn(departmentCache);

        DepartmentRestService service = newTransactionalService();

        service.delete(DepartmentRequest.builder().uid("root").orgUid("org-1").build());

        assertThat(root.isDeleted()).isTrue();
        assertThat(deletedIntermediate.isDeleted()).isTrue();
        assertThat(leaf.isDeleted()).isTrue();
        assertThat(member.getDeptUid()).isNull();
        verify(departmentRepository).saveAll(any());
        verify(memberRepository).saveAll(List.of(member));
        verify(departmentRepository).flush();
        verify(memberRepository).flush();

        verify(memberCache).evict("member-1");
        verify(departmentCache).clear();
        assertThat(transactionManager.getCommitCount()).isEqualTo(1);
        assertThat(transactionManager.getRollbackCount()).isZero();
    }

    @Test
    void deleteRejectsCrossOrganizationRequestBeforeSaving() {
        DepartmentEntity root = department("root", "org-1", false);
        when(departmentRepository.findByUid("root")).thenReturn(Optional.of(root));

        DepartmentRestService service = newTransactionalService();

        assertThrows(RuntimeException.class,
                () -> service.delete(DepartmentRequest.builder().uid("root").orgUid("org-2").build()));

        verify(departmentRepository, org.mockito.Mockito.never()).saveAll(any());
        verify(memberRepository, org.mockito.Mockito.never()).saveAll(any());
        assertThat(transactionManager.getRollbackCount()).isEqualTo(1);
    }

    @Test
    void deleteRejectsCrossOrganizationDescendantWithoutSaving() {
        DepartmentEntity root = department("root", "org-1", false);
        root.addChild(department("foreign", "org-2", false));
        UserEntity user = new UserEntity();
        user.setCurrentOrganization(organization("org-1"));
        when(authService.getUser()).thenReturn(user);
        when(departmentRepository.findByUid("root")).thenReturn(Optional.of(root));

        assertThrows(IllegalStateException.class,
                () -> newTransactionalService().delete(
                        DepartmentRequest.builder().uid("root").orgUid("org-1").build()));

        verify(departmentRepository, org.mockito.Mockito.never()).saveAll(any());
        verify(memberRepository, org.mockito.Mockito.never()).findByDeptUidInAndOrgUidAndDeletedFalse(
                any(), eq("org-1"));
    }

    @Test
    void cacheFailureAfterCommitIsReportedAsCommitted() {
        DepartmentEntity root = department("root", "org-1", false);
        MemberEntity member = new MemberEntity();
        member.setUid("member-1");
        member.setOrgUid("org-1");
        member.setDeptUid("root");

        UserEntity user = new UserEntity();
        user.setCurrentOrganization(organization("org-1"));
        when(authService.getUser()).thenReturn(user);
        when(departmentRepository.findByUid("root")).thenReturn(Optional.of(root));
        when(memberRepository.findByDeptUidInAndOrgUidAndDeletedFalse(
                Set.of("root"), "org-1")).thenReturn(List.of(member));
        when(cacheManager.getCache("member")).thenReturn(memberCache);
        when(cacheManager.getCache("department")).thenReturn(departmentCache);
        doThrow(new IllegalStateException("cache unavailable"))
                .when(memberCache).evict("member-1");

        DepartmentDeletionCommittedException exception = assertThrows(
                DepartmentDeletionCommittedException.class,
                () -> newTransactionalService().delete(
                        DepartmentRequest.builder().uid("root").orgUid("org-1").build()));

        assertThat(exception.getMessage()).contains("deleted");
        assertThat(transactionManager.getCommitCount()).isEqualTo(1);
        assertThat(transactionManager.getRollbackCount()).isZero();
        verify(departmentCache).clear();
    }

    @Test
    void persistenceFailureRollsBackWithoutEvictingCaches() {
        DepartmentEntity root = department("root", "org-1", false);
        UserEntity user = new UserEntity();
        user.setCurrentOrganization(organization("org-1"));
        when(authService.getUser()).thenReturn(user);
        when(departmentRepository.findByUid("root")).thenReturn(Optional.of(root));
        when(memberRepository.findByDeptUidInAndOrgUidAndDeletedFalse(
                Set.of("root"), "org-1")).thenReturn(List.of());
        doThrow(new IllegalStateException("flush failed")).when(memberRepository).flush();

        assertThrows(IllegalStateException.class,
                () -> newTransactionalService().delete(
                        DepartmentRequest.builder().uid("root").orgUid("org-1").build()));

        assertThat(transactionManager.getCommitCount()).isZero();
        assertThat(transactionManager.getRollbackCount()).isEqualTo(1);
        verify(cacheManager, org.mockito.Mockito.never()).getCache(anyString());
    }

    private TestTransactionManager transactionManager;

    private DepartmentRestService newTransactionalService() {
        DepartmentRestService service = new DepartmentRestService(
                modelMapper, departmentRepository, uidUtils, memberRepository, cacheManager);
        ReflectionTestUtils.setField(service, "authService", authService);
        transactionManager = new TestTransactionManager();
        ProxyFactory proxyFactory = new ProxyFactory(service);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TransactionInterceptor(
                (TransactionManager) transactionManager, new AnnotationTransactionAttributeSource()));
        return (DepartmentRestService) proxyFactory.getProxy();
    }

    private DepartmentEntity department(String uid, String orgUid, boolean deleted) {
        DepartmentEntity department = new DepartmentEntity();
        department.setUid(uid);
        department.setOrgUid(orgUid);
        department.setDeleted(deleted);
        return department;
    }

    private OrganizationEntity organization(String uid) {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid(uid);
        return organization;
    }

    private static class TestTransactionManager extends AbstractPlatformTransactionManager {

        private int commitCount;
        private int rollbackCount;

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
            commitCount++;
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
            rollbackCount++;
        }

        int getCommitCount() {
            return commitCount;
        }

        int getRollbackCount() {
            return rollbackCount;
        }
    }
}
