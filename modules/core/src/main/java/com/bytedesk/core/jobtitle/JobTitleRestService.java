/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 职务字典服务：组织内职务选项的 CRUD 与新组织默认职务初始化。
 * 成员 jobTitle 保存名称字符串快照，字典改名/删除不回写成员。
 */
@Slf4j
@Service
@AllArgsConstructor
public class JobTitleRestService extends BaseRestService<JobTitleEntity, JobTitleRequest, JobTitleResponse> {

    private final JobTitleRepository jobTitleRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final PermissionService permissionService;

    private static final int NAME_MAX_LENGTH = 64;

    private static final int ORDER_MIN = 0;

    private static final int ORDER_MAX = 9999;

    @Override
    public Page<JobTitleResponse> queryByOrg(JobTitleRequest request) {
        Pageable pageable = request.getPageable();
        // 稳定排序：sortOrder 升序 + uid 升序
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "order").and(Sort.by(Sort.Direction.ASC, "uid")));
        Specification<JobTitleEntity> specs = JobTitleSpecification.search(request, authService);
        Page<JobTitleEntity> page = jobTitleRepository.findAll(specs, sorted);
        return page.map(this::convertToResponse);
    }

    @Override
    public Optional<JobTitleEntity> findByUid(String uid) {
        return jobTitleRepository.findByUid(uid);
    }

    /**
     * 按 UID 查询：显式校验组织归属与实体读取权限，
     * 防止知道 UID 即可跨组织读取。
     */
    @Override
    public JobTitleResponse queryByUid(JobTitleRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
        JobTitleEntity entity = jobTitleRepository.findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID + request.getUid()));
        if (entity.isDeleted()) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID + request.getUid());
        }
        if (!permissionService.hasEntityPermission(JobTitlePermissions.MODULE_NAME, "READ", entity)) {
            throw new RuntimeException(I18Consts.I18N_ACCESS_DENIED);
        }
        return convertToResponse(entity);
    }

    public Boolean existsByUid(String uid) {
        return jobTitleRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public JobTitleResponse create(JobTitleRequest request) {
        // 名称校验与规范化
        String name = normalizeAndValidateName(request.getName());
        Integer order = validateOrder(request.getOrder());

        // 组织归属：不信任客户端，未传时取当前用户组织
        String orgUid = resolveRequestOrgUid(request);

        // 层级创建权限检查
        if (!permissionService.canCreateAtLevel(JobTitlePermissions.MODULE_NAME, LevelEnum.ORGANIZATION.name())) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }

        // 幂等：同组织同名有效项已存在时返回冲突错误（不静默返回他人记录）
        String activeKey = computeActiveKey(orgUid, name);
        if (jobTitleRepository.existsByActiveKey(activeKey)) {
            throw new JobTitleAlreadyExistsException(name);
        }

        UserEntity user = authService.getUser();

        JobTitleEntity entity = JobTitleEntity.builder()
                .uid(uidUtils.getUid())
                .name(name)
                .description(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null)
                .order(order)
                .level(LevelEnum.ORGANIZATION.name())
                .orgUid(orgUid)
                .activeKey(activeKey)
                .build();
        if (user != null) {
            entity.setUserUid(user.getUid());
        }

        JobTitleEntity savedEntity = saveWithConflictTranslation(entity, name);
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public JobTitleResponse update(JobTitleRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
        JobTitleEntity entity = jobTitleRepository.findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID + request.getUid()));
        if (entity.isDeleted()) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID + request.getUid());
        }

        // 实体级权限与组织归属校验
        if (!permissionService.hasEntityPermission(JobTitlePermissions.MODULE_NAME, "UPDATE", entity)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
        }

        String name = normalizeAndValidateName(request.getName());
        Integer order = validateOrder(request.getOrder());

        // 名称变更时更新唯一键；并发冲突交给数据库唯一约束
        String newActiveKey = computeActiveKey(entity.getOrgUid(), name);
        if (!name.equals(entity.getName()) && jobTitleRepository.existsByActiveKey(newActiveKey)) {
            throw new JobTitleAlreadyExistsException(name);
        }

        entity.setName(name);
        entity.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
        entity.setOrder(order);
        entity.setActiveKey(newActiveKey);

        JobTitleEntity savedEntity = saveWithConflictTranslation(entity, name);
        return convertToResponse(savedEntity);
    }

    @Override
    protected JobTitleEntity doSave(JobTitleEntity entity) {
        return jobTitleRepository.save(entity);
    }

    @Override
    public JobTitleEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, JobTitleEntity entity) {
        log.error("jobtitle optimistic locking conflict uid={}: {}", entity.getUid(), e.getMessage());
        throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        JobTitleEntity entity = jobTitleRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID + uid));
        if (entity.isDeleted()) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND_WITH_UID + uid);
        }

        if (!permissionService.hasEntityPermission(JobTitlePermissions.MODULE_NAME, "DELETE", entity)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
        }

        // 软删除并释放有效名称键，允许后续同名重建
        entity.setDeleted(true);
        entity.setActiveKey(null);
        jobTitleRepository.save(entity);
    }

    @Override
    public void delete(JobTitleRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public JobTitleResponse convertToResponse(JobTitleEntity entity) {
        return modelMapper.map(entity, JobTitleResponse.class);
    }

    @Override
    protected Specification<JobTitleEntity> createSpecification(JobTitleRequest request) {
        return JobTitleSpecification.search(request, authService);
    }

    @Override
    protected Page<JobTitleEntity> executePageQuery(Specification<JobTitleEntity> spec, Pageable pageable) {
        return jobTitleRepository.findAll(spec, pageable);
    }

    /**
     * 新组织默认职务初始化：与组织创建同一事务调用。
     * 幂等口径：仅在"组织无任何职务记录（含软删除）"时执行初始化。
     * 因此管理员删除/改名默认项后，重试或重启不会重建或恢复已删除项；
     * 首次部分成功后中断的场景由同一事务回滚保证不留半初始化状态。
     * 仅内部调用（创建组织链路），不做登录用户权限检查。
     */
    @Transactional
    public void initDefaultJobTitles(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return;
        }
        // 完成标记：组织存在任何职务记录（含软删除）即视为已初始化或已被管理，直接跳过
        if (jobTitleRepository.countByOrgUidIncludingDeleted(orgUid) > 0) {
            return;
        }

        for (JobTitleInitData.DefaultJobTitle defaultTitle : JobTitleInitData.DEFAULT_JOB_TITLES) {
            String activeKey = computeActiveKey(orgUid, defaultTitle.name());
            if (jobTitleRepository.existsByActiveKey(activeKey)) {
                continue;
            }
            JobTitleEntity entity = JobTitleEntity.builder()
                    .uid(uidUtils.getUid())
                    .name(defaultTitle.name())
                    .description(null)
                    .order(defaultTitle.order())
                    .level(LevelEnum.ORGANIZATION.name())
                    .orgUid(orgUid)
                    .activeKey(activeKey)
                    .build();
            jobTitleRepository.saveAndFlush(entity);
        }
        log.info("jobtitle: initialized {} default titles for org {}", JobTitleInitData.DEFAULT_JOB_TITLES.length, orgUid);
    }

    /**
     * 管理端手动初始化：按默认清单补建当前组织缺失的默认职务。
     * 与新组织自动初始化不同：不要求组织为空，已存在（含改名）的项跳过不覆盖，
     * 返回本次新增数量。权限与组织归属由 Controller 层校验。
     */
    @Transactional
    public int initDefaultJobTitlesManually() {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getOrgUid())) {
            throw new IllegalArgumentException(I18Consts.I18N_ORG_UID_REQUIRED);
        }
        String orgUid = user.getOrgUid();

        int created = 0;
        for (JobTitleInitData.DefaultJobTitle defaultTitle : JobTitleInitData.DEFAULT_JOB_TITLES) {
            String activeKey = computeActiveKey(orgUid, defaultTitle.name());
            if (jobTitleRepository.existsByActiveKey(activeKey)) {
                continue;
            }
            JobTitleEntity entity = JobTitleEntity.builder()
                    .uid(uidUtils.getUid())
                    .name(defaultTitle.name())
                    .description(null)
                    .order(defaultTitle.order())
                    .level(LevelEnum.ORGANIZATION.name())
                    .orgUid(orgUid)
                    .activeKey(activeKey)
                    .build();
            entity.setUserUid(user.getUid());
            jobTitleRepository.saveAndFlush(entity);
            created++;
        }
        log.info("jobtitle: manual init created {} default titles for org {}", created, orgUid);
        return created;
    }

    private String resolveRequestOrgUid(JobTitleRequest request) {
        if (StringUtils.hasText(request.getOrgUid())) {
            return request.getOrgUid();
        }
        UserEntity user = authService.getUser();
        if (user != null && StringUtils.hasText(user.getOrgUid())) {
            return user.getOrgUid();
        }
        throw new IllegalArgumentException(I18Consts.I18N_ORG_UID_REQUIRED);
    }

    private String normalizeAndValidateName(String rawName) {
        if (!StringUtils.hasText(rawName)) {
            throw new IllegalArgumentException("name is required");
        }
        String name = rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("name length must not exceed " + NAME_MAX_LENGTH);
        }
        return name;
    }

    private Integer validateOrder(Integer order) {
        if (order == null) {
            return 0;
        }
        if (order < ORDER_MIN || order > ORDER_MAX) {
            throw new IllegalArgumentException("order must be between " + ORDER_MIN + " and " + ORDER_MAX);
        }
        return order;
    }

    /**
     * 计算有效项唯一键：sha256(orgUid + ':' + NFC 归一化名称) 的十六进制字符串（64 字符）。
     * NFC 归一化保证同形中文名生成一致键；trim 后大小写保留（职务名区分大小写场景罕见，
     * 保留原样避免误合并），如需大小写不敏感可在实施确认后调整 normalize 规则。
     */
    static String computeActiveKey(String orgUid, String name) {
        String normalized = Normalizer.normalize(name.trim(), Normalizer.Form.NFC);
        String input = orgUid + ":" + normalized;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /**
     * 保存并翻译唯一约束冲突为明确的可本地化错误。
     */
    private JobTitleEntity saveWithConflictTranslation(JobTitleEntity entity, String name) {
        try {
            JobTitleEntity saved = save(entity);
            if (saved == null) {
                throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
            }
            return saved;
        } catch (DataIntegrityViolationException e) {
            // 并发同名校验竞争：事务已回滚，不允许在回滚事务中继续查询
            log.warn("jobtitle duplicate name conflict: orgUid={}, name={}", entity.getOrgUid(), name);
            throw new JobTitleAlreadyExistsException(name);
        }
    }
}
