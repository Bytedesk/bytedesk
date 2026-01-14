/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-24 13:02:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 09:25:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRestService extends BaseRestServiceWithExport<UserEntity, UserRequest, UserResponse, UserExcel> {

    private final UserRepository userRepository;
    
    private final AuthService authService;

    private final UserService userService;

    private final UserDetailsServiceImpl userDetailsService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> queryByOrg(UserRequest request) {
        Pageable pageable = request.getPageable();
        Specification<UserEntity> spec = createSpecification(request);
        Page<UserEntity> page = executePageQuery(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<UserResponse> queryByUser(UserRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("Login required");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public UserResponse queryByUid(UserRequest request) {
        Optional<UserEntity> optionalEntity = findByUid(request.getUid());
        if (optionalEntity.isPresent()) {
            return convertToResponse(optionalEntity.get());
        } else {
            throw new RuntimeException("Entity not found for UID: " + request.getUid());
        }
    }

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    @Override
    public Optional<UserEntity> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public UserResponse getProfile() {
        UserEntity user = authService.getUser(); // 返回的是缓存，导致修改后的数据无法获取
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Optional<UserEntity> userOptional = findByUid(user.getUid());
        if (userOptional.isPresent()) {
            return convertToResponse(userOptional.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    /**
     * 获取当前用户所属组织列表（从 userOrganizationRoles + currentOrganization 归并去重）。
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<OrganizationResponseSimple> getOrganizations() {
        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new RuntimeException("Login required");
        }

        // 直接查库拿 managed entity，避免从缓存拿到 detached entity 导致懒加载失败
        UserEntity managedUser = userRepository.findByUid(authUser.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 保持插入顺序，优先把 currentOrganization 放在第一位
        LinkedHashMap<String, OrganizationResponseSimple> result = new LinkedHashMap<>();

        if (managedUser.getCurrentOrganization() != null
                && StringUtils.hasText(managedUser.getCurrentOrganization().getUid())) {
            OrganizationEntity org = managedUser.getCurrentOrganization();
            result.put(org.getUid(), ConvertUtils.toOrganizationResponseSimple(org));
        }

        if (managedUser.getUserOrganizationRoles() != null) {
            for (UserOrganizationRoleEntity uor : managedUser.getUserOrganizationRoles()) {
                if (uor == null || uor.getOrganization() == null || !StringUtils.hasText(uor.getOrganization().getUid())) {
                    continue;
                }
                OrganizationEntity org = uor.getOrganization();
                result.putIfAbsent(org.getUid(), ConvertUtils.toOrganizationResponseSimple(org));
            }
        }

        return new ArrayList<>(result.values());
    }

    /**
     * 切换当前组织：
     * - 校验用户是否属于该组织（superUser 允许任意已存在组织）
     * - 更新 currentOrganization
     * - 同步 currentRoles 为该组织维度的角色集合，并确保包含 ROLE_USER
     * - 刷新 SecurityContext，保证后续请求立即使用新的 orgUid/roles
     */
    @org.springframework.transaction.annotation.Transactional
    public UserResponse switchCurrentOrganization(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            throw new RuntimeException("orgUid is required");
        }

        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new RuntimeException("Login required");
        }

        UserEntity managedUser = userRepository.findByUid(authUser.getUid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // membership check
        if (!managedUser.isSuperUser()) {
            boolean isMember = false;

            if (managedUser.getCurrentOrganization() != null
                    && StringUtils.hasText(managedUser.getCurrentOrganization().getUid())
                    && orgUid.equals(managedUser.getCurrentOrganization().getUid())) {
                isMember = true;
            }

            if (!isMember && managedUser.getUserOrganizationRoles() != null) {
                for (UserOrganizationRoleEntity uor : managedUser.getUserOrganizationRoles()) {
                    if (uor == null || uor.getOrganization() == null || !StringUtils.hasText(uor.getOrganization().getUid())) {
                        continue;
                    }
                    if (orgUid.equals(uor.getOrganization().getUid())) {
                        isMember = true;
                        break;
                    }
                }
            }

            if (!isMember) {
                throw new RuntimeException("Access denied");
            }
        }

        // 1) switch current organization
        userService.ensureCurrentOrganization(managedUser, orgUid);

        // 2) sync currentRoles from userOrganizationRoles for that org
        managedUser.getUserOrganizationRoles().removeIf(u -> u == null || u.getOrganization() == null || !StringUtils.hasText(u.getOrganization().getUid()));

        Set<com.bytedesk.core.rbac.role.RoleEntity> rolesForOrg = new HashSet<>();
        for (UserOrganizationRoleEntity uor : managedUser.getUserOrganizationRoles()) {
            if (uor.getOrganization() != null && orgUid.equals(uor.getOrganization().getUid()) && uor.getRoles() != null) {
                rolesForOrg.addAll(uor.getRoles());
                break;
            }
        }

        managedUser.getCurrentRoles().clear();
        managedUser.getCurrentRoles().addAll(rolesForOrg);

        // 3) ensure ROLE_USER in the new org context
        UserEntity ensured = userService.addRoleUser(managedUser);

        // 4) persist + refresh cache
        UserEntity saved = userService.save(ensured);

        // 5) refresh security context so AuthService.getUser() sees latest org/roles immediately
        try {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            String platform = StringUtils.hasText(saved.getPlatform()) ? saved.getPlatform() : PlatformEnum.BYTEDESK.name();
            UserDetailsImpl refreshedDetails = userDetailsService.loadUserByUsernameAndPlatform(saved.getUsername(), platform);

            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    refreshedDetails,
                    currentAuth != null ? currentAuth.getCredentials() : null,
                    refreshedDetails.getAuthorities());

            if (currentAuth != null) {
                newAuth.setDetails(currentAuth.getDetails());
            }
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } catch (Exception ignored) {
            // If refresh fails, switching is still persisted; client can re-fetch profile.
        }

        return convertToResponse(saved);
    }

    @Override
    public UserResponse create(UserRequest request) {
        return userService.register(request);
    }

    @Override
    public UserResponse update(UserRequest request) {
        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new RuntimeException("Login required");
        }

        final String targetUid = StringUtils.hasText(request.getUid()) ? request.getUid() : authUser.getUid();

        if (!authUser.isSuperUser() && !authUser.getUid().equals(targetUid)) {
            throw new RuntimeException("Access denied");
        }

        // 更新时候不使用缓存，直接查询
        Optional<UserEntity> userOptional = userRepository.findByUid(targetUid);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        UserEntity userEntity = userOptional.get();

        // 非 super：仅允许修改自己的基础资料；敏感字段请走专用接口（changePassword/changeEmail/changeMobile）
        if (!authUser.isSuperUser()) {
            if (StringUtils.hasText(request.getNickname())) {
                userEntity.setNickname(request.getNickname());
            }
            if (StringUtils.hasText(request.getAvatar())) {
                userEntity.setAvatar(request.getAvatar());
            }
            if (request.getDescription() != null) {
                userEntity.setDescription(request.getDescription());
            }
            if (StringUtils.hasText(request.getCountry())) {
                userEntity.setCountry(request.getCountry());
            }
            if (request.getSex() != null) {
                userEntity.setSex(request.getSex().name());
            }
        } else {
            // super：保留原有能力（按需更新，避免 null 覆盖）
            if (StringUtils.hasText(request.getUsername())) {
                userEntity.setUsername(request.getUsername());
            }
            if (StringUtils.hasText(request.getNickname())) {
                userEntity.setNickname(request.getNickname());
            }
            if (StringUtils.hasText(request.getAvatar())) {
                userEntity.setAvatar(request.getAvatar());
            }
            if (request.getDescription() != null) {
                userEntity.setDescription(request.getDescription());
            }
            if (StringUtils.hasText(request.getMobile())) {
                // 禁止将其他用户手机号设置为超管手机号
                userService.validateNotUsingSuperCredentials(null, request.getMobile(), targetUid);
                userEntity.setMobile(request.getMobile());
            }
            if (request.getMobileVerified() != null) {
                userEntity.setMobileVerified(request.getMobileVerified());
            }
            if (StringUtils.hasText(request.getEmail())) {
                // 禁止将其他用户邮箱设置为超管邮箱
                userService.validateNotUsingSuperCredentials(request.getEmail(), null, targetUid);
                userEntity.setEmail(request.getEmail());
            }
            if (request.getEmailVerified() != null) {
                userEntity.setEmailVerified(request.getEmailVerified());
            }
            if (request.getEnabled() != null) {
                userEntity.setEnabled(request.getEnabled());
            }
            if (StringUtils.hasText(request.getCountry())) {
                userEntity.setCountry(request.getCountry());
            }
            if (request.getSex() != null) {
                userEntity.setSex(request.getSex().name());
            }

            if (StringUtils.hasText(request.getPassword())) {
                String encodedPassword = passwordEncoder.encode(request.getPassword());
                userEntity.setPassword(encodedPassword);
            }
        }

        UserEntity savedUserEntity = save(userEntity);
        if (savedUserEntity == null) {
            throw new RuntimeException("Failed to save user");
        }

        // superUser: 允许通过 roleUids 更新角色列表（当前组织维度）
        if (authUser.isSuperUser() && request.getRoleUids() != null) {
            savedUserEntity = userService.updateUserRoles(savedUserEntity, request.getRoleUids());
        }

        // Ensure all users have ROLE_USER (df_role_user_uid)
        UserEntity ensured = userService.addRoleUser(savedUserEntity);
        return convertToResponse(ensured);
    }

    @Override
    protected UserEntity doSave(UserEntity entity) {
        return userRepository.save(entity);
    }

    @Override
    public UserEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UserEntity entity) {
        try {
            Optional<UserEntity> latest = userRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                UserEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return userRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<UserEntity> userOptional = userRepository.findByUid(uid);
        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            userEntity.setDeleted(true);
            save(userEntity);
        }
    }

    @Override
    public void delete(UserRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public UserResponse convertToResponse(UserEntity entity) {
        return ConvertUtils.convertToUserResponse(entity);
    }

    public List<UserEntity> findAll(UserRequest request) {
        Specification<UserEntity> specification = UserSpecification.search(request, authService);
        return userRepository.findAll(specification);
    }

    @Override
    public UserExcel convertToExcel(UserEntity entity) {
        UserExcel excel = new UserExcel();
        excel.setNickname(entity.getNickname());
        excel.setEmail(entity.getEmail());
        excel.setMobile(entity.getMobile());
        excel.setDescription(entity.getDescription());
        return excel;
    }

    @Override
    protected Specification<UserEntity> createSpecification(UserRequest request) {
        return UserSpecification.search(request, authService);
    }

    @Override
    protected Page<UserEntity> executePageQuery(Specification<UserEntity> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }


}
