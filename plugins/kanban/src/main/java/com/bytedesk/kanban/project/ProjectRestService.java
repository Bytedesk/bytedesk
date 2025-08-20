/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:48:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project;

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
public class ProjectRestService extends BaseRestService<ProjectEntity, ProjectRequest, ProjectResponse> {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    // === 实现必需的抽象方法 ===

    @Override
    protected Specification<ProjectEntity> createSpecification(ProjectRequest request) {
        return ProjectSpecification.search(request);
    }

    @Override
    protected Page<ProjectEntity> executePageQuery(Specification<ProjectEntity> spec, Pageable pageable) {
        return projectRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "project", key = "#uid", unless="#result==null")
    @Override
    public Optional<ProjectEntity> findByUid(String uid) {
        return projectRepository.findByUid(uid);
    }

    @Override
    public ProjectResponse convertToResponse(ProjectEntity entity) {
        return modelMapper.map(entity, ProjectResponse.class);
    }

    @Override
    protected ProjectEntity doSave(ProjectEntity entity) {
        return projectRepository.save(entity);
    }

    @Override
    public ProjectEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ProjectEntity entity) {
        try {
            Optional<ProjectEntity> latest = projectRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ProjectEntity latestEntity = latest.get();
                // 合并需要保留的数据
                modelMapper.map(entity, latestEntity);
                return projectRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    // === 业务逻辑方法 ===

    @Override
    public ProjectResponse create(ProjectRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        
        // ProjectEntity entity = modelMapper.map(request, ProjectEntity.class);
        ProjectEntity entity = ProjectEntity.builder()
            .name(request.getName())
            .description(request.getDescription())
        .build();
        entity.setUid(uidUtils.getUid());
        entity.setUserUid(request.getUserUid());
        entity.setOrgUid(user.getOrgUid());

        ProjectEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create project failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ProjectResponse update(ProjectRequest request) {
        Optional<ProjectEntity> optional = projectRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ProjectEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ProjectEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update project failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Project not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ProjectEntity> optional = projectRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("Project not found");
        }
    }

    @Override
    public void delete(ProjectRequest request) {
        deleteByUid(request.getUid());
    }
}
