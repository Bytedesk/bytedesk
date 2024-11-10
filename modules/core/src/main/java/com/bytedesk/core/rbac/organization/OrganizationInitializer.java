/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 12:40:31
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

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.authority.AuthorityInitializer;
import com.bytedesk.core.rbac.role.RoleConsts;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleInitializer;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserInitializer;
import com.bytedesk.core.rbac.user.UserService;

// import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class OrganizationInitializer implements SmartInitializingSingleton {

    private final AuthorityInitializer authorityInitializer;

    private final RoleInitializer roleInitializer;

    private final UserInitializer userInitializer;

    protected final OrganizationRepository organizationRepository;

    private final BytedeskProperties bytedeskProperties;

    private final UserService userService;

    private final OrganizationService organizationService;

    private final RoleService roleService;

    @Override
    public void afterSingletonsInstantiated() {
        // 
        authorityInitializer.init();
        roleInitializer.init();
        userInitializer.init();
        // 
        initOrganization();
    }

    // @PostConstruct
    private void initOrganization() {
        // 
        if (organizationRepository.count() > 0) {
            return;
        }
        log.info("Organization: Default Organization Initializing...");
        //
        Optional<UserEntity> adminOptional = userService.getAdmin();
        if (adminOptional.isPresent()) {
            UserEntity user = adminOptional.get();
            //
            OrganizationEntity organization = OrganizationEntity.builder()
                    .name(bytedeskProperties.getOrganizationName())
                    .code(bytedeskProperties.getOrganizationCode())
                    .description(bytedeskProperties.getOrganizationName() + " Description")
                    .user(user)
                    .build();
            organization.setUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            //
           OrganizationEntity savedOrganization = organizationService.save(organization);
            if (savedOrganization != null) {
                Optional<RoleEntity> roleOptional = roleService.findByNamePlatform(RoleConsts.ROLE_SUPER);
                if (roleOptional.isPresent()) {
                    RoleEntity role = roleOptional.get();
                    user.addOrganizationRole(savedOrganization, role);
                    userService.save(user);
                } else {
                    throw new RuntimeException("Organization: Super Role Not Found");
                }
            } else {
                throw new RuntimeException("Organization: Default Organization Save Failed");
            }
        } else {
            throw new RuntimeException("Organization: Admin User Not Found");
        }
    }
    

}
