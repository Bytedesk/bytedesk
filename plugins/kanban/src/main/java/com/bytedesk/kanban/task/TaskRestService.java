/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-09 00:00:26
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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

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
    public TaskEntity save(TaskEntity entity) {
        log.info("Attempting to save task: {}", entity.getName());
        return taskRepository.save(entity);
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public TaskEntity recover(Exception e, TaskEntity entity) {
        log.error("Failed to save task after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save task after retries: " + e.getMessage());
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TaskEntity entity) {
        // TASK Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
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
