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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.bytedesk.core.relation.RelationEntity;
import com.bytedesk.core.relation.RelationRepository;
import com.bytedesk.core.relation.RelationTypeEnum;
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

    private final RelationRepository relationRepository;

    @Override
    protected Specification<TaskEntity> createSpecification(TaskRequest request) {
        return TaskSpecification.search(request, authService);
    }

    @Override
    protected Page<TaskEntity> executePageQuery(Specification<TaskEntity> spec, Pageable pageable) {
        return taskRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TaskResponse> queryByOrg(TaskRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TaskEntity> spec = createSpecification(request);
        Page<TaskEntity> page = executePageQuery(spec, pageable);

        UserEntity user = authService.getUser();
        if (user == null) {
            return page.map(this::convertToResponseWithoutUser);
        }

        List<String> taskUids = new ArrayList<>();
        for (TaskEntity e : page.getContent()) {
            if (StringUtils.hasText(e.getUid())) taskUids.add(e.getUid());
        }

        Set<String> likedUids = new HashSet<>();
        Set<String> favoritedUids = new HashSet<>();
        if (!taskUids.isEmpty()) {
            List<RelationEntity> relations = relationRepository
                    .findBySubjectUserUidAndObjectContentUidInAndTypeInAndDeletedFalse(
                            user.getUid(),
                            taskUids,
                            List.of(RelationTypeEnum.LIKE.name(), RelationTypeEnum.FAVORITE.name()));
            for (RelationEntity r : relations) {
                if (!StringUtils.hasText(r.getObjectContentUid())) continue;
                if (RelationTypeEnum.LIKE.name().equalsIgnoreCase(r.getType())) {
                    likedUids.add(r.getObjectContentUid());
                } else if (RelationTypeEnum.FAVORITE.name().equalsIgnoreCase(r.getType())) {
                    favoritedUids.add(r.getObjectContentUid());
                }
            }
        }

        return page.map(entity -> convertToResponseWithFlags(entity, likedUids.contains(entity.getUid()), favoritedUids.contains(entity.getUid())));
    }

    @Override
    public TaskResponse queryByUid(TaskRequest request) {
        Optional<TaskEntity> optional = findByUid(request.getUid());
        if (optional.isEmpty()) {
            throw new RuntimeException("Task not found");
        }
        TaskEntity entity = optional.get();

        UserEntity user = authService.getUser();
        boolean liked = false;
        boolean favorited = false;
        if (user != null && StringUtils.hasText(user.getUid())) {
            liked = Boolean.TRUE.equals(relationRepository.hasLiked(user.getUid(), entity.getUid()));
            favorited = Boolean.TRUE.equals(relationRepository.hasFavorited(user.getUid(), entity.getUid()));
        }
        return convertToResponseWithFlags(entity, liked, favorited);
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
        // queryByOrg/queryByUid 已经做了批量/单条 enrich；这里作为兜底
        return convertToResponseWithoutUser(entity);
    }

    private TaskResponse convertToResponseWithoutUser(TaskEntity entity) {
        return convertToResponseWithFlags(entity, false, false);
    }

    private TaskResponse convertToResponseWithFlags(TaskEntity entity, boolean liked, boolean favorited) {
        TaskResponse resp = modelMapper.map(entity, TaskResponse.class);
        resp.setCommentCount(entity.getCommentCount());
        resp.setLikeCount(entity.getLikeCount());
        resp.setFavoriteCount(entity.getFavoriteCount());
        resp.setLiked(liked);
        resp.setFavorited(favorited);
        return resp;
    }

    @Transactional
    public TaskResponse like(TaskRequest request) {
        return toggleRelation(request, RelationTypeEnum.LIKE.name(), true);
    }

    @Transactional
    public TaskResponse unlike(TaskRequest request) {
        return toggleRelation(request, RelationTypeEnum.LIKE.name(), false);
    }

    @Transactional
    public TaskResponse favorite(TaskRequest request) {
        return toggleRelation(request, RelationTypeEnum.FAVORITE.name(), true);
    }

    @Transactional
    public TaskResponse unfavorite(TaskRequest request) {
        return toggleRelation(request, RelationTypeEnum.FAVORITE.name(), false);
    }

    private TaskResponse toggleRelation(TaskRequest request, String relationType, boolean enable) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException("Task uid is required");
        }
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException("Unauthorized");
        }

        TaskEntity task = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("Task not found"));

        Optional<RelationEntity> existing = relationRepository
                .findBySubjectUserUidAndObjectContentUidAndTypeAndDeletedFalse(user.getUid(), task.getUid(), relationType);

        if (enable) {
            if (existing.isEmpty()) {
                RelationEntity relation = RelationEntity.builder()
                        .uid(uidUtils.getUid())
                        .orgUid(task.getOrgUid())
                        .userUid(user.getUid())
                        .type(relationType)
                        .subjectUserUid(user.getUid())
                        .objectContentUid(task.getUid())
                        .build();
                relationRepository.save(relation);

                if (RelationTypeEnum.LIKE.name().equals(relationType)) {
                    task.setLikeCount((task.getLikeCount() == null ? 0 : task.getLikeCount()) + 1);
                } else if (RelationTypeEnum.FAVORITE.name().equals(relationType)) {
                    task.setFavoriteCount((task.getFavoriteCount() == null ? 0 : task.getFavoriteCount()) + 1);
                }
                save(task);
            }
            return convertToResponseWithFlags(task, RelationTypeEnum.LIKE.name().equals(relationType) || Boolean.TRUE.equals(relationRepository.hasLiked(user.getUid(), task.getUid())),
                    RelationTypeEnum.FAVORITE.name().equals(relationType) || Boolean.TRUE.equals(relationRepository.hasFavorited(user.getUid(), task.getUid())));
        }

        if (existing.isPresent()) {
            RelationEntity rel = existing.get();
            rel.setDeleted(true);
            relationRepository.save(rel);

            if (RelationTypeEnum.LIKE.name().equals(relationType)) {
                int cur = task.getLikeCount() == null ? 0 : task.getLikeCount();
                task.setLikeCount(Math.max(0, cur - 1));
            } else if (RelationTypeEnum.FAVORITE.name().equals(relationType)) {
                int cur = task.getFavoriteCount() == null ? 0 : task.getFavoriteCount();
                task.setFavoriteCount(Math.max(0, cur - 1));
            }
            save(task);
        }

        boolean liked = Boolean.TRUE.equals(relationRepository.hasLiked(user.getUid(), task.getUid()));
        boolean favorited = Boolean.TRUE.equals(relationRepository.hasFavorited(user.getUid(), task.getUid()));
        return convertToResponseWithFlags(task, liked, favorited);
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
