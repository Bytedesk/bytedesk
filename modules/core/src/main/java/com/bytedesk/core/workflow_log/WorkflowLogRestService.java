/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:42:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_log;

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
public class WorkflowLogRestService extends BaseRestServiceWithExport<WorkflowLogEntity, WorkflowLogRequest, WorkflowLogResponse, WorkflowLogExcel> {

    private final WorkflowLogRepository workflowResultRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<WorkflowLogEntity> queryByOrgEntity(WorkflowLogRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkflowLogEntity> spec = WorkflowLogSpecification.search(request, authService);
        return workflowResultRepository.findAll(spec, pageable);
    }

    @Override
    public Page<WorkflowLogResponse> queryByOrg(WorkflowLogRequest request) {
        Page<WorkflowLogEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<WorkflowLogResponse> queryByUser(WorkflowLogRequest request) {
        UserEntity user = authService.getUser();
        
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public WorkflowLogResponse queryByUid(WorkflowLogRequest request) {
        Optional<WorkflowLogEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowLogEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("WorkflowLog not found");
        }
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowLogEntity> findByUid(String uid) {
        return workflowResultRepository.findByUid(uid);
    }

    @Cacheable(value = "tag", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<WorkflowLogEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return workflowResultRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return workflowResultRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WorkflowLogResponse create(WorkflowLogRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<WorkflowLogEntity> tag = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tag.isPresent()) {
                return convertToResponse(tag.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        WorkflowLogEntity entity = modelMapper.map(request, WorkflowLogEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorkflowLogEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WorkflowLogResponse update(WorkflowLogRequest request) {
        Optional<WorkflowLogEntity> optional = workflowResultRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowLogEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WorkflowLogEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorkflowLog not found");
        }
    }

    @Override
    protected WorkflowLogEntity doSave(WorkflowLogEntity entity) {
        return workflowResultRepository.save(entity);
    }

    @Override
    public WorkflowLogEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowLogEntity entity) {
        try {
            Optional<WorkflowLogEntity> latest = workflowResultRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkflowLogEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return workflowResultRepository.save(latestEntity);
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
        Optional<WorkflowLogEntity> optional = workflowResultRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorkflowLog not found");
        }
    }

    @Override
    public void delete(WorkflowLogRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowLogResponse convertToResponse(WorkflowLogEntity entity) {
        return modelMapper.map(entity, WorkflowLogResponse.class);
    }

    @Override
    public WorkflowLogExcel convertToExcel(WorkflowLogEntity entity) {
        return modelMapper.map(entity, WorkflowLogExcel.class);
    }

    @Override
    protected Specification<WorkflowLogEntity> createSpecification(WorkflowLogRequest request) {
        return WorkflowLogSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkflowLogEntity> executePageQuery(Specification<WorkflowLogEntity> spec, Pageable pageable) {
        return workflowResultRepository.findAll(spec, pageable);
    }
    
}
