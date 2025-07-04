/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 23:24:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.module;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kanban.project.ProjectEntity;
import com.bytedesk.kanban.project.ProjectRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ModuleRestService extends BaseRestService<ModuleEntity, ModuleRequest, ModuleResponse> {

    private final ModuleRepository moduleRepository;

    private final ProjectRepository projectRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<ModuleResponse> queryByOrg(ModuleRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ModuleEntity> spec = ModuleSpecification.search(request);
        Page<ModuleEntity> page = moduleRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ModuleResponse> queryByUser(ModuleRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Cacheable(value = "module", key = "#uid", unless = "#result==null")
    @Override
    public Optional<ModuleEntity> findByUid(String uid) {
        return moduleRepository.findByUid(uid);
    }

    @Override
    public ModuleResponse initVisitor(ModuleRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());

        // ModuleEntity entity = modelMapper.map(request, ModuleEntity.class);
        ModuleEntity entity = ModuleEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .projectUid(request.getProjectUid())
                .build();
        entity.setUid(uidUtils.getUid());
        entity.setUserUid(user.getUid());
        entity.setOrgUid(user.getOrgUid());
        //
        ModuleEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create module failed");
        }
        //
        Optional<ProjectEntity> projectOptional = projectRepository.findByUid(request.getProjectUid());
        if (projectOptional.isPresent()) {
            projectOptional.get().getModules().add(savedEntity);
            projectRepository.save(projectOptional.get());
        }
        //
        return convertToResponse(savedEntity);
    }

    @Override
    public ModuleResponse update(ModuleRequest request) {
        Optional<ModuleEntity> optional = moduleRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ModuleEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ModuleEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update module failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Module not found");
        }
    }

    /**
     * 保存标签，失败时自动重试
     * maxAttempts: 最大重试次数（包括第一次尝试）
     * backoff: 重试延迟，multiplier是延迟倍数
     */
    @Override
    public ModuleEntity save(ModuleEntity entity) {
        try {
            log.info("Attempting to save module: {}", entity.getName());
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected ModuleEntity doSave(ModuleEntity entity) {
        return moduleRepository.save(entity);
    }

    @Override
    public ModuleEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ModuleEntity entity) {
        try {
            Optional<ModuleEntity> latest = moduleRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ModuleEntity latestEntity = latest.get();
                // 合并需要保留的数据
                modelMapper.map(entity, latestEntity);
                return moduleRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ModuleEntity> optional = moduleRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // moduleRepository.delete(optional.get());
        } else {
            throw new RuntimeException("Module not found");
        }
    }

    @Override
    public void delete(ModuleRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ModuleResponse convertToResponse(ModuleEntity entity) {
        return modelMapper.map(entity, ModuleResponse.class);
    }

    @Override
    public ModuleResponse queryByUid(ModuleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

}
