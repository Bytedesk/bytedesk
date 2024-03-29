/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 13:07:17
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
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

// import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.rbac.role.RoleService;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    // private final AuthService authService;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final BytedeskProperties properties;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse register(UserRequest userRequest) {
        if (userRequest.getUsername() == null) {
            throw new RuntimeException("Parameter username is not found in request..!!");
        } else if (userRequest.getPassword() == null) {
            throw new RuntimeException("Parameter password is not found in request..!!");
        }
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = userRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        //
        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(encodedPassword);
        // user.setCreatedBy(currentUser);
        //
        User savedUser = userRepository.save(user);
        // userRepository.refresh(savedUser);
        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        //
        return userResponse;
    }

    public User createUserFromMember(String nickname, String password, String mobile, String email, boolean isVerified,
                String organization_oid) {

        User user = User.builder()
                .uid(Utils.getUid())
                .avatar(AvatarConsts.DEFAULT_AVATAR_URL)
                .username(mobile)
                .nickname(nickname)
                .mobile(mobile)
                .num(mobile)
                .email(email)
                .superUser(false)
                .verified(isVerified)
                .build();

        if (StringUtils.hasLength(password)) {
            user.setPassword(passwordEncoder.encode(password));
        } else {
            user.setPassword(passwordEncoder.encode("123456"));
        }

        user.getOrganizations().add(organization_oid);

        return user;
    }

    public User updateUserFromMember(User user, String password, String mobile, String email) {

        if (StringUtils.hasLength(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setMobile(mobile);
        user.setNum(mobile);
        user.setEmail(email);
        save(user);

        return user;
    }

    // public UserResponse getUser() {
    //     User user = authService.getCurrentUser();
    //     return modelMapper.map(user, UserResponse.class);
    // }

    @Cacheable(cacheNames = "users", unless="#result == null")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Cacheable(cacheNames = "users", unless="#result == null")
    public Optional<User> findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Cacheable(cacheNames = "users", unless="#result == null")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(cacheNames = "users", unless="#result == null")
    public Optional<User> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    @Cacheable(cacheNames = "admin", unless="#result == null")
    public Optional<User> getAdmin() {
        return userRepository.findByUsername(properties.getUsername());
    }

    // 
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeleted(username, false);
    }

    public Boolean existsByMobile(String mobile) {
        return userRepository.existsByMobileAndDeleted(mobile, false);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeleted(email, false);
    }

    
    @Caching(put = {
        @CachePut(cacheNames = "users", key = "#user.username"),
        @CachePut(cacheNames = "users", key = "#user.mobile"),
        @CachePut(cacheNames = "users", key = "#user.email"),
        @CachePut(cacheNames = "users", key = "#user.uid")
    })
    public User save(@NonNull User user) {
        return userRepository.save(user);
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "users", key = "#user.username"),
        @CacheEvict(cacheNames = "users", key = "#user.mobile"),
        @CacheEvict(cacheNames = "users", key = "#user.email"),
        @CacheEvict(cacheNames = "users", key = "#user.uid")
    })
    public void delete(@NonNull User user) {
        userRepository.delete(user);
    }

    public void initData() {
        
        if (userRepository.count() > 0) {
            log.debug("user already exists");
            return;
        }
        //
        User admin = User.builder()
                .uid(Utils.getUid())
                .email(properties.getEmail())
                .username(properties.getUsername())
                .password(new BCryptPasswordEncoder().encode(properties.getPassword()))
                .nickname("Admin")
                .avatar(AvatarConsts.DEFAULT_AVATAR_URL)
                .mobile(properties.getMobile())
                .num(properties.getMobile())
                .superUser(true)
                .verified(true)
                .build();
        //
        Optional<Role> roleOptional = roleService.findByValue(TypeConsts.ROLE_ADMIN);
        List<Role> roles = new ArrayList<>();
        if (roleOptional.isPresent()) {
            roles.add(roleOptional.get());
        }
        // 
        admin.setRoles(roles);
        userRepository.save(admin);
    }

}
