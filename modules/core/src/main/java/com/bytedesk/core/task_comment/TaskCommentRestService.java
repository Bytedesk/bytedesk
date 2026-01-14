/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task_comment;

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
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.task.TaskEntity;
import com.bytedesk.core.task.TaskRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TaskCommentRestService extends BaseRestServiceWithExport<TaskCommentEntity, TaskCommentRequest, TaskCommentResponse, TaskCommentExcel> {

    private final TaskCommentRepository task_commentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    private final TaskRepository taskRepository;
    
    @Override
    public Page<TaskCommentEntity> queryByOrgEntity(TaskCommentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TaskCommentEntity> specs = TaskCommentSpecification.search(request, authService);
        return task_commentRepository.findAll(specs, pageable);
    }

    @Override
    public Page<TaskCommentResponse> queryByOrg(TaskCommentRequest request) {
        Page<TaskCommentEntity> task_commentPage = queryByOrgEntity(request);
        return task_commentPage.map(this::convertToResponse);
    }

    @Override
    public Page<TaskCommentResponse> queryByUser(TaskCommentRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    public Page<TaskCommentResponse> queryByTask(TaskCommentRequest request) {
        if (!StringUtils.hasText(request.getTaskUid())) {
            throw new RuntimeException("taskUid is required");
        }
        Pageable pageable = request.getPageable();
        Page<TaskCommentEntity> page = task_commentRepository.findByTaskUidAndDeletedFalse(request.getTaskUid(), pageable);
        return page.map(this::convertToResponse);
    }

    @Cacheable(value = "task_comment", key = "#uid", unless="#result==null")
    @Override
    public Optional<TaskCommentEntity> findByUid(String uid) {
        return task_commentRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return task_commentRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TaskCommentResponse create(TaskCommentRequest request) {
        // 任务评论：按 taskUid 直接创建，跳过“标签”式去重逻辑
        if (StringUtils.hasText(request.getTaskUid())) {
            return createTaskComment(request);
        }
        return createInternal(request, false);
    }

    private TaskCommentResponse createTaskComment(TaskCommentRequest request) {
        if (!StringUtils.hasText(request.getTaskUid())) {
            throw new RuntimeException("taskUid is required");
        }
        if (!StringUtils.hasText(request.getContent())) {
            throw new RuntimeException("content is required");
        }
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }

        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            request.setLevel(LevelEnum.USER.name());
        }

        TaskCommentEntity entity = modelMapper.map(request, TaskCommentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }

        TaskCommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create task comment failed");
        }

        // sync task.commentCount
        Optional<TaskEntity> taskOpt = taskRepository.findByUid(request.getTaskUid());
        if (taskOpt.isPresent()) {
            TaskEntity task = taskOpt.get();
            int cur = task.getCommentCount() == null ? 0 : task.getCommentCount();
            task.setCommentCount(cur + 1);
            taskRepository.save(task);
        }

        return convertToResponse(savedEntity);
    }

    @Transactional
    public TaskCommentResponse createSystemTaskComment(TaskCommentRequest request) {
        return createInternal(request, true);
    }

    private TaskCommentResponse createInternal(TaskCommentRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<TaskCommentEntity> task_comment = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (task_comment.isPresent()) {
        //         return convertToResponse(task_comment.get());
        //     }
        // }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(TaskCommentPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        TaskCommentEntity entity = modelMapper.map(request, TaskCommentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TaskCommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create task_comment failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TaskCommentResponse update(TaskCommentRequest request) {
        Optional<TaskCommentEntity> optional = task_commentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TaskCommentEntity entity = optional.get();

            // 任务评论：仅允许创建者更新（默认不开放编辑 UI，但后端兜底）
            if (StringUtils.hasText(entity.getTaskUid())) {
                UserEntity user = authService.getUser();
                if (user == null || !StringUtils.hasText(user.getUid()) || !user.getUid().equals(entity.getUserUid())) {
                    throw new RuntimeException("无权限更新该评论");
                }
            } else {
                // 标签/配置类：沿用原权限
                if (!permissionService.hasEntityPermission(TaskCommentPermissions.MODULE_NAME, "UPDATE", entity)) {
                    throw new RuntimeException("无权限更新该标签数据");
                }
            }
            
            modelMapper.map(request, entity);
            //
            TaskCommentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update task_comment failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TaskComment not found");
        }
    }

    @Override
    protected TaskCommentEntity doSave(TaskCommentEntity entity) {
        return task_commentRepository.save(entity);
    }

    @Override
    public TaskCommentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TaskCommentEntity entity) {
        try {
            Optional<TaskCommentEntity> latest = task_commentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TaskCommentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return task_commentRepository.save(latestEntity);
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
        Optional<TaskCommentEntity> optional = task_commentRepository.findByUid(uid);
        if (optional.isPresent()) {
            TaskCommentEntity entity = optional.get();

            // 任务评论：仅允许创建者删除
            if (StringUtils.hasText(entity.getTaskUid())) {
                UserEntity user = authService.getUser();
                if (user == null || !StringUtils.hasText(user.getUid()) || !user.getUid().equals(entity.getUserUid())) {
                    throw new RuntimeException("无权限删除该评论");
                }
            } else {
                // 标签/配置类：沿用原权限
                if (!permissionService.hasEntityPermission(TaskCommentPermissions.MODULE_NAME, "DELETE", entity)) {
                    throw new RuntimeException("无权限删除该标签数据");
                }
            }
            
            entity.setDeleted(true);
            save(entity);

            // sync task.commentCount
            if (StringUtils.hasText(entity.getTaskUid())) {
                Optional<TaskEntity> taskOpt = taskRepository.findByUid(entity.getTaskUid());
                if (taskOpt.isPresent()) {
                    TaskEntity task = taskOpt.get();
                    int cur = task.getCommentCount() == null ? 0 : task.getCommentCount();
                    task.setCommentCount(Math.max(0, cur - 1));
                    taskRepository.save(task);
                }
            }
            // task_commentRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TaskComment not found");
        }
    }

    @Override
    public void delete(TaskCommentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TaskCommentResponse convertToResponse(TaskCommentEntity entity) {
        return modelMapper.map(entity, TaskCommentResponse.class);
    }

    @Override
    public TaskCommentExcel convertToExcel(TaskCommentEntity entity) {
        return modelMapper.map(entity, TaskCommentExcel.class);
    }

    @Override
    protected Specification<TaskCommentEntity> createSpecification(TaskCommentRequest request) {
        return TaskCommentSpecification.search(request, authService);
    }

    @Override
    protected Page<TaskCommentEntity> executePageQuery(Specification<TaskCommentEntity> spec, Pageable pageable) {
        return task_commentRepository.findAll(spec, pageable);
    }
    
    public void initTaskComments(String orgUid) {
        // log.info("initTaskCommentTaskComment");
        // for (String task_comment : TaskCommentInitData.getAllTaskComments()) {
        //     TaskCommentRequest task_commentRequest = TaskCommentRequest.builder()
        //             .uid(Utils.formatUid(orgUid, task_comment))
        //             .name(task_comment)
        //             .order(0)
        //             .type(TaskCommentTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemTaskComment(task_commentRequest);
        // }
    }

    
    
}
