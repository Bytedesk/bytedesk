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
package com.bytedesk.crm.customer_company;

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
public class CustomerCompanyRestService extends BaseRestServiceWithExport<CustomerCompanyEntity, CustomerCompanyRequest, CustomerCompanyResponse, CustomerCompanyExcel> {

    private final CustomerCompanyRepository customer_companyRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<CustomerCompanyEntity> queryByOrgEntity(CustomerCompanyRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CustomerCompanyEntity> specs = CustomerCompanySpecification.search(request, authService);
        return customer_companyRepository.findAll(specs, pageable);
    }

    @Override
    public Page<CustomerCompanyResponse> queryByOrg(CustomerCompanyRequest request) {
        Page<CustomerCompanyEntity> customer_companyPage = queryByOrgEntity(request);
        return customer_companyPage.map(this::convertToResponse);
    }

    @Override
    public Page<CustomerCompanyResponse> queryByUser(CustomerCompanyRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "customer_company", key = "#uid", unless="#result==null")
    @Override
    public Optional<CustomerCompanyEntity> findByUid(String uid) {
        return customer_companyRepository.findByUid(uid);
    }

    @Cacheable(value = "customer_company", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CustomerCompanyEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return customer_companyRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return customer_companyRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CustomerCompanyResponse create(CustomerCompanyRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public CustomerCompanyResponse createSystemCustomerCompany(CustomerCompanyRequest request) {
        return createInternal(request, true);
    }

    private CustomerCompanyResponse createInternal(CustomerCompanyRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CustomerCompanyEntity> customer_company = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (customer_company.isPresent()) {
                return convertToResponse(customer_company.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(CustomerCompanyPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        CustomerCompanyEntity entity = modelMapper.map(request, CustomerCompanyEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CustomerCompanyEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create customer_company failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CustomerCompanyResponse update(CustomerCompanyRequest request) {
        Optional<CustomerCompanyEntity> optional = customer_companyRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CustomerCompanyEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(CustomerCompanyPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            CustomerCompanyEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update customer_company failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("CustomerCompany not found");
        }
    }

    @Override
    protected CustomerCompanyEntity doSave(CustomerCompanyEntity entity) {
        return customer_companyRepository.save(entity);
    }

    @Override
    public CustomerCompanyEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CustomerCompanyEntity entity) {
        try {
            Optional<CustomerCompanyEntity> latest = customer_companyRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CustomerCompanyEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setIndustry(entity.getIndustry());
                latestEntity.setSize(entity.getSize());
                latestEntity.setWebsite(entity.getWebsite());
                latestEntity.setOwnerUserUid(entity.getOwnerUserUid());
                latestEntity.setDescription(entity.getDescription());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return customer_companyRepository.save(latestEntity);
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
        Optional<CustomerCompanyEntity> optional = customer_companyRepository.findByUid(uid);
        if (optional.isPresent()) {
            CustomerCompanyEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(CustomerCompanyPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // customer_companyRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("CustomerCompany not found");
        }
    }

    @Override
    public void delete(CustomerCompanyRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CustomerCompanyResponse convertToResponse(CustomerCompanyEntity entity) {
        return modelMapper.map(entity, CustomerCompanyResponse.class);
    }

    @Override
    public CustomerCompanyExcel convertToExcel(CustomerCompanyEntity entity) {
        return modelMapper.map(entity, CustomerCompanyExcel.class);
    }

    @Override
    protected Specification<CustomerCompanyEntity> createSpecification(CustomerCompanyRequest request) {
        return CustomerCompanySpecification.search(request, authService);
    }

    @Override
    protected Page<CustomerCompanyEntity> executePageQuery(Specification<CustomerCompanyEntity> spec, Pageable pageable) {
        return customer_companyRepository.findAll(spec, pageable);
    }
    
    public void initCustomerCompanys(String orgUid) {
        // log.info("initCustomerCompanyCustomerCompany");
        // for (String customer_company : CustomerCompanyInitData.getAllCustomerCompanys()) {
        //     CustomerCompanyRequest customer_companyRequest = CustomerCompanyRequest.builder()
        //             .uid(Utils.formatUid(orgUid, customer_company))
        //             .name(customer_company)
        //             .order(0)
        //             .type(CustomerCompanyTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemCustomerCompany(customer_companyRequest);
        // }
    }

    
    
}
