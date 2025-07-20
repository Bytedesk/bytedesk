/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-20 16:11:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 * Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.rbac.token.TokenEntity;
import com.bytedesk.core.rbac.token.TokenRepository;
import com.bytedesk.core.rbac.token.TokenRequest;
import com.bytedesk.core.rbac.token.TokenTypeEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserDetailsServiceImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://www.baeldung.com/get-user-in-spring-security
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsServiceImpl userDetailsService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;
    
    private final TokenRepository tokenRepository;

    public UserEntity getUser() {
        // not logged in
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        //
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            return modelMapper.map(userDetails, UserEntity.class);
        } catch (Exception e) {
            // TODO: handle exception
            // FIXME: 验证码错误时会报错：java.lang.ClassCastException: class java.lang.String cannot be cast to
            // class com.bytedesk.core.rbac.user.UserDetailsImpl (java.lang.String is in
            // module java.base of loader 'bootstrap';
            // com.bytedesk.core.rbac.user.UserDetailsImpl is in unnamed module of loader 'app')
            // e.printStackTrace();
        }
        return null;
    }

    public UserEntity getCurrentUser() {
        return getUser();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(@NonNull HttpServletRequest request, String subject) {

        UserDetails userDetails = userDetailsService.loadUserByUsernameAndPlatform(subject);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //
        return authentication;
    }

    public AuthToken authenticationWithMobileAndPlatform(String mobile, String platform, String client, String device) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByMobileAndPlatform(mobile, platform);
        userDetails.setChannel(client);
        userDetails.setDevice(device);
        return new AuthToken(userDetails);
    }

    public AuthToken authenticationWithEmailAndPlatform(String email, String platform, String client, String device) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByEmailAndPlatform(email, platform);
        userDetails.setChannel(client);
        userDetails.setDevice(device);
        return new AuthToken(userDetails);
    }

    public AuthResponse formatResponse(Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UserResponse userResponse = ConvertUtils.convertToUserResponse(userDetails);

        // 登录成功后，将生成的accessToken同时保存到数据库中
        String channel = userDetails.getChannel();
        if (channel == null || channel.isEmpty()) {
            // 如果UserDetailsImpl中没有client信息，使用默认值
            channel = ChannelEnum.WEB.name();
        }

        String accessToken = JwtUtils.generateJwtToken(userDetails.getUsername(), userDetails.getPlatform(), channel);
        
        String device = userDetails.getDevice();
        if (device == null || device.isEmpty()) {
            // 如果没有设备信息，使用一个描述性值
            device = "Unknown Device";
        }
        
        // 使用create接口创建保存token
        TokenRequest tokenRequest = TokenRequest.builder()
            .name("Login Token")
            .description("User login authentication token")
            .accessToken(accessToken)
            .type(TokenTypeEnum.BEARER.name())
            .revoked(false)
            .channel(channel)
            .device(device)
            .userUid(userDetails.getUid())
            .build();
        
        // 使用TokenRestService来创建token，它会统一处理过期时间
        TokenEntity entity = modelMapper.map(tokenRequest, TokenEntity.class);
        entity.setUid(uidUtils.getUid());
        
        // 统一设置过期时间，与JWT配置保持一致
        entity.setExpiresAt(JwtUtils.calculateExpirationTime(channel));
        
        TokenEntity savedEntity = tokenRepository.save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create token failed");
        }
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .user(userResponse)
                .build();
    }

    /**
     * TODO: 密码哈希登录
     */
    public Authentication authenticateWithPasswordHash(AuthRequest authRequest) {
        // 1. 查询用户
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails == null) {
            log.warn("User not found: {}", authRequest.getUsername());
            return null;
        }
        // 2. 验证哈希
        // String dbPassword = userDetails.getPassword(); // 数据库存储的BCrypt加密密码
        // 由于数据库存储的是BCrypt加密后的密码，无法直接反向获取明文
        // 这里假设数据库还存储了明文密码或有其他方式校验（实际生产环境不推荐明文存储）
        // 这里演示：如果数据库存储明文密码，则直接用PasswordHashUtils校验
        // 如果存储的是BCrypt，则无法支持前端hash+salt方式，建议前端直接传明文密码
        //
        // 这里假设数据库存储明文密码（仅演示，实际应存BCrypt）
        // String plainPassword = dbPassword;
        // boolean valid = PasswordHashUtils.verifyPasswordHash(plainPassword, authRequest.getPasswordSalt(), authRequest.getPasswordHash());
        //
        // 实际情况：如果数据库存储BCrypt，则无法支持前端hash+salt方式
        // 这里直接返回null，提示不支持
        log.warn("当前系统仅支持明文密码登录，BCrypt加密无法支持前端hash+salt方式");
        return null;

        // 3. 用 passwordHash 作为“明文”去匹配
        // org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        // boolean valid = encoder.matches(authRequest.getPasswordHash(), dbPassword);
        // if (!valid) {
        //     log.warn("Password hash verification failed for user: {}", authRequest.getUsername());
        //     return null;
        // }
        // 4. 构造认证对象
        // return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


}
