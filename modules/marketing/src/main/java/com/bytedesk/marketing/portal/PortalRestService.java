/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.marketing.portal;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.utils.MarkdownRenderUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PortalRestService extends BaseRestServiceWithExport<PortalEntity, PortalRequest, PortalResponse, PortalExcel> {

    private final PortalRepository portalRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<PortalEntity> queryByOrgEntity(PortalRequest request) {
        Pageable pageable = request.getPageable();
        Specification<PortalEntity> specs = PortalSpecification.search(request, authService);
        return portalRepository.findAll(specs, pageable);
    }

    @Override
    public Page<PortalResponse> queryByOrg(PortalRequest request) {
        Page<PortalEntity> portalPage = queryByOrgEntity(request);
        return portalPage.map(this::convertToResponse);
    }

    @Override
    public Page<PortalResponse> queryByUser(PortalRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "portal", key = "#uid", unless="#result==null")
    @Override
    public Optional<PortalEntity> findByUid(String uid) {
        return portalRepository.findByUid(uid);
    }

    @Cacheable(value = "portal", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<PortalEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return portalRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public List<PortalEntity> findByKbUid(String kbUid) {
        return portalRepository.findByKbUidAndDeletedFalse(kbUid);
    }

    public Boolean existsByUid(String uid) {
        return portalRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public PortalResponse create(PortalRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public PortalResponse createSystemPortal(PortalRequest request) {
        return createInternal(request, true);
    }

    private PortalResponse createInternal(PortalRequest request, boolean skipPermissionCheck) {
        log.info("portal - create request uid={}, kbUid={}, type={}, name={}",
                request.getUid(), request.getKbUid(), request.getType(), request.getName());

        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            log.info("portal - create dedup hit uid={}", request.getUid());
            return convertToResponse(findByUid(request.getUid()).get());
        }

        if (!StringUtils.hasText(request.getKbUid())) {
            throw new RuntimeException("kbUid required");
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
            request.setOrgUid(user.getOrgUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(PortalPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        PortalEntity entity = modelMapper.map(request, PortalEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        if (user != null) {
            entity.setOrgUid(user.getOrgUid());
            entity.setUserUid(user.getUid());
        }
        // 
        PortalEntity savedEntity;
        try {
            savedEntity = save(entity);
        } catch (DataIntegrityViolationException e) {
            // 并发/重试导致相同 uid 重复插入：直接返回已存在的记录
            if (StringUtils.hasText(entity.getUid())) {
                Optional<PortalEntity> existing = portalRepository.findByUid(entity.getUid());
                if (existing.isPresent()) {
                    log.info("portal - create dedup by unique uid={} (race)", entity.getUid());
                    return convertToResponse(existing.get());
                }
            }
            throw e;
        }
        if (savedEntity == null) {
            throw new RuntimeException("Create portal failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public PortalResponse update(PortalRequest request) {
        Optional<PortalEntity> optional = portalRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            PortalEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(PortalPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            if (StringUtils.hasText(request.getName())) {
                entity.setName(request.getName());
            }
            if (request.getDescription() != null) {
                entity.setDescription(request.getDescription());
            }
            if (request.getType() != null) {
                entity.setType(request.getType());
            }
            if (request.getCoverImageUrl() != null) {
                entity.setCoverImageUrl(request.getCoverImageUrl());
            }
            if (request.getContentMarkdown() != null) {
                entity.setContentMarkdown(request.getContentMarkdown());
            }
            if (request.getContentHtml() != null) {
                entity.setContentHtml(request.getContentHtml());
            }
            if (request.getCategoryUid() != null) {
                entity.setCategoryUid(request.getCategoryUid());
            }
            if (request.getKbUid() != null) {
                entity.setKbUid(request.getKbUid());
            }
            if (request.getTagList() != null) {
                entity.setTagList(request.getTagList());
            }
            if (request.getPublished() != null) {
                entity.setPublished(request.getPublished());
            }
            if (request.getTop() != null) {
                entity.setTop(request.getTop());
            }
            if (request.getReadCount() != null) {
                entity.setReadCount(request.getReadCount());
            }
            if (request.getLikeCount() != null) {
                entity.setLikeCount(request.getLikeCount());
            }
            if (request.getEditor() != null) {
                entity.setEditor(request.getEditor());
            }
            //
            PortalEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update portal failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Portal not found");
        }
    }

    @Override
    protected PortalEntity doSave(PortalEntity entity) {
        return portalRepository.save(entity);
    }

    @Override
    public PortalEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PortalEntity entity) {
        try {
            Optional<PortalEntity> latest = portalRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                PortalEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setType(entity.getType());
                latestEntity.setCoverImageUrl(entity.getCoverImageUrl());
                latestEntity.setContentMarkdown(entity.getContentMarkdown());
                latestEntity.setContentHtml(entity.getContentHtml());
                latestEntity.setCategoryUid(entity.getCategoryUid());
                latestEntity.setKbUid(entity.getKbUid());
                latestEntity.setTagList(entity.getTagList());
                latestEntity.setPublished(entity.getPublished());
                latestEntity.setTop(entity.getTop());
                latestEntity.setReadCount(entity.getReadCount());
                latestEntity.setLikeCount(entity.getLikeCount());
                latestEntity.setEditor(entity.getEditor());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return portalRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<PortalEntity> optional = portalRepository.findByUid(uid);
        if (optional.isPresent()) {
            PortalEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(PortalPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // portalRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Portal not found");
        }
    }

    @Override
    public void delete(PortalRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public PortalResponse convertToResponse(PortalEntity entity) {
        PortalResponse response = modelMapper.map(entity, PortalResponse.class);
        // For template rendering (ftl): ensure MARKDOWN portals have contentHtml available.
        String type = response.getType();
        boolean isMarkdown = type != null && "MARKDOWN".equalsIgnoreCase(type);
        if (isMarkdown && (response.getContentHtml() == null || response.getContentHtml().isBlank())
                && response.getContentMarkdown() != null && !response.getContentMarkdown().isBlank()) {
            response.setContentHtml(MarkdownRenderUtils.toHtml(response.getContentMarkdown()));
        }
        return response;
    }

    @Override
    public PortalExcel convertToExcel(PortalEntity entity) {
        return modelMapper.map(entity, PortalExcel.class);
    }

    @Override
    protected Specification<PortalEntity> createSpecification(PortalRequest request) {
        return PortalSpecification.search(request, authService);
    }

    @Override
    protected Page<PortalEntity> executePageQuery(Specification<PortalEntity> spec, Pageable pageable) {
        return portalRepository.findAll(spec, pageable);
    }
    
    public void initPortals(String orgUid) {
        // log.info("initPortalPortal");
    }

    
    
}
