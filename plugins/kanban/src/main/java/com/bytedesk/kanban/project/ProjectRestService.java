/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 22:16:37
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
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

    private final AuthService authService;

    @Override
    public Page<ProjectResponse> queryByOrg(ProjectRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ProjectEntity> spec = ProjectSpecification.search(request);
        Page<ProjectEntity> page = projectRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ProjectResponse> queryByUser(ProjectRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public ProjectResponse queryByUid(ProjectRequest request) {
        Optional<ProjectEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        return null;
    }

    @Cacheable(value = "project", key = "#uid", unless="#result==null")
    @Override
    public Optional<ProjectEntity> findByUid(String uid) {
        return projectRepository.findByUid(uid);
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        
        // ProjectEntity entity = modelMapper.map(request, ProjectEntity.class);
        ProjectEntity entity = ProjectEntity.builder()
            .name(request.getName())
            .description(request.getDescription())
        .build();
        entity.setUid(uidUtils.getUid());
        // 
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

    /**
     * 保存标签，失败时自动重试
     * maxAttempts: 最大重试次数（包括第一次尝试）
     * backoff: 重试延迟，multiplier是延迟倍数
     * recover: 当重试次数用完后的回调方法
     */
    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public ProjectEntity save(ProjectEntity entity) {
        log.info("Attempting to save project: {}", entity.getName());
        return projectRepository.save(entity);
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public ProjectEntity recover(Exception e, ProjectEntity entity) {
        log.error("Failed to save project after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save project after retries: " + e.getMessage());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ProjectEntity> optional = projectRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // projectRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Project not found");
        }
    }

    @Override
    public void delete(ProjectRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ProjectEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ProjectResponse convertToResponse(ProjectEntity entity) {
        return modelMapper.map(entity, ProjectResponse.class);
    }
    
}
