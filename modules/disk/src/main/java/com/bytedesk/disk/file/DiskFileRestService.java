package com.bytedesk.disk.file;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.disk.constant.DiskI18nConsts;
import com.bytedesk.disk.space.DiskSpaceRepository;
import com.bytedesk.disk.space.DiskSpaceEntity;
import com.bytedesk.disk.permission.DiskPermEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.disk.audit.DiskLogService;
import com.bytedesk.disk.constant.DiskConsts;
import com.bytedesk.disk.permission.DiskPermissionRestService;
import com.bytedesk.disk.space.DiskSpaceResponse;

import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 网盘文件/文件夹核心服务
 */
@Slf4j
@Service
public class DiskFileRestService extends BaseRestService<DiskFileEntity, DiskFileRequest, DiskFileResponse> {

    private final DiskFileRepository diskFileRepository;

    private final DiskSpaceRepository diskSpaceRepository;

    private final AuthService authService;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final UploadRestService uploadRestService;

    private final DiskPermissionRestService diskPermissionRestService;

    private final DiskLogService diskLogService;

    public DiskFileRestService(
            DiskFileRepository diskFileRepository,
            DiskSpaceRepository diskSpaceRepository,
            AuthService authService,
            UidUtils uidUtils,
            ModelMapper modelMapper,
            UploadRestService uploadRestService,
            DiskPermissionRestService diskPermissionRestService,
            DiskLogService diskLogService) {
        this.diskFileRepository = diskFileRepository;
        this.diskSpaceRepository = diskSpaceRepository;
        this.authService = authService;
        this.uidUtils = uidUtils;
        this.modelMapper = modelMapper;
        this.uploadRestService = uploadRestService;
        this.diskPermissionRestService = diskPermissionRestService;
        this.diskLogService = diskLogService;
    }

    @Override
    protected Specification<DiskFileEntity> createSpecification(DiskFileRequest request) {
        return DiskFileSpecification.search(request, authService);
    }

    @Override
    protected Page<DiskFileEntity> executePageQuery(Specification<DiskFileEntity> spec, Pageable pageable) {
        return diskFileRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<DiskFileEntity> findByUid(String uid) {
        return diskFileRepository.findByUid(uid);
    }

    @Override
    protected DiskFileEntity doSave(DiskFileEntity entity) {
        return diskFileRepository.save(entity);
    }

    @Override
    public DiskFileEntity handleOptimisticLockingFailureException(
            org.springframework.orm.ObjectOptimisticLockingFailureException e, DiskFileEntity entity) {
        log.warn("disk file optimistic locking failure: uid={}, version={}", entity.getUid(), entity.getVersion());
        return diskFileRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        diskFileRepository.findByUid(uid).ifPresent(diskFileRepository::delete);
    }

    // ==================== 空间 ====================

    /**
     * 查询当前用户可见空间：个人空间（懒创建）+ 组织共享空间
     */
    public List<DiskSpaceResponse> queryMySpaces() {
        UserEntity user = authService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("not login");
        }
        String orgUid = user.getOrgUid();
        String userUid = user.getUid();

        List<DiskSpaceResponse> result = new ArrayList<>();

        // 个人空间：不存在则创建
        DiskSpaceEntity personal = ensurePersonalSpace(orgUid, userUid);
        result.add(convertSpaceToResponse(personal));

        // 共享空间：不存在则创建（根目录 MANAGE 权限授予创建人）
        DiskSpaceEntity shared = ensureSharedSpace(orgUid, userUid);
        result.add(convertSpaceToResponse(shared));

        return result;
    }

    private DiskSpaceEntity ensurePersonalSpace(String orgUid, String userUid) {
        Optional<DiskSpaceEntity> existing = diskSpaceRepository
                .findByOrgUidAndUserUidAndType(orgUid, userUid, DiskConsts.SPACE_TYPE_PERSONAL);
        if (existing.isPresent()) {
            return existing.get();
        }
        DiskSpaceEntity space = DiskSpaceEntity.builder().build();
        space.setUid(uidUtils.getUid());
        space.setOrgUid(orgUid);
        space.setUserUid(userUid);
        space.setName(DiskConsts.ROOT_NAME_PERSONAL);
        space.setType(DiskConsts.SPACE_TYPE_PERSONAL);
        DiskSpaceEntity saved = diskSpaceRepository.save(space);
        DiskFileEntity root = createRootFolder(saved, DiskConsts.ROOT_NAME_PERSONAL);
        saved.setRootUid(root.getUid());
        return diskSpaceRepository.save(saved);
    }

    private DiskSpaceEntity ensureSharedSpace(String orgUid, String userUid) {
        Optional<DiskSpaceEntity> existing = diskSpaceRepository
                .findByOrgUidAndType(orgUid, DiskConsts.SPACE_TYPE_SHARED);
        if (existing.isPresent()) {
            return existing.get();
        }
        DiskSpaceEntity space = DiskSpaceEntity.builder().build();
        space.setUid(uidUtils.getUid());
        space.setOrgUid(orgUid);
        space.setUserUid(userUid);
        space.setName(DiskConsts.ROOT_NAME_SHARED);
        space.setType(DiskConsts.SPACE_TYPE_SHARED);
        DiskSpaceEntity saved = diskSpaceRepository.save(space);
        DiskFileEntity root = createRootFolder(saved, DiskConsts.ROOT_NAME_SHARED);
        saved.setRootUid(root.getUid());
        diskSpaceRepository.save(saved);
        // 共享空间根目录授予创建人 MANAGE 全权限（后续管理员可再授权他人）
        diskPermissionRestService.createInternal(saved.getUid(), root.getUid(), "MEMBER", userUid, "root-admin",
                "VIEW,DOWNLOAD,UPLOAD,EDIT,MANAGE");
        return saved;
    }

    private DiskFileEntity createRootFolder(DiskSpaceEntity space, String name) {
        DiskFileEntity root = DiskFileEntity.builder().build();
        root.setUid(uidUtils.getUid());
        root.setOrgUid(space.getOrgUid());
        root.setUserUid(space.getUserUid());
        root.setName(name);
        root.setType(DiskConsts.NODE_TYPE_FOLDER);
        root.setSpaceUid(space.getUid());
        root.setParentUid(null);
        root.setSize(0L);
        return diskFileRepository.save(root);
    }

    // ==================== 目录/文件查询 ====================

    /**
     * 目录分页列表（文件夹在前），含子节点计数；支持按文件类型分类/扩展名过滤
     */
    public Page<DiskFileResponse> queryByFolder(DiskFileRequest request) {
        String parentUid = StringUtils.hasText(request.getParentUid()) ? request.getParentUid() : null;
        // parentUid 为空时视为空间根
        String effectiveParent = parentUid;
        if (effectiveParent == null) {
            DiskSpaceEntity space = requireSpace(request.getSpaceUid());
            effectiveParent = space.getRootUid();
        }
        requireViewPerm(effectiveParent);

        Pageable pageable = buildPageable(request);
        // 类型过滤仅作用于文件节点；分类编码（如 IMAGE）展开为该分类全部扩展名
        List<String> fileTypeExtensions = DiskFileTypeEnum.resolveExtensions(request.getFileType());
        Page<DiskFileEntity> page = fileTypeExtensions.isEmpty()
                ? diskFileRepository.findBySpaceUidAndParentUidAndDeletedFalse(
                        request.getSpaceUid(), effectiveParent, pageable)
                : diskFileRepository.findBySpaceUidAndParentUidAndDeletedFalseAndTypeAndFileTypeIn(
                        request.getSpaceUid(), effectiveParent, DiskConsts.NODE_TYPE_FILE, fileTypeExtensions, pageable);
        Map<String, Long> childCounts = countChildren(page.getContent());
        return page.map(entity -> convertToResponse(entity, childCounts.get(entity.getUid())));
    }

    private Pageable buildPageable(DiskFileRequest request) {
        int pageNumber = request.getPageNumber() > 0 ? request.getPageNumber() - 1 : 0;
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : DiskConsts.DEFAULT_PAGE_SIZE;
        String sortBy = StringUtils.hasText(request.getSortField()) ? request.getSortField() : "name";
        Sort.Direction direction = "descend".equalsIgnoreCase(request.getSortDirection())
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        // 文件夹始终排在文件前
        return PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.ASC, "type").and(Sort.by(direction, sortBy)));
    }

    private Map<String, Long> countChildren(List<DiskFileEntity> nodes) {
        Map<String, Long> counts = new HashMap<>();
        for (DiskFileEntity node : nodes) {
            if (DiskConsts.NODE_TYPE_FOLDER.equals(node.getType())) {
                counts.put(node.getUid(), diskFileRepository.countByParentUidAndDeletedFalse(node.getUid()));
            }
        }
        return counts;
    }

    /**
     * 目录树（懒加载：只返回直接子目录，供前端 Tree 组件逐层展开）
     */
    public List<DiskFileResponse> queryFolderTree(DiskFileRequest request) {
        String parentUid = request.getParentUid();
        String effectiveParent;
        if (!StringUtils.hasText(parentUid)) {
            DiskSpaceEntity space = requireSpace(request.getSpaceUid());
            effectiveParent = space.getRootUid();
        } else {
            effectiveParent = parentUid;
        }
        requireViewPerm(effectiveParent);
        return diskFileRepository
                .findBySpaceUidAndParentUidAndDeletedFalseOrderByTypeAscNameAsc(request.getSpaceUid(), effectiveParent)
                .stream()
                .filter(n -> DiskConsts.NODE_TYPE_FOLDER.equals(n.getType()))
                .map(n -> convertToResponse(n,
                        diskFileRepository.countByParentUidAndDeletedFalse(n.getUid())))
                .collect(Collectors.toList());
    }

    /**
     * 内链定位：鉴权后返回节点 + 祖先链（面包屑）
     */
    public DiskFileResponse queryByUidForNavigate(DiskFileRequest request) {
        DiskFileEntity node = requireNode(request.getUid());
        requireViewPerm(node.getUid());
        return convertToResponse(node, null);
    }

    /**
     * 面包屑：从节点向上到空间根
     */
    public List<DiskFileResponse> queryBreadcrumb(DiskFileRequest request) {
        DiskFileEntity node = requireNode(request.getUid());
        List<DiskFileResponse> chain = new ArrayList<>();
        DiskFileEntity current = node;
        int depth = 0;
        while (current != null && depth < 64) {
            chain.add(convertToResponse(current, null));
            if (!StringUtils.hasText(current.getParentUid())) {
                break;
            }
            current = diskFileRepository.findByUid(current.getParentUid()).orElse(null);
            depth++;
        }
        java.util.Collections.reverse(chain);
        return chain;
    }

    /**
     * 当前空间文件名搜索（仅活跃节点；权限：须对文件所在目录有 VIEW）；支持类型过滤
     */
    public Page<DiskFileResponse> search(DiskFileRequest request) {
        if (!StringUtils.hasText(request.getKeyword())) {
            return Page.empty();
        }
        DiskSpaceEntity space = requireSpace(request.getSpaceUid());
        // 类型过滤仅作用于文件节点；分类编码（如 IMAGE）展开为该分类全部扩展名
        List<String> fileTypeExtensions = DiskFileTypeEnum.resolveExtensions(request.getFileType());
        List<DiskFileEntity> hits = fileTypeExtensions.isEmpty()
                ? diskFileRepository.findBySpaceUidAndDeletedFalseAndNameContainingIgnoreCase(space.getUid(), request.getKeyword())
                : diskFileRepository.findBySpaceUidAndDeletedFalseAndNameContainingIgnoreCaseAndTypeAndFileTypeIn(
                        space.getUid(), request.getKeyword(), DiskConsts.NODE_TYPE_FILE, fileTypeExtensions);
        // 逐条校验 VIEW 权限，无权节点不可见
        List<DiskFileEntity> visible = hits.stream()
                .filter(node -> hasPerm(node, DiskPermEnum.VIEW))
                .collect(Collectors.toList());
        int pageNumber = request.getPageNumber() > 0 ? request.getPageNumber() - 1 : 0;
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : DiskConsts.DEFAULT_PAGE_SIZE;
        int from = Math.min(pageNumber * pageSize, visible.size());
        int to = Math.min(from + pageSize, visible.size());
        List<DiskFileResponse> responses = visible.subList(from, to).stream()
                .map(node -> convertToResponse(node, null))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, PageRequest.of(pageNumber, pageSize), visible.size());
    }

    // ==================== 写操作 ====================

    /**
     * 新建文件夹（同名冲突返回错误）
     */
    public DiskFileResponse createFolder(DiskFileRequest request) {
        validateName(request.getName());
        String parentUid = resolveParentUid(request);
        requirePerm(parentUid, DiskPermEnum.UPLOAD);
        if (diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(
                request.getSpaceUid(), parentUid, request.getName())) {
            throw new RuntimeException("同名文件或文件夹已存在: " + request.getName());
        }
        UserEntity user = authService.getCurrentUser();
        DiskFileEntity entity = DiskFileEntity.builder().build();
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(user != null ? user.getOrgUid() : request.getOrgUid());
        entity.setUserUid(user != null ? user.getUid() : null);
        entity.setName(request.getName());
        entity.setType(DiskConsts.NODE_TYPE_FOLDER);
        entity.setSpaceUid(request.getSpaceUid());
        entity.setParentUid(parentUid);
        entity.setSize(0L);
        return convertToResponse(diskFileRepository.save(entity), null);
    }

    /**
     * 上传文件：复用 core Upload 存储（含安全校验），再写 DiskFile 记录
     */
    public DiskFileResponse upload(MultipartFile file, DiskFileRequest request) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        // RFC 7578：浏览器（目录上传等场景）可能在 multipart filename 中携带相对路径，统一取基础名
        String originalName = normalizeUploadFileName(file.getOriginalFilename());
        validateName(originalName);
        String parentUid = resolveParentUid(request);
        requirePerm(parentUid, DiskPermEnum.UPLOAD);
        if (diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(
                request.getSpaceUid(), parentUid, originalName)) {
            throw new RuntimeException("同名文件已存在: " + originalName);
        }
        // 存储物理文件（core upload 会做类型白名单/大小/图片校验）
        String fileUrl = uploadRestService.store(file, originalName);
        if (!StringUtils.hasText(fileUrl)) {
            throw new RuntimeException("文件存储失败");
        }
        UserEntity user = authService.getCurrentUser();
        DiskFileEntity entity = DiskFileEntity.builder().build();
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(user != null ? user.getOrgUid() : request.getOrgUid());
        entity.setUserUid(user != null ? user.getUid() : null);
        entity.setName(originalName);
        entity.setType(DiskConsts.NODE_TYPE_FILE);
        entity.setFileType(extractExtension(originalName));
        entity.setSpaceUid(request.getSpaceUid());
        entity.setParentUid(parentUid);
        entity.setSize(file.getSize());
        entity.setStorageBackend(DiskConsts.STORAGE_BACKEND_UPLOAD);
        entity.setStorageKey(fileUrl);
        entity.setUploader(user != null ? user.getNickname() : null);
        return convertToResponse(diskFileRepository.save(entity), null);
    }

    /**
     * 重命名（同名冲突校验）
     */
    public DiskFileResponse rename(DiskFileRequest request) {
        validateName(request.getName());
        DiskFileEntity node = requireNode(request.getUid());
        if (isRoot(node)) {
            throw new RuntimeException("根目录不可重命名");
        }
        requirePerm(node.getParentUid(), DiskPermEnum.EDIT);
        if (!node.getName().equals(request.getName())
                && diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(
                        node.getSpaceUid(), node.getParentUid(), request.getName())) {
            throw new RuntimeException("同名文件或文件夹已存在: " + request.getName());
        }
        node.setName(request.getName());
        if (DiskConsts.NODE_TYPE_FILE.equals(node.getType())) {
            node.setFileType(extractExtension(request.getName()));
        }
        return convertToResponse(diskFileRepository.save(node), null);
    }

    /**
     * 移动（批量）：防环校验 + 源 EDIT + 目标 UPLOAD；单条提交单位
     */
    public Map<String, Object> move(DiskFileRequest request) {
        String targetUid = resolveTargetUid(request);
        requirePerm(targetUid, DiskPermEnum.UPLOAD);
        Map<String, Object> results = new HashMap<>();
        for (String uid : request.getUids()) {
            try {
                DiskFileEntity node = requireNode(uid);
                requirePerm(node.getParentUid(), DiskPermEnum.EDIT);
                if (isRoot(node)) {
                    results.put(uid, "根目录不可移动");
                    continue;
                }
                if (uid.equals(targetUid)) {
                    results.put(uid, "不能移动到自身");
                    continue;
                }
                if (isInSubtree(targetUid, uid)) {
                    results.put(uid, "不能移动到自身子目录");
                    continue;
                }
                if (node.getParentUid() != null && node.getParentUid().equals(targetUid)) {
                    results.put(uid, "已在目标目录中");
                    continue;
                }
                if (diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(
                        node.getSpaceUid(), targetUid, node.getName())) {
                    results.put(uid, "目标目录存在同名: " + node.getName());
                    continue;
                }
                node.setParentUid(targetUid);
                diskFileRepository.save(node);
                results.put(uid, "success");
            } catch (Exception e) {
                results.put(uid, e.getMessage() != null ? e.getMessage() : "failed");
            }
        }
        return results;
    }

    /**
     * 复制（单条提交单位）：源 DOWNLOAD + 目标 UPLOAD；递归复制目录
     */
    public Map<String, Object> copy(DiskFileRequest request) {
        String targetUid = resolveTargetUid(request);
        requirePerm(targetUid, DiskPermEnum.UPLOAD);
        Map<String, Object> results = new HashMap<>();
        for (String uid : request.getUids()) {
            try {
                DiskFileEntity node = requireNode(uid);
                requirePerm(node.getParentUid(), DiskPermEnum.DOWNLOAD);
                if (isRoot(node)) {
                    results.put(uid, "根目录不可复制");
                    continue;
                }
                copyNode(node, targetUid);
                results.put(uid, "success");
            } catch (Exception e) {
                results.put(uid, e.getMessage() != null ? e.getMessage() : "failed");
            }
        }
        return results;
    }

    private void copyNode(DiskFileEntity source, String targetParentUid) {
        String name = uniqueName(source.getSpaceUid(), targetParentUid, source.getName());
        UserEntity user = authService.getCurrentUser();
        DiskFileEntity copy = DiskFileEntity.builder().build();
        copy.setUid(uidUtils.getUid());
        copy.setOrgUid(source.getOrgUid());
        copy.setUserUid(user != null ? user.getUid() : source.getUserUid());
        copy.setName(name);
        copy.setType(source.getType());
        copy.setFileType(source.getFileType());
        copy.setSpaceUid(source.getSpaceUid());
        copy.setParentUid(targetParentUid);
        copy.setSize(source.getSize());
        copy.setStorageBackend(source.getStorageBackend());
        copy.setStorageKey(source.getStorageKey());
        copy.setUploader(source.getUploader());
        DiskFileEntity saved = diskFileRepository.save(copy);
        // 递归复制子节点
        if (DiskConsts.NODE_TYPE_FOLDER.equals(source.getType())) {
            for (DiskFileEntity child : diskFileRepository
                    .findBySpaceUidAndParentUidAndDeletedFalse(source.getSpaceUid(), source.getUid())) {
                copyNode(child, saved.getUid());
            }
        }
    }

    private String uniqueName(String spaceUid, String parentUid, String name) {
        if (!diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(spaceUid, parentUid, name)) {
            return name;
        }
        String base = name;
        String ext = "";
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            base = name.substring(0, dot);
            ext = name.substring(dot);
        }
        for (int i = 1; i < 1000; i++) {
            String candidate = base + " (" + i + ")" + ext;
            if (!diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(spaceUid, parentUid, candidate)) {
                return candidate;
            }
        }
        throw new RuntimeException("无法生成唯一名称: " + name);
    }

    // ==================== 回收站 ====================

    /**
     * 软删除（批量）：目录连同子树整批删除；此前单独删除的后代保留其原删除批次
     */
    public Map<String, Object> deleteBatch(DiskFileRequest request) {
        UserEntity user = authService.getCurrentUser();
        Map<String, Object> results = new HashMap<>();
        for (String uid : request.getUids()) {
            try {
                DiskFileEntity node = requireNode(uid);
                if (isRoot(node)) {
                    results.put(uid, "根目录不可删除");
                    continue;
                }
                requirePerm(node.getParentUid(), DiskPermEnum.MANAGE);
                softDeleteSubtree(node, user != null ? user.getUid() : null);
                results.put(uid, "success");
            } catch (Exception e) {
                results.put(uid, e.getMessage() != null ? e.getMessage() : "failed");
            }
        }
        return results;
    }

    private void softDeleteSubtree(DiskFileEntity node, String deletedBy) {
        ZonedDateTime now = ZonedDateTime.now();
        node.setDeleted(true);
        node.setDeletedAt(now);
        node.setDeletedBy(deletedBy);
        node.setOriginParentUid(node.getParentUid());
        diskFileRepository.save(node);
        for (DiskFileEntity child : diskFileRepository
                .findBySpaceUidAndParentUidAndDeletedFalse(node.getSpaceUid(), node.getUid())) {
            softDeleteSubtree(child, deletedBy);
        }
    }

    /**
     * 回收站分页（当前空间）；支持按文件类型分类/扩展名过滤
     */
    public Page<DiskFileResponse> queryRecycle(DiskFileRequest request) {
        DiskSpaceEntity space = requireSpace(request.getSpaceUid());
        // 个人空间仅本人可见；共享空间需对空间根有 VIEW（一期从简：有空间权限即可见回收站顶层条目）
        UserEntity user = authService.getCurrentUser();
        if (DiskConsts.SPACE_TYPE_PERSONAL.equals(space.getType())) {
            if (user == null || !user.getUid().equals(space.getUserUid())) {
                throw new RuntimeException(DiskI18nConsts.I18N_DISK_RECYCLE_FORBIDDEN);
            }
        }
        int pageNumber = request.getPageNumber() > 0 ? request.getPageNumber() - 1 : 0;
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : DiskConsts.DEFAULT_PAGE_SIZE;
        // 只显示顶层删除条目（其后代跟随父条目）；顶层判定须基于全部已删节点，
        // 否则启用类型过滤后，已删文件夹内的文件会被误判为顶层
        List<DiskFileEntity> allDeleted = diskFileRepository.findBySpaceUidAndDeletedTrue(space.getUid());
        List<String> deletedUids = allDeleted.stream().map(n -> n.getUid()).toList();
        List<DiskFileEntity> topLevel = allDeleted.stream()
                .filter(n -> n.getOriginParentUid() == null || !deletedUids.contains(n.getOriginParentUid()))
                .collect(Collectors.toList());
        // 类型过滤仅作用于文件节点；分类编码（如 IMAGE）展开为该分类全部扩展名
        List<String> fileTypeExtensions = DiskFileTypeEnum.resolveExtensions(request.getFileType());
        if (!fileTypeExtensions.isEmpty()) {
            topLevel = topLevel.stream()
                    .filter(n -> DiskConsts.NODE_TYPE_FILE.equals(n.getType())
                            && n.getFileType() != null
                            && fileTypeExtensions.contains(n.getFileType().toLowerCase()))
                    .collect(Collectors.toList());
        }
        int from = Math.min(pageNumber * pageSize, topLevel.size());
        int to = Math.min(from + pageSize, topLevel.size());
        return new PageImpl<>(topLevel.subList(from, to).stream().map(this::convertToResponse).toList(),
                PageRequest.of(pageNumber, pageSize), topLevel.size());
    }

    /**
     * 恢复（批量）：逐项校验权限与同名；原父目录不存在或不可写时须指定 restoreTargetUid
     */
    public Map<String, Object> restoreBatch(DiskFileRequest request) {
        Map<String, Object> results = new HashMap<>();
        for (String uid : request.getUids()) {
            try {
                DiskFileEntity node = requireNode(uid);
                if (!node.isDeleted()) {
                    results.put(uid, "该节点不在回收站中");
                    continue;
                }
                String targetParent = StringUtils.hasText(request.getRestoreTargetUid())
                        ? request.getRestoreTargetUid() : node.getOriginParentUid();
                if (!StringUtils.hasText(targetParent)) {
                    DiskSpaceEntity space = requireSpace(node.getSpaceUid());
                    targetParent = space.getRootUid();
                }
                requirePerm(targetParent, DiskPermEnum.UPLOAD);
                if (diskFileRepository.existsBySpaceUidAndParentUidAndNameAndDeletedFalse(
                        node.getSpaceUid(), targetParent, node.getName())) {
                    results.put(uid, "目标目录存在同名: " + node.getName());
                    continue;
                }
                restoreSubtree(node, targetParent);
                results.put(uid, "success");
            } catch (Exception e) {
                results.put(uid, e.getMessage() != null ? e.getMessage() : "failed");
            }
        }
        return results;
    }

    private void restoreSubtree(DiskFileEntity node, String targetParentUid) {
        node.setDeleted(false);
        node.setDeletedAt(null);
        node.setDeletedBy(null);
        node.setParentUid(targetParentUid);
        node.setOriginParentUid(null);
        diskFileRepository.save(node);
        // 后代跟随恢复：软删除子树的子节点 parentUid 保持不变，仅恢复活跃标记
        restoreDeletedChildren(node.getUid());
    }

    private void restoreDeletedChildren(String parentUid) {
        // 软删除子树的子节点 parentUid 未变，按 parentUid+deleted 定位并逐层恢复
        diskFileRepository.findAll().stream()
                .filter(n -> parentUid.equals(n.getParentUid()) && n.isDeleted())
                .forEach(child -> {
                    child.setDeleted(false);
                    child.setDeletedAt(null);
                    child.setDeletedBy(null);
                    child.setOriginParentUid(null);
                    diskFileRepository.save(child);
                    restoreDeletedChildren(child.getUid());
                });
    }

    /**
     * 彻底删除（批量）：软删除节点 + 物理文件；记录留痕
     */
    public Map<String, Object> deletePermanent(DiskFileRequest request) {
        UserEntity user = authService.getCurrentUser();
        Map<String, Object> results = new HashMap<>();
        for (String uid : request.getUids()) {
            try {
                DiskFileEntity node = requireNode(uid);
                if (!node.isDeleted()) {
                    results.put(uid, "请先移入回收站");
                    continue;
                }
                diskLogService.logPermanentDelete(
                        node.getOrgUid(), node.getSpaceUid(), node.getUid(),
                        "{\"name\":\"" + escapeJson(node.getName()) + "\",\"operator\":\""
                                + (user != null ? user.getUid() : "") + "\"}");
                purgeSubtree(node);
                results.put(uid, "success");
            } catch (Exception e) {
                results.put(uid, e.getMessage() != null ? e.getMessage() : "failed");
            }
        }
        return results;
    }

    private void purgeSubtree(DiskFileEntity node) {
        // 先递归删除后代
        diskFileRepository.findAll().stream()
                .filter(n -> node.getUid().equals(n.getParentUid()))
                .forEach(this::purgeSubtree);
        // 物理文件删除交给 core upload 记录清理（一期从简：仅删除节点记录，物理文件随上传记录生命周期管理）
        diskFileRepository.delete(node);
    }

    /**
     * 清空回收站（当前空间）
     */
    public Map<String, Object> clearRecycle(DiskFileRequest request) {
        DiskSpaceEntity space = requireSpace(request.getSpaceUid());
        UserEntity user = authService.getCurrentUser();
        List<DiskFileEntity> deleted = diskFileRepository.findBySpaceUidAndDeletedTrue(space.getUid());
        for (DiskFileEntity node : deleted) {
            diskLogService.logPermanentDelete(node.getOrgUid(), space.getUid(), node.getUid(),
                    "{\"name\":\"" + escapeJson(node.getName()) + "\",\"operator\":\""
                            + (user != null ? user.getUid() : "") + "\"}");
            purgeSubtree(node);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("cleared", deleted.size());
        return result;
    }

    // ==================== 下载/预览 ====================

    /**
     * 下载：流式输出，中文文件名 RFC 5987 编码
     */
    public void download(DiskFileRequest request, HttpServletResponse response) {
        DiskFileEntity node = requireNode(request.getUid());
        if (!DiskConsts.NODE_TYPE_FILE.equals(node.getType())) {
            throw new RuntimeException("只能下载文件");
        }
        requirePerm(node.getParentUid(), DiskPermEnum.DOWNLOAD);
        if (!StringUtils.hasText(node.getStorageKey())) {
            throw new RuntimeException("文件存储信息缺失");
        }
        org.springframework.core.io.Resource resource = uploadRestService
                .loadAsResource(extractRelativePath(node.getStorageKey()));
        if (resource == null || !resource.exists()) {
            throw new RuntimeException("文件不存在或已清理");
        }
        writeResourceToResponse(resource, node.getName(), response);
    }

    /**
     * 受控预览：与下载同权限口径（VIEW 即可预览，一期从简与下载一致，二期再拆分）
     */
    public void preview(DiskFileRequest request, HttpServletResponse response) {
        DiskFileEntity node = requirePreviewFile(request.getUid());
        org.springframework.core.io.Resource resource = uploadRestService
                .loadAsResource(extractRelativePath(node.getStorageKey()));
        if (resource == null || !resource.exists()) {
            throw new RuntimeException("文件不存在或已清理");
        }
        // 预览内联展示
        response.setContentType(resolveContentType(node));
        try {
            resource.getInputStream().transferTo(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("预览输出失败: " + e.getMessage());
        }
    }

    public DiskFileEntity requirePreviewFile(String uid) {
        DiskFileEntity node = requireNode(uid);
        if (!DiskConsts.NODE_TYPE_FILE.equals(node.getType())) {
            throw new RuntimeException("只能预览文件");
        }
        if (node.isDeleted()) {
            throw new RuntimeException("文件已删除");
        }
        requirePerm(node.getParentUid(), DiskPermEnum.VIEW);
        if (!StringUtils.hasText(node.getStorageKey())) {
            throw new RuntimeException("文件存储信息缺失");
        }
        return node;
    }

    /**
     * ZIP 批量下载：先整体鉴权再流式打包，不静默跳过无权条目
     */
    public void downloadZip(DiskFileRequest request, HttpServletResponse response) {
        if (request.getUids() == null || request.getUids().isEmpty()) {
            throw new RuntimeException("未选择文件");
        }
        int maxEntries = 1000;
        // 先鉴权全部条目（任一无 DOWNLOAD 则整体失败）
        List<DiskFileEntity> nodes = new ArrayList<>();
        for (String uid : request.getUids()) {
            DiskFileEntity node = requireNode(uid);
            requirePerm(node.getParentUid(), DiskPermEnum.DOWNLOAD);
            nodes.add(node);
        }
        // 统计条目数（含目录展开）
        long entryCount = countZipEntries(nodes);
        if (entryCount > maxEntries) {
            throw new RuntimeException("打包条目数超过上限 " + maxEntries + "，当前 " + entryCount);
        }
        try {
            String zipName = "disk-" + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".zip";
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(zipName, StandardCharsets.UTF_8));
            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                for (DiskFileEntity node : nodes) {
                    writeNodeToZip(zos, node, "");
                }
                zos.finish();
            }
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("打包下载失败: " + e.getMessage());
        }
    }

    private long countZipEntries(List<DiskFileEntity> nodes) {
        long count = 0;
        for (DiskFileEntity node : nodes) {
            count += countNodeEntries(node);
        }
        return count;
    }

    private long countNodeEntries(DiskFileEntity node) {
        if (DiskConsts.NODE_TYPE_FILE.equals(node.getType())) {
            return 1;
        }
        long count = 1;
        for (DiskFileEntity child : diskFileRepository
                .findBySpaceUidAndParentUidAndDeletedFalse(node.getSpaceUid(), node.getUid())) {
            count += countNodeEntries(child);
        }
        return count;
    }

    private void writeNodeToZip(ZipOutputStream zos, DiskFileEntity node, String pathPrefix) throws IOException {
        String entryPath = pathPrefix + node.getName();
        if (DiskConsts.NODE_TYPE_FILE.equals(node.getType())) {
            zos.putNextEntry(new ZipEntry(entryPath));
            org.springframework.core.io.Resource resource = uploadRestService
                    .loadAsResource(extractRelativePath(node.getStorageKey()));
            if (resource != null && resource.exists()) {
                resource.getInputStream().transferTo(zos);
            }
            zos.closeEntry();
        } else {
            zos.putNextEntry(new ZipEntry(entryPath + "/"));
            zos.closeEntry();
            for (DiskFileEntity child : diskFileRepository
                    .findBySpaceUidAndParentUidAndDeletedFalse(node.getSpaceUid(), node.getUid())) {
                writeNodeToZip(zos, child, entryPath + "/");
            }
        }
    }

    private void writeResourceToResponse(org.springframework.core.io.Resource resource, String fileName,
            HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setContentLengthLong(resource.contentLength());
            resource.getInputStream().transferTo(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("下载输出失败: " + e.getMessage());
        }
    }

    private String resolveContentType(DiskFileEntity node) {
        String ext = node.getFileType() == null ? "" : node.getFileType().toLowerCase();
        return switch (ext) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            case "mp4" -> "video/mp4";
            case "mp3" -> "audio/mpeg";
            case "txt", "md", "log" -> "text/plain; charset=utf-8";
            case "json" -> "application/json";
            default -> "application/octet-stream";
        };
    }

    // ==================== 权限辅助 ====================

    private DiskSpaceEntity requireSpace(String spaceUid) {
        return diskSpaceRepository.findByUid(spaceUid)
                .orElseThrow(() -> new RuntimeException(DiskI18nConsts.I18N_DISK_SPACE_NOT_FOUND));
    }

    private DiskFileEntity requireNode(String uid) {
        DiskFileEntity node = diskFileRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException(DiskI18nConsts.I18N_DISK_FOLDER_NOT_FOUND));
        // 组织隔离
        UserEntity user = authService.getCurrentUser();
        if (user != null && !node.getOrgUid().equals(user.getOrgUid())) {
            throw new RuntimeException(DiskI18nConsts.I18N_DISK_FOLDER_VIEW_FORBIDDEN);
        }
        return node;
    }

    private boolean isRoot(DiskFileEntity node) {
        return node.getParentUid() == null;
    }

    /** target 是否在 source 的子树中（防环） */
    private boolean isInSubtree(String targetUid, String sourceUid) {
        if (targetUid == null) {
            return false;
        }
        if (targetUid.equals(sourceUid)) {
            return true;
        }
        DiskFileEntity current = diskFileRepository.findByUid(targetUid).orElse(null);
        int depth = 0;
        while (current != null && StringUtils.hasText(current.getParentUid()) && depth < 64) {
            if (current.getParentUid().equals(sourceUid)) {
                return true;
            }
            current = diskFileRepository.findByUid(current.getParentUid()).orElse(null);
            depth++;
        }
        return false;
    }

    private String resolveParentUid(DiskFileRequest request) {
        if (StringUtils.hasText(request.getParentUid())) {
            return request.getParentUid();
        }
        DiskSpaceEntity space = requireSpace(request.getSpaceUid());
        return space.getRootUid();
    }

    private String resolveTargetUid(DiskFileRequest request) {
        if (StringUtils.hasText(request.getTargetUid())) {
            return request.getTargetUid();
        }
        DiskSpaceEntity space = requireSpace(request.getSpaceUid());
        return space.getRootUid();
    }

    private void requireViewPerm(String folderUid) {
        if (!hasPerm(folderUid, DiskPermEnum.VIEW)) {
            throw new RuntimeException(DiskI18nConsts.I18N_DISK_FOLDER_VIEW_FORBIDDEN);
        }
    }

    private void requirePerm(String folderUid, DiskPermEnum perm) {
        if (!hasPerm(folderUid, perm)) {
            throw new RuntimeException(I18Consts.withArgs(
                    DiskI18nConsts.I18N_DISK_FOLDER_PERM_FORBIDDEN, perm.getDescription()));
        }
    }

    private boolean hasPerm(String folderUid, DiskPermEnum perm) {
        if (!StringUtils.hasText(folderUid)) {
            return false;
        }
        DiskFileEntity folder = diskFileRepository.findByUid(folderUid).orElse(null);
        if (folder == null) {
            return false;
        }
        return diskPermissionRestService.hasPerm(folder, perm);
    }

    private boolean hasPerm(DiskFileEntity node, DiskPermEnum perm) {
        // 文件权限 = 父目录权限
        if (DiskConsts.NODE_TYPE_FILE.equals(node.getType()) && StringUtils.hasText(node.getParentUid())) {
            return hasPerm(node.getParentUid(), perm);
        }
        return diskPermissionRestService.hasPerm(node, perm);
    }

    // ==================== 工具 ====================

    private void validateName(String name) {
        if (!StringUtils.hasText(name) || name.trim().isEmpty()) {
            throw new RuntimeException("名称不能为空");
        }
        if (name.length() > DiskConsts.MAX_NAME_LENGTH) {
            throw new RuntimeException("名称过长");
        }
        if (name.contains("/") || name.contains("\\") || name.contains("..")) {
            throw new RuntimeException("名称包含非法字符");
        }
    }

    /**
     * multipart filename 规范化：目录上传（webkitdirectory）场景下，部分浏览器
     * （Safari/WebKit、Electron 拖拽）会在 filename 中携带相对路径（如 "dir/sub/a.txt"）。
     * 取最后一段作为基础名，并去除路径穿越片段；仍校验空值。
     */
    static String normalizeUploadFileName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        String normalized = name.replace('\\', '/');
        String base = normalized.substring(normalized.lastIndexOf('/') + 1);
        base = base.replace("..", "_");
        // 去掉首尾空白与点（"." / ".." 或以点结尾在部分文件系统非法）
        base = base.trim().replaceAll("^[.\\s]+|[.\\s]+$", "");
        return base.isEmpty() ? "file" : base;
    }

    private String extractExtension(String name) {
        if (name == null) {
            return null;
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return null;
        }
        return name.substring(dot + 1).toLowerCase();
    }

    /** 从 core upload 的 fileUrl 提取相对路径（yyyy/MM/dd/filename） */
    private String extractRelativePath(String fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        int idx = fileUrl.indexOf("/file/");
        if (idx >= 0) {
            return fileUrl.substring(idx + "/file/".length());
        }
        return fileUrl;
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public DiskFileResponse convertToResponse(DiskFileEntity entity) {
        return convertToResponse(entity, null);
    }

    public DiskFileResponse convertToResponse(DiskFileEntity entity, Long childCount) {
        DiskFileResponse response = modelMapper.map(entity, DiskFileResponse.class);
        response.setChildCount(childCount);
        if (entity.getDeletedAt() != null) {
            response.setDeletedAt(entity.getDeletedAt().toString());
        }
        return response;
    }

    public DiskSpaceResponse convertSpaceToResponse(DiskSpaceEntity entity) {
        return modelMapper.map(entity, DiskSpaceResponse.class);
    }

    @Override
    public DiskFileResponse create(DiskFileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public DiskFileResponse update(DiskFileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(DiskFileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
