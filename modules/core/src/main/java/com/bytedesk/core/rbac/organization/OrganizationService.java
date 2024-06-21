/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-21 14:31:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.event.BytedeskEventPublisher;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OrganizationService {

    private final AuthService authService;

    private final UserService userService;

    private final RoleService roleService;

    private final BytedeskProperties bytedeskProperties;

    private final OrganizationRepository organizationRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    public Page<OrganizationResponse> query(OrganizationRequest pageParam) {

        User user = authService.getCurrentUser();

        Pageable pageable = PageRequest.of(pageParam.getPageNumber(), pageParam.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Organization> orgPage = organizationRepository.findByUser(user, pageable);

        return orgPage.map(organization -> convertToResponse(organization));
    }

    @Transactional
    public OrganizationResponse create(OrganizationRequest organizationRequest) {

        if (existsByName(organizationRequest.getName())) {
            throw new RuntimeException("Organization with name: " + organizationRequest.getName() + " already exists.");
        }

        if (existsByCode(organizationRequest.getCode())) {
            throw new RuntimeException("Organization with code: " + organizationRequest.getCode() + " already exists.");
        }

        User user = authService.getCurrentUser();
        String orgUid = uidUtils.getCacheSerialUid();
        // 
        Organization organization = modelMapper.map(organizationRequest, Organization.class);
        organization.setUid(orgUid);
        organization.setUser(user);
        log.info("Creating organization: {}", organization.toString());
        // 
        try {
            //
            Organization savedOrganization = save(organization);
            if (savedOrganization == null) {
                throw new RuntimeException("Failed to create organization.");
            }
            // 
            log.info("Organization created with UID: {}", orgUid);
            // 初始化组织的角色
            roleService.initOrgRoles(orgUid);
            //
            Optional<Role> roleOptional = roleService.findByNameAndOrgUid(TypeConsts.ROLE_ADMIN, orgUid);
            if (roleOptional.isPresent()) {
                log.info("roleOptional success");
                user.addOrganizationRole(savedOrganization, roleOptional.get());
                userService.save(user);
            } else {
                log.info("roleOptional fail");
            }
            // 放到listener中会报错，所以放在此处
            // event listener order 1. member, 2. robot, 3. agent, 4. category, 5. faq, 6. quickbutton, 7. workgroup, 
            bytedeskEventPublisher.publishOrganizationCreateEvent(organization);
            //
            return convertToResponse(savedOrganization);

        } catch (Exception e) {
            // 如果在事务中发生异常，则重新抛出以触发事务回滚
            e.printStackTrace();
            throw new RuntimeException("Error occurred during organization creation", e);
        }
    }

    public OrganizationResponse update(OrganizationRequest organizationRequest) {

        // 查找要更新的组织
        Optional<Organization> organizationOptional = findByUid(organizationRequest.getUid());
        if (!organizationOptional.isPresent()) {
            // 如果组织不存在，可以抛出一个自定义异常，例如OrganizationNotFoundException
            throw new RuntimeException("Organization with UID: " + organizationRequest.getUid() + " not found.");
        }

        // 获取要更新的组织实体
        Organization organization = organizationOptional.get();
        // 使用ModelMapper进行属性拷贝，避免逐一设置字段
        // modelMapper.map(organizationRequest, organization); // 一些默认值会被清空，待前端支持完善之后再启用
        organization.setName(organizationRequest.getName());
        organization.setLogo(organizationRequest.getLogo());
        organization.setCode(organizationRequest.getCode());
        organization.setDescription(organizationRequest.getDescription());

        // 保存更新后的组织
        Organization updatedOrganization = save(organization);

        if (updatedOrganization == null) {
            throw new RuntimeException("Failed to update organization.");
        }

        // 转换为响应对象
        return convertToResponse(updatedOrganization);
    }

    @Cacheable(value = "organization", key = "#uid", unless = "#result == null")
    public Optional<Organization> findByUid(String uid) {
        return organizationRepository.findByUid(uid);
    }

    @Cacheable(value = "organization", key = "#name", unless = "#result == null")
    public Optional<Organization> findByName(String name) {
        return organizationRepository.findByNameAndDeleted(name, false);
    }

    @Cacheable(value = "organization", key = "#code", unless = "#result == null")
    public Optional<Organization> findByCode(String code) {
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
    public Organization save(Organization organization) {
        try {
            return organizationRepository.save(organization);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, organization);
        }
        return null;
    }
    
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, 
            Organization organization) {
        log.info("handleOptimisticLockingFailureException: " + e.getMessage());
    }

    public OrganizationResponse convertToResponse(Organization organization) {
        return modelMapper.map(organization, OrganizationResponse.class);
    }

    public void initData() {

        if (organizationRepository.count() > 0) {
            return;
        }
        //
        Optional<User> adminOptional = userService.getAdmin();
        if (adminOptional.isPresent()) {
            //
            Organization organization = Organization.builder()
                    .name(bytedeskProperties.getOrganizationName())
                    .code(bytedeskProperties.getOrganizationCode())
                    .description(bytedeskProperties.getOrganizationName() + " Description")
                    .user(adminOptional.get())
                    .build();
            organization.setUid(UserConsts.DEFAULT_ORGANIZATION_UID);
            save(organization);
        }

    }

}
