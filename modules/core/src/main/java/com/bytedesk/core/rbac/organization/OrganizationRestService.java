/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 16:27:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OrganizationRestService extends BaseRestService<OrganizationEntity, OrganizationRequest, OrganizationResponse> {

    private final AuthService authService;

    private final UserService userService;

    // private final RoleService roleService;

    private final OrganizationRepository organizationRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    @Override
    public Page<OrganizationResponse> queryByOrg(OrganizationRequest request) {
        Pageable pageable = request.getPageable();
        Specification<OrganizationEntity> specification = OrganizationSpecification.search(request);
        Page<OrganizationEntity> orgPage = organizationRepository.findAll(specification, pageable);
        return orgPage.map(this::convertToResponse);
    }

    @Override
    public Page<OrganizationResponse> queryByUser(OrganizationRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found.");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    public List<OrganizationEntity> findAll() {
        return organizationRepository.findAll();
    }

    @Transactional
    public OrganizationResponse create(OrganizationRequest organizationRequest) {
        //
        if (existsByName(organizationRequest.getName())) {
            throw new RuntimeException("Organization with name: " + organizationRequest.getName() + " already exists.");
        }
        if (existsByCode(organizationRequest.getCode())) {
            throw new RuntimeException("Organization with code: " + organizationRequest.getCode() + " already exists.");
        }
        //
        UserEntity authUser = authService.getUser();
        UserEntity user = userService.findByUid(authUser.getUid())
                .orElseThrow(() -> new RuntimeException("User not found."));
        String orgUid = uidUtils.getUid();
        //
        OrganizationEntity organization = modelMapper.map(organizationRequest, OrganizationEntity.class);
        organization.setUid(orgUid);
        organization.setUser(user);
        log.info("Creating organization: {}", organization.toString());
        //
        OrganizationEntity savedOrganization = save(organization);
        if (savedOrganization == null) {
            throw new RuntimeException("Failed to create organization.");
        }
        user.setCurrentOrganization(savedOrganization);
        userService.addRoleAdmin(user);
        //
        return convertToResponse(savedOrganization);

    }

    public OrganizationResponse update(OrganizationRequest organizationRequest) {

        // 查找要更新的组织
        Optional<OrganizationEntity> organizationOptional = findByUid(organizationRequest.getUid());
        if (!organizationOptional.isPresent()) {
            // 如果组织不存在，可以抛出一个自定义异常，例如OrganizationNotFoundException
            throw new RuntimeException("Organization with UID: " + organizationRequest.getUid() + " not found.");
        }

        // 获取要更新的组织实体
        OrganizationEntity organization = organizationOptional.get();
        // 使用ModelMapper进行属性拷贝，避免逐一设置字段
        // modelMapper.map(organizationRequest, organization); // 一些默认值会被清空，待前端支持完善之后再启用
        organization.setName(organizationRequest.getName());
        organization.setLogo(organizationRequest.getLogo());
        organization.setCode(organizationRequest.getCode());
        organization.setDescription(organizationRequest.getDescription());
        // 保存更新后的组织
        OrganizationEntity updatedOrganization = save(organization);
        if (updatedOrganization == null) {
            throw new RuntimeException("Failed to update organization.");
        }
        // 转换为响应对象
        return convertToResponse(updatedOrganization);
    }

    @Cacheable(value = "organization", key = "#uid", unless = "#result == null")
    public Optional<OrganizationEntity> findByUid(String uid) {
        return organizationRepository.findByUid(uid);
    }

    @Cacheable(value = "organization", key = "#name", unless = "#result == null")
    public Optional<OrganizationEntity> findByName(String name) {
        return organizationRepository.findByNameAndDeleted(name, false);
    }

    @Cacheable(value = "organization", key = "#code", unless = "#result == null")
    public Optional<OrganizationEntity> findByCode(String code) {
        return organizationRepository.findByCodeAndDeleted(code, false);
    }

    public Boolean existsByName(String name) {
        return organizationRepository.existsByNameAndDeleted(name, false);
    }

    public Boolean existsByCode(String code) {
        return organizationRepository.existsByCodeAndDeleted(code, false);
    }

    @Caching(put = {
            @CachePut(value = "organization", key = "#organization.uid"),
            @CachePut(value = "organization", key = "#organization.name")
    })
    public OrganizationEntity save(OrganizationEntity organization) {
        try {
            return doSave(organization);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, organization);
        }
    }

    @Override
    @Transactional
    protected OrganizationEntity doSave(OrganizationEntity entity) {
        return organizationRepository.save(entity);
    }

    @Override
    public OrganizationEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            OrganizationEntity organization) {
        log.info("handleOptimisticLockingFailureException: " + e.getMessage());
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(OrganizationRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public OrganizationResponse convertToResponse(OrganizationEntity organization) {
        return modelMapper.map(organization, OrganizationResponse.class);
    }

    @Override
    public OrganizationResponse queryByUid(OrganizationRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
    

    // public void initData() {

    // if (organizationRepository.count() > 0) {
    // return;
    // }
    // //
    // Optional<UserEntity> adminOptional = userService.getAdmin();
    // if (adminOptional.isPresent()) {
    // //
    // OrganizationEntity organization = OrganizationEntity.builder()
    // .name(bytedeskProperties.getOrganizationName())
    // .code(bytedeskProperties.getOrganizationCode())
    // .description(bytedeskProperties.getOrganizationName() + " Description")
    // .user(adminOptional.get())
    // .build();
    // organization.setUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
    // //
    // save(organization);
    // }

    // }

}
