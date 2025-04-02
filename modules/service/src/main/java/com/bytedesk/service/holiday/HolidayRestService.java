/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 09:22:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.holiday;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class HolidayRestService extends BaseRestService<HolidayEntity, HolidayRequest, HolidayResponse> {

    private final HolidayRepository holidayRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<HolidayResponse> queryByOrg(HolidayRequest request) {
        Pageable pageable = request.getPageable();
        Specification<HolidayEntity> spec = HolidaySpecification.search(request);
        Page<HolidayEntity> page = holidayRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<HolidayResponse> queryByUser(HolidayRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "holiday", key = "#uid", unless="#result==null")
    @Override
    public Optional<HolidayEntity> findByUid(String uid) {
        return holidayRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return holidayRepository.existsByUid(uid);
    }

    @Override
    public HolidayResponse create(HolidayRequest request) {
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
        HolidayEntity entity = modelMapper.map(request, HolidayEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        HolidayEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create holiday failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public HolidayResponse update(HolidayRequest request) {
        Optional<HolidayEntity> optional = holidayRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            HolidayEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            HolidayEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update holiday failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Holiday not found");
        }
    }

    /**
     * 保存标签，失败时自动重试
     * maxAttempts: 最大重试次数（包括第一次尝试）
     * backoff: 重试延迟，multiplier是延迟倍数
     * recover: 当重试次数用完后的回调方法
     */
    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public HolidayEntity save(HolidayEntity entity) {
        log.info("Attempting to save holiday: {}", entity.getName());
        return holidayRepository.save(entity);
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public HolidayEntity recover(Exception e, HolidayEntity entity) {
        log.error("Failed to save holiday after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save holiday after retries: " + e.getMessage());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<HolidayEntity> optional = holidayRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // holidayRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Holiday not found");
        }
    }

    @Override
    public void delete(HolidayRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, HolidayEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public HolidayResponse convertToResponse(HolidayEntity entity) {
        return modelMapper.map(entity, HolidayResponse.class);
    }

    @Override
    public HolidayResponse queryByUid(HolidayRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
    
}
