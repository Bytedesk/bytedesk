package com.bytedesk.disk.permission;

import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.disk.audit.DiskLogService;
import com.bytedesk.disk.constant.DiskConsts;
import com.bytedesk.disk.file.DiskFileEntity;
import com.bytedesk.disk.file.DiskFileRepository;
import com.bytedesk.disk.space.DiskSpaceEntity;
import com.bytedesk.disk.space.DiskSpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * 共享空间有效权限计算单元测试（基线 VIEW + 显式授权并集）
 */
@ExtendWith(MockitoExtension.class)
class DiskPermissionEffectivePermsTest {

    @Mock
    private DiskPermissionRepository diskPermissionRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DiskFileRepository diskFileRepository;

    @Mock
    private DiskSpaceRepository diskSpaceRepository;

    @Mock
    private DiskLogService diskLogService;

    @Mock
    private MemberRepository memberRepository;

    private DiskPermissionRestService service;

    private static final String ORG = "org-1";
    private static final String OTHER_ORG = "org-2";
    private static final String SHARED_SPACE = "space-shared";
    private static final String PERSONAL_SPACE = "space-personal";
    private static final String SHARED_ROOT = "folder-shared-root";
    private static final String USER = "user-1";
    private static final String OWNER = "owner-1";

    @BeforeEach
    void setUp() {
        service = new DiskPermissionRestService(diskPermissionRepository, authService, uidUtils, modelMapper,
                diskFileRepository, diskSpaceRepository, diskLogService, memberRepository);
        lenient().when(diskSpaceRepository.findByUid(SHARED_SPACE))
                .thenReturn(Optional.of(space(DiskConsts.SPACE_TYPE_SHARED, ORG, OWNER)));
        lenient().when(diskSpaceRepository.findByUid(PERSONAL_SPACE))
                .thenReturn(Optional.of(space(DiskConsts.SPACE_TYPE_PERSONAL, ORG, OWNER)));
        lenient().when(diskPermissionRepository.findBySpaceUidAndFolderUidInAndDeletedFalse(anyString(), anyList()))
                .thenReturn(Collections.emptyList());
    }

    private DiskSpaceEntity space(String type, String orgUid, String userUid) {
        DiskSpaceEntity space = DiskSpaceEntity.builder().build();
        space.setUid(DiskConsts.SPACE_TYPE_SHARED.equals(type) ? SHARED_SPACE : PERSONAL_SPACE);
        space.setOrgUid(orgUid);
        space.setUserUid(userUid);
        space.setName(type);
        space.setType(type);
        return space;
    }

    private DiskFileEntity folder(String uid, String spaceUid) {
        DiskFileEntity folder = DiskFileEntity.builder().build();
        folder.setUid(uid);
        folder.setOrgUid(ORG);
        folder.setSpaceUid(spaceUid);
        folder.setParentUid(null);
        folder.setName("folder");
        folder.setType(DiskConsts.NODE_TYPE_FOLDER);
        return folder;
    }

    @Test
    void sharedSpace_sameOrgMemberGetsBaselineViewOnly() {
        Set<DiskPermEnum> perms = service.effectivePerms(folder(SHARED_ROOT, SHARED_SPACE), USER, ORG, List.of());
        assertThat(perms).containsExactly(DiskPermEnum.VIEW);
    }

    @Test
    void sharedSpace_crossOrgMemberGetsNothing() {
        Set<DiskPermEnum> perms = service.effectivePerms(folder(SHARED_ROOT, SHARED_SPACE), USER, OTHER_ORG, List.of());
        assertThat(perms).isEmpty();
    }

    @Test
    void sharedSpace_explicitGrantAddsPermsOnTopOfBaseline() {
        DiskPermissionEntity grant = DiskPermissionEntity.builder().build();
        grant.setSpaceUid(SHARED_SPACE);
        grant.setFolderUid(SHARED_ROOT);
        grant.setTargetType("MEMBER");
        grant.setTargetUid(USER);
        grant.setPerms("UPLOAD,DOWNLOAD");
        when(diskPermissionRepository.findBySpaceUidAndFolderUidInAndDeletedFalse(anyString(), anyList()))
                .thenReturn(List.of(grant));

        Set<DiskPermEnum> perms = service.effectivePerms(folder(SHARED_ROOT, SHARED_SPACE), USER, ORG, List.of());
        assertThat(perms).containsOnly(DiskPermEnum.VIEW, DiskPermEnum.UPLOAD, DiskPermEnum.DOWNLOAD);
    }

    @Test
    void sharedSpace_deptGrantMatchesByDeptUid() {
        DiskPermissionEntity grant = DiskPermissionEntity.builder().build();
        grant.setSpaceUid(SHARED_SPACE);
        grant.setFolderUid(SHARED_ROOT);
        grant.setTargetType("DEPT");
        grant.setTargetUid("dept-1");
        grant.setPerms("EDIT");
        when(diskPermissionRepository.findBySpaceUidAndFolderUidInAndDeletedFalse(anyString(), anyList()))
                .thenReturn(List.of(grant));

        Set<DiskPermEnum> perms = service.effectivePerms(folder(SHARED_ROOT, SHARED_SPACE), USER, ORG,
                List.of("dept-1"));
        assertThat(perms).containsOnly(DiskPermEnum.VIEW, DiskPermEnum.EDIT);
    }

    @Test
    void personalSpace_ownerGetsAllPerms() {
        Set<DiskPermEnum> perms = service.effectivePerms(folder("folder-p", PERSONAL_SPACE), OWNER, ORG, List.of());
        assertThat(perms).containsExactlyInAnyOrder(DiskPermEnum.values());
    }

    @Test
    void personalSpace_nonOwnerMemberGetsNothing() {
        Set<DiskPermEnum> perms = service.effectivePerms(folder("folder-p", PERSONAL_SPACE), USER, ORG, List.of());
        assertThat(perms).isEmpty();
    }
}
