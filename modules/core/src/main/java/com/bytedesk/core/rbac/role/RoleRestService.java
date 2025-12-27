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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class RoleRestService extends BaseRestService<RoleEntity, RoleRequest, RoleResponse> {

        private final RoleRepository roleRepository;

        private final UidUtils uidUtils;

        private final AuthorityRestService authorityRestService;

        private final AuthService authService;

        private final PermissionService permissionService;

        private final ModelMapper modelMapper;

        private final CacheManager cacheManager;

        @Override
        public Page<RoleResponse> queryByOrg(RoleRequest request) {
                UserEntity user = authService.getUser();
                if (user == null) {
                        return null;
                }
                // 非超级管理员查询系统角色：将请求提升为平台级查询，并固定到默认组织
                // - 避免 BaseSpecification 对 orgUid 的强制校验
                // - 避免将 orgUid 绑定为用户 org 导致查询不到系统角色
                if (!user.isSuperUser() && request != null && Boolean.TRUE.equals(request.getSystem())) {
                        request.setSuperUser(false);
                        if (!StringUtils.hasText(request.getLevel())) {
                                request.setLevel(LevelEnum.PLATFORM.name());
                        }
                        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
                } else {
                        bindOrgUidForNonSuper(request, user);
                }
                // 
                Pageable pageable = request.getPageable();
                Specification<RoleEntity> specification = RoleSpecification.search(request, authService);
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
                if (!user.isSuperUser() && request != null && Boolean.TRUE.equals(request.getSystem())) {
                        request.setSuperUser(false);
                        if (!StringUtils.hasText(request.getLevel())) {
                                request.setLevel(LevelEnum.PLATFORM.name());
                        }
                        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
                } else {
                        bindOrgUidForNonSuper(request, user);
                }
                //
                return queryByOrg(request);
        }

        @Override
        public RoleResponse queryByUid(RoleRequest request) {
                UserEntity user = authService.getUser();
                if (user == null) {
                        return null;
                }
                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        // assertRoleAccessible(roleOptional.get(), user);
                        return convertToResponse(roleOptional.get());
                }
                return null;
        }

        @Cacheable(value = "role", key = "'uid:' + #p0", unless = "#result == null")
        @Override
        public Optional<RoleEntity> findByUid(String uid) {
                return roleRepository.findByUid(uid);
        }

        @Cacheable(value = "role", key = "'nameOrg:' + #p0 + '-' + #p1", unless = "#result == null")
        public Optional<RoleEntity> findByNameAndOrgUid(String name, String orgUid) {
                return roleRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
        }

        @Cacheable(value = "role", key = "'namePlatform:' + #p0", unless = "#result == null")
        public Optional<RoleEntity> findByNamePlatform(String name) {
                LevelEnum preferredLevel = LevelEnum.PLATFORM;

                Optional<RoleEntity> roleOptional = roleRepository.findByNameAndLevel(name, preferredLevel.name());
                if (roleOptional.isPresent()) {
                        return roleOptional;
                }

                for (LevelEnum level : LevelEnum.values()) {
                        if (level == preferredLevel) {
                                continue;
                        }
                        roleOptional = roleRepository.findByNameAndLevel(name, level.name());
                        if (roleOptional.isPresent()) {
                                return roleOptional;
                        }
                }

                return Optional.empty();
        }

        public Boolean existsByUid(String uid) {
                return roleRepository.existsByUid(uid);
        }

        public RoleResponse create(RoleRequest request) {
                UserEntity user = authService.getUser();
                if (user != null) {
                        request.setUserUid(user.getUid());
                        // bindOrgUidForNonSuper(request, user);
                }

                // 判断uid是否已存在
                if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
                        return convertToResponse(findByUid(request.getUid()).get());
                }
                if (StringUtils.hasText(request.getOrgUid())) {
                        if (existsByNameAndOrgUid(request.getName(), request.getOrgUid())) {
                                throw new RuntimeException("role " + request.getName() + " already exists");
                        }
                }

                // validateDelegableAuthorities(request.getAuthorityUids(), user);

                RoleEntity role = modelMapper.map(request, RoleEntity.class);
                if (StringUtils.hasText(request.getUid())) {
                        role.setUid(request.getUid());
                } else {
                        role.setUid(uidUtils.getUid());
                }
                //
                if (request.getAuthorityUids() != null) {
                        for (String authorityUid : request.getAuthorityUids()) {
                                Optional<AuthorityEntity> authorityOptional = authorityRestService
                                                .findByUid(authorityUid);
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
                UserEntity user = authService.getUser();
                if (user == null) {
                        throw new AccessDeniedException("Login required");
                }
                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        RoleEntity role = roleOptional.get();
                        // cleanupMissingAuthorityLinks(role);
                        String oldName = role.getName();
                        String oldOrgUid = role.getOrgUid();
                        // assertRoleAccessible(role, user);
                        // validateDelegableAuthorities(request.getAuthorityUids(), user);
                        // modelMapper.map(request, role);
                        if (StringUtils.hasText(request.getName())) {
                                role.setName(request.getName());
                        }
                        if (request.getDescription() != null) {
                                role.setDescription(request.getDescription());
                        }
                        // 仅当请求明确携带 authorityUids 时，才重建关联；否则保持原权限不变
                        if (request.getAuthorityUids() != null) {
                                role.getAuthorities().clear();
                                for (String authorityUid : request.getAuthorityUids()) {
                                        Optional<AuthorityEntity> authorityOptional = authorityRestService
                                                        .findByUid(authorityUid);
                                        authorityOptional.ifPresent(role::addAuthority);
                                }
                        }
                        //
                        RoleEntity savedRole = save(role);
                        if (savedRole == null) {
                                throw new RuntimeException("role " + request.getUid() + " update failed");
                        }

                        // 如果更新了名称，清理旧的 name-org 关联缓存 key（避免按旧 name 查询到过期数据）
                        if (StringUtils.hasText(oldName) && StringUtils.hasText(oldOrgUid)
                                        && !oldName.equals(savedRole.getName())) {
                                Cache roleCache = cacheManager.getCache("role");
                                if (roleCache != null) {
                                        roleCache.evict("nameOrg:" + oldName + "-" + oldOrgUid);
                                }
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

        // @Override
        public RoleExcel convertToExcel(RoleEntity entity) {
                return modelMapper.map(entity, RoleExcel.class);
        }

        @Cacheable(value = "roleExists", key = "#name + '-' + #orgUid", unless = "#result == null")
        public Boolean existsByNameAndOrgUid(String name, String orgUid) {
                return roleRepository.existsByNameAndOrgUidAndDeletedFalse(name, orgUid);
        }

        // 查询平台级别角色名是否存在
        public Boolean existsByNamePlatform(String name) {
                return roleRepository.existsByNameAndLevel(name, LevelEnum.PLATFORM.name());
        }

        @Caching(evict = {
                // 更新实体后，驱逐相关缓存，确保 queryByUid/findByUid/findByNameAndOrgUid 不会返回旧数据
                @CacheEvict(value = "role", key = "'resp:' + #role.uid"),
                @CacheEvict(value = "role", key = "'uid:' + #role.uid"),
                @CacheEvict(value = "role", key = "'nameOrg:' + #role.name + '-' + #role.orgUid")
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
                return RoleSpecification.search(request, authService);
        }

        @Override
        protected Page<RoleEntity> executePageQuery(Specification<RoleEntity> spec, Pageable pageable) {
                return roleRepository.findAll(spec, pageable);
        }

        @Transactional
        public RoleResponse resetAuthorities(RoleRequest request) {
                // 
                if (request == null || !StringUtils.hasText(request.getUid())) {
                        throw new IllegalArgumentException("role uid is required for reset");
                }

                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isEmpty()) {
                        throw new RuntimeException("role " + request.getUid() + " not found");
                }
                RoleEntity role = roleOptional.get();
                // LevelEnum level = resolveTargetLevel(role, request);

                // 新权限模型（MODULE_ACTION）不再依赖 "_LEVEL_" marker。
                // 这里按 RoleInitializer 的规则恢复默认权限：
                // - SUPER: 全量权限
                // - ADMIN: 除 SETTINGS_CREATE/SETTINGS_UPDATE 外的所有权限
                // - AGENT: 知识库模块所有 *_READ 权限
                // - USER: 基础权限（用户/消息/会话/工单）
                // - 其他角色：退化为恢复全部启用权限
                // 兼容历史数据：可能存在仍携带 _PLATFORM_/_ORGANIZATION_ 等 marker 或同 value 多条记录
                Set<AuthorityEntity> allActiveRaw = authorityRestService.findAllActive();
                Map<String, AuthorityEntity> byValue = new LinkedHashMap<>();
                for (AuthorityEntity authority : allActiveRaw) {
                        if (authority == null || !StringUtils.hasText(authority.getValue())) {
                                continue;
                        }
                        String value = authority.getValue();
                        if (containsLevelMarker(value)) {
                                continue;
                        }

                        AuthorityEntity existing = byValue.get(value);
                        if (existing == null) {
                                byValue.put(value, authority);
                                continue;
                        }

                        boolean authorityIsPlatform = LevelEnum.PLATFORM.name().equalsIgnoreCase(authority.getLevel());
                        boolean existingIsPlatform = LevelEnum.PLATFORM.name().equalsIgnoreCase(existing.getLevel());
                        if (authorityIsPlatform && !existingIsPlatform) {
                                byValue.put(value, authority);
                        }
                }
                Set<AuthorityEntity> allActive = new HashSet<>(byValue.values());
                Set<AuthorityEntity> selectedAuthorities;

                String roleUid = role.getUid();
                if (BytedeskConsts.DEFAULT_ROLE_SUPER_UID.equals(roleUid)) {
                        selectedAuthorities = allActive;
                } else if (BytedeskConsts.DEFAULT_ROLE_ADMIN_UID.equals(roleUid)) {
                        selectedAuthorities = allActive.stream()
                                        .filter(a -> a != null && a.getValue() != null)
                                        .filter(a -> !RoleAuthorityRules.isAdminExcludedPermission(a.getValue()))
                                        .collect(Collectors.toSet());
                } else if (BytedeskConsts.DEFAULT_ROLE_AGENT_UID.equals(roleUid)) {
                        selectedAuthorities = allActive.stream()
                                        .filter(a -> a != null
                                                        && RoleAuthorityRules.isKbaseReadPermission(a.getValue()))
                                        .collect(Collectors.toSet());
                } else if (BytedeskConsts.DEFAULT_ROLE_USER_UID.equals(roleUid)) {
                        selectedAuthorities = allActive.stream()
                                        .filter(a -> a != null && a.getValue() != null)
                                        .filter(a -> RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES
                                                        .contains(a.getValue()))
                                        .collect(Collectors.toSet());
                } else {
                        selectedAuthorities = allActive;
                }

                // 重置默认系统角色描述为 i18n key（与 RoleInitializer 保持一致）
                if (BytedeskConsts.DEFAULT_ROLE_SUPER_UID.equals(roleUid)) {
                        role.setName(RoleConsts.ROLE_SUPER);
                        role.setValue(RoleConsts.ROLE_SUPER);
                        role.setDescription(I18Consts.I18N_ROLE_SUPER_DESCRIPTION);
                } else if (BytedeskConsts.DEFAULT_ROLE_ADMIN_UID.equals(roleUid)) {
                        role.setName(RoleConsts.ROLE_ADMIN);
                        role.setValue(RoleConsts.ROLE_ADMIN);
                        role.setDescription(I18Consts.I18N_ROLE_ADMIN_DESCRIPTION);
                } else if (BytedeskConsts.DEFAULT_ROLE_AGENT_UID.equals(roleUid)) {
                        role.setName(RoleConsts.ROLE_AGENT);
                        role.setValue(RoleConsts.ROLE_AGENT);
                        role.setDescription(I18Consts.I18N_ROLE_AGENT_DESCRIPTION);
                } else if (BytedeskConsts.DEFAULT_ROLE_USER_UID.equals(roleUid)) {
                        role.setName(RoleConsts.ROLE_USER);
                        role.setValue(RoleConsts.ROLE_USER);
                        role.setDescription(I18Consts.I18N_ROLE_USER_DESCRIPTION);
                }

                // cleanupMissingAuthorityLinks(role);
                role.getAuthorities().clear();
                selectedAuthorities.forEach(role::addAuthority);
                // role.setLevel(level.name());

                RoleEntity savedRole = save(role);
                if (savedRole == null) {
                        throw new RuntimeException("role " + request.getUid() + " reset failed");
                }
                return convertToResponse(savedRole);
        }

        private static boolean containsLevelMarker(String authorityValue) {
                if (!StringUtils.hasText(authorityValue)) {
                        return false;
                }
                return authorityValue.contains("_PLATFORM_")
                                || authorityValue.contains("_ORGANIZATION_")
                                || authorityValue.contains("_DEPARTMENT_")
                                || authorityValue.contains("_WORKGROUP_")
                                || authorityValue.contains("_AGENT_")
                                || authorityValue.contains("_USER_");
        }

        @Transactional
        public RoleResponse addAuthoritiesSystem(RoleRequest request) {
                return addAuthoritiesInternal(request, true);
        }

        @Transactional
        public RoleResponse addAuthorities(RoleRequest request) {
                return addAuthoritiesInternal(request, false);
        }

        @Transactional
        public RoleResponse removeAuthorities(RoleRequest request) {
                return removeAuthoritiesInternal(request, false);
        }

        private RoleResponse removeAuthoritiesInternal(RoleRequest request, boolean systemContext) {
                UserEntity user = authService.getUser();
                if (!systemContext) {
                        if (user == null) {
                                throw new AccessDeniedException("Login required");
                        }
                }

                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        RoleEntity role = roleOptional.get();
                        // cleanupMissingAuthorityLinks(role);
                        if (!systemContext) {
                                assertRoleAccessible(role, user);
                                validateDelegableAuthorities(request.getAuthorityUids(), user);
                        }

                        if (request.getAuthorityUids() != null && !request.getAuthorityUids().isEmpty()) {
                                Set<String> removeUids = request.getAuthorityUids();
                                role.getAuthorities().removeIf(a -> a != null && removeUids.contains(a.getUid()));
                        }

                        RoleEntity savedRole = save(role);
                        if (savedRole == null) {
                                throw new RuntimeException("role " + request.getUid() + " update failed");
                        }
                        return convertToResponse(savedRole);
                } else {
                        throw new RuntimeException("role " + request.getUid() + " not found");
                }
        }

        private RoleResponse addAuthoritiesInternal(RoleRequest request, boolean systemContext) {
                UserEntity user = authService.getUser();
                if (!systemContext) {
                        if (user == null) {
                                throw new AccessDeniedException("Login required");
                        }
                }

                Optional<RoleEntity> roleOptional = findByUid(request.getUid());
                if (roleOptional.isPresent()) {
                        RoleEntity role = roleOptional.get();
                        // cleanupMissingAuthorityLinks(role);
                        if (!systemContext) {
                                assertRoleAccessible(role, user);
                                validateDelegableAuthorities(request.getAuthorityUids(), user);
                        }

                        if (request.getAuthorityUids() != null) {
                                for (String authorityUid : request.getAuthorityUids()) {
                                        Optional<AuthorityEntity> authorityOptional = authorityRestService
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

        private void bindOrgUidForNonSuper(RoleRequest request, UserEntity user) {
                if (request == null || user == null) {
                        return;
                }
                if (!user.isSuperUser()) {
                        request.setOrgUid(user.getOrgUid());
                        request.setSuperUser(false);
                }
        }

        private void assertRoleAccessible(RoleEntity role, UserEntity user) {
                if (role == null || user == null) {
                        throw new AccessDeniedException("Login required");
                }
                if (user.isSuperUser()) {
                        return;
                }

                String userOrgUid = user.getOrgUid();
                String roleOrgUid = role.getOrgUid();
                boolean isPlatformDefaultRole = LevelEnum.PLATFORM.name().equalsIgnoreCase(role.getLevel())
                                && BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(roleOrgUid);

                if (isPlatformDefaultRole) {
                        // 平台默认组织下的平台角色，仅允许超级管理员访问
                        throw new AccessDeniedException("No permission to access platform role");
                }

                if (!StringUtils.hasText(userOrgUid) || !StringUtils.hasText(roleOrgUid)
                                || !userOrgUid.equals(roleOrgUid)) {
                        throw new AccessDeniedException("No permission to access role of other organization");
                }
        }

        private void validateDelegableAuthorities(Set<String> authorityUids, UserEntity user) {
                if (authorityUids == null || authorityUids.isEmpty()) {
                        return;
                }
                if (user != null && user.isSuperUser()) {
                        return;
                }
                Set<String> currentAuthorities = permissionService.getCurrentUserAuthorities();
                for (String authorityUid : authorityUids) {
                        Optional<AuthorityEntity> authorityOptional = authorityRestService.findByUid(authorityUid);
                        if (authorityOptional.isEmpty()) {
                                continue;
                        }
                        String value = authorityOptional.get().getValue();
                        if (StringUtils.hasText(value) && !currentAuthorities.contains(value)) {
                                throw new AccessDeniedException("No permission to delegate authority: " + value);
                        }
                }
        }

}
