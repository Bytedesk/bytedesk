/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 20:54:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.mrcp;

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
public class CallMrcpRestService extends BaseRestServiceWithExcel<CallMrcpEntity, CallMrcpRequest, CallMrcpResponse, CallMrcpExcel> {

    private final CallMrcpRepository mrcpRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CallMrcpEntity> queryByOrgEntity(CallMrcpRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallMrcpEntity> spec = CallMrcpSpecification.search(request);
        return mrcpRepository.findAll(spec, pageable);
    }

    @Override
    public Page<CallMrcpResponse> queryByOrg(CallMrcpRequest request) {
        Page<CallMrcpEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CallMrcpResponse> queryByUser(CallMrcpRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public CallMrcpResponse queryByUid(CallMrcpRequest request) {
        Optional<CallMrcpEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            CallMrcpEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("CallMrcp not found");
        }
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<CallMrcpEntity> findByUid(String uid) {
        return mrcpRepository.findByUid(uid);
    }

    @Cacheable(value = "tag", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CallMrcpEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return mrcpRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return mrcpRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CallMrcpResponse create(CallMrcpRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CallMrcpEntity> tag = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
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
        CallMrcpEntity entity = modelMapper.map(request, CallMrcpEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CallMrcpEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CallMrcpResponse update(CallMrcpRequest request) {
        Optional<CallMrcpEntity> optional = mrcpRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CallMrcpEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            CallMrcpEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("CallMrcp not found");
        }
    }

    @Override
    protected CallMrcpEntity doSave(CallMrcpEntity entity) {
        return mrcpRepository.save(entity);
    }

    @Override
    public CallMrcpEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallMrcpEntity entity) {
        try {
            Optional<CallMrcpEntity> latest = mrcpRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallMrcpEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return mrcpRepository.save(latestEntity);
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
        Optional<CallMrcpEntity> optional = mrcpRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("CallMrcp not found");
        }
    }

    @Override
    public void delete(CallMrcpRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CallMrcpResponse convertToResponse(CallMrcpEntity entity) {
        return modelMapper.map(entity, CallMrcpResponse.class);
    }

    @Override
    public CallMrcpExcel convertToExcel(CallMrcpEntity entity) {
        return modelMapper.map(entity, CallMrcpExcel.class);
    }
    
    public void initCallMrcps(String orgUid) {
        // log.info("initThreadCallMrcp");
        for (String tag : CallMrcpInitData.getAllCallMrcps()) {
            CallMrcpRequest tagRequest = CallMrcpRequest.builder()
                    .uid(Utils.formatUid(orgUid, tag))
                    .name(tag)
                    .order(0)
                    .type(CallMrcpTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(tagRequest);
        }
    }
    
}
