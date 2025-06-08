/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
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
import com.bytedesk.core.utils.Utils;

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
    public Page<FreeSwitchCdrEntity> queryByOrgEntity(FreeSwitchCdrRequest request) {

        Pageable pageable = Utils.getPageable(request);
        Specification<FreeSwitchCdrEntity> specification = FreeSwitchCdrSpecification.search(request);

        return freeSwitchCdrRepository.findAll(specification, pageable);
    }

    @Override
    public Page<FreeSwitchCdrEntity> queryByUserEntity(FreeSwitchCdrRequest request) {

        UserEntity user = authService.getCurrentUser();
        request.setOrgUid(user.getOrgUid());

        return queryByOrgEntity(request);
    }

    @Override
    public Optional<FreeSwitchCdrEntity> findByUid(String uid) {
        return freeSwitchCdrRepository.findByUidAndDeleted(uid, false);
    }

    @Override
    public FreeSwitchCdrResponse convertToResponse(FreeSwitchCdrEntity entity) {
        return modelMapper.map(entity, FreeSwitchCdrResponse.class);
    }

    @Override
    public FreeSwitchCdrEntity convertToEntity(FreeSwitchCdrRequest request) {
        return modelMapper.map(request, FreeSwitchCdrEntity.class);
    }

    @Override
    public FreeSwitchCdrExcel convertToExcel(FreeSwitchCdrEntity entity) {
        return modelMapper.map(entity, FreeSwitchCdrExcel.class);
    }

    @Override
    public FreeSwitchCdrResponse create(FreeSwitchCdrRequest request) {

        UserEntity user = authService.getCurrentUser();
        if (StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        
        if (StringUtils.hasText(request.getUid())) {
            request.setUid(uidUtils.getCacheSerialUid());
        }

        FreeSwitchCdrEntity entity = convertToEntity(request);
        entity.setLevel(LevelEnum.PLATFORM.name());
        entity.setPlatform(BytedeskConsts.PLATFORM_BYTEDESK);

        FreeSwitchCdrEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("创建FreeSwitch通话详单失败");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public FreeSwitchCdrResponse update(FreeSwitchCdrRequest request) {

        Optional<FreeSwitchCdrEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("FreeSwitch通话详单不存在");
        }

        FreeSwitchCdrEntity entity = optional.get();
        
        // CDR记录通常不允许修改，只读数据
        // 如果需要修改，可以在这里添加具体字段更新逻辑
        log.warn("尝试修改通话详单记录 [{}]，CDR记录通常应为只读数据", request.getUid());

        FreeSwitchCdrEntity updatedEntity = save(entity);
        if (updatedEntity == null) {
            throw new RuntimeException("更新FreeSwitch通话详单失败");
        }

        return convertToResponse(updatedEntity);
    }

    @Override
    public FreeSwitchCdrEntity save(FreeSwitchCdrEntity entity) {
        try {
            return freeSwitchCdrRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FreeSwitchCdrEntity> optional = findByUid(uid);
        optional.ifPresent(entity -> {
            // CDR记录通常不应删除，只做软删除标记
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
        throw new RuntimeException("FreeSwitch通话详单不存在");
    }

    /**
     * 根据UUID查询CDR记录
     */
    public Optional<FreeSwitchCdrEntity> findByCallUuid(String uuid) {
        return freeSwitchCdrRepository.findByUuid(uuid);
    }

    /**
     * 查询指定号码的通话记录
     */
    public Page<FreeSwitchCdrEntity> findByCallerNumber(String callerNumber, Pageable pageable) {
        return freeSwitchCdrRepository.findByCallerIdNumberContaining(callerNumber, pageable);
    }

    /**
     * 查询指定被叫号码的通话记录
     */
    public Page<FreeSwitchCdrEntity> findByDestinationNumber(String destinationNumber, Pageable pageable) {
        return freeSwitchCdrRepository.findByDestinationNumberContaining(destinationNumber, pageable);
    }

}
