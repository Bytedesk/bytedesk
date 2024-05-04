/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:22:24
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
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;

import java.util.HashSet;

// @Slf4j
@Service
@AllArgsConstructor
public class UserService {

    // cycle dependency - 循环引用，不能使用
    // private final AuthService authService;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final BytedeskProperties properties;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UidUtils uidUtils;

    public Page<User> query(UserRequest userRequest) {

        Pageable pageable = PageRequest.of(userRequest.getPageNumber(), userRequest.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        return userRepository.findAll(pageable);
    }

    @Transactional
    public UserResponse register(UserRequest userRequest) {
        
        if (existsByEmail(userRequest.getEmail())) {
            throw new EmailExistsException("Email already exists..!!");
        }
        if (existsByMobile(userRequest.getMobile())) {
            throw new MobileExistsException("Mobile already exists..!!");
        }
        //
        User user = modelMapper.map(userRequest, User.class);
        user.setUid(uidUtils.getCacheSerialUid());
        // 
        if (StringUtils.hasLength(userRequest.getNickname())) {
            user.setNickname(userRequest.getNickname());
        } else {
            user.setNickname(createNickname());
        }
        // 
        if (StringUtils.hasLength(userRequest.getAvatar())) {
            user.setAvatar(userRequest.getAvatar());
        } else {
            user.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        }
        // 
        if (StringUtils.hasLength(userRequest.getPassword())) {
            String rawPassword = userRequest.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
        }
        // 只有经过验证的邮箱，才真正执行注册
        if (StringUtils.hasLength(userRequest.getEmail())) {
            user.setUsername(userRequest.getEmail());
            user.setNum(userRequest.getEmail());
            user.setEmailVerified(true);
            user.setEnabled(true);
        }
        // 只有经过验证的手机号，才真正执行注册
        if (StringUtils.hasLength(userRequest.getMobile())) {
            user.setNum(userRequest.getMobile());
            user.setMobileVerified(true);
            user.setEnabled(true);
        }
        // TODO: 设置角色role
        //
        
        // 
        return convertToUserResponse(save(user));
    }

    @Transactional
    public UserResponse update(UserRequest userRequest) {

        Optional<User> userOptional = findByUid(userRequest.getUid());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (StringUtils.hasLength(userRequest.getNickname())) {
                user.setNickname(userRequest.getNickname());
            }

            if (StringUtils.hasLength(userRequest.getAvatar())) {
                user.setAvatar(userRequest.getAvatar());
            }

            if (StringUtils.hasLength(userRequest.getPassword())) {
                String rawPassword = userRequest.getPassword();
                String encodedPassword = passwordEncoder.encode(rawPassword);
                user.setPassword(encodedPassword);
            }

            if (StringUtils.hasLength(userRequest.getEmail())) {
                user.setEmail(userRequest.getEmail());
            }

            if (StringUtils.hasLength(userRequest.getMobile())) {
                user.setMobile(userRequest.getMobile());
            }

            if(StringUtils.hasLength(userRequest.getDescription())) {
                user.setDescription(userRequest.getDescription());
            }

            // TODO: 设置角色role

            return convertToUserResponse(save(user));

        } else {
            throw new NotFoundException("User not found..!!");
        }
    }

    @Transactional
    public User createUser(String nickname, String avatar, String password, String mobile, String email, boolean isVerified,
                String orgUid) {

        User user = User.builder()
            // .uid(uidUtils.getCacheSerialUid())
            .avatar(avatar)
            // use email as default username
            .username(email)
            .nickname(nickname)
            .mobile(mobile)
            .num(mobile)
            .email(email)
            .superUser(false)
            .emailVerified(isVerified)
            .mobileVerified(isVerified)
                .build();
        user.setUid(uidUtils.getCacheSerialUid());

        if (StringUtils.hasLength(password)) {
            user.setPassword(passwordEncoder.encode(password));
        } else {
            user.setPassword(passwordEncoder.encode("123456"));
        }

        user.getOrganizations().add(orgUid);

        return save(user);
        // return user;
    }

    public User updateUser(User user, String password, String mobile, String email) {

        if (StringUtils.hasLength(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setMobile(mobile);
        user.setNum(mobile);
        user.setEmail(email);

        return save(user);
    }

    @Cacheable(value = "user", key = "#email", unless="#result == null")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Cacheable(value = "user", key = "#mobile", unless="#result == null")
    public Optional<User> findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Cacheable(value = "user", key = "#username", unless="#result == null")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "user", key = "#uid", unless="#result == null")
    public Optional<User> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    @Cacheable(value = "admin", unless="#result == null")
    public Optional<User> getAdmin() {
        return userRepository.findByUsername(properties.getUsername());
    }

    // 
    @Cacheable(value = "userExists", key = "#username", unless="#result == null")
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeleted(username, false);
    }

    @Cacheable(value = "userExists", key = "#mobile", unless="#result == null")
    public Boolean existsByMobile(String mobile) {
        return userRepository.existsByMobileAndDeleted(mobile, false);
    }

    @Cacheable(value = "userExists", key = "#email", unless="#result == null")
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeleted(email, false);
    }

    @Caching(put = {
        @CachePut(value = "user", key = "#user.username"),
        @CachePut(value = "user", key = "#user.mobile"),
        @CachePut(value = "user", key = "#user.email"),
            @CachePut(value = "user", key = "#user.uid"),
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

    public UserResponse convertToUserResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponseSimple convertToUserResponseSimple(User user) {
        return modelMapper.map(user, UserResponseSimple.class);
    }

    // TODO: 待完善
    public String createNickname() {
        String randomId = uidUtils.getCacheSerialUid().substring(11, 15);
        return "User" + randomId;
    }

    public void initData() {
        
        if (userRepository.count() > 0) {
            return;
        }
        
        User admin = User.builder()
                // .uid(uidUtils.getCacheSerialUid())
                .email(properties.getEmail())
                .username(properties.getUsername())
                .password(new BCryptPasswordEncoder().encode(properties.getPassword()))
                .nickname(properties.getNickname())
                .avatar(AvatarConsts.DEFAULT_AVATAR_URL)
                .mobile(properties.getMobile())
                .num(properties.getMobile())
                .superUser(true)
                .emailVerified(true)
                .mobileVerified(true)
                .build();
        admin.setUid(uidUtils.getCacheSerialUid());
        //
        Optional<Role> roleOptional = roleService.findByName(TypeConsts.ROLE_SUPER);
        Set<Role> roles = new HashSet<>();
        roleOptional.ifPresent(role -> {
            roles.add(role);
        });
        admin.setRoles(roles);
        save(admin);
    }

}
