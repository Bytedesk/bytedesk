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
package com.bytedesk.crm.opportunity;

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
public class OpportunityRestService extends BaseRestServiceWithExport<OpportunityEntity, OpportunityRequest, OpportunityResponse, OpportunityExcel> {

    private final OpportunityRepository opportunityRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<OpportunityEntity> queryByOrgEntity(OpportunityRequest request) {
        Pageable pageable = request.getPageable();
        Specification<OpportunityEntity> specs = OpportunitySpecification.search(request, authService);
        return opportunityRepository.findAll(specs, pageable);
    }

    @Override
    public Page<OpportunityResponse> queryByOrg(OpportunityRequest request) {
        Page<OpportunityEntity> opportunityPage = queryByOrgEntity(request);
        return opportunityPage.map(this::convertToResponse);
    }

    @Override
    public Page<OpportunityResponse> queryByUser(OpportunityRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "opportunity", key = "#uid", unless="#result==null")
    @Override
    public Optional<OpportunityEntity> findByUid(String uid) {
        return opportunityRepository.findByUid(uid);
    }

    @Cacheable(value = "opportunity", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<OpportunityEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return opportunityRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return opportunityRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public OpportunityResponse create(OpportunityRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public OpportunityResponse createSystemOpportunity(OpportunityRequest request) {
        return createInternal(request, true);
    }

    private OpportunityResponse createInternal(OpportunityRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<OpportunityEntity> opportunity = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (opportunity.isPresent()) {
                return convertToResponse(opportunity.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(OpportunityPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        OpportunityEntity entity = modelMapper.map(request, OpportunityEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        OpportunityEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create opportunity failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public OpportunityResponse update(OpportunityRequest request) {
        Optional<OpportunityEntity> optional = opportunityRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            OpportunityEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(OpportunityPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            OpportunityEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update opportunity failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Opportunity not found");
        }
    }

    @Override
    protected OpportunityEntity doSave(OpportunityEntity entity) {
        return opportunityRepository.save(entity);
    }

    @Override
    public OpportunityEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OpportunityEntity entity) {
        try {
            Optional<OpportunityEntity> latest = opportunityRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                OpportunityEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return opportunityRepository.save(latestEntity);
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
        Optional<OpportunityEntity> optional = opportunityRepository.findByUid(uid);
        if (optional.isPresent()) {
            OpportunityEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(OpportunityPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // opportunityRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Opportunity not found");
        }
    }

    @Override
    public void delete(OpportunityRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public OpportunityResponse convertToResponse(OpportunityEntity entity) {
        return modelMapper.map(entity, OpportunityResponse.class);
    }

    @Override
    public OpportunityExcel convertToExcel(OpportunityEntity entity) {
        return modelMapper.map(entity, OpportunityExcel.class);
    }

    @Override
    protected Specification<OpportunityEntity> createSpecification(OpportunityRequest request) {
        return OpportunitySpecification.search(request, authService);
    }

    @Override
    protected Page<OpportunityEntity> executePageQuery(Specification<OpportunityEntity> spec, Pageable pageable) {
        return opportunityRepository.findAll(spec, pageable);
    }
    
    public void initOpportunitys(String orgUid) {
        // log.info("initOpportunityOpportunity");
        // for (String opportunity : OpportunityInitData.getAllOpportunitys()) {
        //     OpportunityRequest opportunityRequest = OpportunityRequest.builder()
        //             .uid(Utils.formatUid(orgUid, opportunity))
        //             .name(opportunity)
        //             .order(0)
        //             .type(OpportunityTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemOpportunity(opportunityRequest);
        // }
    }

    
    
}
