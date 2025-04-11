/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 14:26:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.task;

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
import com.bytedesk.kanban.todo_list.TodoListEntity;
import com.bytedesk.kanban.todo_list.TodoListRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TaskRestService extends BaseRestService<TaskEntity, TaskRequest, TaskResponse> {

    private final TaskRepository taskRepository;

    private final TodoListRepository todoListRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TaskResponse> queryByOrg(TaskRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TaskEntity> spec = TaskSpecification.search(request);
        Page<TaskEntity> page = taskRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TaskResponse> queryByUser(TaskRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "task", key = "#uid", unless="#result==null")
    @Override
    public Optional<TaskEntity> findByUid(String uid) {
        return taskRepository.findByUid(uid);
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        TaskEntity entity = modelMapper.map(request, TaskEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(user.getOrgUid());
        // 
        TaskEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create task failed");
        }
        // 
        Optional<TodoListEntity> todoListOptional = todoListRepository.findByUid(request.getTodoListUid());
        if (todoListOptional.isPresent()) {
            todoListOptional.get().getTasks().add(savedEntity);
            todoListRepository.save(todoListOptional.get());
        }
        // 
        return convertToResponse(savedEntity);
    }

    @Override
    public TaskResponse update(TaskRequest request) {
        Optional<TaskEntity> optional = taskRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TaskEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TaskEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update task failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Task not found");
        }
    }

    @Override
    public TaskEntity save(TaskEntity entity) {
        try {
            log.info("Attempting to save task: {}", entity.getName());
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected TaskEntity doSave(TaskEntity entity) {
        return taskRepository.save(entity);
    }

    @Override
    public TaskEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TaskEntity entity) {
        try {
            Optional<TaskEntity> latest = taskRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TaskEntity latestEntity = latest.get();
                // 合并需要保留的数据
                modelMapper.map(entity, latestEntity);
                return taskRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TaskEntity> optional = taskRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // taskRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Task not found");
        }
    }

    @Override
    public void delete(TaskRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TaskResponse convertToResponse(TaskEntity entity) {
        return modelMapper.map(entity, TaskResponse.class);
    }

    @Override
    public TaskResponse queryByUid(TaskRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
