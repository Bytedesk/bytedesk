/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-26 18:46:56
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
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;
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
    public UserResponse register(UserRequest request) {

        if (StringUtils.hasText(request.getEmail())
                && existsByEmailAndPlatform(request.getEmail(),
                        request.getPlatform())) {
            throw new EmailExistsException("Email " + request.getEmail() + " already exists..!!");
        }
        if (StringUtils.hasText(request.getMobile())
                && existsByMobileAndPlatform(request.getMobile(),
                        request.getPlatform())) {
            throw new MobileExistsException("Mobile " + request.getMobile() + " already exists..!!");
        }
        //
        User user = modelMapper.map(request, User.class);
        user.setUid(uidUtils.getCacheSerialUid());
        user.setPlatform(request.getPlatform());
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
            user.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        }
        //
        if (StringUtils.hasText(request.getPassword())) {
            String rawPassword = request.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
        }
        // 只有经过验证的邮箱，才真正执行注册
        if (StringUtils.hasText(request.getEmail())) {
            user.setUsername(request.getEmail());
            user.setNum(request.getEmail());
            // 默认注册时，仅验证手机号，无需验证邮箱
            user.setEmailVerified(false);
        }
        // 只有经过验证的手机号，才真正执行注册
        if (StringUtils.hasText(request.getMobile())) {
            user.setNum(request.getMobile());
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
    public UserResponse update(UserRequest request) {

        User currentUser = AuthUser.getCurrentUser(); // FIXME: 直接使用此user save，会报错
        Optional<User> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

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
    public UserResponse changePassword(UserRequest request) {

        User currentUser = AuthUser.getCurrentUser(); // FIXME: 直接使用此user save，会报错
        Optional<User> userOptional = findByUid(currentUser.getUid());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String oldEncryptedPassword = user.getPassword(); // 假设这是数据库中加密后的密码
            String oldRawPassword = request.getOldPassword(); // 用户输入的旧密码
            String newRawPassword = request.getNewPassword(); // 用户输入的新密码

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

    @Transactional
    public User createUser(UserRequest request) {
        //
        if (StringUtils.hasText(request.getMobile())
            && existsByMobileAndPlatform(request.getMobile(), request.getPlatform())) {
            Optional<User> userOptional = findByMobileAndPlatform(request.getMobile(), request.getPlatform());
            return userOptional.get();
        }

        if (StringUtils.hasText(request.getEmail())
                && existsByEmailAndPlatform(request.getEmail(), request.getPlatform())) {
            Optional<User> userOptional = findByEmailAndPlatform(request.getEmail(),
                    request.getPlatform());
            return userOptional.get();
        }
        //
        User user = User.builder()
                .avatar(request.getAvatar())
                // .username(request.getEmail())
                .nickname(request.getNickname())
                .mobile(request.getMobile())
                .num(request.getMobile())
                .email(request.getEmail())
                .superUser(false)
                .emailVerified(false)
                .mobileVerified(false)
                .password(request.getPassword())
                .build();
        user.setUid(uidUtils.getCacheSerialUid());
        // 
        if (StringUtils.hasText(request.getEmail())) {
            user.setUsername(request.getEmail());
        } else if (StringUtils.hasText(request.getMobile())) {
            user.setUsername(request.getMobile());
        }
        //
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode(bytedeskProperties.getPasswordDefault()));
        }
        //
        Optional<Organization> orgOptional = organizationRepository.findByUid(request.getOrgUid());
        Optional<Role> roleOptional = roleService.findByNameAndOrgUid(TypeConsts.ROLE_CUSTOMER_SERVICE, request.getOrgUid());
        if (orgOptional.isPresent() && roleOptional.isPresent()) {
            Organization organization = orgOptional.get();
            Role role = roleOptional.get();
            //
            user.addOrganizationRole(organization, role);
        }
        //
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
    public Boolean existsByUsernameAndPlatform(@NonNull String username, @NonNull PlatformEnum platform) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        return userRepository.existsByUsernameAndPlatformAndDeleted(username, platform, false);
    }

    @Cacheable(value = "userExists", key = "#mobile", unless = "#result == null")
    public Boolean existsByMobileAndPlatform(@NonNull String mobile, @NonNull PlatformEnum platform) {
        if (!StringUtils.hasText(mobile)) {
            return false;
        }
        return userRepository.existsByMobileAndPlatformAndDeleted(mobile, platform, false);
    }

    @Cacheable(value = "userExists", key = "#email", unless = "#result == null")
    public Boolean existsByEmailAndPlatform(@NonNull String email, @NonNull PlatformEnum platform) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
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
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
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
        // userRepository.delete(user);
        user.setDeleted(true);
        save(user);
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
                .email(bytedeskProperties.getEmail())
                .username(bytedeskProperties.getEmail())
                .password(new BCryptPasswordEncoder().encode(bytedeskProperties.getPassword()))
                .nickname(bytedeskProperties.getNickname())
                .avatar(AvatarConsts.DEFAULT_AVATAR_URL)
                .mobile(bytedeskProperties.getMobile())
                .num(bytedeskProperties.getMobile())
                .superUser(true)
                .emailVerified(true)
                .mobileVerified(true)
                .build();
        admin.setUid(uidUtils.getCacheSerialUid());
        //
        save(admin);
    }

    public void updateInitData() {
        //
        Optional<Organization> orgOptional = organizationRepository.findByUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        Optional<Role> roleOptional = roleService.findByNameAndOrgUid(TypeConsts.ROLE_SUPER,
                BdConstants.DEFAULT_ORGANIZATION_UID);
        Optional<User> adminOptional = findByEmailAndPlatform(bytedeskProperties.getEmail(),
                PlatformEnum.BYTEDESK);
        if (orgOptional.isPresent() && roleOptional.isPresent() && adminOptional.isPresent()) {
            Organization organization = orgOptional.get();
            Role role = roleOptional.get();
            User user = adminOptional.get();
            //
            user.addOrganizationRole(organization, role);
            //
            save(user);
        }
    }

}
