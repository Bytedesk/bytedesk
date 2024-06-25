/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-25 17:25:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.exception.UsernameExistsException;
import com.bytedesk.core.rbac.auth.AuthUser;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;

// @Slf4j
@Service
@AllArgsConstructor
public class UserService {

    // cycle dependency - 循环引用，不能使用
    // private final AuthService authService;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final BytedeskProperties bytedeskProperties;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UidUtils uidUtils;

    private final OrganizationRepository organizationRepository;

    @Transactional
    public UserResponse register(UserRequest userRequest) {

        if (StringUtils.hasText(userRequest.getEmail())
                && existsByEmailAndPlatform(userRequest.getEmail(),
                        PlatformEnum.fromValue(userRequest.getPlatform()))) {
            throw new EmailExistsException("Email " + userRequest.getEmail() + " already exists..!!");
        }
        if (StringUtils.hasText(userRequest.getMobile())
                && existsByMobileAndPlatform(userRequest.getMobile(),
                        PlatformEnum.fromValue(userRequest.getPlatform()))) {
            throw new MobileExistsException("Mobile " + userRequest.getMobile() + " already exists..!!");
        }
        //
        User user = modelMapper.map(userRequest, User.class);
        user.setUid(uidUtils.getCacheSerialUid());
        user.setPlatform(PlatformEnum.fromValue(userRequest.getPlatform()));
        //
        if (StringUtils.hasText(userRequest.getNickname())) {
            user.setNickname(userRequest.getNickname());
        } else {
            user.setNickname(createNickname());
        }
        //
        if (StringUtils.hasText(userRequest.getAvatar())) {
            user.setAvatar(userRequest.getAvatar());
        } else {
            user.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        }
        //
        if (StringUtils.hasText(userRequest.getPassword())) {
            String rawPassword = userRequest.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
        }
        // 只有经过验证的邮箱，才真正执行注册
        if (StringUtils.hasText(userRequest.getEmail())) {
            user.setUsername(userRequest.getEmail());
            user.setNum(userRequest.getEmail());
            // 默认注册时，仅验证手机号，无需验证邮箱
            user.setEmailVerified(false);
        }
        // 只有经过验证的手机号，才真正执行注册
        if (StringUtils.hasText(userRequest.getMobile())) {
            user.setNum(userRequest.getMobile());
            user.setMobileVerified(true);
        }
        user.setEnabled(true);
        //
        // TODO: 设置角色role
        //
        user = save(user);
        //
        return ConvertUtils.convertToUserResponse(user);
    }

    @Transactional
    public UserResponse update(UserRequest userRequest) {

        User currentUser = AuthUser.getCurrentUser(); // FIXME: 直接使用此user save，会报错
        Optional<User> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (StringUtils.hasText(userRequest.getUsername())) {
                // 如果新用户名跟旧用户名不同，需要首先判断新用户名是否已经存在，如果存在则抛出异常
                if (!userRequest.getUsername().equals(user.getUsername())) {
                    if (existsByUsernameAndPlatform(userRequest.getUsername(),
                            PlatformEnum.fromValue(userRequest.getPlatform()))) {
                        throw new UsernameExistsException(
                                "Username " + userRequest.getUsername() + " already exists..!!");
                    }
                }
                user.setUsername(userRequest.getUsername());
            }

            if (StringUtils.hasText(userRequest.getNickname())) {
                user.setNickname(userRequest.getNickname());
            }

            if (StringUtils.hasText(userRequest.getAvatar())) {
                user.setAvatar(userRequest.getAvatar());
            }

            if (StringUtils.hasText(userRequest.getEmail())) {
                // 如果新邮箱跟旧邮箱不同，需要首先判断新邮箱是否已经存在，如果存在则抛出异常
                if (!userRequest.getEmail().equals(user.getEmail())) {
                    if (existsByEmailAndPlatform(userRequest.getEmail(),
                            PlatformEnum.fromValue(userRequest.getPlatform()))) {
                        throw new EmailExistsException("Email " + userRequest.getEmail() + " already exists..!!");
                    }
                }
                user.setEmail(userRequest.getEmail());
            }

            if (StringUtils.hasText(userRequest.getMobile())) {
                // 如果新手机号跟旧手机号不同，需要首先判断新手机号是否已经存在，如果存在则抛出异常
                if (!userRequest.getMobile().equals(user.getMobile())) {
                    if (existsByMobileAndPlatform(userRequest.getMobile(),
                            PlatformEnum.fromValue(userRequest.getPlatform()))) {
                        throw new MobileExistsException("Mobile " + userRequest.getMobile() + " already exists..!!");
                    }
                }
                user.setMobile(userRequest.getMobile());
            }

            if (StringUtils.hasText(userRequest.getDescription())) {
                user.setDescription(userRequest.getDescription());
            }

            // TODO: 设置角色role

            User updatedUser = save(user);
            if (updatedUser == null) {
                throw new RuntimeException("User update failed..!!");
            }

            return ConvertUtils.convertToUserResponse(user);

        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    @Transactional
    public UserResponse changePassword(UserRequest userRequest) {

        User currentUser = AuthUser.getCurrentUser(); // FIXME: 直接使用此user save，会报错
        Optional<User> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String oldEncryptedPassword = user.getPassword(); // 假设这是数据库中加密后的密码
            String oldRawPassword = userRequest.getOldPassword(); // 用户输入的旧密码
            String newRawPassword = userRequest.getNewPassword(); // 用户输入的新密码

            // 验证旧密码
            if (oldEncryptedPassword == null || passwordEncoder.matches(oldRawPassword, oldEncryptedPassword)) {
                // 旧密码验证通过，设置新密码
                String newEncryptedPassword = passwordEncoder.encode(newRawPassword);
                user.setPassword(newEncryptedPassword); // 更新用户密码
                user = save(user); // 保存用户信息到数据库，假设save方法已经存在
                //
                return ConvertUtils.convertToUserResponse(user); // 返回更新后的用户信息
            } else {
                // 旧密码验证失败，抛出异常或返回错误信息
                throw new RuntimeException("old password wrong, please try again..!!");
            }
        } else {
            throw new RuntimeException("User not found..!!");
        }
    }

    // String nickname, String avatar, String password, String mobile, String email,
    // String platform, String orgUid
    @Transactional
    public User createUser(UserRequest userRequest) {
        //
        if (existsByMobileAndPlatform(userRequest.getMobile(), PlatformEnum.fromValue(userRequest.getPlatform()))) {
            Optional<User> userOptional = findByMobileAndPlatform(userRequest.getMobile(),
                    PlatformEnum.fromValue(userRequest.getPlatform()));
            return userOptional.get();
            // throw new MobileExistsException("Mobile " + userRequest.getMobile() + " on "
            // + userRequest.getPlatform()+" already exists..!!");
        }

        if (existsByEmailAndPlatform(userRequest.getEmail(), PlatformEnum.fromValue(userRequest.getPlatform()))) {
            Optional<User> userOptional = findByEmailAndPlatform(userRequest.getEmail(),
                    PlatformEnum.fromValue(userRequest.getPlatform()));
            return userOptional.get();
            // throw new EmailExistsException("Email " + userRequest.getEmail() + " on " +
            // userRequest.getPlatform() + " already exists..!!");
        }

        //
        User user = User.builder()
                // .avatar(userRequest.getAvatar())
                // use email as default username
                .username(userRequest.getEmail())
                // .nickname(userRequest.getNickname())
                .mobile(userRequest.getMobile())
                .num(userRequest.getMobile())
                .email(userRequest.getEmail())
                .superUser(false)
                .emailVerified(false)
                .mobileVerified(false)
                .password(userRequest.getPassword())
                .build();
        user.setUid(uidUtils.getCacheSerialUid());
        user.setNickname(userRequest.getNickname());
        user.setAvatar(userRequest.getAvatar());
        //
        if (StringUtils.hasText(userRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode(bytedeskProperties.getPasswordDefault()));
        }

        Optional<Organization> orgOptional = organizationRepository.findByUid(UserConsts.DEFAULT_ORGANIZATION_UID);
        Optional<Role> roleOptional = roleService.findByNameAndOrgUid(TypeConsts.ROLE_CUSTOMER_SERVICE,
                UserConsts.DEFAULT_ORGANIZATION_UID);
        if (orgOptional.isPresent() && roleOptional.isPresent()) {
            Organization organization = orgOptional.get();
            Role role = roleOptional.get();
            //
            user.addOrganizationRole(organization, role);
        }

        return save(user);
    }

    public User updateUser(User user, String password, String mobile, String email) {

        if (StringUtils.hasText(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setMobile(mobile);
        user.setNum(mobile);
        user.setEmail(email);

        return save(user);
    }

    @Cacheable(value = "user", key = "#email", unless = "#result == null")
    public Optional<User> findByEmailAndPlatform(String email, PlatformEnum platform) {
        return userRepository.findByEmailAndPlatformAndDeleted(email, platform, false);
    }

    @Cacheable(value = "user", key = "#mobile", unless = "#result == null")
    public Optional<User> findByMobileAndPlatform(String mobile, PlatformEnum platform) {
        return userRepository.findByMobileAndPlatformAndDeleted(mobile, platform, false);
    }

    @Cacheable(value = "user", key = "#username", unless = "#result == null")
    public Optional<User> findByUsernameAndPlatform(String username, PlatformEnum platform) {
        return userRepository.findByUsernameAndPlatformAndDeleted(username, platform, false);
    }

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    public Optional<User> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    @Cacheable(value = "admin", unless = "#result == null")
    public Optional<User> getAdmin() {
        return userRepository.findByUsernameAndPlatformAndDeleted(bytedeskProperties.getEmail(),
                PlatformEnum.BYTEDESK, false);
    }

    //
    @Cacheable(value = "userExists", key = "#username", unless = "#result == null")
    public Boolean existsByUsernameAndPlatform(String username, PlatformEnum platform) {
        return userRepository.existsByUsernameAndPlatformAndDeleted(username, platform, false);
    }

    @Cacheable(value = "userExists", key = "#mobile", unless = "#result == null")
    public Boolean existsByMobileAndPlatform(String mobile, PlatformEnum platform) {
        return userRepository.existsByMobileAndPlatformAndDeleted(mobile, platform, false);
    }

    @Cacheable(value = "userExists", key = "#email", unless = "#result == null")
    public Boolean existsByEmailAndPlatform(String email, PlatformEnum platform) {
        return userRepository.existsByEmailAndPlatformAndDeleted(email, platform, false);
    }

    @Caching(put = {
            @CachePut(value = "user", key = "#user.username", unless = "#user.username == null"),
            @CachePut(value = "user", key = "#user.mobile", unless = "#user.mobile == null"),
            @CachePut(value = "user", key = "#user.email", unless = "#user.email == null"),
            @CachePut(value = "user", key = "#user.uid", unless = "#user.uid == null"),
            // TODO: 此处put的exists内容跟缓存时内容类型是否一致？
            // @CachePut(value = "userExists", key = "#user.username"),
            // @CachePut(value = "userExists", key = "#user.mobile"),
            // @CachePut(value = "userExists", key = "#user.email"),
    })
    public User save(@NonNull User user) {
        return userRepository.save(user);
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.username"),
            @CacheEvict(value = "user", key = "#user.mobile"),
            @CacheEvict(value = "user", key = "#user.email"),
            @CacheEvict(value = "user", key = "#user.uid"),
            @CacheEvict(value = "userExists", key = "#user.username"),
            @CacheEvict(value = "userExists", key = "#user.mobile"),
            @CacheEvict(value = "userExists", key = "#user.email"),
    })
    public void delete(@NonNull User user) {
        userRepository.delete(user);
    }

    // TODO: 待完善
    public String createNickname() {
        String randomId = uidUtils.getCacheSerialUid().substring(11, 15);
        return "User" + randomId;
    }

    public void initData() {

        if (existsByMobileAndPlatform(bytedeskProperties.getMobile(), PlatformEnum.BYTEDESK)) {
            return;
        }

        User admin = User.builder()
                // .uid(uidUtils.getCacheSerialUid())
                .email(bytedeskProperties.getEmail())
                .username(bytedeskProperties.getEmail())
                .password(new BCryptPasswordEncoder().encode(bytedeskProperties.getPassword()))
                // .nickname(bytedeskProperties.getNickname())
                // .avatar(AvatarConsts.DEFAULT_AVATAR_URL)
                .mobile(bytedeskProperties.getMobile())
                .num(bytedeskProperties.getMobile())
                .superUser(true)
                .emailVerified(true)
                .mobileVerified(true)
                .build();
        admin.setUid(uidUtils.getCacheSerialUid());
        admin.setNickname(bytedeskProperties.getNickname());
        admin.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        // admin.getOrganizations().add(UserConsts.DEFAULT_ORGANIZATION_UID);

        // Optional<Role> roleOptional = roleService.findByName(TypeConsts.ROLE_SUPER);
        // Set<Role> roles = new HashSet<>();
        // roleOptional.ifPresent(role -> {
        // roles.add(role);
        // });
        // admin.setRoles(roles);
        //
        save(admin);
    }

    public void updateInitData() {

        Optional<Organization> orgOptional = organizationRepository.findByUid(UserConsts.DEFAULT_ORGANIZATION_UID);
        Optional<Role> roleOptional = roleService.findByNameAndOrgUid(TypeConsts.ROLE_SUPER,
                UserConsts.DEFAULT_ORGANIZATION_UID);
        Optional<User> adminOptional = findByEmailAndPlatform(bytedeskProperties.getEmail(),
                PlatformEnum.BYTEDESK);
        if (orgOptional.isPresent() && roleOptional.isPresent() && adminOptional.isPresent()) {
            Organization organization = orgOptional.get();
            Role role = roleOptional.get();
            User user = adminOptional.get();
            //
            user.addOrganizationRole(organization, role);
            save(user);
        }
    }

}
