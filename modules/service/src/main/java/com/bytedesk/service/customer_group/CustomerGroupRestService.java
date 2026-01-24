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
package com.bytedesk.service.customer_group;

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
public class CustomerGroupRestService extends BaseRestServiceWithExport<CustomerGroupEntity, CustomerGroupRequest, CustomerGroupResponse, CustomerGroupExcel> {

    private final CustomerGroupRepository customer_groupRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<CustomerGroupEntity> queryByOrgEntity(CustomerGroupRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CustomerGroupEntity> specs = CustomerGroupSpecification.search(request, authService);
        return customer_groupRepository.findAll(specs, pageable);
    }

    @Override
    public Page<CustomerGroupResponse> queryByOrg(CustomerGroupRequest request) {
        Page<CustomerGroupEntity> customer_groupPage = queryByOrgEntity(request);
        return customer_groupPage.map(this::convertToResponse);
    }

    @Override
    public Page<CustomerGroupResponse> queryByUser(CustomerGroupRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "customer_group", key = "#uid", unless="#result==null")
    @Override
    public Optional<CustomerGroupEntity> findByUid(String uid) {
        return customer_groupRepository.findByUid(uid);
    }

    @Cacheable(value = "customer_group", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CustomerGroupEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return customer_groupRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return customer_groupRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CustomerGroupResponse create(CustomerGroupRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public CustomerGroupResponse createSystemCustomerGroup(CustomerGroupRequest request) {
        return createInternal(request, true);
    }

    private CustomerGroupResponse createInternal(CustomerGroupRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CustomerGroupEntity> customer_group = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (customer_group.isPresent()) {
                return convertToResponse(customer_group.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(CustomerGroupPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        CustomerGroupEntity entity = modelMapper.map(request, CustomerGroupEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CustomerGroupEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create customer_group failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CustomerGroupResponse update(CustomerGroupRequest request) {
        Optional<CustomerGroupEntity> optional = customer_groupRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CustomerGroupEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(CustomerGroupPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            CustomerGroupEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update customer_group failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("CustomerGroup not found");
        }
    }

    @Override
    protected CustomerGroupEntity doSave(CustomerGroupEntity entity) {
        return customer_groupRepository.save(entity);
    }

    @Override
    public CustomerGroupEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CustomerGroupEntity entity) {
        try {
            Optional<CustomerGroupEntity> latest = customer_groupRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CustomerGroupEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return customer_groupRepository.save(latestEntity);
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
        Optional<CustomerGroupEntity> optional = customer_groupRepository.findByUid(uid);
        if (optional.isPresent()) {
            CustomerGroupEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(CustomerGroupPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // customer_groupRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("CustomerGroup not found");
        }
    }

    @Override
    public void delete(CustomerGroupRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CustomerGroupResponse convertToResponse(CustomerGroupEntity entity) {
        return modelMapper.map(entity, CustomerGroupResponse.class);
    }

    @Override
    public CustomerGroupExcel convertToExcel(CustomerGroupEntity entity) {
        return modelMapper.map(entity, CustomerGroupExcel.class);
    }

    @Override
    protected Specification<CustomerGroupEntity> createSpecification(CustomerGroupRequest request) {
        return CustomerGroupSpecification.search(request, authService);
    }

    @Override
    protected Page<CustomerGroupEntity> executePageQuery(Specification<CustomerGroupEntity> spec, Pageable pageable) {
        return customer_groupRepository.findAll(spec, pageable);
    }
    
    public void initCustomerGroups(String orgUid) {
        // log.info("initCustomerGroupCustomerGroup");
        // for (String customer_group : CustomerGroupInitData.getAllCustomerGroups()) {
        //     CustomerGroupRequest customer_groupRequest = CustomerGroupRequest.builder()
        //             .uid(Utils.formatUid(orgUid, customer_group))
        //             .name(customer_group)
        //             .order(0)
        //             .type(CustomerGroupTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemCustomerGroup(customer_groupRequest);
        // }
    }

    
    
}
