/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 13:38:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_ratedown;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RatedownSettingsRestService extends BaseRestServiceWithExport<RatedownSettingsEntity, RatedownSettingsRequest, RatedownSettingsResponse, RatedownSettingsExcel> {

    private final RatedownSettingsRepository ratedownSettingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<RatedownSettingsEntity> createSpecification(RatedownSettingsRequest request) {
        return RatedownSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<RatedownSettingsEntity> executePageQuery(Specification<RatedownSettingsEntity> spec, Pageable pageable) {
        return ratedownSettingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<RatedownSettingsEntity> queryByOrgEntity(RatedownSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<RatedownSettingsEntity> spec = RatedownSettingsSpecification.search(request, authService);
        return ratedownSettingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<RatedownSettingsResponse> queryByOrg(RatedownSettingsRequest request) {
        Page<RatedownSettingsEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<RatedownSettingsResponse> queryByUser(RatedownSettingsRequest request) {
        UserEntity user = authService.getUser();
        
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Override
    public RatedownSettingsResponse queryByUid(RatedownSettingsRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "ratedownSetting", key = "#uid", unless="#result==null")
    @Override
    public Optional<RatedownSettingsEntity> findByUid(String uid) {
        return ratedownSettingRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return ratedownSettingRepository.existsByUid(uid);
    }

    @Override
    public RatedownSettingsResponse create(RatedownSettingsRequest request) {
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
        RatedownSettingsEntity entity = modelMapper.map(request, RatedownSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        RatedownSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ratedownSetting failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public RatedownSettingsResponse update(RatedownSettingsRequest request) {
        Optional<RatedownSettingsEntity> optional = ratedownSettingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            RatedownSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            RatedownSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ratedownSetting failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("RatedownSettings not found");
        }
    }

    @Override
    protected RatedownSettingsEntity doSave(RatedownSettingsEntity entity) {
        return ratedownSettingRepository.save(entity);
    }

    @Override
    public RatedownSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RatedownSettingsEntity entity) {
        try {
            Optional<RatedownSettingsEntity> latest = ratedownSettingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RatedownSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                return ratedownSettingRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<RatedownSettingsEntity> optional = ratedownSettingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("RatedownSettings not found");
        }
    }

    @Override
    public void delete(RatedownSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RatedownSettingsResponse convertToResponse(RatedownSettingsEntity entity) {
        return modelMapper.map(entity, RatedownSettingsResponse.class);
    }

    @Override
    public RatedownSettingsExcel convertToExcel(RatedownSettingsEntity entity) {
        return modelMapper.map(entity, RatedownSettingsExcel.class);
    }
    
    
}
