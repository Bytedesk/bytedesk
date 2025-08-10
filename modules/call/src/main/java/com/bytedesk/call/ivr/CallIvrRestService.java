/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 20:54:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.ivr;

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
public class CallIvrRestService extends BaseRestServiceWithExcel<CallIvrEntity, CallIvrRequest, CallIvrResponse, CallIvrExcel> {

    private final CallIvrRepository ivrRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CallIvrEntity> queryByOrgEntity(CallIvrRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CallIvrEntity> spec = CallIvrSpecification.search(request);
        return ivrRepository.findAll(spec, pageable);
    }

    @Override
    public Page<CallIvrResponse> queryByOrg(CallIvrRequest request) {
        Page<CallIvrEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CallIvrResponse> queryByUser(CallIvrRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public CallIvrResponse queryByUid(CallIvrRequest request) {
        Optional<CallIvrEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            CallIvrEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("CallIvr not found");
        }
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<CallIvrEntity> findByUid(String uid) {
        return ivrRepository.findByUid(uid);
    }

    @Cacheable(value = "tag", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CallIvrEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return ivrRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return ivrRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CallIvrResponse create(CallIvrRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CallIvrEntity> tag = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
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
        CallIvrEntity entity = modelMapper.map(request, CallIvrEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CallIvrEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CallIvrResponse update(CallIvrRequest request) {
        Optional<CallIvrEntity> optional = ivrRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CallIvrEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            CallIvrEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("CallIvr not found");
        }
    }

    @Override
    protected CallIvrEntity doSave(CallIvrEntity entity) {
        return ivrRepository.save(entity);
    }

    @Override
    public CallIvrEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CallIvrEntity entity) {
        try {
            Optional<CallIvrEntity> latest = ivrRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallIvrEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return ivrRepository.save(latestEntity);
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
        Optional<CallIvrEntity> optional = ivrRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("CallIvr not found");
        }
    }

    @Override
    public void delete(CallIvrRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CallIvrResponse convertToResponse(CallIvrEntity entity) {
        return modelMapper.map(entity, CallIvrResponse.class);
    }

    @Override
    public CallIvrExcel convertToExcel(CallIvrEntity entity) {
        return modelMapper.map(entity, CallIvrExcel.class);
    }
    
    public void initCallIvrs(String orgUid) {
        // log.info("initThreadCallIvr");
        for (String tag : CallIvrInitData.getAllCallIvrs()) {
            CallIvrRequest tagRequest = CallIvrRequest.builder()
                    .uid(Utils.formatUid(orgUid, tag))
                    .name(tag)
                    .order(0)
                    .type(CallIvrTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(tagRequest);
        }
    }
    
}
