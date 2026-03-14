/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.esl_event;

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

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EslEventRestService extends BaseRestServiceWithExport<EslEventEntity, EslEventRequest, EslEventResponse, EslEventExcel> {

    private final EslEventRepository eslEventRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<EslEventEntity> queryByOrgEntity(EslEventRequest request) {
        Pageable pageable = request.getPageable();
        Specification<EslEventEntity> specs = EslEventSpecification.search(request, authService);
        return eslEventRepository.findAll(specs, pageable);
    }

    @Override
    public Page<EslEventResponse> queryByOrg(EslEventRequest request) {
        Page<EslEventEntity> esl_eventPage = queryByOrgEntity(request);
        return esl_eventPage.map(this::convertToResponse);
    }

    @Override
    public Page<EslEventResponse> queryByUser(EslEventRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "esl_event", key = "#uid", unless="#result==null")
    @Override
    public Optional<EslEventEntity> findByUid(String uid) {
        return eslEventRepository.findByUid(uid);
    }

    @Cacheable(value = "esl_event", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<EslEventEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return eslEventRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return eslEventRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public EslEventResponse create(EslEventRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public EslEventResponse createSystemEslEvent(EslEventRequest request) {
        return createInternal(request, true);
    }

    private EslEventResponse createInternal(EslEventRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<EslEventEntity> esl_event = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (esl_event.isPresent()) {
                return convertToResponse(esl_event.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(EslEventPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        EslEventEntity entity = modelMapper.map(request, EslEventEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        EslEventEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create esl_event failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public EslEventResponse update(EslEventRequest request) {
        Optional<EslEventEntity> optional = eslEventRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            EslEventEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(EslEventPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            EslEventEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update esl_event failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("EslEvent not found");
        }
    }

    @Override
    protected EslEventEntity doSave(EslEventEntity entity) {
        return eslEventRepository.save(entity);
    }

    @Override
    public EslEventEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, EslEventEntity entity) {
        try {
            Optional<EslEventEntity> latest = eslEventRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                EslEventEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return eslEventRepository.save(latestEntity);
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
        Optional<EslEventEntity> optional = eslEventRepository.findByUid(uid);
        if (optional.isPresent()) {
            EslEventEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(EslEventPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // esl_eventRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("EslEvent not found");
        }
    }

    @Override
    public void delete(EslEventRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public EslEventResponse convertToResponse(EslEventEntity entity) {
        return modelMapper.map(entity, EslEventResponse.class);
    }

    @Override
    public EslEventExcel convertToExcel(EslEventEntity entity) {
        return modelMapper.map(entity, EslEventExcel.class);
    }

    @Override
    protected Specification<EslEventEntity> createSpecification(EslEventRequest request) {
        return EslEventSpecification.search(request, authService);
    }

    @Override
    protected Page<EslEventEntity> executePageQuery(Specification<EslEventEntity> spec, Pageable pageable) {
        return eslEventRepository.findAll(spec, pageable);
    }
    
    public void initEslEvents(String orgUid) {
        // log.info("initEslEventEslEvent");
        // for (String esl_event : EslEventInitData.getAllEslEvents()) {
        //     EslEventRequest esl_eventRequest = EslEventRequest.builder()
        //             .uid(Utils.formatUid(orgUid, esl_event))
        //             .name(esl_event)
        //             .order(0)
        //             .type(EslEventTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemEslEvent(esl_eventRequest);
        // }
    }

    
    
}
