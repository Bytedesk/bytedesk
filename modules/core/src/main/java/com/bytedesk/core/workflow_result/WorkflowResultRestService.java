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
package com.bytedesk.core.workflow_result;

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
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkflowResultRestService extends BaseRestServiceWithExcel<WorkflowResultEntity, WorkflowResultRequest, WorkflowResultResponse, WorkflowResultExcel> {

    private final WorkflowResultRepository workflowResultRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<WorkflowResultEntity> queryByOrgEntity(WorkflowResultRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkflowResultEntity> spec = WorkflowResultSpecification.search(request);
        return workflowResultRepository.findAll(spec, pageable);
    }

    @Override
    public Page<WorkflowResultResponse> queryByOrg(WorkflowResultRequest request) {
        Page<WorkflowResultEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<WorkflowResultResponse> queryByUser(WorkflowResultRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public WorkflowResultResponse queryByUid(WorkflowResultRequest request) {
        Optional<WorkflowResultEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowResultEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("WorkflowResult not found");
        }
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowResultEntity> findByUid(String uid) {
        return workflowResultRepository.findByUid(uid);
    }

    @Cacheable(value = "tag", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<WorkflowResultEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return workflowResultRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return workflowResultRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WorkflowResultResponse create(WorkflowResultRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<WorkflowResultEntity> tag = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
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
        WorkflowResultEntity entity = modelMapper.map(request, WorkflowResultEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorkflowResultEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WorkflowResultResponse update(WorkflowResultRequest request) {
        Optional<WorkflowResultEntity> optional = workflowResultRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowResultEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WorkflowResultEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorkflowResult not found");
        }
    }

    @Override
    protected WorkflowResultEntity doSave(WorkflowResultEntity entity) {
        return workflowResultRepository.save(entity);
    }

    @Override
    public WorkflowResultEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowResultEntity entity) {
        try {
            Optional<WorkflowResultEntity> latest = workflowResultRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkflowResultEntity latestEntity = latest.get();
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
        Optional<WorkflowResultEntity> optional = workflowResultRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorkflowResult not found");
        }
    }

    @Override
    public void delete(WorkflowResultRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowResultResponse convertToResponse(WorkflowResultEntity entity) {
        return modelMapper.map(entity, WorkflowResultResponse.class);
    }

    @Override
    public WorkflowResultExcel convertToExcel(WorkflowResultEntity entity) {
        return modelMapper.map(entity, WorkflowResultExcel.class);
    }

    @Override
    protected Specification<WorkflowResultEntity> createSpecification(WorkflowResultRequest request) {
        return WorkflowResultSpecification.search(request);
    }

    @Override
    protected Page<WorkflowResultEntity> executePageQuery(Specification<WorkflowResultEntity> spec, Pageable pageable) {
        return workflowResultRepository.findAll(spec, pageable);
    }
    
}
