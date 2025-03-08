/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 18:16:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.todo;

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
public class TodoRestService extends BaseRestService<TodoEntity, TodoRequest, TodoResponse> {

    private final TodoRepository todoRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TodoResponse> queryByOrg(TodoRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TodoEntity> spec = TodoSpecification.search(request);
        Page<TodoEntity> page = todoRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TodoResponse> queryByUser(TodoRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "todo", key = "#uid", unless="#result==null")
    @Override
    public Optional<TodoEntity> findByUid(String uid) {
        return todoRepository.findByUid(uid);
    }

    @Override
    public TodoResponse create(TodoRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        
        TodoEntity entity = modelMapper.map(request, TodoEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        entity.setOrgUid(user.getOrgUid());

        TodoEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create todo failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TodoResponse update(TodoRequest request) {
        Optional<TodoEntity> optional = todoRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TodoEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TodoEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update todo failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Todo not found");
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
    public TodoEntity save(TodoEntity entity) {
        log.info("Attempting to save todo: {}", entity.getName());
        return todoRepository.save(entity);
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public TodoEntity recover(Exception e, TodoEntity entity) {
        log.error("Failed to save todo after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save todo after retries: " + e.getMessage());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TodoEntity> optional = todoRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // todoRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Todo not found");
        }
    }

    @Override
    public void delete(TodoRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TodoEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TodoResponse convertToResponse(TodoEntity entity) {
        return modelMapper.map(entity, TodoResponse.class);
    }
    
}
