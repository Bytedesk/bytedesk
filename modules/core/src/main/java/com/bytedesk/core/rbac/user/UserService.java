/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 09:25:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;
import java.util.Set;
import java.util.LinkedHashSet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.exception.UsernameExistsException;
import com.bytedesk.core.member.MemberRequest;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleConsts;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.rbac.user.UserEntity.RegisterSource;
import com.bytedesk.core.rbac.user.event.UserLogoutEvent;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleRestService roleRestService;

    private final BytedeskProperties bytedeskProperties;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UidUtils uidUtils;

    @PersistenceContext
    private EntityManager entityManager;

    private final OrganizationRepository organizationRepository;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final AuthService authService;

    private final TokenRestService tokenRestService;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user:exists", key = "#request.username + '-' + #request.platform", condition = "#request.username != null"),
            @CacheEvict(value = "user:exists", key = "#request.mobile + '-' + #request.platform", condition = "#request.mobile != null"),
            @CacheEvict(value = "user:exists", key = "#request.email + '-' + #request.platform", condition = "#request.email != null"),
    })
    public UserResponse register(UserRequest request) {
        // log.info("register {}", request.toString());

        // platform 不能为空；管理员后台创建用户时可能未传该字段，默认使用 BYTEDESK
        String platform = request.getPlatform();
        if (!StringUtils.hasText(platform)) {
            platform = PlatformEnum.BYTEDESK.name();
            request.setPlatform(platform);
        }

        if (!StringUtils.hasText(request.getEmail())
                && !StringUtils.hasText(request.getMobile())) {
            throw new RuntimeException("email or mobile is required..!!");
        }

        if (StringUtils.hasText(request.getEmail())
                && existsByEmailAndPlatform(request.getEmail(), platform)) {
            throw new EmailExistsException("Email " + request.getEmail() + " already exists..!!");
        }

        if (StringUtils.hasText(request.getMobile())
                && existsByMobileAndPlatform(request.getMobile(), platform)) {
            throw new MobileExistsException("Mobile " + request.getMobile() + " already exists..!!");
        }
        //
        UserEntity user = modelMapper.map(request, UserEntity.class);
        user.setUid(uidUtils.getUid());
        user.setPlatform(platform);
        // 设置注册来源：优先取请求值，否则根据提供的信息进行推断
        String rs = request.getRegisterSource();
        if (!StringUtils.hasText(rs)) {
            if (StringUtils.hasText(request.getEmail())) {
                rs = UserEntity.RegisterSource.EMAIL.name();
            } else if (StringUtils.hasText(request.getMobile())) {
                rs = UserEntity.RegisterSource.MOBILE.name();
            } else if (StringUtils.hasText(request.getUsername())) {
                rs = UserEntity.RegisterSource.USERNAME.name();
            } else {
                rs = UserEntity.RegisterSource.UNKNOWN.name();
            }
        }
        user.setRegisterSource(rs);
        //
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        } else if (StringUtils.hasText(request.getMobile())) {
            user.setNickname("User" + request.getMobile().substring(7));
        } else {
            user.setNickname(createNickname());
        }
        //
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        } else {
            user.setAvatar(AvatarConsts.getDefaultAvatarUrl());
        }
        //
        if (StringUtils.hasText(request.getPassword())) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
            // 设置密码修改时间为当前时间
            user.setPasswordModifiedAt(BdDateUtils.now());
        }
        // 只有经过验证的邮箱，才真正执行注册
        if (StringUtils.hasText(request.getEmail())) {
            // 如果username为空，则使用email作为username
            if (!StringUtils.hasText(request.getUsername())) {
                user.setUsername(request.getEmail());
            } else {
                user.setUsername(request.getUsername());
            }
            user.setNum(request.getEmail());
            // 默认注册时，仅验证手机号，无需验证邮箱
            if (request.getEmailVerified() != null) {
                user.setEmailVerified(request.getEmailVerified());
            } else {
                user.setEmailVerified(false);
            }
        }
        // 只有经过验证的手机号，才真正执行注册
        if (StringUtils.hasText(request.getMobile())) {
            if (!StringUtils.hasText(request.getUsername()) 
                && !StringUtils.hasText(request.getEmail())) {
                user.setUsername(request.getMobile());
            } else {
                user.setUsername(request.getUsername());
            }
            // 如果有手机号，则使用手机号作为num
            user.setNum(request.getMobile());
            if (request.getMobileVerified() != null) {
                user.setMobileVerified(request.getMobileVerified());
            } else {
                user.setMobileVerified(true);
            }
        }
        user.setEnabled(true);
        // 设置密码修改时间为账号创建时间（当前时间）
        user.setPasswordModifiedAt(BdDateUtils.now());
        //
        user = save(user);
        user = addRoleUser(user);
        return ConvertUtils.convertToUserResponse(user);
    }

    @Transactional
    public UserResponse update(UserRequest request) {
        UserEntity currentUser = authService.getUser();
        Optional<UserEntity> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            if (StringUtils.hasText(request.getUsername())) {
                // 如果新用户名跟旧用户名不同，需要首先判断新用户名是否已经存在，如果存在则抛出异常
                if (!request.getUsername().equals(user.getUsername())) {
                    if (existsByUsernameAndPlatform(request.getUsername(),
                            request.getPlatform())) {
                        throw new UsernameExistsException(
                                "Username " + request.getUsername() + " already exists..!!");
                    }
                }
                user.setUsername(request.getUsername());
            }

            if (StringUtils.hasText(request.getNickname())) {
                user.setNickname(request.getNickname());
            }

            if (StringUtils.hasText(request.getAvatar())) {
                user.setAvatar(request.getAvatar());
            }

            if (StringUtils.hasText(request.getEmail())) {
                // 如果新邮箱跟旧邮箱不同，需要首先判断新邮箱是否已经存在，如果存在则抛出异常
                if (!request.getEmail().equals(user.getEmail())) {
                    if (existsByEmailAndPlatform(request.getEmail(),
                            request.getPlatform())) {
                        throw new EmailExistsException("Email " + request.getEmail() + " already exists..!!");
                    }
                }
                user.setEmail(request.getEmail());
            }

            if (StringUtils.hasText(request.getMobile())) {
                // 如果新手机号跟旧手机号不同，需要首先判断新手机号是否已经存在，如果存在则抛出异常
                if (!request.getMobile().equals(user.getMobile())) {
                    if (existsByMobileAndPlatform(request.getMobile(),
                            request.getPlatform())) {
                        throw new MobileExistsException("Mobile " + request.getMobile() + " already exists..!!");
                    }
                }
                user.setMobile(request.getMobile());
            }

            if (StringUtils.hasText(request.getDescription())) {
                user.setDescription(request.getDescription());
            }

            UserEntity updatedUser = save(user);
            if (updatedUser == null) {
                throw new RuntimeException("User update failed..!!");
            }

            return ConvertUtils.convertToUserResponse(user);

        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    @Transactional
    public UserResponse changePassword(UserRequest request) {
        UserEntity currentUser = authService.getUser();
        Optional<UserEntity> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            String oldEncryptedPassword = user.getPassword(); // 假设这是数据库中加密后的密码
            String oldRawPassword = request.getOldPassword(); // 用户输入的旧密码
            String newRawPassword = request.getNewPassword(); // 用户输入的新密码

            // 验证旧密码
            if (oldEncryptedPassword == null || passwordEncoder.matches(oldRawPassword, oldEncryptedPassword)) {
                // 旧密码验证通过，设置新密码
                String newEncryptedPassword = passwordEncoder.encode(newRawPassword);
                user.setPassword(newEncryptedPassword); // 更新用户密码
                // 更新密码修改时间
                user.setPasswordModifiedAt(BdDateUtils.now());
                user = save(user); // 保存用户信息到数据库，假设save方法已经存在
                //
                return ConvertUtils.convertToUserResponse(user); // 返回更新后的用户信息
            } else {
                // 旧密码验证失败，抛出异常或返回错误信息
                throw new RuntimeException(I18Consts.I18N_USER_OLD_PASSWORD_WRONG);
            }
        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    // 管理员修改子成员用户密码, 无需验证旧密码
    @Transactional
    public UserResponse adminChangePassword(UserRequest request) {
        Optional<UserEntity> userOptional = findByUid(request.getUid());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            String newRawPassword = request.getNewPassword(); // 用户输入的新密码
            // 设置新密码
            String newEncryptedPassword = passwordEncoder.encode(newRawPassword);
            user.setPassword(newEncryptedPassword); // 更新用户密码
            // 更新密码修改时间
            user.setPasswordModifiedAt(BdDateUtils.now());
            user = save(user); // 保存用户信息到数据库，假设save方法已经存在
            //
            return ConvertUtils.convertToUserResponse(user); // 返回更新后的用户信息
        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    @Transactional
    public UserResponse changeEmail(UserRequest request) {
        UserEntity currentUser = authService.getUser();
        Optional<UserEntity> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if (StringUtils.hasText(request.getEmail())) {
                // 如果新邮箱跟旧邮箱不同，需要首先判断新邮箱是否已经存在，如果存在则抛出异常
                if (!request.getEmail().equals(user.getEmail())) {
                    if (existsByEmailAndPlatform(request.getEmail(),
                            request.getPlatform())) {
                        throw new EmailExistsException("Email " + request.getEmail() + " already exists..!!");
                    }
                }
                user.setEmail(request.getEmail());
            } else {
                throw new RuntimeException("Email is required..!!");
            }
            user.setEmailVerified(true);
            user = save(user);
            //
            return ConvertUtils.convertToUserResponse(user);
        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    @Transactional
    public UserResponse changeMobile(UserRequest request) {
        UserEntity currentUser = authService.getUser();
        Optional<UserEntity> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if (StringUtils.hasText(request.getMobile())) {
                // 如果新手机号跟旧手机号不同，需要首先判断新手机号是否已经存在，如果存在则抛出异常
                if (!request.getMobile().equals(user.getMobile())) {
                    if (existsByMobileAndPlatform(request.getMobile(),
                            request.getPlatform())) {
                        throw new MobileExistsException("Mobile " + request.getMobile() + " already exists..!!");
                    }
                }
                user.setMobile(request.getMobile());
            } else {
                throw new RuntimeException("Mobile is required..!!");
            }
            user.setMobileVerified(true);
            user = save(user);

            return ConvertUtils.convertToUserResponse(user);
        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    @Transactional
    public UserEntity createUserFromMember(UserRequest request) {
        //
        if (StringUtils.hasText(request.getMobile())
                && existsByMobileAndPlatform(request.getMobile(), request.getPlatform())) {
            Optional<UserEntity> userOptional = findByMobileAndPlatform(request.getMobile(), request.getPlatform());
            return userOptional.get();
        }

        if (StringUtils.hasText(request.getEmail())
                && existsByEmailAndPlatform(request.getEmail(), request.getPlatform())) {
            Optional<UserEntity> userOptional = findByEmailAndPlatform(request.getEmail(),
                    request.getPlatform());
            return userOptional.get();
        }
        //
        UserEntity user = UserEntity.builder()
                .avatar(request.getAvatar())
                .nickname(request.getNickname())
                .mobile(request.getMobile())
                .num(request.getMobile())
                .email(request.getEmail())
                .superUser(false)
                .emailVerified(false)
                .mobileVerified(false)
                .registerSource(RegisterSource.ADMIN.name())
                .build();
        user.setUid(uidUtils.getUid());

        // 设置默认用户名，优先使用邮箱，如果没有则使用手机号
        // if (StringUtils.hasText(request.getUsername())) {
        //     user.setUsername(request.getUsername());
        // } else 
        if (StringUtils.hasText(request.getEmail())) {
            user.setUsername(request.getEmail());
        } else if (StringUtils.hasText(request.getMobile())) {
            user.setUsername(request.getMobile());
        }
        //
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode(bytedeskProperties.getMemberDefaultPassword()));
        }
        // 设置密码修改时间为账号创建时间
        user.setPasswordModifiedAt(BdDateUtils.now());
        //
        Optional<OrganizationEntity> orgOptional = organizationRepository.findByUid(request.getOrgUid());
        if (!orgOptional.isPresent()) {
            throw new RuntimeException("Organization not found..!!");
        } else {
            user.setCurrentOrganization(orgOptional.get());
        }
        //
        user = addRoleUser(user);
        return user;
    }

    /**
     * 确保 user 的 currentOrganization 指向 orgUid。
     * updateUserRoles 依赖 currentOrganization 来写入“组织维度”的角色关联。
     */
    public UserEntity ensureCurrentOrganization(UserEntity user, String orgUid) {
        if (user == null) {
            return null;
        }
        if (!StringUtils.hasText(orgUid)) {
            return user;
        }
        if (user.getCurrentOrganization() != null && orgUid.equals(user.getCurrentOrganization().getUid())) {
            return user;
        }

        Optional<OrganizationEntity> orgOptional = organizationRepository.findByUid(orgUid);
        if (!orgOptional.isPresent()) {
            throw new RuntimeException("Organization not found..!!");
        }
        user.setCurrentOrganization(orgOptional.get());
        return user;
    }

    public UserEntity updateUserFromMember(UserEntity user, MemberRequest request) {
        return updateUserRoles(user, request.getRoleUids());
    }

    /**
     * 更新用户在“当前组织(currentOrganization)”下的角色列表。
     * - 忽略空值
     * - 将废弃的 member 角色 uid 归一为 user 角色 uid
     * - 强制确保包含 ROLE_USER
     */
    @Transactional
    public UserEntity updateUserRoles(UserEntity user, Set<String> roleUids) {
        if (user == null) {
            return null;
        }
        if (user.getId() == null) {
            throw new RuntimeException("User id is required for role update");
        }

        // Always operate on a managed entity to avoid merge side-effects on join tables
        UserEntity managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found: " + user.getUid()));

        // Ensure currentOrganization is set on the managed instance
        String orgUid = null;
        if (user.getCurrentOrganization() != null && StringUtils.hasText(user.getCurrentOrganization().getUid())) {
            orgUid = user.getCurrentOrganization().getUid();
        }
        ensureCurrentOrganization(managedUser, orgUid);

        Set<String> incomingRoleUids = roleUids;
        if (incomingRoleUids == null) {
            incomingRoleUids = new LinkedHashSet<>();
        }

        // Backward compatibility:
        // - Older payloads may still contain deprecated role uid: df_role_member_uid
        // - Ignore blank role uid values
        Set<String> normalizedRoleUids = new LinkedHashSet<>();
        for (String roleUid : incomingRoleUids) {
            if (!StringUtils.hasText(roleUid)) {
                continue;
            }
            if (BytedeskConsts.DEPRECATED_ROLE_MEMBER_UID.equals(roleUid)) {
                normalizedRoleUids.add(BytedeskConsts.DEFAULT_ROLE_USER_UID);
            } else {
                normalizedRoleUids.add(roleUid);
            }
        }

        // All users must have ROLE_USER (df_role_user_uid)
        normalizedRoleUids.add(BytedeskConsts.DEFAULT_ROLE_USER_UID);

        // 首先判断是否有变化，如果无变化则不更新
        if (managedUser.getRoleUids() != null && managedUser.getRoleUids().equals(normalizedRoleUids)) {
            return managedUser;
        }

        // 删除当前组织下的非平台角色
        managedUser.removeOrganizationRoles();

        // 增加角色，遍历roleUids，逐个添加
        for (String roleUid : normalizedRoleUids) {
            Optional<RoleEntity> optional = roleRestService.findByUid(roleUid);
            if (optional.isPresent()) {
                RoleEntity role = optional.get();
                // Avoid entityManager.merge(detachedRole) which may trigger OptimisticLockException
                // and mark the surrounding transaction rollback-only.
                Long roleId = role.getId();
                if (roleId == null) {
                    throw new RuntimeException("Role id is null for uid: " + roleUid);
                }
                RoleEntity managedRole = entityManager.find(RoleEntity.class, roleId);
                if (managedRole == null) {
                    throw new RuntimeException("Role not found by id: " + roleId + ", uid: " + roleUid);
                }
                managedUser.addOrganizationRole(managedRole);
            } else {
                throw new RuntimeException("Role not found: " + roleUid);
            }
        }

        // managedUser is tracked by JPA; changes will flush on transaction commit
        return managedUser;
    }

    public UserEntity addRoleAgent(UserEntity user) {
        return addRole(user, RoleConsts.ROLE_AGENT);
    }

    public UserEntity removeRoleAgent(UserEntity user) {
        return removeRole(user, RoleConsts.ROLE_AGENT);
    }

    @Transactional
    public UserEntity addRoleUser(UserEntity user) {
        return addRole(user, RoleConsts.ROLE_USER);
    }

    public UserEntity addRoleAdmin(UserEntity user) {
        return addRole(user, RoleConsts.ROLE_ADMIN);
    }

    public UserEntity removeRoleAdmin(UserEntity user) {
        return removeRole(user, RoleConsts.ROLE_ADMIN);
    }

    public UserEntity addRoleDeptAdmin(UserEntity user) {
        return addRole(user, RoleConsts.ROLE_DEPT_ADMIN);
    }

    public UserEntity removeRoleDeptAdmin(UserEntity user) {
        return removeRole(user, RoleConsts.ROLE_DEPT_ADMIN);
    }

    public UserEntity addRoleWorkgroupAdmin(UserEntity user) {
        return addRole(user, RoleConsts.ROLE_WORKGROUP_ADMIN);
    }

    public UserEntity removeRoleWorkgroupAdmin(UserEntity user) {
        return removeRole(user, RoleConsts.ROLE_WORKGROUP_ADMIN);
    }

    @Transactional
    public UserEntity addRoleSuper(UserEntity user) {
        return addRole(user, RoleConsts.ROLE_SUPER);
    }

    @Transactional
    public UserEntity removeRoleSuper(UserEntity user) {
        return removeRole(user, RoleConsts.ROLE_SUPER);
    }

    @Transactional
    public UserEntity addRole(UserEntity user, String roleName) {
        Optional<RoleEntity> roleOptional = roleRestService.findByNamePlatform(roleName);
        if (roleOptional.isPresent()) {
            RoleEntity role = roleOptional.get();
            
            // 处理乐观锁冲突：使用重试机制或重新获取最新实体
            RoleEntity managedRole;
            try {
                // 尝试合并实体状态
                managedRole = entityManager.merge(role);
            } catch (jakarta.persistence.OptimisticLockException e) {
                log.warn("乐观锁冲突，重新获取角色实体: {}", roleName);
                // 重新从数据库获取最新的角色实体
                Optional<RoleEntity> freshRoleOptional = roleRestService.findByNamePlatform(roleName);
                if (freshRoleOptional.isPresent()) {
                    managedRole = freshRoleOptional.get();
                } else {
                    throw new RuntimeException("重新获取角色失败: " + roleName);
                }
            }

            final RoleEntity targetRole = managedRole;

            // Allow ROLE_USER without organization context so auto-registered users have a default role
            if (user.getCurrentOrganization() == null) {
                if (!RoleConsts.ROLE_USER.equals(roleName)) {
                    throw new RuntimeException("当前用户未加入任何组织，无法分配角色: " + roleName);
                }

                boolean alreadyHasRole = user.getCurrentRoles().stream()
                        .anyMatch(r -> r.getId() != null && r.getId().equals(targetRole.getId()));
                if (!alreadyHasRole) {
                    user.getCurrentRoles().add(targetRole);
                }

                try {
                    return userRepository.save(user);
                } catch (Exception e) {
                    log.error("User add role failed..!!", e);
                    throw new RuntimeException("User add role failed..!!", e);
                }
            }

            user.addOrganizationRole(targetRole);

            // 直接保存用户实体
            try {
                return userRepository.save(user);
            } catch (Exception e) {
                log.error("User add role failed..!!", e);
                throw new RuntimeException("User add role failed..!!", e);
            }
        } else {
            throw new RuntimeException("Role not found..!!");
        }
    }

    public UserEntity removeRole(UserEntity user, String roleName) {
        Optional<RoleEntity> roleOptional = roleRestService.findByNamePlatform(roleName);
        if (roleOptional.isPresent()) {
            user.removeOrganizationRole(roleOptional.get());
            //
            try {
                return userRepository.save(user);
            } catch (Exception e) {
                log.error("User remove role failed..!!", e);
                throw new RuntimeException("User remove role failed..!!", e);
            }
        } else {
            throw new RuntimeException("Role not found..!!");
        }
    }

    @Cacheable(value = "user", key = "#email + '-' + #platform", unless = "#result == null")
    public Optional<UserEntity> findByEmailAndPlatform(String email, String platform) {
        return userRepository.findByEmailAndPlatformAndDeletedFalse(email, platform);
    }

    @Cacheable(value = "user", key = "#mobile + '-' + #platform", unless = "#result == null")
    public Optional<UserEntity> findByMobileAndPlatform(String mobile, String platform) {
        return userRepository.findByMobileAndPlatformAndDeletedFalse(mobile, platform);
    }

    @Cacheable(value = "user", key = "#username + '-' + #platform", unless = "#result == null")
    public Optional<UserEntity> findByUsernameAndPlatform(String username, String platform) {
        return userRepository.findByUsernameAndPlatformAndDeletedFalse(username, platform);
    }

    // @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    public Optional<UserEntity> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    @Cacheable(value = "admin", unless = "#result == null")
    public Optional<UserEntity> getSuper() {
        return userRepository.findByUsernameAndPlatformAndDeletedFalse(bytedeskProperties.getEmail(),
                PlatformEnum.BYTEDESK.name());
    }

    @Cacheable(value = "user:exists", key = "#username + '-' + #platform", unless = "#result == null")
    public Boolean existsByUsernameAndPlatform(@NonNull String username, @NonNull String platform) {
        return userRepository.existsByUsernameAndPlatformAndDeletedFalse(username, platform);
    }

    @Cacheable(value = "user:exists", key = "#mobile + '-' + #platform", unless = "#result == null")
    public Boolean existsByMobileAndPlatform(@NonNull String mobile, @NonNull String platform) {
        return userRepository.existsByMobileAndPlatformAndDeletedFalse(mobile, platform);
    }

    @Cacheable(value = "user:exists", key = "#email + '-' + #platform", unless = "#result == null")
    public Boolean existsByEmailAndPlatform(@NonNull String email, @NonNull String platform) {
        return userRepository.existsByEmailAndPlatformAndDeletedFalse(email, platform);
    }

    // exists by username and mobile
    @Cacheable(value = "user:exists", key = "#username + '-' + #mobile + '-' + #platform", unless = "#result == null")
    public Boolean existsByUsernameAndMobileAndPlatform(@NonNull String username, @NonNull String mobile, @NonNull String platform) {
        return userRepository.existsByUsernameAndMobileAndPlatformAndDeletedFalse(username, mobile, platform);
    }

    public Boolean existsBySuperUser() {
        return userRepository.existsBySuperUserAndDeletedFalse(true);
    }

    @Transactional
    @Caching(put = {
            @CachePut(value = "user", key = "#user.username + '-' + #user.platform", unless = "#user.username == null"),
            @CachePut(value = "user", key = "#user.mobile + '-' + #user.platform", unless = "#user.mobile == null"),
            @CachePut(value = "user", key = "#user.email + '-' + #user.platform", unless = "#user.email == null"),
            @CachePut(value = "user", key = "#user.uid", unless = "#user.uid == null"),
        }, evict = {
            @CacheEvict(value = "user:exists", key = "#user.username + '-' + #user.platform", condition = "#user.username != null"),
            @CacheEvict(value = "user:exists", key = "#user.mobile + '-' + #user.platform", condition = "#user.mobile != null"),
            @CacheEvict(value = "user:exists", key = "#user.email + '-' + #user.platform", condition = "#user.email != null"),
    })
    public UserEntity save(@NonNull UserEntity user) {
        try {
            return userRepository.save(user);
        } catch (ObjectOptimisticLockingFailureException optimisticLockingFailureException) {
            log.error("User save failed..!!", optimisticLockingFailureException);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("User save failed..!!", e);
            throw new RuntimeException("User save failed..!!", e);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.username + '-' + #user.platform"),
            @CacheEvict(value = "user", key = "#user.mobile + '-' + #user.platform"),
            @CacheEvict(value = "user", key = "#user.email + '-' + #user.platform"),
            @CacheEvict(value = "user", key = "#user.uid"),
            @CacheEvict(value = "user:exists", key = "#user.username + '-' + #user.platform"),
            @CacheEvict(value = "user:exists", key = "#user.mobile + '-' + #user.platform"),
            @CacheEvict(value = "user:exists", key = "#user.email + '-' + #user.platform"),
    })
    public void delete(@NonNull UserEntity user) {
        user.setDeleted(true);
        save(user);
    }

    public void logout(String accessToken) {
        log.debug("logout {}", accessToken);

        // 发布用户登出事件
        UserEntity user = authService.getUser();
        bytedeskEventPublisher.publishEvent(new UserLogoutEvent(this, user));

        // 将token设置为已撤销状态
        tokenRestService.revokeAccessToken(accessToken, "logout");
    }

    // TODO: 待完善
    public String createNickname() {
        String randomId = uidUtils.getUid().substring(11, 15);
        return "User" + randomId;
    }

}
