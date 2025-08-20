/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:46:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 12:52:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorktimeService extends BaseRestService<WorktimeEntity, WorktimeRequest, WorktimeResponse> {

    private final WorktimeRepository worktimeRepository;

    private final UidUtils uidUtils;

    // private final ModelMapper modelMapper;

    @Override
    public Page<WorktimeResponse> queryByOrg(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<WorktimeResponse> queryByUser(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<WorktimeEntity> findByUid(String uid) {
        return worktimeRepository.findByUid(uid);
    }

    @Override
    public WorktimeResponse create(WorktimeRequest request) {

        // Worktime worktime = modelMapper.map(request, Worktime.class);
        WorktimeEntity worktime = WorktimeEntity.builder()
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .build();
        worktime.setUid(uidUtils.getCacheSerialUid());

        return convertToResponse(save(worktime));
    }

    @Override
    public WorktimeResponse update(WorktimeRequest request) {

        Optional<WorktimeEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WorktimeEntity worktime = optional.get();
            // modelMapper.map(request, worktime);
            worktime.setStartTime(request.getStartTime());
            worktime.setEndTime(request.getEndTime());
            // 
            return convertToResponse(save(worktime));
        } else {
            throw new RuntimeException("Worktime not found");
        }
    }

    @Override
    public WorktimeEntity save(WorktimeEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }
    
    @Override
    protected WorktimeEntity doSave(WorktimeEntity entity) {
        return worktimeRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WorktimeEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            WorktimeEntity worktime = optional.get();
            worktime.setDeleted(true);
            // worktimeRepository.delete(worktime);
            save(worktime);
        }
    }

    @Override
    public void delete(WorktimeRequest entity) {
        // worktimeRepository.delete(entity);
    }

    @Override
    public WorktimeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorktimeEntity entity) {
        try {
            Optional<WorktimeEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorktimeEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setStartTime(entity.getStartTime());
                latestEntity.setEndTime(entity.getEndTime());
                return worktimeRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public WorktimeResponse convertToResponse(WorktimeEntity entity) {
        // return modelMapper.map(entity, WorktimeResponse.class);
        WorktimeResponse worktimeResponse = WorktimeResponse.builder()
                // .uid(entity.getUid())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
        worktimeResponse.setUid(entity.getUid());
        return worktimeResponse;
    }

    public String createDefault() {

        WorktimeRequest worktimeRequest = WorktimeRequest.builder()
                .startTime("00:00:00")
                .endTime("23:59:59")
                .build();

        WorktimeResponse worktimeResponse = create(worktimeRequest);

        return worktimeResponse.getUid();
    }

    @Override
    protected Specification<WorktimeEntity> createSpecification(WorktimeRequest request) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and();
    }

    @Override
    protected Page<WorktimeEntity> executePageQuery(Specification<WorktimeEntity> spec, Pageable pageable) {
        return worktimeRepository.findAll(pageable);
    }

}
