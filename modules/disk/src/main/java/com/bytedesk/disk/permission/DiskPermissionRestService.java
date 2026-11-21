package com.bytedesk.disk.permission;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.disk.audit.DiskLogService;
import com.bytedesk.disk.constant.DiskConsts;
import com.bytedesk.disk.file.DiskFileEntity;
import com.bytedesk.disk.file.DiskFileRepository;
import com.bytedesk.disk.space.DiskSpaceEntity;
import com.bytedesk.disk.space.DiskSpaceRepository;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 网盘目录权限服务：授权 CRUD + 有效权限计算（成员直授 ∪ 部门授权 ∪ 父目录继承）
 */
@Slf4j
@Service
public class DiskPermissionRestService extends BaseRestService<DiskPermissionEntity, DiskPermissionRequest, DiskPermissionResponse> {

    private final DiskPermissionRepository diskPermissionRepository;

    private final AuthService authService;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final DiskFileRepository diskFileRepository;

    private final DiskSpaceRepository diskSpaceRepository;

    private final DiskLogService diskLogService;

    private final MemberRepository memberRepository;

    public DiskPermissionRestService(
            DiskPermissionRepository diskPermissionRepository,
            AuthService authService,
            UidUtils uidUtils,
            ModelMapper modelMapper,
            DiskFileRepository diskFileRepository,
            DiskSpaceRepository diskSpaceRepository,
            DiskLogService diskLogService,
            MemberRepository memberRepository) {
        this.diskPermissionRepository = diskPermissionRepository;
        this.authService = authService;
        this.uidUtils = uidUtils;
        this.modelMapper = modelMapper;
        this.diskFileRepository = diskFileRepository;
        this.diskSpaceRepository = diskSpaceRepository;
        this.diskLogService = diskLogService;
        this.memberRepository = memberRepository;
    }

    @Override
    protected Specification<DiskPermissionEntity> createSpecification(DiskPermissionRequest request) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(cb.equal(root.get("orgUid"), request.getOrgUid()));
            }
            if (StringUtils.hasText(request.getFolderUid())) {
                predicates.add(cb.equal(root.get("folderUid"), request.getFolderUid()));
            }
            predicates.add(cb.isFalse(root.get("deleted")));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    @Override
    protected Page<DiskPermissionEntity> executePageQuery(Specification<DiskPermissionEntity> spec, Pageable pageable) {
        return diskPermissionRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<DiskPermissionEntity> findByUid(String uid) {
        return diskPermissionRepository.findByUid(uid);
    }

    @Override
    protected DiskPermissionEntity doSave(DiskPermissionEntity entity) {
        return diskPermissionRepository.save(entity);
    }

    @Override
    public DiskPermissionEntity handleOptimisticLockingFailureException(
            org.springframework.orm.ObjectOptimisticLockingFailureException e, DiskPermissionEntity entity) {
        log.warn("disk permission optimistic locking failure: uid={}", entity.getUid());
        return diskPermissionRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        diskPermissionRepository.findByUid(uid).ifPresent(entity -> {
            entity.setDeleted(true);
            diskPermissionRepository.save(entity);
        });
    }

    @Override
    public DiskPermissionResponse create(DiskPermissionRequest request) {
        UserEntity user = authService.getCurrentUser();
        String orgUid = user != null ? user.getOrgUid() : request.getOrgUid();

        // 同目录同主体去重：存在则合并更新
        Optional<DiskPermissionEntity> existing = diskPermissionRepository
                .findByFolderUidAndTargetTypeAndTargetUidAndDeletedFalse(
                        request.getFolderUid(), request.getTargetType(), request.getTargetUid());
        if (existing.isPresent()) {
            DiskPermissionEntity entity = existing.get();
            entity.setPerms(normalizePerms(request.getPerms()));
            DiskPermissionEntity saved = diskPermissionRepository.save(entity);
            diskLogService.logPermissionChange(orgUid, request.getSpaceUid(), request.getFolderUid(), "PERM_UPDATE");
            return convertToResponse(saved);
        }

        DiskPermissionEntity entity = DiskPermissionEntity.builder().build();
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(orgUid);
        entity.setUserUid(user != null ? user.getUid() : null);
        entity.setSpaceUid(request.getSpaceUid());
        entity.setFolderUid(request.getFolderUid());
        entity.setTargetType(request.getTargetType());
        entity.setTargetUid(request.getTargetUid());
        entity.setTargetName(request.getTargetName());
        entity.setPerms(normalizePerms(request.getPerms()));
        DiskPermissionEntity saved = diskPermissionRepository.save(entity);
        diskLogService.logPermissionChange(orgUid, request.getSpaceUid(), request.getFolderUid(), "PERM_CREATE");
        return convertToResponse(saved);
    }

    @Override
    public DiskPermissionResponse update(DiskPermissionRequest request) {
        DiskPermissionEntity entity = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("permission not found"));
        entity.setPerms(normalizePerms(request.getPerms()));
        if (StringUtils.hasText(request.getTargetName())) {
            entity.setTargetName(request.getTargetName());
        }
        DiskPermissionEntity saved = diskPermissionRepository.save(entity);
        diskLogService.logPermissionChange(entity.getOrgUid(), entity.getSpaceUid(), entity.getFolderUid(), "PERM_UPDATE");
        return convertToResponse(saved);
    }

    @Override
    public void delete(DiskPermissionRequest request) {
        DiskPermissionEntity entity = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("permission not found"));
        entity.setDeleted(true);
        diskPermissionRepository.save(entity);
        diskLogService.logPermissionChange(entity.getOrgUid(), entity.getSpaceUid(), entity.getFolderUid(), "PERM_DELETE");
    }

    /**
     * 查询目录授权列表（目录管理员）
     */
    public Page<DiskPermissionResponse> queryByFolder(DiskPermissionRequest request) {
        DiskPermissionRequest queryRequest = DiskPermissionRequest.builder()
                .orgUid(resolveOrgUid())
                .folderUid(request.getFolderUid())
                .build();
        Specification<DiskPermissionEntity> spec = createSpecification(queryRequest);
        Pageable pageable = request.getPageable();
        return diskPermissionRepository.findAll(spec, pageable).map(this::convertToResponse);
    }

    /**
     * 计算用户在指定目录的有效权限集合：
     * 个人空间 → 仅所有者全权限；
     * 共享空间 → 同组织成员基线 VIEW（组织内可见）∪ 成员直授 ∪ 部门授权 ∪ 沿途父目录继承并集，
     * 写操作（下载/上传/编辑/管理）仍需显式授权
     */
    public Set<DiskPermEnum> effectivePerms(DiskFileEntity folder, String userUid, String orgUid,
            List<String> deptUids) {
        Set<DiskPermEnum> result = new HashSet<>();
        Optional<DiskSpaceEntity> spaceOpt = diskSpaceRepository.findByUid(folder.getSpaceUid());
        if (spaceOpt.isEmpty()) {
            return result;
        }
        DiskSpaceEntity space = spaceOpt.get();
        // 个人空间：仅所有者全权限
        if (DiskConsts.SPACE_TYPE_PERSONAL.equals(space.getType())) {
            if (userUid != null && userUid.equals(space.getUserUid())) {
                Collections.addAll(result, DiskPermEnum.values());
            }
            return result;
        }
        // 共享空间基线：同组织成员默认可见（仅 VIEW），跨组织成员不获得任何基线权限
        if (StringUtils.hasText(orgUid) && orgUid.equals(space.getOrgUid())) {
            result.add(DiskPermEnum.VIEW);
        }
        // 共享空间：沿目录链自根向目标目录收集（继承模型：任一祖先授权均生效）
        List<String> chain = new ArrayList<>();
        DiskFileEntity current = folder;
        int depth = 0;
        while (current != null && depth < 64) {
            chain.add(current.getUid());
            String parentUid = current.getParentUid();
            if (parentUid == null || parentUid.isEmpty()) {
                break;
            }
            Optional<DiskFileEntity> parent = diskFileRepository.findByUid(parentUid);
            current = parent.orElse(null);
            depth++;
        }
        if (chain.isEmpty()) {
            return result;
        }
        List<DiskPermissionEntity> grants = diskPermissionRepository
                .findBySpaceUidAndFolderUidInAndDeletedFalse(folder.getSpaceUid(), chain);
        for (DiskPermissionEntity grant : grants) {
            boolean hit = false;
            if ("MEMBER".equals(grant.getTargetType()) && grant.getTargetUid().equals(userUid)) {
                hit = true;
            } else if ("DEPT".equals(grant.getTargetType()) && deptUids != null
                    && deptUids.contains(grant.getTargetUid())) {
                hit = true;
            }
            if (hit) {
                for (String p : grant.getPerms().split(DiskConsts.PERM_SEPARATOR)) {
                    try {
                        result.add(DiskPermEnum.valueOf(p.trim()));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }
        return result;
    }

    /**
     * 当前登录用户对目录是否拥有指定权限
     */
    public boolean hasPerm(DiskFileEntity folder, DiskPermEnum perm) {
        UserEntity user = authService.getCurrentUser();
        if (user == null) {
            return false;
        }
        List<String> deptUids = resolveDeptUids(user);
        return effectivePerms(folder, user.getUid(), user.getOrgUid(), deptUids).contains(perm);
    }

    private List<String> resolveDeptUids(UserEntity user) {
        try {
            // 一个用户在同组织下通常对应一个 member，member.deptUid 即其部门；
            // 未来支持多部门时此处扩展为列表
            return memberRepository.findByUser_UidAndDeletedFalse(user.getUid()).stream()
                    .map(member -> member.getDeptUid())
                    .filter(StringUtils::hasText)
                    .distinct()
                    .toList();
        } catch (Exception e) {
            log.warn("resolve dept uids failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String normalizePerms(String perms) {
        if (!StringUtils.hasText(perms)) {
            return DiskPermEnum.VIEW.name();
        }
        Set<String> normalized = new HashSet<>();
        for (String p : perms.split(DiskConsts.PERM_SEPARATOR)) {
            String trimmed = p.trim().toUpperCase();
            try {
                normalized.add(DiskPermEnum.valueOf(trimmed).name());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return normalized.isEmpty() ? DiskPermEnum.VIEW.name() : String.join(DiskConsts.PERM_SEPARATOR, normalized);
    }

    private String resolveOrgUid() {
        UserEntity user = authService.getCurrentUser();
        return user != null ? user.getOrgUid() : null;
    }

    /**
     * 内部创建授权（初始化共享空间根管理员时使用，不记录留痕避免循环依赖）
     */
    public com.bytedesk.disk.permission.DiskPermissionResponse createInternal(
            String spaceUid, String folderUid, String targetType, String targetUid, String targetName, String perms) {
        Optional<DiskPermissionEntity> existing = diskPermissionRepository
                .findByFolderUidAndTargetTypeAndTargetUidAndDeletedFalse(folderUid, targetType, targetUid);
        if (existing.isPresent()) {
            DiskPermissionEntity entity = existing.get();
            entity.setPerms(normalizePerms(perms));
            return convertToResponse(diskPermissionRepository.save(entity));
        }
        DiskPermissionEntity entity = DiskPermissionEntity.builder().build();
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(resolveOrgUid());
        entity.setSpaceUid(spaceUid);
        entity.setFolderUid(folderUid);
        entity.setTargetType(targetType);
        entity.setTargetUid(targetUid);
        entity.setTargetName(targetName);
        entity.setPerms(normalizePerms(perms));
        return convertToResponse(diskPermissionRepository.save(entity));
    }

    @Override
    public DiskPermissionResponse convertToResponse(DiskPermissionEntity entity) {
        return modelMapper.map(entity, DiskPermissionResponse.class);
    }
}
