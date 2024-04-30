/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-23 18:18:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.organization;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OrganizationService {

    private final AuthService authService;

    private final UserService userService;

    private final BytedeskProperties properties;

    private final OrganizationRepository organizationRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    public Page<OrganizationResponse> query(BaseRequest pageParam) {

        User user = authService.getCurrentUser();

        Pageable pageable = PageRequest.of(pageParam.getPageNumber(), pageParam.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Organization> orgPage = organizationRepository.findByUser(user, pageable);

        return orgPage.map(organization -> convertToOrganizationResponse(organization));
    }

    @Cacheable(value = "organization", key = "#oid", unless = "#result == null")
    public Optional<Organization> findByOid(String oid) {
        return organizationRepository.findByOid(oid);
    }

    @Cacheable(value = "organization", key = "#name", unless = "#result == null")
    public Optional<Organization> findByName(String name) {
        return organizationRepository.findFirstByName(name);
    }

    @Caching(put = {
        @CachePut(value = "organization", key = "#organization.oid"),
        @CachePut(value = "organization", key = "#organization.name")
    })
    public Organization save(Organization organization) {
        return organizationRepository.save(organization);
    }

    public OrganizationResponse convertToOrganizationResponse(Organization organization) {
        return modelMapper.map(organization, OrganizationResponse.class);
    }

    public void initData() {

        if (organizationRepository.count() > 0) {
            log.debug("organization already exist");
            return;
        }
        // 
        Optional<User> adminOptional = userService.getAdmin();
        if (adminOptional.isPresent()) {
            //
            Organization organization = Organization.builder()
                    .oid(uidUtils.getCacheSerialUid())
                    .name(properties.getCompany())
                    .description(properties.getCompany() + " Description")
                    .user(adminOptional.get())
                .build();
            save(organization);
            //
            adminOptional.get().getOrganizations().add(organization.getOid());
            userService.save(adminOptional.get());
        }
        
    }

}
