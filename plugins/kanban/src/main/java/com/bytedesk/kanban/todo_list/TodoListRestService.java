/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 12:08:50
 * @Description: 使用改进BaseRestService的TodoListRestService示例
 */
package com.bytedesk.kanban.todo_list;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceImproved;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kanban.module.ModuleEntity;
import com.bytedesk.kanban.module.ModuleRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TodoListRestService extends BaseRestServiceImproved<TodoListEntity, TodoListRequest, TodoListResponse> {

    private final TodoListRepository todoRepository;
    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    // === 实现必需的抽象方法 ===

    @Override
    protected Specification<TodoListEntity> createSpecification(TodoListRequest request) {
        return TodoListSpecification.search(request);
    }

    @Override
    protected Page<TodoListEntity> executePageQuery(Specification<TodoListEntity> spec, Pageable pageable) {
        return todoRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "todo", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TodoListEntity> findByUid(String uid) {
        return todoRepository.findByUid(uid);
    }

    @Override
    public TodoListResponse convertToResponse(TodoListEntity entity) {
        return modelMapper.map(entity, TodoListResponse.class);
    }

    @Override
    protected TodoListEntity doSave(TodoListEntity entity) {
        return todoRepository.save(entity);
    }

    @Override
    public TodoListEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TodoListEntity entity) {
        try {
            Optional<TodoListEntity> latest = todoRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TodoListEntity latestEntity = latest.get();
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
    public TodoListResponse create(TodoListRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        
        TodoListEntity entity = modelMapper.map(request, TodoListEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(user.getOrgUid());
        
        TodoListEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create todo failed");
        }
        
        // 处理与Module的关联
        handleModuleAssociation(request, savedEntity);
        
        return convertToResponse(savedEntity);
    }

    @Override
    public TodoListResponse update(TodoListRequest request) {
        Optional<TodoListEntity> optional = todoRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TodoListEntity entity = optional.get();
            modelMapper.map(request, entity);
            
            TodoListEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update todo failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("TodoList not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TodoListEntity> optional = todoRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("TodoList not found");
        }
    }

    @Override
    public void delete(TodoListRequest request) {
        deleteByUid(request.getUid());
    }

    // === 私有辅助方法 ===

    /**
     * 处理与Module的关联关系
     */
    private void handleModuleAssociation(TodoListRequest request, TodoListEntity savedEntity) {
        if (request.getModuleUid() != null) {
            Optional<ModuleEntity> moduleOptional = moduleRepository.findByUid(request.getModuleUid());
            if (moduleOptional.isPresent()) {
                moduleOptional.get().getTodoLists().add(savedEntity);
                moduleRepository.save(moduleOptional.get());
            }
        }
    }
}
