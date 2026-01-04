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
package com.bytedesk.service.workgroup_routing;

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
public class WorkgroupRoutingRestService extends BaseRestServiceWithExport<WorkgroupRoutingEntity, WorkgroupRoutingRequest, WorkgroupRoutingResponse, WorkgroupRoutingExcel> {

    private final WorkgroupRoutingRepository workgroup_routingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<WorkgroupRoutingEntity> queryByOrgEntity(WorkgroupRoutingRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkgroupRoutingEntity> specs = WorkgroupRoutingSpecification.search(request, authService);
        return workgroup_routingRepository.findAll(specs, pageable);
    }

    @Override
    public Page<WorkgroupRoutingResponse> queryByOrg(WorkgroupRoutingRequest request) {
        Page<WorkgroupRoutingEntity> workgroup_routingPage = queryByOrgEntity(request);
        return workgroup_routingPage.map(this::convertToResponse);
    }

    @Override
    public Page<WorkgroupRoutingResponse> queryByUser(WorkgroupRoutingRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "workgroup_routing", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkgroupRoutingEntity> findByUid(String uid) {
        return workgroup_routingRepository.findByUid(uid);
    }

    @Cacheable(value = "workgroup_routing", key = "#name + '_' + #orgUid", unless="#result==null")
    public Optional<WorkgroupRoutingEntity> findByNameAndOrgUid(String name, String orgUid) {
        return workgroup_routingRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    public Boolean existsByUid(String uid) {
        return workgroup_routingRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WorkgroupRoutingResponse create(WorkgroupRoutingRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public WorkgroupRoutingResponse createSystemWorkgroupRouting(WorkgroupRoutingRequest request) {
        return createInternal(request, true);
    }

    private WorkgroupRoutingResponse createInternal(WorkgroupRoutingRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<WorkgroupRoutingEntity> workgroup_routing = findByNameAndOrgUid(request.getName(), request.getOrgUid());
            if (workgroup_routing.isPresent()) {
                return convertToResponse(workgroup_routing.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(WorkgroupRoutingPermissions.WORKGROUP_ROUTING_MODULE, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        WorkgroupRoutingEntity entity = modelMapper.map(request, WorkgroupRoutingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorkgroupRoutingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create workgroup_routing failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WorkgroupRoutingResponse update(WorkgroupRoutingRequest request) {
        Optional<WorkgroupRoutingEntity> optional = workgroup_routingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkgroupRoutingEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(WorkgroupRoutingPermissions.WORKGROUP_ROUTING_MODULE, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            WorkgroupRoutingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update workgroup_routing failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorkgroupRouting not found");
        }
    }

    @Override
    protected WorkgroupRoutingEntity doSave(WorkgroupRoutingEntity entity) {
        return workgroup_routingRepository.save(entity);
    }

    @Override
    public WorkgroupRoutingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkgroupRoutingEntity entity) {
        try {
            Optional<WorkgroupRoutingEntity> latest = workgroup_routingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkgroupRoutingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return workgroup_routingRepository.save(latestEntity);
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
        Optional<WorkgroupRoutingEntity> optional = workgroup_routingRepository.findByUid(uid);
        if (optional.isPresent()) {
            WorkgroupRoutingEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(WorkgroupRoutingPermissions.WORKGROUP_ROUTING_MODULE, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // workgroup_routingRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorkgroupRouting not found");
        }
    }

    @Override
    public void delete(WorkgroupRoutingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkgroupRoutingResponse convertToResponse(WorkgroupRoutingEntity entity) {
        return modelMapper.map(entity, WorkgroupRoutingResponse.class);
    }

    @Override
    public WorkgroupRoutingExcel convertToExcel(WorkgroupRoutingEntity entity) {
        return modelMapper.map(entity, WorkgroupRoutingExcel.class);
    }

    @Override
    protected Specification<WorkgroupRoutingEntity> createSpecification(WorkgroupRoutingRequest request) {
        return WorkgroupRoutingSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkgroupRoutingEntity> executePageQuery(Specification<WorkgroupRoutingEntity> spec, Pageable pageable) {
        return workgroup_routingRepository.findAll(spec, pageable);
    }
    
    public void initWorkgroupRoutings(String orgUid) {
        // log.info("initWorkgroupRoutingWorkgroupRouting");
        // for (String workgroup_routing : WorkgroupRoutingInitData.getAllWorkgroupRoutings()) {
        //     WorkgroupRoutingRequest workgroup_routingRequest = WorkgroupRoutingRequest.builder()
        //             .uid(Utils.formatUid(orgUid, workgroup_routing))
        //             .name(workgroup_routing)
        //             .order(0)
        //             .type(WorkgroupRoutingTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemWorkgroupRouting(workgroup_routingRequest);
        // }
    }

    
    
}
