/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 12:08:50
 * @Description: 使用改进BaseRestService的ProjectTodoRestService示例
 */
package com.bytedesk.kanban.project_todo;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kanban.module.ModuleEntity;
import com.bytedesk.kanban.module.ModuleRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProjectTodoRestService extends BaseRestService<ProjectTodoEntity, ProjectTodoRequest, ProjectTodoResponse> {

    private final ProjectTodoRepository todoRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    // === 实现必需的抽象方法 ===

    @Override
    protected Specification<ProjectTodoEntity> createSpecification(ProjectTodoRequest request) {
        return ProjectTodoSpecification.search(request, authService);
    }

    @Override
    protected Page<ProjectTodoEntity> executePageQuery(Specification<ProjectTodoEntity> spec, Pageable pageable) {
        return todoRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "todo", key = "#uid", unless = "#result == null")
    @Override
    public Optional<ProjectTodoEntity> findByUid(String uid) {
        return todoRepository.findByUid(uid);
    }

    @Override
    public ProjectTodoResponse convertToResponse(ProjectTodoEntity entity) {
        return modelMapper.map(entity, ProjectTodoResponse.class);
    }

    @Override
    protected ProjectTodoEntity doSave(ProjectTodoEntity entity) {
        return todoRepository.save(entity);
    }

    @Override
    public ProjectTodoEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ProjectTodoEntity entity) {
        try {
            Optional<ProjectTodoEntity> latest = todoRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ProjectTodoEntity latestEntity = latest.get();
                // 合并需要保留的数据
                modelMapper.map(entity, latestEntity);
                return todoRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    // === 业务逻辑方法 ===

    @Override
    public ProjectTodoResponse create(ProjectTodoRequest request) {
        UserEntity user = authService.getUser();
        
        request.setUserUid(user.getUid());
        
        ProjectTodoEntity entity = modelMapper.map(request, ProjectTodoEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(user.getOrgUid());
        
        ProjectTodoEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create todo failed");
        }
        
        // 处理与Module的关联
        handleModuleAssociation(request, savedEntity);
        
        return convertToResponse(savedEntity);
    }

    @Override
    public ProjectTodoResponse update(ProjectTodoRequest request) {
        Optional<ProjectTodoEntity> optional = todoRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ProjectTodoEntity entity = optional.get();
            modelMapper.map(request, entity);
            
            ProjectTodoEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update todo failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("ProjectTodo not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ProjectTodoEntity> optional = todoRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("ProjectTodo not found");
        }
    }

    @Override
    public void delete(ProjectTodoRequest request) {
        deleteByUid(request.getUid());
    }

    // === 私有辅助方法 ===

    /**
     * 处理与Module的关联关系
     */
    private void handleModuleAssociation(ProjectTodoRequest request, ProjectTodoEntity savedEntity) {
        if (request.getModuleUid() != null) {
            Optional<ModuleEntity> moduleOptional = moduleRepository.findByUid(request.getModuleUid());
            if (moduleOptional.isPresent()) {
                moduleOptional.get().getTodoLists().add(savedEntity);
                moduleRepository.save(moduleOptional.get());
            }
        }
    }
}
