package com.bytedesk.disk.archive;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.user.*;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiskArchiveAccessTest {
    private final AuthService auth = mock(AuthService.class);
    private final UserRepository users = mock(UserRepository.class);
    private final MemberRepository members = mock(MemberRepository.class);
    private final DiskArchiveAccess access = new DiskArchiveAccess(auth, users, members);
    private UserEntity user;
    private MemberEntity member;
    private RoleEntity role;
    private UserOrganizationRoleEntity relation;

    @BeforeEach
    void setup() {
        user = UserEntity.builder().build();
        user.setUid("user");
        var current = OrganizationEntity.builder().build();
        current.setUid("org");
        user.setCurrentOrganization(current);
        user.setEnabled(true);
        member = MemberEntity.builder().build();
        member.setOrgUid("org");
        member.setStatus("ACTIVE");
        var org = OrganizationEntity.builder().build();
        org.setUid("org");
        role = RoleEntity.builder().build();
        role.setAuthorities(new HashSet<>());
        relation = UserOrganizationRoleEntity.builder().organization(org).roles(Set.of(role)).build();
        user.setUserOrganizationRoles(Set.of(relation));
        when(auth.getCurrentUser()).thenReturn(user);
        when(users.findByUid("user")).thenReturn(Optional.of(user));
        when(members.findByUser_UidAndOrgUidAndDeletedFalse("user", "org")).thenReturn(Optional.of(member));
    }

    private void grant(String permission) {
        var authority = AuthorityEntity.builder().build();
        authority.setValue(permission);
        role.getAuthorities().add(authority);
    }

    @Test
    void permissionsAreModuleScopedAndRefreshedPerRequest() {
        grant("DISK_ARCHIVE_CONTRACT_READ");
        assertTrue(access.current().has("DISK_ARCHIVE_CONTRACT_READ"));
        assertFalse(access.current().has("DISK_ARCHIVE_INVOICE_READ"));
        assertFalse(access.current().has("DISK_FILE_READ"));
        role.getAuthorities().clear();
        assertFalse(access.current().has("DISK_ARCHIVE_CONTRACT_READ"));
        grant("DISK_ARCHIVE_INVOICE_READ");
        role.setExpiresAt(ZonedDateTime.now().minusMinutes(1));
        assertFalse(access.current().has("DISK_ARCHIVE_INVOICE_READ"));
    }

    @Test
    void expiredRelationsOtherOrganizationsAndDisabledMembersAreDenied() {
        grant("DISK_ARCHIVE_CONTRACT_READ");
        relation.setEndDate(ZonedDateTime.now().minusSeconds(1));
        assertFalse(access.current().has("DISK_ARCHIVE_CONTRACT_READ"));
        relation.setEndDate(null);
        relation.getOrganization().setUid("other-org");
        assertFalse(access.current().has("DISK_ARCHIVE_CONTRACT_READ"));
        relation.getOrganization().setUid("org");
        member.setForceLogout(true);
        assertThrows(DiskArchiveException.class, access::current);
        member.setForceLogout(false);
        user.setEnabled(false);
        assertThrows(DiskArchiveException.class, access::current);
        when(auth.getCurrentUser()).thenReturn(null);
        assertEquals(401, assertThrows(DiskArchiveException.class, access::current).getStatus());
    }

    @Test
    void writeRequiresOwnershipOrManageAndOrdinaryAdminHasNoImplicitGrant() {
        grant("DISK_ARCHIVE_CONTRACT_READ");
        var file = new DiskArchiveFileEntity();
        file.setModule("CONTRACT");
        file.setOrgUid("org");
        file.setUserUid("another");
        assertDoesNotThrow(() -> access.read(access.current(), "CONTRACT"));
        assertThrows(DiskArchiveException.class, () -> access.write(access.current(), "CONTRACT", file));
        grant("ROLE_ADMIN");
        assertThrows(DiskArchiveException.class, () -> access.write(access.current(), "CONTRACT", file));
        grant("DISK_ARCHIVE_CONTRACT_MANAGE");
        assertDoesNotThrow(() -> access.write(access.current(), "CONTRACT", file));
        file.setUserUid("user");
        role.getAuthorities().removeIf(a -> "DISK_ARCHIVE_CONTRACT_MANAGE".equals(a.getValue()));
        assertDoesNotThrow(() -> access.write(access.current(), "CONTRACT", file));
    }
}
