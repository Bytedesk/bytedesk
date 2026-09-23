/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.llm_feishu.event.FeishuDocUpdateDocEvent;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 飞书文档 RestService：标准 CRUD + 渠道同步专用 ingest() 幂等落库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuDocRestService extends BaseRestService<FeishuDocEntity, FeishuDocRequest, FeishuDocResponse> {

    private final FeishuDocRepository feishuDocRepository;

    private final UidUtils uidUtils;

    private final BytedeskEventPublisher eventPublisher;

    public static final String RESOURCE_TYPE_WIKI_NODE = "WIKI_NODE";
    public static final String RESOURCE_TYPE_DRIVE_FILE = "DRIVE_FILE";

    @Override
    public Optional<FeishuDocEntity> findByUid(String uid) {
        return feishuDocRepository.findByUid(uid);
    }

    /**
     * 渠道同步幂等落库（upsert by feishuUid + resourceType + objToken）：
     * - 不存在 → 新建（PostPersist → CreateEvent → 索引）
     * - 存在且内容 hash/标题/软删除状态变化 → 更新并发布 UpdateDocEvent → 重索引
     * - 存在且无变化 → 不落库不索引（skipped）
     * 事务提交后才发送索引消息（AFTER_COMMIT 监听）
     */
    @Transactional
    public FeishuDocResponse ingest(FeishuDocRequest request) {
        validateIngest(request);

        Optional<FeishuDocEntity> existingOpt = feishuDocRepository
                .findByFeishuUidAndResourceTypeAndObjToken(request.getFeishuUid(), request.getResourceType(),
                        request.getObjToken());

        if (existingOpt.isEmpty()) {
            FeishuDocEntity entity = mapRequestToEntity(request);
            entity.setUid(uidUtils.getUid());
            FeishuDocEntity saved = save(entity);
            feishuDocRepository.updateSyncStatusOnly(saved.getUid(), FeishuDocStatusEnum.SUCCESS.name());
            return convertToResponse(saved);
        }

        FeishuDocEntity entity = existingOpt.get();
        boolean changed = isContentChanged(entity, request);
        if (!changed) {
            // 无变化：仅可能刷新编辑时间，不触发索引
            if (request.getObjEditTime() != null && !request.getObjEditTime().equals(entity.getObjEditTime())) {
                entity.setObjEditTime(request.getObjEditTime());
                save(entity);
            }
            return convertToResponse(entity);
        }

        // 软删除行复活或内容变化：更新并显式触发重索引
        boolean revive = entity.isDeleted();
        entity.setDeleted(false);
        applySyncFields(entity, request);
        entity.setSyncStatus(FeishuDocStatusEnum.SUCCESS.name());
        FeishuDocEntity saved = save(entity);
        if (revive || changed) {
            eventPublisher.publishEvent(new FeishuDocUpdateDocEvent(saved));
        }
        return convertToResponse(saved);
    }

    private void validateIngest(FeishuDocRequest request) {
        if (!StringUtils.hasText(request.getFeishuUid())) {
            throw new IllegalArgumentException("feishuUid is required for ingest");
        }
        if (!StringUtils.hasText(request.getResourceType())) {
            throw new IllegalArgumentException("resourceType is required for ingest");
        }
        if (!StringUtils.hasText(request.getObjToken())) {
            throw new IllegalArgumentException("objToken is required for ingest");
        }
        if (!StringUtils.hasText(request.getKbUid())) {
            throw new IllegalArgumentException("kbUid is required for ingest");
        }
        if (!StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required for ingest");
        }
    }

    private boolean isContentChanged(FeishuDocEntity entity, FeishuDocRequest request) {
        if (entity.isDeleted()) {
            return true;
        }
        boolean hashChanged = !StringUtils.hasText(entity.getContentHash())
                || (StringUtils.hasText(request.getContentHash())
                        && !entity.getContentHash().equals(request.getContentHash()));
        boolean titleChanged = (entity.getTitle() == null && request.getTitle() != null)
                || (entity.getTitle() != null && !entity.getTitle().equals(request.getTitle()));
        boolean kbChanged = (entity.getKbUid() == null && request.getKbUid() != null)
                || (entity.getKbUid() != null && !entity.getKbUid().equals(request.getKbUid()));
        boolean enabledChanged = request.getEnabled() != null
                && !Boolean.TRUE.equals(request.getEnabled()) == Boolean.TRUE.equals(entity.getEnabled());
        return hashChanged || titleChanged || kbChanged || enabledChanged;
    }

    private FeishuDocEntity mapRequestToEntity(FeishuDocRequest request) {
        return FeishuDocEntity.builder()
                .feishuUid(request.getFeishuUid())
                .kbUid(request.getKbUid())
                .categoryUid(request.getCategoryUid())
                .resourceType(request.getResourceType())
                .spaceId(request.getSpaceId())
                .nodeToken(request.getNodeToken())
                .folderToken(request.getFolderToken())
                .objToken(request.getObjToken())
                .objType(request.getObjType())
                .title(request.getTitle())
                .content(request.getContent())
                .sourceUrl(request.getSourceUrl())
                .objCreateTime(request.getObjCreateTime())
                .objEditTime(request.getObjEditTime())
                .contentHash(request.getContentHash())
                .lastSyncBatchUid(null)
                .lastSyncError(null)
                .syncStatus(FeishuDocStatusEnum.SUCCESS.name())
                .enabled(request.getEnabled() != null ? request.getEnabled() : Boolean.TRUE)
                .orgUid(request.getOrgUid())
                .build();
    }

    private void applySyncFields(FeishuDocEntity entity, FeishuDocRequest request) {
        entity.setKbUid(request.getKbUid());
        if (request.getCategoryUid() != null) {
            entity.setCategoryUid(request.getCategoryUid());
        }
        entity.setSpaceId(request.getSpaceId());
        entity.setNodeToken(request.getNodeToken());
        entity.setFolderToken(request.getFolderToken());
        entity.setObjType(request.getObjType());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setSourceUrl(request.getSourceUrl());
        entity.setObjCreateTime(request.getObjCreateTime());
        entity.setObjEditTime(request.getObjEditTime());
        if (StringUtils.hasText(request.getContentHash())) {
            entity.setContentHash(request.getContentHash());
        }
        if (StringUtils.hasText(request.getOrgUid())) {
            entity.setOrgUid(request.getOrgUid());
        }
        if (request.getEnabled() != null) {
            entity.setEnabled(request.getEnabled());
        }
        // 内容变化后索引状态重置
        entity.setElasticStatus(FeishuDocStatusEnum.NEW.name());
        entity.setVectorStatus(FeishuDocStatusEnum.NEW.name());
    }

    /**
     * 批量查询入口（渠道管理端点使用）
     */
    public Page<FeishuDocEntity> queryByCondition(FeishuDocRequest request) {
        Specification<FeishuDocEntity> spec = createSpecification(request);
        Pageable pageable = request.getPageable();
        return executePageQuery(spec, pageable);
    }

    public Page<FeishuDocResponse> queryByConditionResponse(FeishuDocRequest request) {
        return queryByCondition(request).map(this::convertToResponse);
    }

    public List<FeishuDocEntity> findByFeishuUidAndSpaceId(String feishuUid, String spaceId) {
        return feishuDocRepository.findByFeishuUidAndSpaceIdAndResourceTypeAndDeletedFalse(feishuUid, spaceId,
                RESOURCE_TYPE_WIKI_NODE);
    }

    // 状态/docIdList 回写（供向量服务与消费者调用）

    @Transactional
    public void updateElasticStatusOnly(String uid, String status) {
        feishuDocRepository.updateElasticStatusOnly(uid, status);
    }

    @Transactional
    public void updateVectorStatusOnly(String uid, String status) {
        feishuDocRepository.updateVectorStatusOnly(uid, status);
    }

    @Transactional
    public void updateSyncStatusOnly(String uid, String status) {
        feishuDocRepository.updateSyncStatusOnly(uid, status);
    }

    @Transactional
    public void updateDocIdList(String uid, List<String> docIdList) {
        feishuDocRepository.findByUid(uid).ifPresent(entity -> {
            entity.setDocIdList(docIdList != null ? docIdList : new java.util.ArrayList<>());
            feishuDocRepository.save(entity);
        });
    }

    @Transactional
    public void updateSyncError(String uid, String batchUid, String error) {
        feishuDocRepository.findByUid(uid).ifPresent(entity -> {
            entity.setLastSyncBatchUid(batchUid);
            entity.setLastSyncError(error);
            entity.setSyncStatus(FeishuDocStatusEnum.ERROR.name());
            feishuDocRepository.save(entity);
        });
    }

    // BaseRestService 抽象方法实现

    @Override
    @Transactional
    public FeishuDocResponse create(FeishuDocRequest request) {
        FeishuDocEntity entity = mapRequestToEntity(request);
        entity.setUid(uidUtils.getUid());
        FeishuDocEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public FeishuDocResponse update(FeishuDocRequest request) {
        FeishuDocEntity entity = feishuDocRepository.findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found"));
        boolean changed = isContentChanged(entity, request);
        applySyncFields(entity, request);
        FeishuDocEntity saved = save(entity);
        if (changed) {
            eventPublisher.publishEvent(new FeishuDocUpdateDocEvent(saved));
        }
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteByUid(String uid) {
        FeishuDocEntity entity = feishuDocRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found"));
        entity.setDeleted(true);
        save(entity);
    }

    @Override
    public void delete(FeishuDocRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected FeishuDocEntity doSave(FeishuDocEntity entity) {
        return feishuDocRepository.save(entity);
    }

    @Override
    public FeishuDocEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            FeishuDocEntity entity) {
        try {
            Optional<FeishuDocEntity> latest = feishuDocRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                latest.get().setTitle(entity.getTitle());
                latest.get().setContent(entity.getContent());
                return feishuDocRepository.save(latest.get());
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public FeishuDocResponse convertToResponse(FeishuDocEntity entity) {
        return FeishuDocResponse.builder()
                .uid(entity.getUid())
                .feishuUid(entity.getFeishuUid())
                .kbUid(entity.getKbUid())
                .categoryUid(entity.getCategoryUid())
                .resourceType(entity.getResourceType())
                .spaceId(entity.getSpaceId())
                .nodeToken(entity.getNodeToken())
                .folderToken(entity.getFolderToken())
                .objToken(entity.getObjToken())
                .objType(entity.getObjType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .sourceUrl(entity.getSourceUrl())
                .objCreateTime(entity.getObjCreateTime())
                .objEditTime(entity.getObjEditTime())
                .contentHash(entity.getContentHash())
                .lastSyncBatchUid(entity.getLastSyncBatchUid())
                .lastSyncError(entity.getLastSyncError())
                .syncStatus(entity.getSyncStatus())
                .elasticStatus(entity.getElasticStatus())
                .vectorStatus(entity.getVectorStatus())
                .docIdList(entity.getDocIdList())
                .enabled(entity.getEnabled())
                .orgUid(entity.getOrgUid())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected Specification<FeishuDocEntity> createSpecification(FeishuDocRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
            }
            if (StringUtils.hasText(request.getFeishuUid())) {
                predicates.add(criteriaBuilder.equal(root.get("feishuUid"), request.getFeishuUid()));
            }
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            if (StringUtils.hasText(request.getSpaceId())) {
                predicates.add(criteriaBuilder.equal(root.get("spaceId"), request.getSpaceId()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    protected Page<FeishuDocEntity> executePageQuery(Specification<FeishuDocEntity> spec, Pageable pageable) {
        return feishuDocRepository.findAll(spec, pageable);
    }
}
