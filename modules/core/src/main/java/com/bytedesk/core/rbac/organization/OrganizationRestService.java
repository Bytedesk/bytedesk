/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 23:26:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

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
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.rbac.user.UserResponse;

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

    @Transactional
    @Override
    public OrganizationResponse create(OrganizationRequest request) {
        //
        if (existsByName(request.getName())) {
            throw new ExistsException("组织名: " + request.getName() + " 已经存在，请修改组织名称.");
        }
        if (existsByCode(request.getCode())) {
            throw new ExistsException("组织代码: " + request.getCode() + " 已经存在。");
        }
        //
        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new NotLoginException("login required");
        }
        // 
        UserEntity user = userService.findByUid(authUser.getUid())
                .orElseThrow(() -> new NotFoundException("用户不存在."));
        String orgUid = uidUtils.getUid();
        //
        OrganizationEntity organization = modelMapper.map(request, OrganizationEntity.class);
        organization.setUid(orgUid);
        organization.setUser(user);
        log.info("Creating organization: {}", organization.toString());
        organization.setVip(true);
        organization.setVipExpireDate(BdDateUtils.now().plusDays(30));
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
    public OrganizationResponse createBySuper(OrganizationRequest request) {
        // 
        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new NotLoginException("login required");
        }
        if (!authUser.isSuperUser()) {
            throw new ForbiddenException("super admin required");
        }
        //
        if (existsByName(request.getName())) {
            throw new ExistsException("组织名: " + request.getName() + " 已经存在，请修改组织名称.");
        }
        if (existsByCode(request.getCode())) {
            throw new ExistsException("组织代码: " + request.getCode() + " 已经存在。");
        }
        // 
        OrganizationEntity organization = modelMapper.map(request, OrganizationEntity.class);
        organization.setUid(uidUtils.getUid());
        // 使用 userUid 查询用户
        if (StringUtils.hasText(request.getUserUid())) {
            UserEntity user = userService.findByUid(request.getUserUid())
                    .orElseThrow(() -> new NotFoundException("用户不存在."));
            organization.setUser(user);
            
        }
        // 
        OrganizationEntity savedOrganization = save(organization);
        if (savedOrganization == null) {
            throw new RuntimeException("Failed to create organization.");
        }
        if (savedOrganization.getUser() != null) {
            savedOrganization.getUser().setCurrentOrganization(savedOrganization);
            userService.addRoleAdmin(savedOrganization.getUser());
        }
        return convertToResponse(savedOrganization);
    }

    @Transactional
    @Override
    public OrganizationResponse update(OrganizationRequest request) {
        // 查找要更新的组织
        Optional<OrganizationEntity> organizationOptional = findByUid(request.getUid());
        if (!organizationOptional.isPresent()) {
            // 如果组织不存在，可以抛出一个自定义异常，例如OrganizationNotFoundException
            throw new NotFoundException("Organization with UID: " + request.getUid() + " not found.");
        }

        // 获取要更新的组织实体
        OrganizationEntity organization = organizationOptional.get();
        
        // 检查 name 唯一性（排除当前组织）
        if (!organization.getName().equals(request.getName())) {
            if (organizationRepository.existsByNameAndDeletedAndUidNot(request.getName(), false, request.getUid())) {
                throw new ExistsException("组织名: " + request.getName() + " 已经存在，请修改组织名称.");
            }
        }
        
        // 检查 code 唯一性（排除当前组织）
        if (!organization.getCode().equals(request.getCode())) {
            if (organizationRepository.existsByCodeAndDeletedAndUidNot(request.getCode(), false, request.getUid())) {
                throw new ExistsException("组织代码: " + request.getCode() + " 已经存在。");
            }
        }
        
        // 使用ModelMapper进行属性拷贝，避免逐一设置字段
        // modelMapper.map(organizationRequest, organization); // 一些默认值会被清空，待前端支持完善之后再启用
        organization.setName(request.getName());
        organization.setLogo(request.getLogo());
        organization.setCode(request.getCode());
        organization.setDescription(request.getDescription());

        // 保存更新后的组织
        OrganizationEntity updatedOrganization = save(organization);
        if (updatedOrganization == null) {
            throw new RuntimeException("Failed to update organization.");
        }
        
        // 转换为响应对象
        return convertToResponse(updatedOrganization);
    }

    // update by super
    @Transactional
    public OrganizationResponse updateBySuper(OrganizationRequest request) {
        // 查找要更新的组织
        Optional<OrganizationEntity> organizationOptional = findByUid(request.getUid());
        if (!organizationOptional.isPresent()) {
            // 如果组织不存在，可以抛出一个自定义异常，例如OrganizationNotFoundException
            throw new NotFoundException("Organization with UID: " + request.getUid() + " not found.");
        }

        // 获取要更新的组织实体
        OrganizationEntity organization = organizationOptional.get();
        
        // 检查 name 唯一性（排除当前组织）
        if (!organization.getName().equals(request.getName())) {
            if (organizationRepository.existsByNameAndDeletedAndUidNot(request.getName(), false, request.getUid())) {
                throw new ExistsException("组织名: " + request.getName() + " 已经存在，请修改组织名称.");
            }
        }
        
        // 检查 code 唯一性（排除当前组织）
        if (!organization.getCode().equals(request.getCode())) {
            if (organizationRepository.existsByCodeAndDeletedAndUidNot(request.getCode(), false, request.getUid())) {
                throw new ExistsException("组织代码: " + request.getCode() + " 已经存在。");
            }
        }
        
        // 使用ModelMapper进行属性拷贝，避免逐一设置字段
        // modelMapper.map(organizationRequest, organization); // 一些默认值会被清空，待前端支持完善之后再启用
        organization.setName(request.getName());
        organization.setLogo(request.getLogo());
        organization.setCode(request.getCode());
        organization.setDescription(request.getDescription());
        // 认证
        organization.setVerifiedType(request.getVerifiedType());
        organization.setIdentityType(request.getIdentityType());
        organization.setIdentityImage(request.getIdentityImage());
        organization.setIdentityNumber(request.getIdentityNumber());
        organization.setVerifyDate(request.getVerifyDate());
        organization.setVerifyStatus(request.getVerifyStatus());
        organization.setRejectReason(request.getRejectReason());
        // 
        organization.setVip(request.getVip());
        organization.setVipExpireDate(request.getVipExpireDate());
        // 
        organization.setEnabled(request.getEnabled());
        // 保存原先的用户引用，用于后续比较
        UserEntity originalUser = organization.getUser();
        
        // 使用 userUid 查询用户
        UserEntity newUser = null;
        if (StringUtils.hasText(request.getUserUid())) {
            newUser = userService.findByUid(request.getUserUid())
                    .orElseThrow(() -> new NotFoundException("用户不存在."));
            organization.setUser(newUser);
        }
        
        // 保存更新后的组织
        OrganizationEntity updatedOrganization = save(organization);
        if (updatedOrganization == null) {
            throw new RuntimeException("Failed to update organization.");
        }
        
        // 处理用户组织和角色的更新
        if (newUser != null) {
            // 判断是否与原先用户相同
            if (originalUser == null || !originalUser.getUid().equals(newUser.getUid())) {
                // 用户发生变化，需要处理组织和角色
                
                // 1. 清除原先用户的当前组织和角色（如果存在）
                if (originalUser != null) {
                    originalUser.setCurrentOrganization(null);
                    originalUser.removeOrganizationRoles();
                    userService.removeRoleAdmin(originalUser);
                    userService.save(originalUser);
                    log.info("Cleared original user's current organization and roles: {}", originalUser.getUid());
                }
                
                // 2. 设置新用户的当前组织和角色
                newUser.setCurrentOrganization(updatedOrganization);
                userService.addRoleAdmin(newUser);
                log.info("Set new user's current organization and admin role: {}", newUser.getUid());
            } else {
                // 用户相同，无需重复设置
                log.info("User unchanged, no need to update organization and roles: {}", newUser.getUid());
            }
        } else if (originalUser != null) {
            // 新用户为空，但原先有用户，需要清除原先用户的组织和角色
            originalUser.setCurrentOrganization(null);
            originalUser.removeOrganizationRoles();
            userService.removeRoleAdmin(originalUser);
            userService.save(originalUser);
            log.info("Cleared original user's current organization and roles (new user is null): {}", originalUser.getUid());
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
        OrganizationResponse response = new OrganizationResponse();
        
        // 映射基本字段
        response.setUid(organization.getUid());
        response.setName(organization.getName());
        response.setLogo(organization.getLogo());
        response.setCode(organization.getCode());
        response.setDescription(organization.getDescription());
        response.setVerifiedType(organization.getVerifiedType());
        response.setIdentityType(organization.getIdentityType());
        response.setIdentityImage(organization.getIdentityImage());
        response.setIdentityNumber(organization.getIdentityNumber());
        response.setVerifyDate(organization.getVerifyDate());
        response.setVerifyStatus(organization.getVerifyStatus());
        response.setRejectReason(organization.getRejectReason());
        response.setVip(organization.getVip());
        response.setVipExpireDate(organization.getVipExpireDate());
        response.setEnabled(organization.getEnabled());
        response.setCreatedAt(organization.getCreatedAt());
        response.setUpdatedAt(organization.getUpdatedAt());
        
        // 手动映射用户信息
        if (organization.getUser() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUid(organization.getUser().getUid());
            userResponse.setUsername(organization.getUser().getUsername());
            userResponse.setNickname(organization.getUser().getNickname());
            userResponse.setEmail(organization.getUser().getEmail());
            userResponse.setMobile(organization.getUser().getMobile());
            userResponse.setCountry(organization.getUser().getCountry());
            userResponse.setAvatar(organization.getUser().getAvatar());
            userResponse.setDescription(organization.getUser().getDescription());
            userResponse.setPlatform(organization.getUser().getPlatform());
            
            // 将字符串转换为 Sex 枚举
            if (organization.getUser().getSex() != null) {
                try {
                    userResponse.setSex(UserEntity.Sex.valueOf(organization.getUser().getSex()));
                } catch (IllegalArgumentException e) {
                    userResponse.setSex(UserEntity.Sex.UNKNOWN);
                }
            } else {
                userResponse.setSex(UserEntity.Sex.UNKNOWN);
            }
            
            userResponse.setEnabled(organization.getUser().isEnabled());
            userResponse.setSuperUser(organization.getUser().isSuperUser());
            userResponse.setEmailVerified(organization.getUser().isEmailVerified());
            userResponse.setMobileVerified(organization.getUser().isMobileVerified());
            userResponse.setCreatedAt(organization.getUser().getCreatedAt());
            userResponse.setUpdatedAt(organization.getUser().getUpdatedAt());
            
            // 设置用户的当前组织为 null，避免循环引用
            userResponse.setCurrentOrganization(null);
            
            response.setUser(userResponse);
        }
        
        return response;
    }

    @Override
    protected Specification<OrganizationEntity> createSpecification(OrganizationRequest request) {
        return OrganizationSpecification.search(request);
    }

    @Override
    protected Page<OrganizationEntity> executePageQuery(Specification<OrganizationEntity> spec, Pageable pageable) {
        return organizationRepository.findAll(spec, pageable);
    }

}
