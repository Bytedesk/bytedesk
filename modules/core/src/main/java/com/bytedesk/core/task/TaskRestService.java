/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TaskRestService extends BaseRestServiceWithExport<TaskEntity, TaskRequest, TaskResponse, TaskExcel> {

    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<TaskEntity> createSpecification(TaskRequest request) {
        return TaskSpecification.search(request, authService);
    }

    @Override
    protected Page<TaskEntity> executePageQuery(Specification<TaskEntity> spec, Pageable pageable) {
        return taskRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "task", key = "#uid", unless="#result==null")
    @Override
    public Optional<TaskEntity> findByUid(String uid) {
        return taskRepository.findByUid(uid);
    }

    @Cacheable(value = "task", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TaskEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return taskRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return taskRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TaskResponse create(TaskRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TaskEntity> task = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (task.isPresent()) {
                return convertToResponse(task.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TaskEntity entity = modelMapper.map(request, TaskEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TaskEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create task failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
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
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return taskRepository.save(latestEntity);
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
    public TaskExcel convertToExcel(TaskEntity entity) {
        return modelMapper.map(entity, TaskExcel.class);
    }
    
    public void initTasks(String orgUid) {
        // log.info("initThreadTask");
        // for (String task : TaskInitData.getAllTasks()) {
        //     TaskRequest taskRequest = TaskRequest.builder()
        //             .uid(Utils.formatUid(orgUid, task))
        //             .name(task)
        //             .order(0)
        //             .type(TaskTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(taskRequest);
        // }
    }

    
    
}
