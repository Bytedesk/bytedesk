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
package com.bytedesk.conference.conference;

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
public class ConferenceRestService extends BaseRestServiceWithExport<ConferenceEntity, ConferenceRequest, ConferenceResponse, ConferenceExcel> {

    private final ConferenceRepository conferenceRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ConferenceEntity> queryByOrgEntity(ConferenceRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ConferenceEntity> specs = ConferenceSpecification.search(request, authService);
        return conferenceRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ConferenceResponse> queryByOrg(ConferenceRequest request) {
        Page<ConferenceEntity> conferencePage = queryByOrgEntity(request);
        return conferencePage.map(this::convertToResponse);
    }

    @Override
    public Page<ConferenceResponse> queryByUser(ConferenceRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "conference", key = "#uid", unless="#result==null")
    @Override
    public Optional<ConferenceEntity> findByUid(String uid) {
        return conferenceRepository.findByUid(uid);
    }

    @Cacheable(value = "conference", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ConferenceEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return conferenceRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return conferenceRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ConferenceResponse create(ConferenceRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ConferenceResponse createSystemConference(ConferenceRequest request) {
        return createInternal(request, true);
    }

    private ConferenceResponse createInternal(ConferenceRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ConferenceEntity> conference = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (conference.isPresent()) {
                return convertToResponse(conference.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ConferencePermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ConferenceEntity entity = modelMapper.map(request, ConferenceEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ConferenceEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create conference failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ConferenceResponse update(ConferenceRequest request) {
        Optional<ConferenceEntity> optional = conferenceRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ConferenceEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ConferencePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ConferenceEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update conference failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Conference not found");
        }
    }

    @Override
    protected ConferenceEntity doSave(ConferenceEntity entity) {
        return conferenceRepository.save(entity);
    }

    @Override
    public ConferenceEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ConferenceEntity entity) {
        try {
            Optional<ConferenceEntity> latest = conferenceRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ConferenceEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return conferenceRepository.save(latestEntity);
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
        Optional<ConferenceEntity> optional = conferenceRepository.findByUid(uid);
        if (optional.isPresent()) {
            ConferenceEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ConferencePermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // conferenceRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Conference not found");
        }
    }

    @Override
    public void delete(ConferenceRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ConferenceResponse convertToResponse(ConferenceEntity entity) {
        return modelMapper.map(entity, ConferenceResponse.class);
    }

    @Override
    public ConferenceExcel convertToExcel(ConferenceEntity entity) {
        return modelMapper.map(entity, ConferenceExcel.class);
    }

    @Override
    protected Specification<ConferenceEntity> createSpecification(ConferenceRequest request) {
        return ConferenceSpecification.search(request, authService);
    }

    @Override
    protected Page<ConferenceEntity> executePageQuery(Specification<ConferenceEntity> spec, Pageable pageable) {
        return conferenceRepository.findAll(spec, pageable);
    }
    
    public void initConferences(String orgUid) {
        // log.info("initConferenceConference");
        // for (String conference : ConferenceInitData.getAllConferences()) {
        //     ConferenceRequest conferenceRequest = ConferenceRequest.builder()
        //             .uid(Utils.formatUid(orgUid, conference))
        //             .name(conference)
        //             .order(0)
        //             .type(ConferenceTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemConference(conferenceRequest);
        // }
    }

    
    
}
