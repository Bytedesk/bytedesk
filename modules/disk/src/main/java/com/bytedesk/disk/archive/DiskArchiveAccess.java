package com.bytedesk.disk.archive;

import java.time.ZonedDateTime;
import java.util.Set;
import com.bytedesk.core.member.*;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiskArchiveAccess {
    private final AuthService auth;
    private final UserRepository users;
    private final MemberRepository members;

    public record Actor(String userUid, String orgUid, Set<String> permissions) {
        public boolean has(String permission) { return permissions.contains(permission); }
        public boolean manager(String module) { return has(DiskArchivePermissions.manage(module)); }
        public void require(String permission) {
            if (!has(permission)) throw new DiskArchiveException(403, "无权执行归档文件操作");
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Actor current() {
        UserEntity principal = auth.getCurrentUser();
        if (principal == null || principal.getOrgUid() == null) {
            throw new DiskArchiveException(401, "请先登录并选择组织");
        }
        return forUser(principal.getUid(), principal.getOrgUid());
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Actor forUser(String userUid, String orgUid) {
        MemberEntity member = members.findByUser_UidAndOrgUidAndDeletedFalse(userUid, orgUid)
                .filter(m -> MemberStatusEnum.ACTIVE.name().equals(m.getStatus())
                        && !Boolean.TRUE.equals(m.getForceLogout()))
                .orElseThrow(() -> new DiskArchiveException(403, "组织成员无效或已停用"));
        UserEntity user = users.findByUid(userUid)
                .filter(u -> !u.isDeleted() && u.isEnabled())
                .orElseThrow(() -> new DiskArchiveException(403, "用户无效或已停用"));
        Set<String> permissions = new java.util.HashSet<>();
        ZonedDateTime now = ZonedDateTime.now();
        for (UserOrganizationRoleEntity relation : user.getUserOrganizationRoles()) {
            if (relation.getOrganization() == null || !orgUid.equals(relation.getOrganization().getUid())
                    || relation.getOrganization().isDeleted()
                    || (relation.getStartDate() != null && relation.getStartDate().isAfter(now))
                    || (relation.getEndDate() != null && !relation.getEndDate().isAfter(now))) continue;
            relation.getRoles().stream()
                    .filter(r -> !r.isDeleted() && (r.getExpiresAt() == null || r.getExpiresAt().isAfter(now)))
                    .flatMap(r -> r.getAuthorities().stream())
                    .filter(a -> !a.isDeleted() && a.getValue() != null
                            && a.getValue().startsWith(DiskArchivePermissions.PREFIX))
                    .forEach(a -> permissions.add(a.getValue()));
        }
        return new Actor(user.getUid(), member.getOrgUid(), Set.copyOf(permissions));
    }

    public void read(Actor actor, String module) {
        actor.require(DiskArchivePermissions.read(module));
    }

    public void write(Actor actor, String module, DiskArchiveFileEntity file) {
        actor.require(DiskArchivePermissions.read(module));
        if (!actor.manager(module) && !actor.userUid().equals(file.getUserUid())) {
            throw new DiskArchiveException(403, "仅文件上传者或归档管理员可执行此操作");
        }
    }
}
