/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:43:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.webrtc;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CallWebRTCRestService extends BaseRestServiceWithExport<CallWebRTCEntity, CallWebRTCRequest, CallWebRTCResponse, CallWebRTCExcel> {

    private final CallWebRTCRepository webrtcRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<CallWebRTCEntity> createSpecification(CallWebRTCRequest request) {
        return CallWebRTCSpecification.search(request, authService);
    }

    @Override
    protected Page<CallWebRTCEntity> executePageQuery(Specification<CallWebRTCEntity> spec, Pageable pageable) {
        return webrtcRepository.findAll(spec, pageable);
    }

    @Override
    public Page<CallWebRTCEntity> queryByOrgEntity(CallWebRTCRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallWebRTCEntity> spec = CallWebRTCSpecification.search(request, authService);
        return webrtcRepository.findAll(spec, pageable);
    }

    @Override
    public Page<CallWebRTCResponse> queryByOrg(CallWebRTCRequest request) {
        Page<CallWebRTCEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CallWebRTCResponse> queryByUser(CallWebRTCRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public CallWebRTCResponse queryByUid(CallWebRTCRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "webrtc", key = "#uid", unless="#result==null")
    @Override
    public Optional<CallWebRTCEntity> findByUid(String uid) {
        return webrtcRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return webrtcRepository.existsByUid(uid);
    }

    @Override
    public CallWebRTCResponse create(CallWebRTCRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        CallWebRTCEntity entity = modelMapper.map(request, CallWebRTCEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CallWebRTCEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create webrtc failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public CallWebRTCResponse update(CallWebRTCRequest request) {
        Optional<CallWebRTCEntity> optional = webrtcRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CallWebRTCEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            CallWebRTCEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webrtc failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("CallWebRTC not found");
        }
    }

    @Override
    protected CallWebRTCEntity doSave(CallWebRTCEntity entity) {
        return webrtcRepository.save(entity);
    }

    @Override
    public CallWebRTCEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallWebRTCEntity entity) {
        try {
            Optional<CallWebRTCEntity> latest = webrtcRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallWebRTCEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return webrtcRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CallWebRTCEntity> optional = webrtcRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // webrtcRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("CallWebRTC not found");
        }
    }

    @Override
    public void delete(CallWebRTCRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CallWebRTCResponse convertToResponse(CallWebRTCEntity entity) {
        return modelMapper.map(entity, CallWebRTCResponse.class);
    }

    @Override
    public CallWebRTCExcel convertToExcel(CallWebRTCEntity entity) {
        return modelMapper.map(entity, CallWebRTCExcel.class);
    }
    
    
}
