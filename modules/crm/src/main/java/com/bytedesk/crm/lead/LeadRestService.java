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
package com.bytedesk.crm.lead;

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
public class LeadRestService extends BaseRestServiceWithExport<LeadEntity, LeadRequest, LeadResponse, LeadExcel> {

    private final LeadRepository leadRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<LeadEntity> queryByOrgEntity(LeadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<LeadEntity> specs = LeadSpecification.search(request, authService);
        return leadRepository.findAll(specs, pageable);
    }

    @Override
    public Page<LeadResponse> queryByOrg(LeadRequest request) {
        Page<LeadEntity> leadPage = queryByOrgEntity(request);
        return leadPage.map(this::convertToResponse);
    }

    @Override
    public Page<LeadResponse> queryByUser(LeadRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "lead", key = "#uid", unless="#result==null")
    @Override
    public Optional<LeadEntity> findByUid(String uid) {
        return leadRepository.findByUid(uid);
    }

    @Cacheable(value = "lead", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<LeadEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return leadRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return leadRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public LeadResponse create(LeadRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public LeadResponse createSystemLead(LeadRequest request) {
        return createInternal(request, true);
    }

    private LeadResponse createInternal(LeadRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<LeadEntity> lead = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (lead.isPresent()) {
                return convertToResponse(lead.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(LeadPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        LeadEntity entity = modelMapper.map(request, LeadEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        LeadEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create lead failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public LeadResponse update(LeadRequest request) {
        Optional<LeadEntity> optional = leadRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            LeadEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(LeadPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            LeadEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update lead failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Lead not found");
        }
    }

    @Override
    protected LeadEntity doSave(LeadEntity entity) {
        return leadRepository.save(entity);
    }

    @Override
    public LeadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, LeadEntity entity) {
        try {
            Optional<LeadEntity> latest = leadRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                LeadEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return leadRepository.save(latestEntity);
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
        Optional<LeadEntity> optional = leadRepository.findByUid(uid);
        if (optional.isPresent()) {
            LeadEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(LeadPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // leadRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Lead not found");
        }
    }

    @Override
    public void delete(LeadRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public LeadResponse convertToResponse(LeadEntity entity) {
        return modelMapper.map(entity, LeadResponse.class);
    }

    @Override
    public LeadExcel convertToExcel(LeadEntity entity) {
        return modelMapper.map(entity, LeadExcel.class);
    }

    @Override
    protected Specification<LeadEntity> createSpecification(LeadRequest request) {
        return LeadSpecification.search(request, authService);
    }

    @Override
    protected Page<LeadEntity> executePageQuery(Specification<LeadEntity> spec, Pageable pageable) {
        return leadRepository.findAll(spec, pageable);
    }
    
    public void initLeads(String orgUid) {
        // log.info("initLeadLead");
        // for (String lead : LeadInitData.getAllLeads()) {
        //     LeadRequest leadRequest = LeadRequest.builder()
        //             .uid(Utils.formatUid(orgUid, lead))
        //             .name(lead)
        //             .order(0)
        //             .type(LeadTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemLead(leadRequest);
        // }
    }

    
    
}
