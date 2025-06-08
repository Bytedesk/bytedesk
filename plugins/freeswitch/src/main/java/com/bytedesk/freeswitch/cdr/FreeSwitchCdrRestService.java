/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 19:50:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.cdr;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FreeSwitchCdrRestService extends BaseRestServiceWithExcel<FreeSwitchCdrEntity, FreeSwitchCdrRequest, FreeSwitchCdrResponse, FreeSwitchCdrExcel> {

    private final FreeSwitchCdrRepository freeSwitchCdrRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FreeSwitchCdrResponse> queryByOrg(FreeSwitchCdrRequest request) {
        return queryByOrgEntity(request).map(this::convertToResponse);
    }

    @Override
    public Page<FreeSwitchCdrResponse> queryByUser(FreeSwitchCdrRequest request) {
        return queryByUserEntity(request).map(this::convertToResponse);
    }

    @Override
    public Page<FreeSwitchCdrEntity> queryByOrgEntity(FreeSwitchCdrRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FreeSwitchCdrEntity> specification = FreeSwitchCdrSpecification.search(request);
        return freeSwitchCdrRepository.findAll(specification, pageable);
    }

    public Page<FreeSwitchCdrEntity> queryByUserEntity(FreeSwitchCdrRequest request) {

        UserEntity user = authService.getUser();
        request.setOrgUid(user.getOrgUid());

        return queryByOrgEntity(request);
    }

    @Override
    public Optional<FreeSwitchCdrEntity> findByUid(String uid) {
        return freeSwitchCdrRepository.findByUid(uid);
    }

    @Override
    public FreeSwitchCdrResponse convertToResponse(FreeSwitchCdrEntity entity) {
        return modelMapper.map(entity, FreeSwitchCdrResponse.class);
    }

    public FreeSwitchCdrEntity convertToEntity(FreeSwitchCdrRequest request) {
        return modelMapper.map(request, FreeSwitchCdrEntity.class);
    }

    @Override
    public FreeSwitchCdrExcel convertToExcel(FreeSwitchCdrEntity entity) {
        return modelMapper.map(entity, FreeSwitchCdrExcel.class);
    }

    @Override
    public FreeSwitchCdrResponse create(FreeSwitchCdrRequest request) {

        UserEntity user = authService.getUser();
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (!StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getCacheSerialUid());
        }

        FreeSwitchCdrEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        FreeSwitchCdrEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建FreeSwitch CDR失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public FreeSwitchCdrResponse update(FreeSwitchCdrRequest request) {

        Optional<FreeSwitchCdrEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("FreeSwitch CDR不存在");
        }

        FreeSwitchCdrEntity entity = optional.get();
        
        // CDR记录通常不允许修改，这里只是示例
        // 实际应用中，CDR记录通常是只读的
        
        FreeSwitchCdrEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新FreeSwitch CDR失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public FreeSwitchCdrEntity save(FreeSwitchCdrEntity entity) {
        try {
            return freeSwitchCdrRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    public FreeSwitchCdrEntity doSave(FreeSwitchCdrEntity entity) {
        return freeSwitchCdrRepository.save(entity);
    }

    @Override
    public void delete(FreeSwitchCdrRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FreeSwitchCdrEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FreeSwitchCdrEntity entity) {
        log.warn("FreeSwitch CDR保存时发生乐观锁异常 uid: {}, version: {}", entity.getUid(), entity.getVersion());
        try {
            Optional<FreeSwitchCdrEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                FreeSwitchCdrEntity latestEntity = latest.get();
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FreeSwitchCdrEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            entity.setDeleted(true);
            save(entity);
        });
    }

    @Override
    @Cacheable(value = "freeswitch_cdr", key = "#uid", unless = "#result == null")
    public FreeSwitchCdrResponse queryByUid(FreeSwitchCdrRequest request) {
        Optional<FreeSwitchCdrEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        throw new RuntimeException("FreeSwitch CDR不存在");
    }

    // 业务方法
    // public Page<FreeSwitchCdrEntity> findByCallerNumber(String callerNumber, Pageable pageable) {
    //     return freeSwitchCdrRepository.findByCallerIdNumber(callerNumber, pageable);
    // }

    // public Page<FreeSwitchCdrEntity> findByDestinationNumber(String destinationNumber, Pageable pageable) {
    //     return freeSwitchCdrRepository.findByDestinationNumber(destinationNumber, pageable);
    // }

}
