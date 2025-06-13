/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 21:49:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.webrtc;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FreeswitchWebRTCRestService extends BaseRestServiceWithExcel<FreeswitchWebRTCEntity, FreeswitchWebRTCRequest, FreeswitchWebRTCResponse, FreeswitchWebRTCExcel> {

    private final FreeswitchWebRTCRepository webrtcRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FreeswitchWebRTCEntity> queryByOrgEntity(FreeswitchWebRTCRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FreeswitchWebRTCEntity> spec = FreeswitchWebRTCSpecification.search(request);
        return webrtcRepository.findAll(spec, pageable);
    }

    @Override
    public Page<FreeswitchWebRTCResponse> queryByOrg(FreeswitchWebRTCRequest request) {
        Page<FreeswitchWebRTCEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FreeswitchWebRTCResponse> queryByUser(FreeswitchWebRTCRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public FreeswitchWebRTCResponse queryByUid(FreeswitchWebRTCRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "webrtc", key = "#uid", unless="#result==null")
    @Override
    public Optional<FreeswitchWebRTCEntity> findByUid(String uid) {
        return webrtcRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return webrtcRepository.existsByUid(uid);
    }

    @Override
    public FreeswitchWebRTCResponse create(FreeswitchWebRTCRequest request) {
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
        FreeswitchWebRTCEntity entity = modelMapper.map(request, FreeswitchWebRTCEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        FreeswitchWebRTCEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create webrtc failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FreeswitchWebRTCResponse update(FreeswitchWebRTCRequest request) {
        Optional<FreeswitchWebRTCEntity> optional = webrtcRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FreeswitchWebRTCEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FreeswitchWebRTCEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webrtc failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("FreeswitchWebRTC not found");
        }
    }

    @Override
    protected FreeswitchWebRTCEntity doSave(FreeswitchWebRTCEntity entity) {
        return webrtcRepository.save(entity);
    }

    @Override
    public FreeswitchWebRTCEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FreeswitchWebRTCEntity entity) {
        try {
            Optional<FreeswitchWebRTCEntity> latest = webrtcRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FreeswitchWebRTCEntity latestEntity = latest.get();
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
        Optional<FreeswitchWebRTCEntity> optional = webrtcRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // webrtcRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("FreeswitchWebRTC not found");
        }
    }

    @Override
    public void delete(FreeswitchWebRTCRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FreeswitchWebRTCResponse convertToResponse(FreeswitchWebRTCEntity entity) {
        return modelMapper.map(entity, FreeswitchWebRTCResponse.class);
    }

    @Override
    public FreeswitchWebRTCExcel convertToExcel(FreeswitchWebRTCEntity entity) {
        return modelMapper.map(entity, FreeswitchWebRTCExcel.class);
    }
    
    
}
