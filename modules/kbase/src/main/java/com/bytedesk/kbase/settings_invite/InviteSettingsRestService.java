/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 13:37:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_invite;

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
public class InviteSettingsRestService extends BaseRestServiceWithExport<InviteSettingsEntity, InviteSettingsRequest, InviteSettingsResponse, InviteSettingsExcel> {

    private final InviteSettingsRepository inviteSettingRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final AuthService authService;

    @Override
    protected Specification<InviteSettingsEntity> createSpecification(InviteSettingsRequest request) {
        return InviteSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<InviteSettingsEntity> executePageQuery(Specification<InviteSettingsEntity> spec, Pageable pageable) {
        return inviteSettingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InviteSettingsEntity> queryByOrgEntity(InviteSettingsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<InviteSettingsEntity> spec = InviteSettingsSpecification.search(request, authService);
        return inviteSettingRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InviteSettingsResponse> queryByOrg(InviteSettingsRequest request) {
        Page<InviteSettingsEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<InviteSettingsResponse> queryByUser(InviteSettingsRequest request) {
        UserEntity user = authService.getUser();
        
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Override
    public InviteSettingsResponse queryByUid(InviteSettingsRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "inviteSetting", key = "#uid", unless="#result==null")
    @Override
    public Optional<InviteSettingsEntity> findByUid(String uid) {
        return inviteSettingRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return inviteSettingRepository.existsByUid(uid);
    }

    @Override
    public InviteSettingsResponse create(InviteSettingsRequest request) {
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
        InviteSettingsEntity entity = modelMapper.map(request, InviteSettingsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        InviteSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create inviteSetting failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public InviteSettingsResponse update(InviteSettingsRequest request) {
        Optional<InviteSettingsEntity> optional = inviteSettingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            InviteSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            InviteSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update inviteSetting failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("InviteSettings not found");
        }
    }

    @Override
    protected InviteSettingsEntity doSave(InviteSettingsEntity entity) {
        return inviteSettingRepository.save(entity);
    }

    @Override
    public InviteSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, InviteSettingsEntity entity) {
        try {
            Optional<InviteSettingsEntity> latest = inviteSettingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                InviteSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                return inviteSettingRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<InviteSettingsEntity> optional = inviteSettingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("InviteSettings not found");
        }
    }

    @Override
    public void delete(InviteSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public InviteSettingsResponse convertToResponse(InviteSettingsEntity entity) {
        return modelMapper.map(entity, InviteSettingsResponse.class);
    }

    @Override
    public InviteSettingsExcel convertToExcel(InviteSettingsEntity entity) {
        return modelMapper.map(entity, InviteSettingsExcel.class);
    }
    
    
}
