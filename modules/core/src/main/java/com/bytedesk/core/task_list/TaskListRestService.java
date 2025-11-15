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
package com.bytedesk.core.task_list;

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
public class TaskListRestService extends BaseRestServiceWithExport<TaskListEntity, TaskListRequest, TaskListResponse, TaskListExcel> {

    private final TaskListRepository taskListRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<TaskListEntity> createSpecification(TaskListRequest request) {
        return TaskListSpecification.search(request, authService);
    }

    @Override
    protected Page<TaskListEntity> executePageQuery(Specification<TaskListEntity> spec, Pageable pageable) {
        return taskListRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "task_list", key = "#uid", unless="#result==null")
    @Override
    public Optional<TaskListEntity> findByUid(String uid) {
        return taskListRepository.findByUid(uid);
    }

    @Cacheable(value = "task_list", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TaskListEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return taskListRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return taskListRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TaskListResponse create(TaskListRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TaskListEntity> task_list = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (task_list.isPresent()) {
                return convertToResponse(task_list.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TaskListEntity entity = modelMapper.map(request, TaskListEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TaskListEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create task_list failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TaskListResponse update(TaskListRequest request) {
        Optional<TaskListEntity> optional = taskListRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TaskListEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TaskListEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update task_list failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TaskList not found");
        }
    }

    @Override
    protected TaskListEntity doSave(TaskListEntity entity) {
        return taskListRepository.save(entity);
    }

    @Override
    public TaskListEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TaskListEntity entity) {
        try {
            Optional<TaskListEntity> latest = taskListRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TaskListEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return taskListRepository.save(latestEntity);
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
        Optional<TaskListEntity> optional = taskListRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // task_listRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TaskList not found");
        }
    }

    @Override
    public void delete(TaskListRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TaskListResponse convertToResponse(TaskListEntity entity) {
        return modelMapper.map(entity, TaskListResponse.class);
    }

    @Override
    public TaskListExcel convertToExcel(TaskListEntity entity) {
        return modelMapper.map(entity, TaskListExcel.class);
    }
    
    public void initTaskLists(String orgUid) {
        // log.info("initThreadTaskList");
        // for (String task_list : TaskListInitData.getAllTaskLists()) {
        //     TaskListRequest task_listRequest = TaskListRequest.builder()
        //             .uid(Utils.formatUid(orgUid, task_list))
        //             .name(task_list)
        //             .order(0)
        //             .type(TaskListTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(task_listRequest);
        // }
    }

    
    
}
