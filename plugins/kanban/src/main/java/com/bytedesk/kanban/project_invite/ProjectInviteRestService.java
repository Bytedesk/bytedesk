/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:27:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project_invite;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectInviteRestService extends BaseRestService<ProjectInviteEntity, ProjectInviteRequest, ProjectInviteResponse> {

    private final ProjectInviteRepository projectInviteRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    // === 实现必需的抽象方法 ===

    @Override
    protected Specification<ProjectInviteEntity> createSpecification(ProjectInviteRequest request) {
        return ProjectInviteSpecification.search(request);
    }

    @Override
    protected Page<ProjectInviteEntity> executePageQuery(Specification<ProjectInviteEntity> spec, Pageable pageable) {
        return projectInviteRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "project_invite", key = "#uid", unless="#result==null")
    @Override
    public Optional<ProjectInviteEntity> findByUid(String uid) {
        return projectInviteRepository.findByUid(uid);
    }

    @Override
    public ProjectInviteResponse convertToResponse(ProjectInviteEntity entity) {
        return modelMapper.map(entity, ProjectInviteResponse.class);
    }

    @Override
    protected ProjectInviteEntity doSave(ProjectInviteEntity entity) {
        return projectInviteRepository.save(entity);
    }

    @Override
    public ProjectInviteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ProjectInviteEntity entity) {
        try {
            Optional<ProjectInviteEntity> latest = projectInviteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ProjectInviteEntity latestEntity = latest.get();
                // 合并需要保留的数据
                modelMapper.map(entity, latestEntity);
                return projectInviteRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    // === 业务逻辑方法 ===

    @Override
    public ProjectInviteResponse create(ProjectInviteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        
        ProjectInviteEntity entity = modelMapper.map(request, ProjectInviteEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(user.getOrgUid());

        ProjectInviteEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create project_invite failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ProjectInviteResponse update(ProjectInviteRequest request) {
        Optional<ProjectInviteEntity> optional = projectInviteRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ProjectInviteEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ProjectInviteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update project_invite failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ProjectInvite not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ProjectInviteEntity> optional = projectInviteRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("ProjectInvite not found");
        }
    }

    @Override
    public void delete(ProjectInviteRequest request) {
        deleteByUid(request.getUid());
    }
}
