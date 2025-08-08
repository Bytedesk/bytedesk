/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 09:47:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.bytedesk.core.utils.PasswordCryptoUtils;
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

    private final BCryptPasswordEncoder passwordEncoder;

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
     * 密码哈希登录 - 支持AES解密
     * 前端使用AES加密密码，后端解密后验证
     */
    public Authentication authenticateWithPasswordHash(AuthRequest authRequest) {
        // 1. 查询用户
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails == null) {
            log.warn("User not found: {}", authRequest.getUsername());
            return null;
        }
        // 实际情况：现在支持AES解密前端加密的密码
        try {
            // 2. 使用AES解密前端发送的加密密码
            String decryptedPassword = PasswordCryptoUtils.decryptPassword(
                authRequest.getPasswordHash(), 
                authRequest.getPasswordSalt()
            );
            log.debug("Password decrypted successfully for user: {}", authRequest.getUsername());

            // 3. 验证解密后的密码与数据库中存储的BCrypt密码
            String dbPassword = userDetails.getPassword(); // 数据库存储的BCrypt加密密码
            boolean isValid = passwordEncoder.matches(decryptedPassword, dbPassword);
            // 密码验证失败
            if (!isValid) {
                log.warn("Password verification failed for user: {}", authRequest.getUsername());
                return null;
            }

            // 4. 密码验证成功，构造认证对象
            log.info("Password hash authentication successful for user: {}", authRequest.getUsername());
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
        } catch (Exception e) {
            log.error("Password hash authentication error for user: {}: {}", authRequest.getUsername(), e.getMessage());
            return null;
        }
    }


}
