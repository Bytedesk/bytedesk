/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-26 06:44:50
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.AuthorityService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import java.util.Iterator;
import java.util.Optional;

@AllArgsConstructor
// @Slf4j
@Service
public class RoleService {

        private final RoleRepository roleRepository;

        private final ModelMapper modelMapper;

        private final UidUtils uidUtils;

        private final AuthorityService authorityService;

        public RoleEntity create(RoleRequest rolerRequest) {

                if (existsByNameAndOrgUid(rolerRequest.getName(), rolerRequest.getOrgUid())) {
                        throw new RuntimeException("role " + rolerRequest.getName() + " already exists");
                }

                RoleEntity role = modelMapper.map(rolerRequest, RoleEntity.class);
                role.setUid(uidUtils.getCacheSerialUid());
                role.setType(RoleTypeEnum.fromValue(rolerRequest.getType()).name());
                //
                Iterator<String> iterator = rolerRequest.getAuthorityUids().iterator();
                while (iterator.hasNext()) {
                        String authorityUid = iterator.next();
                        Optional<AuthorityEntity> authorityOptional = authorityService.findByUid(authorityUid);
                        if (authorityOptional.isPresent()) {
                                role.addAuthority(authorityOptional.get());
                        }
                }
                //
                return save(role);
        }

        public Page<RoleResponse> queryByOrg(RoleRequest roleRequest) {

                Pageable pageable = PageRequest.of(roleRequest.getPageNumber(), roleRequest.getPageSize(),
                                Sort.Direction.ASC,
                                "id");

                Specification<RoleEntity> specification = RoleSpecification.search(roleRequest);
                Page<RoleEntity> rolePage = roleRepository.findAll(specification, pageable);

                return rolePage.map(ConvertUtils::convertToRoleResponse);
        }

        @Cacheable(value = "roleExists", key = "#name + '-' + #orgUid", unless = "#result == null")
        public Boolean existsByNameAndOrgUid(String name, String orgUid) {
                return roleRepository.existsByNameAndOrgUidAndDeleted(name, orgUid, false);
        }

        @Cacheable(value = "role", key = "#name + '-' + #orgUid", unless = "#result == null")
        public Optional<RoleEntity> findByNameAndOrgUid(String name, String orgUid) {
                return roleRepository.findByNameAndOrgUidAndDeleted(name, orgUid, false);
        }

        @Caching(put = {
                        @CachePut(value = "role", key = "#role.name+ '-' + #role.orgUid"),
        })
        public RoleEntity save(RoleEntity role) {
                return roleRepository.save(role);
        }

        //
        private static final String[] authorities = {
                        TypeConsts.SUPER,
                        TypeConsts.ADMIN,
                        TypeConsts.HR,
                        TypeConsts.ORG,
                        TypeConsts.IT,
                        TypeConsts.MONEY,
                        TypeConsts.MARKETING,
                        TypeConsts.SALES,
                        TypeConsts.CUSTOMER_SERVICE,
        };

        public void initOrgRoles(String orgUid) {
                //
                for (String authority : authorities) {
                        String name = TypeConsts.ROLE_PREFIX + authority;
                        String displayName = I18Consts.I18N_PREFIX + name;
                        RoleRequest roleRequest = RoleRequest.builder()
                                        .name(name)
                                        .displayName(displayName)
                                        .description(name)
                                        .build();
                        roleRequest.setType(TypeConsts.TYPE_SYSTEM);
                        roleRequest.setOrgUid(orgUid);
                        //
                        Optional<AuthorityEntity> authorityOptional = authorityService.findByValue(authority);
                        if (authorityOptional.isPresent()) {
                                roleRequest.getAuthorityUids().add(authorityOptional.get().getUid());
                        }
                        //
                        create(roleRequest);
                }
        }

        public void initData() {
                //
                if (roleRepository.count() > 0) {
                        return;
                }
                //
                initOrgRoles(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        }

}
