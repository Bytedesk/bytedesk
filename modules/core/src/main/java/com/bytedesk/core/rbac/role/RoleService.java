/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-27 12:55:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

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
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.authority.Authority;
import com.bytedesk.core.rbac.authority.AuthorityService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdConvertUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

@AllArgsConstructor
// @Slf4j
@Service
public class RoleService {

        private final RoleRepository roleRepository;

        private final UserRepository userRepository;

        private final BytedeskProperties properties;

        private final ModelMapper modelMapper;

        private final UidUtils uidUtils;

        private final AuthorityService authorityService;

        public Role create(RoleRequest rolerRequest) {

                if (existsByName(rolerRequest.getName())) {
                        throw new RuntimeException("角色已存在");
                }

                Role role = modelMapper.map(rolerRequest, Role.class);
                role.setRid(uidUtils.getCacheSerialUid());
                // 
                Iterator<String> iterator = rolerRequest.getAuthorityAids().iterator();
                while (iterator.hasNext()) {
                        String authorityAid = iterator.next();
                        Optional<Authority> authorityOptional = authorityService.findByAid(authorityAid);
                        if (authorityOptional.isPresent()) {
                                role.addAuthority(authorityOptional.get());
                        }
                }
                // 
                return save(role);
        }

        public Page<RoleResponse> query(RoleRequest roleRequest) {

                Pageable pageable = PageRequest.of(roleRequest.getPageNumber(), roleRequest.getPageSize(),
                                Sort.Direction.DESC,
                                "id");

                Page<Role> rolePage = roleRepository.findByOrgOid(roleRequest.getOrgOid(), pageable);

                return rolePage.map(BdConvertUtils::convertToRoleResponse);
        }
        
        @Cacheable(value = "roleExists", key = "#name", unless="#result == null")
        public Boolean existsByName(String namString) {
                return roleRepository.existsByName(namString);
        }

        @Cacheable(value = "role", key = "#name", unless="#result == null")
        public Optional<Role> findByName(String name) {
                return roleRepository.findByName(name);
        }

        @Caching(put = {
                @CachePut(value = "role", key = "#role.name"),
                // TODO: 此处put的exists内容跟缓存时内容类型是否一致？
                // @CachePut(value = "roleExists", key = "#role.name")
        })
        public Role save(Role role) {
                return roleRepository.save(role);
        }

        // 
        // private static final String [] roles = {
        //         TypeConsts.ROLE_ADMIN,
        //         TypeConsts.ROLE_ORG,
        //         TypeConsts.ROLE_IT,
        //         TypeConsts.ROLE_MONEY,
        //         TypeConsts.ROLE_HR,
        //         TypeConsts.ROLE_MARKETING,
        //         TypeConsts.ROLE_SALES,
        //         TypeConsts.ROLE_CUSTOMER_SERVICE,
        // };
        // 
        private static final String[] authorities = {
            TypeConsts.AUTHORITY_SUPER,
            TypeConsts.AUTHORITY_ADMIN,
            TypeConsts.AUTHORITY_HR,
            TypeConsts.AUTHORITY_ORG,
            TypeConsts.AUTHORITY_IT,
            TypeConsts.AUTHORITY_MONEY,
            TypeConsts.AUTHORITY_MARKETING,
            TypeConsts.AUTHORITY_SALES,
            TypeConsts.AUTHORITY_CUSTOMER_SERVICE,
        };

        public void initData() {
                if (roleRepository.count() > 0) {
                        // log.debug("role already exists");
                        return;
                }
                //
                for (String authority : authorities) {
                        String role = TypeConsts.ROLE_ + authority;
                        RoleRequest roleRequest = RoleRequest.builder().name(role).description(role).build();
                        roleRequest.setType(TypeConsts.TYPE_SYSTEM);
                        // 
                        Optional<Authority> authorityOptional = authorityService.findByValue(authority);
                        if (authorityOptional.isPresent()) {
                                roleRequest.getAuthorityAids().add(authorityOptional.get().getAid());
                        }
                        // 
                        create(roleRequest);
                }
        }

        public void updateInitData() {
                //
                Optional<User> adminOptional = userRepository.findByEmail(properties.getEmail());
                if (adminOptional.isPresent()) {
                        //
                        Arrays.stream(authorities).forEach((authority) -> {
                                String roleName = TypeConsts.ROLE_ + authority;
                                Optional<Role> roleOptional = findByName(roleName);
                                roleOptional.ifPresent(role -> {
                                        role.setOrgOid(adminOptional.get().getOrgOid());
                                        roleRepository.save(role);
                                });
                        });
                }
                
        }

}
