/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 18:22:55
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.exception.ExistsException;
import com.bytedesk.core.exception.ForbiddenException;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.exception.NotLoginException;
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

    @Override
    public OrganizationResponse queryByUid(OrganizationRequest request) {
        Optional<OrganizationEntity> organizationOptional = findByUid(request.getUid());
        if (!organizationOptional.isPresent()) {
            throw new NotFoundException("Organization with UID: " + request.getUid() + " not found.");
        }
        return convertToResponse(organizationOptional.get());
    }

    public List<OrganizationEntity> findAll() {
        return organizationRepository.findAll();
    }

    @Transactional
    @Override
    public OrganizationResponse create(OrganizationRequest organizationRequest) {
        //
        if (existsByName(organizationRequest.getName())) {
            throw new ExistsException("组织名: " + organizationRequest.getName() + " 已经存在，请修改组织名称.");
        }
        if (existsByCode(organizationRequest.getCode())) {
            throw new ExistsException("组织代码: " + organizationRequest.getCode() + " 已经存在。");
        }
        //
        UserEntity authUser = authService.getUser();
        UserEntity user = userService.findByUid(authUser.getUid())
                .orElseThrow(() -> new NotFoundException("用户不存在."));
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

    // 超级管理员创建组织
    @Transactional
    public OrganizationResponse createByAdmin(OrganizationRequest organizationRequest) {
        // 
        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new NotLoginException("login required");
        }
        if (!authUser.isSuperUser()) {
            throw new ForbiddenException("super admin required");
        }
        //
        if (existsByName(organizationRequest.getName())) {
            throw new ExistsException("组织名: " + organizationRequest.getName() + " 已经存在，请修改组织名称.");
        }
        if (existsByCode(organizationRequest.getCode())) {
            throw new ExistsException("组织代码: " + organizationRequest.getCode() + " 已经存在。");
        }
        // 
        OrganizationEntity organization = modelMapper.map(organizationRequest, OrganizationEntity.class);
        organization.setUid(uidUtils.getUid());
        // 使用 userUid 查询用户
        if (StringUtils.hasText(organizationRequest.getUserUid())) {
            UserEntity user = userService.findByUid(organizationRequest.getUserUid())
                    .orElseThrow(() -> new NotFoundException("用户不存在."));
            organization.setUser(user);
            
        }
        // 
        OrganizationEntity savedOrganization = save(organization);
        if (savedOrganization == null) {
            throw new RuntimeException("Failed to create organization.");
        }
        if (organization.getUser() != null) {
            organization.getUser().setCurrentOrganization(organization);
            userService.addRoleAdmin(organization.getUser());
        }
        return convertToResponse(savedOrganization);
    }

    @Transactional
    @Override
    public OrganizationResponse update(OrganizationRequest organizationRequest) {
        // 查找要更新的组织
        Optional<OrganizationEntity> organizationOptional = findByUid(organizationRequest.getUid());
        if (!organizationOptional.isPresent()) {
            // 如果组织不存在，可以抛出一个自定义异常，例如OrganizationNotFoundException
            throw new NotFoundException("Organization with UID: " + organizationRequest.getUid() + " not found.");
        }

        // 获取要更新的组织实体
        OrganizationEntity organization = organizationOptional.get();
        
        // 检查 name 唯一性（排除当前组织）
        if (!organization.getName().equals(organizationRequest.getName())) {
            if (organizationRepository.existsByNameAndDeletedAndUidNot(organizationRequest.getName(), false, organizationRequest.getUid())) {
                throw new ExistsException("组织名: " + organizationRequest.getName() + " 已经存在，请修改组织名称.");
            }
        }
        
        // 检查 code 唯一性（排除当前组织）
        if (!organization.getCode().equals(organizationRequest.getCode())) {
            if (organizationRepository.existsByCodeAndDeletedAndUidNot(organizationRequest.getCode(), false, organizationRequest.getUid())) {
                throw new ExistsException("组织代码: " + organizationRequest.getCode() + " 已经存在。");
            }
        }
        
        // 使用ModelMapper进行属性拷贝，避免逐一设置字段
        // modelMapper.map(organizationRequest, organization); // 一些默认值会被清空，待前端支持完善之后再启用
        organization.setName(organizationRequest.getName());
        organization.setLogo(organizationRequest.getLogo());
        organization.setCode(organizationRequest.getCode());
        organization.setDescription(organizationRequest.getDescription());
        // 认证
        organization.setVerifiedType(organizationRequest.getVerifiedType());
        organization.setIdentityType(organizationRequest.getIdentityType());
        organization.setIdentityImage(organizationRequest.getIdentityImage());
        organization.setIdentityNumber(organizationRequest.getIdentityNumber());
        organization.setVerifyDate(organizationRequest.getVerifyDate());
        organization.setVerifyStatus(organizationRequest.getVerifyStatus());
        organization.setRejectReason(organizationRequest.getRejectReason());
        // 
        organization.setVip(organizationRequest.getVip());
        organization.setVipExpireDate(organizationRequest.getVipExpireDate());
        // 
        organization.setEnabled(organizationRequest.getEnabled());
        // 使用 userUid 查询用户
        if (StringUtils.hasText(organizationRequest.getUserUid())) {
            UserEntity user = userService.findByUid(organizationRequest.getUserUid())
                    .orElseThrow(() -> new NotFoundException("用户不存在."));
            organization.setUser(user);
        }
        // 保存更新后的组织
        OrganizationEntity updatedOrganization = save(organization);
        if (updatedOrganization == null) {
            throw new RuntimeException("Failed to update organization.");
        }
        // TODO: 超级管理员更新用户当前组织
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

    @Cacheable(value = "organization", key = "#organization.uid", unless = "#result == null")
    @Override
    @Transactional
    protected OrganizationEntity doSave(OrganizationEntity entity) {
        return organizationRepository.save(entity);
    }

    @Cacheable(value = "organization", key = "#organization.uid", unless = "#result == null")
    @Override
    public OrganizationEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            OrganizationEntity organization) {
        log.info("handleOptimisticLockingFailureException: " + e.getMessage());
        try {
            Optional<OrganizationEntity> latest = organizationRepository.findByUid(organization.getUid());
            if (latest.isPresent()) {
                OrganizationEntity latestEntity = latest.get();

                // 检查 name 唯一性（排除当前组织）
                if (!latestEntity.getName().equals(organization.getName())) {
                    if (organizationRepository.existsByNameAndDeletedAndUidNot(organization.getName(), false, organization.getUid())) {
                        throw new ExistsException("组织名: " + organization.getName() + " 已经存在，请修改组织名称.");
                    }
                }
                
                // 检查 code 唯一性（排除当前组织）
                if (!latestEntity.getCode().equals(organization.getCode())) {
                    if (organizationRepository.existsByCodeAndDeletedAndUidNot(organization.getCode(), false, organization.getUid())) {
                        throw new ExistsException("组织代码: " + organization.getCode() + " 已经存在。");
                    }
                }

                // 合并需要保留的数据
                latestEntity.setName(organization.getName());
                latestEntity.setLogo(organization.getLogo());
                latestEntity.setCode(organization.getCode());
                latestEntity.setDescription(organization.getDescription());

                // 保存更新后的数据
                return organizationRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Error retrieving latest organization: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<OrganizationEntity> organizationOptional = findByUid(uid);
        if (organizationOptional.isPresent()) {
            OrganizationEntity organization = organizationOptional.get();
            organization.setDeleted(true); // 逻辑删除
            save(organization);
        } else {
            throw new NotFoundException("Organization with UID: " + uid + " not found.");
        }
    }

    @Override
    public void delete(OrganizationRequest request) {
        deleteByUid(request.getUid());
    }

    public OrganizationResponse convertToResponse(OrganizationEntity organization) {
        return modelMapper.map(organization, OrganizationResponse.class);
    }

}
