/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-02 11:07:32
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
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.utils.BdConvertUtils;
import com.bytedesk.core.utils.Utils;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
public class RoleService {

        private final RoleRepository roleRepository;

        private final UserRepository userRepository;

        private final BytedeskProperties properties;

        public Page<RoleResponse> query(User user, RoleRequest roleRequest) {
                
                Pageable pageable = PageRequest.of(roleRequest.getPageNumber(), roleRequest.getPageSize(), Sort.Direction.DESC,
                                "id");
                
                Page<Role> rolePage = roleRepository.findByUser(user, pageable);

                return rolePage.map(BdConvertUtils::convertToRoleResponse);
        }

        public Optional<Role> findByValue(String value) {
                return roleRepository.findByValue(value);
        }

        private static Role[] roles = new Role[] {
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_ADMIN).value(
                                                TypeConsts.ROLE_ADMIN)
                                .description(
                                                TypeConsts.ROLE_ADMIN)
                                .type(TypeConsts.TYPE_SYSTEM).build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_ORG).value(
                                                TypeConsts.ROLE_ORG)
                                .description(
                                                TypeConsts.ROLE_ORG)
                                .type(TypeConsts.TYPE_SYSTEM).build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_IT).value(
                                                TypeConsts.ROLE_IT)
                                .description(
                                                TypeConsts.ROLE_ADMIN)
                                .type(TypeConsts.TYPE_SYSTEM).build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_MONEY).value(
                                                TypeConsts.ROLE_MONEY)
                                .description(
                                                TypeConsts.ROLE_MONEY)
                                .type(TypeConsts.TYPE_SYSTEM).build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_HR).value(
                                                TypeConsts.ROLE_HR)
                                .description(
                                                TypeConsts.ROLE_HR)
                                .type(TypeConsts.TYPE_SYSTEM).build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_MARKETING).value(
                                                TypeConsts.ROLE_MARKETING)
                                .description(
                                                TypeConsts.ROLE_MARKETING)
                                .type(TypeConsts.TYPE_SYSTEM).build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_SALES).value(
                                                TypeConsts.ROLE_SALES)
                                .description(TypeConsts.ROLE_SALES).type(TypeConsts.TYPE_SYSTEM)
                                .build(),
                Role.builder().rid(Utils.getUid()).name(
                                TypeConsts.ROLE_CUSTOMER_SERVICE).value(
                                                TypeConsts.ROLE_CUSTOMER_SERVICE)
                                .description(TypeConsts.ROLE_CUSTOMER_SERVICE)
                                .type(TypeConsts.TYPE_SYSTEM).build(),

        };

        public void initData() {
                if (roleRepository.count() > 0) {
                        log.debug("role already exists");
                        return;
                }
                //
                Arrays.stream(roles).forEach((role) -> {
                        if (!roleRepository.existsByValue(role.getValue())) {
                                roleRepository.save(role);
                        }
                });
        }

        @SuppressWarnings("null")
        public void updateInitData() {


                //
                Optional<User> adminOptional = userRepository.findByEmail(properties.getEmail());
                if (adminOptional.isPresent()) {
                        //
                        Arrays.stream(roles).forEach((role) -> {
                                Optional<Role> roleOptional = roleRepository.findByValue(role.getValue());
                                if (roleOptional.isPresent()) {
                                        roleOptional.get().setUser(adminOptional.get());
                                        roleRepository.save(roleOptional.get());
                                }
                        });
                }
                
        }

}
