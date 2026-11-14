package com.bytedesk.core.jobtitle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

/**
 * 职务字典服务单元测试：重名冲突、软删除释放键、初始化幂等、组织隔离。
 */
@ExtendWith(MockitoExtension.class)
class JobTitleRestServiceTest {

    @Mock
    private JobTitleRepository jobTitleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private JobTitleRestService jobTitleRestService;

    @Test
    void computeActiveKeyIsDeterministicAndOrgScoped() {
        String key1 = JobTitleRestService.computeActiveKey("org-1", "经理");
        String key2 = JobTitleRestService.computeActiveKey("org-1", "经理");
        String otherOrg = JobTitleRestService.computeActiveKey("org-2", "经理");
        String trimmed = JobTitleRestService.computeActiveKey("org-1", "  经理  ");

        assertThat(key1).isEqualTo(key2);
        assertThat(key1).isNotEqualTo(otherOrg);
        assertThat(key1).isEqualTo(trimmed);
        assertThat(key1).hasSize(64);
    }

    @Test
    void createRejectsDuplicateActiveNameInSameOrg() {
        UserEntity user = userInOrg("org-1");
        when(authService.getUser()).thenReturn(user);
        when(permissionService.canCreateAtLevel(anyString(), anyString())).thenReturn(true);
        String activeKey = JobTitleRestService.computeActiveKey("org-1", "经理");
        when(jobTitleRepository.existsByActiveKey(activeKey)).thenReturn(true);

        JobTitleRequest request = JobTitleRequest.builder()
                .name("经理")
                .order(10)
                .build();

        assertThrows(JobTitleAlreadyExistsException.class,
                () -> jobTitleRestService.create(request));
        verify(jobTitleRepository, never()).save(any(JobTitleEntity.class));
    }

    @Test
    void createTranslatesUniqueConstraintViolationToConflict() {
        UserEntity user = userInOrg("org-1");
        when(authService.getUser()).thenReturn(user);
        when(permissionService.canCreateAtLevel(anyString(), anyString())).thenReturn(true);
        when(jobTitleRepository.existsByActiveKey(anyString())).thenReturn(false);
        when(uidUtils.getUid()).thenReturn("jt-uid-1");
        // save 走 BaseRestService.save -> doSave；模拟并发插入触发唯一约束
        when(jobTitleRepository.save(any(JobTitleEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        JobTitleRequest request = JobTitleRequest.builder()
                .name("经理")
                .build();

        assertThrows(JobTitleAlreadyExistsException.class,
                () -> jobTitleRestService.create(request));
    }

    @Test
    void createRejectsBlankOrTooLongName() {
        JobTitleRequest blank = JobTitleRequest.builder().name("   ").build();
        assertThrows(IllegalArgumentException.class, () -> jobTitleRestService.create(blank));

        JobTitleRequest tooLong = JobTitleRequest.builder().name("x".repeat(65)).build();
        assertThrows(IllegalArgumentException.class, () -> jobTitleRestService.create(tooLong));
    }

    @Test
    void deleteSoftDeletesAndReleasesActiveKey() {
        JobTitleEntity entity = JobTitleEntity.builder()
                .uid("jt-1")
                .name("经理")
                .orgUid("org-1")
                .activeKey("key-1")
                .build();
        when(jobTitleRepository.findByUid("jt-1")).thenReturn(Optional.of(entity));
        when(permissionService.hasEntityPermission(anyString(), anyString(), any())).thenReturn(true);
        when(jobTitleRepository.save(any(JobTitleEntity.class))).thenReturn(entity);

        jobTitleRestService.deleteByUid("jt-1");

        assertThat(entity.isDeleted()).isTrue();
        assertThat(entity.getActiveKey()).isNull();
    }

    @Test
    void initDefaultJobTitlesSkipsWhenOrgHasAnyRecord() {
        when(jobTitleRepository.countByOrgUidIncludingDeleted("org-1")).thenReturn(1L);

        jobTitleRestService.initDefaultJobTitles("org-1");

        verify(jobTitleRepository, never()).saveAndFlush(any(JobTitleEntity.class));
    }

    @Test
    void initDefaultJobTitlesCreatesDefaultsForFreshOrg() {
        when(jobTitleRepository.countByOrgUidIncludingDeleted("org-1")).thenReturn(0L);
        when(jobTitleRepository.existsByActiveKey(anyString())).thenReturn(false);
        when(uidUtils.getUid()).thenReturn("jt-uid");
        when(jobTitleRepository.saveAndFlush(any(JobTitleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        jobTitleRestService.initDefaultJobTitles("org-1");

        // 与默认清单数量一致（当前 6 项），逐项落库
        verify(jobTitleRepository, org.mockito.Mockito.times(JobTitleInitData.DEFAULT_JOB_TITLES.length))
                .saveAndFlush(any(JobTitleEntity.class));
    }

    @Test
    void initDefaultJobTitlesIgnoresBlankOrgUid() {
        jobTitleRestService.initDefaultJobTitles("");
        jobTitleRestService.initDefaultJobTitles(null);
        verify(jobTitleRepository, never()).countByOrgUidIncludingDeleted(anyString());
    }

    @Test
    void manualInitBackfillsMissingDefaultsWithoutRequiringEmptyOrg() {
        when(authService.getUser()).thenReturn(userInOrg("org-1"));
        when(uidUtils.getUid()).thenReturn("jt-uid");
        when(jobTitleRepository.saveAndFlush(any(JobTitleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // 已存在“经理”，其余默认项补建
        when(jobTitleRepository.existsByActiveKey(anyString())).thenAnswer(invocation -> {
            String activeKey = invocation.getArgument(0);
            return activeKey.equals(JobTitleRestService.computeActiveKey("org-1", "经理"));
        });

        int created = jobTitleRestService.initDefaultJobTitlesManually();

        assertThat(created).isEqualTo(JobTitleInitData.DEFAULT_JOB_TITLES.length - 1);
        verify(jobTitleRepository, org.mockito.Mockito.times(created))
                .saveAndFlush(any(JobTitleEntity.class));
    }

    @Test
    void manualInitCreatesNothingWhenAllDefaultsExist() {
        when(authService.getUser()).thenReturn(userInOrg("org-1"));
        when(jobTitleRepository.existsByActiveKey(anyString())).thenReturn(true);

        int created = jobTitleRestService.initDefaultJobTitlesManually();

        assertThat(created).isZero();
        verify(jobTitleRepository, never()).saveAndFlush(any(JobTitleEntity.class));
    }

    @Test
    void manualInitRejectsMissingUserOrOrg() {
        when(authService.getUser()).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> jobTitleRestService.initDefaultJobTitlesManually());
        verify(jobTitleRepository, never()).existsByActiveKey(anyString());
    }

    private static UserEntity userInOrg(String orgUid) {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        user.setCurrentOrganization(organization(orgUid));
        return user;
    }

    private static OrganizationEntity organization(String uid) {
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid(uid);
        return organization;
    }
}
