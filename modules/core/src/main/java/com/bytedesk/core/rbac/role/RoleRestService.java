/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 08:48:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import java.util.Iterator;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class RoleRestService extends BaseRestService<RoleEntity, RoleRequest, RoleResponse> {

        private final RoleRepository roleRepository;

        private final UidUtils uidUtils;

        private final AuthorityRestService authorityService;

        private final AuthService authService;

        private final ModelMapper modelMapper;

        public Page<RoleResponse> queryByOrg(RoleRequest request) {
                Pageable pageable = request.getPageable();
                Specification<RoleEntity> specification = RoleSpecification.search(request);
                Page<RoleEntity> rolePage = roleRepository.findAll(specification, pageable);
                return rolePage.map(this::convertToResponse);
        }

        @Override
        public Page<RoleResponse> queryByUser(RoleRequest request) {
                UserEntity user = authService.getUser();
                if (user == null) {
                        return null;
                }
                request.setUserUid(user.getUid());
                //
                return queryByOrg(request);
        }

        @Cacheable(value = "role", key = "#uid", unless = "#result == null")
        @Override
        public RoleResponse queryByUid(RoleRequest request) {
                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        return convertToResponse(roleOptional.get());
                }
                return null;
        }

        @Cacheable(value = "role", key = "#uid", unless = "#result == null")
        @Override
        public Optional<RoleEntity> findByUid(String uid) {
                return roleRepository.findByUid(uid);
        }

        @Cacheable(value = "role", key = "#name + '-' + #orgUid", unless = "#result == null")
        public Optional<RoleEntity> findByNameAndOrgUid(String name, String orgUid) {
                return roleRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
        }

        @Cacheable(value = "role", key = "#name", unless = "#result == null")
        public Optional<RoleEntity> findByNamePlatform(String name) {
                return roleRepository.findByNameAndLevel(name, LevelEnum.PLATFORM.name());
        }

        public Boolean existsByUid(String uid) {
                return roleRepository.existsByUid(uid);
        }

        public RoleResponse create(RoleRequest request) {
                // 判断uid是否已存在
                if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
                        return convertToResponse(findByUid(request.getUid()).get());
                }
                if (StringUtils.hasText(request.getOrgUid())) {
                        if (existsByNameAndOrgUid(request.getName(), request.getOrgUid())) {
                                throw new RuntimeException("role " + request.getName() + " already exists");
                        }
                }
                UserEntity user = authService.getUser();
                if (user != null) {
                        request.setUserUid(user.getUid());
                }
                RoleEntity role = modelMapper.map(request, RoleEntity.class);
                if (StringUtils.hasText(request.getUid())) {
                        role.setUid(request.getUid());
                } else {
                        role.setUid(uidUtils.getUid());
                }
                //
                if (request.getAuthorityUids() != null) {
                        Iterator<String> iterator = request.getAuthorityUids().iterator();
                        while (iterator.hasNext()) {
                                String authorityUid = iterator.next();
                                Optional<AuthorityEntity> authorityOptional = authorityService.findByUid(authorityUid);
                                if (authorityOptional.isPresent()) {
                                        role.addAuthority(authorityOptional.get());
                                }
                        }
                }
                //
                RoleEntity savedEntity = save(role);
                if (savedEntity == null) {
                        throw new RuntimeException("role " + request.getName() + " create failed");
                }
                //
                return convertToResponse(savedEntity);
        }

        @Override
        public RoleResponse update(RoleRequest request) {
                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        RoleEntity role = roleOptional.get();
                        // modelMapper.map(request, role);
                        role.setName(request.getName());
                        role.setDescription(request.getDescription());
                        //
                        role.getAuthorities().clear();
                        if (request.getAuthorityUids() != null) {
                                for (String authorityUid : request.getAuthorityUids()) {
                                        Optional<AuthorityEntity> authorityOptional = authorityService
                                                        .findByUid(authorityUid);
                                        authorityOptional.ifPresent(role::addAuthority);
                                }
                        }
                        //
                        RoleEntity savedRole = save(role);
                        if (savedRole == null) {
                                throw new RuntimeException("role " + request.getUid() + " update failed");
                        }
                        return convertToResponse(savedRole);
                } else {
                        throw new RuntimeException("role " + request.getUid() + " not found");
                }
        }

        @Transactional
        public RoleResponse addAuthorities(RoleRequest request) {
                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        RoleEntity role = roleOptional.get();
                        if (request.getAuthorityUids() != null) {
                                for (String authorityUid : request.getAuthorityUids()) {
                                        Optional<AuthorityEntity> authorityOptional = authorityService
                                                        .findByUid(authorityUid);
                                        authorityOptional.ifPresent(role::addAuthority);
                                }
                        }
                        //
                        RoleEntity savedRole = save(role);
                        if (savedRole == null) {
                                throw new RuntimeException("role " + request.getUid() + " update failed");
                        }
                        return convertToResponse(savedRole);
                } else {
                        throw new RuntimeException("role " + request.getUid() + " not found");
                }
        }

        @Override
        public void deleteByUid(String uid) {
                Optional<RoleEntity> roleOptional = findByUid(uid);
                if (roleOptional.isPresent()) {
                        RoleEntity role = roleOptional.get();
                        role.setDeleted(true);
                        save(role);
                }
        }

        @Override
        public void delete(RoleRequest request) {
                deleteByUid(request.getUid());
        }

        @Override
        public RoleResponse convertToResponse(RoleEntity entity) {
                return ConvertUtils.convertToRoleResponse(entity);
        }

        @Cacheable(value = "roleExists", key = "#name + '-' + #orgUid", unless = "#result == null")
        public Boolean existsByNameAndOrgUid(String name, String orgUid) {
                return roleRepository.existsByNameAndOrgUidAndDeletedFalse(name, orgUid);
        }

        // 查询平台级别角色名是否存在
        public Boolean existsByNamePlatform(String name) {
                return roleRepository.existsByNameAndLevel(name, LevelEnum.PLATFORM.name());
        }

        @Caching(put = {
                        @CachePut(value = "role", key = "#role.name+ '-' + #role.orgUid"),
        })
        public RoleEntity save(RoleEntity role) {
                try {
                        return doSave(role);
                } catch (ObjectOptimisticLockingFailureException e) {
                        return handleOptimisticLockingFailureException(e, role);
                }
        }

        @Override
        protected RoleEntity doSave(RoleEntity entity) {
                return roleRepository.save(entity);
        }

        @Override
        public RoleEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
                        RoleEntity entity) {
                try {
                        Optional<RoleEntity> latest = roleRepository.findByUid(entity.getUid());
                        if (latest.isPresent()) {
                                RoleEntity latestEntity = latest.get();
                                // 合并需要保留的数据
                                return roleRepository.save(latestEntity);
                        }
                } catch (Exception ex) {
                        throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
                }
                return null;
        }

        @Override
        protected Specification<RoleEntity> createSpecification(RoleRequest request) {
                return RoleSpecification.search(request);
        }

        @Override
        protected Page<RoleEntity> executePageQuery(Specification<RoleEntity> spec, Pageable pageable) {
                return roleRepository.findAll(spec, pageable);
        }

}
