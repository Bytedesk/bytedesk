/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 09:35:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CallCdrRestService extends BaseRestServiceWithExcel<CallCdrEntity, CallCdrRequest, CallCdrResponse, CallCdrExcel> {

    private final CallCdrRepository callCdrRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<CallCdrEntity> createSpecification(CallCdrRequest request) {
        return CallCdrSpecification.search(request);
    }

    @Override
    protected Page<CallCdrEntity> executePageQuery(Specification<CallCdrEntity> spec, Pageable pageable) {
        return callCdrRepository.findAll(spec, pageable);
    }

    @Override
    public Page<CallCdrEntity> queryByOrgEntity(CallCdrRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallCdrEntity> specification = CallCdrSpecification.search(request);
        return callCdrRepository.findAll(specification, pageable);
    }

    @Override
    public Page<CallCdrResponse> queryByOrg(CallCdrRequest request) {
        Page<CallCdrEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CallCdrResponse> queryByUser(CallCdrRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }
    
    @Override
    @Cacheable(value = "freeswitch_cdr", key = "#uid", unless = "#result == null")
    public CallCdrResponse queryByUid(CallCdrRequest request) {
        Optional<CallCdrEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("Call CDR不存在");
    }

    @Cacheable(value = "freeswitch_cdr", key = "#uid", unless = "#result == null")
    @Override
    public Optional<CallCdrEntity> findByUid(String uid) {
        return callCdrRepository.findByUid(uid);
    }
    @Cacheable(value = "freeswitch_cdr_org", key = "#orgUid", unless = "#result == null")
    @Override
    public List<CallCdrEntity> findByOrgUid(String orgUid) {
        return callCdrRepository.findByOrgUid(orgUid);
    }

    @Override
    public CallCdrResponse create(CallCdrRequest request) {
        UserEntity user = authService.getUser();
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (!StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getUid());
        }

        CallCdrEntity entity = modelMapper.map(request, CallCdrEntity.class);
        // 
        CallCdrEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建Call CDR失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public CallCdrResponse update(CallCdrRequest request) {
        Optional<CallCdrEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("Call CDR不存在");
        }

        CallCdrEntity entity = optional.get();
        
        // CDR记录通常不允许修改，这里只是示例
        // 实际应用中，CDR记录通常是只读的
        
        CallCdrEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新Call CDR失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public CallCdrEntity doSave(CallCdrEntity entity) {
        return callCdrRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CallCdrEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }
    
    @Override
    public void deleteByOrgUid(String orgUid) {
        List<CallCdrEntity> entities = findByOrgUid(orgUid);
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                entity.setDeleted(true);
                save(entity);
            });
        }
    }

    @Override
    public void delete(CallCdrRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CallCdrEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallCdrEntity entity) {
        log.warn("Call CDR保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        try {
            Optional<CallCdrEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallCdrEntity latestEntity = latest.get();
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }
    
    @Override
    public CallCdrResponse convertToResponse(CallCdrEntity entity) {
        return modelMapper.map(entity, CallCdrResponse.class);
    }

    @Override
    public CallCdrExcel convertToExcel(CallCdrEntity entity) {
        return modelMapper.map(entity, CallCdrExcel.class);
    }

}
