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
package com.bytedesk.crm.lead_follow;

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
import com.bytedesk.crm.lead.LeadRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LeadFollowRestService extends BaseRestServiceWithExport<LeadFollowEntity, LeadFollowRequest, LeadFollowResponse, LeadFollowExcel> {

    private final LeadFollowRepository lead_followRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    private final LeadRepository leadRepository;
    
    @Override
    public Page<LeadFollowEntity> queryByOrgEntity(LeadFollowRequest request) {
        Pageable pageable = request.getPageable();
        Specification<LeadFollowEntity> specs = LeadFollowSpecification.search(request, authService);
        return lead_followRepository.findAll(specs, pageable);
    }

    @Override
    public Page<LeadFollowResponse> queryByOrg(LeadFollowRequest request) {
        Page<LeadFollowEntity> lead_followPage = queryByOrgEntity(request);
        return lead_followPage.map(this::convertToResponse);
    }

    @Override
    public Page<LeadFollowResponse> queryByUser(LeadFollowRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "lead_follow", key = "#uid", unless="#result==null")
    @Override
    public Optional<LeadFollowEntity> findByUid(String uid) {
        return lead_followRepository.findByUid(uid);
    }

    @Cacheable(value = "lead_follow", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<LeadFollowEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return lead_followRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return lead_followRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public LeadFollowResponse create(LeadFollowRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public LeadFollowResponse createSystemLeadFollow(LeadFollowRequest request) {
        return createInternal(request, true);
    }

    private LeadFollowResponse createInternal(LeadFollowRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<LeadFollowEntity> lead_follow = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (lead_follow.isPresent()) {
                return convertToResponse(lead_follow.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(LeadFollowPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        LeadFollowEntity entity = modelMapper.map(request, LeadFollowEntity.class);
        applyLeadRelation(request, entity);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        LeadFollowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create lead_follow failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public LeadFollowResponse update(LeadFollowRequest request) {
        Optional<LeadFollowEntity> optional = lead_followRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            LeadFollowEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(LeadFollowPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            applyLeadRelation(request, entity);
            //
            LeadFollowEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update lead_follow failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("LeadFollow not found");
        }
    }

    @Override
    protected LeadFollowEntity doSave(LeadFollowEntity entity) {
        return lead_followRepository.save(entity);
    }

    @Override
    public LeadFollowEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LeadFollowEntity entity) {
        try {
            Optional<LeadFollowEntity> latest = lead_followRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                LeadFollowEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return lead_followRepository.save(latestEntity);
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
        Optional<LeadFollowEntity> optional = lead_followRepository.findByUid(uid);
        if (optional.isPresent()) {
            LeadFollowEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(LeadFollowPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // lead_followRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("LeadFollow not found");
        }
    }

    @Override
    public void delete(LeadFollowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public LeadFollowResponse convertToResponse(LeadFollowEntity entity) {
        LeadFollowResponse response = modelMapper.map(entity, LeadFollowResponse.class);
        if (entity.getLead() != null) {
            response.setLeadUid(entity.getLead().getUid());
        }
        return response;
    }

    @Override
    public LeadFollowExcel convertToExcel(LeadFollowEntity entity) {
        return modelMapper.map(entity, LeadFollowExcel.class);
    }

    @Override
    protected Specification<LeadFollowEntity> createSpecification(LeadFollowRequest request) {
        return LeadFollowSpecification.search(request, authService);
    }

    @Override
    protected Page<LeadFollowEntity> executePageQuery(Specification<LeadFollowEntity> spec, Pageable pageable) {
        return lead_followRepository.findAll(spec, pageable);
    }

    private void applyLeadRelation(LeadFollowRequest request, LeadFollowEntity entity) {
        if (StringUtils.hasText(request.getLeadUid())) {
            leadRepository.findByUid(request.getLeadUid()).ifPresent(entity::setLead);
        } else {
            entity.setLead(null);
        }
    }
    
    public void initLeadFollows(String orgUid) {
        // log.info("initLeadFollowLeadFollow");
        // for (String lead_follow : LeadFollowInitData.getAllLeadFollows()) {
        //     LeadFollowRequest lead_followRequest = LeadFollowRequest.builder()
        //             .uid(Utils.formatUid(orgUid, lead_follow))
        //             .name(lead_follow)
        //             .order(0)
        //             .type(LeadFollowTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemLeadFollow(lead_followRequest);
        // }
    }

    
    
}
