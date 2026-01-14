/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 17:43:54
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
import org.springframework.util.StringUtils;

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
import com.bytedesk.core.config.properties.BytedeskProperties;

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

    private final BytedeskProperties bytedeskProperties;

    public UserEntity getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }

        // 常见情况：未登录/匿名访问时 principal 可能是 String（例如 anonymousUser）
        if (principal instanceof UserDetailsImpl userDetails) {
            return modelMapper.map(userDetails, UserEntity.class);
        }
        if (principal instanceof UserDetails userDetails) {
            // 兼容其它实现的 UserDetails
            return modelMapper.map(userDetails, UserEntity.class);
        }

        // 未登录/匿名访问：principal 可能是 String（例如 anonymousUser）
        if (principal instanceof String) {
            return null;
        }

        // 其它类型（例如 String）视为未登录，避免刷 ClassCastException 日志
        log.debug("Unexpected principal type: {}", principal.getClass().getName());
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

    public AuthResponse formatResponse(AuthRequest authRequest, Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UserResponse userResponse = ConvertUtils.convertToUserResponse(userDetails);

        // 登录成功后，将生成的accessToken同时保存到数据库中
        String channel = authRequest.getChannel();
        if (!StringUtils.hasText(channel)) {
            channel = ChannelEnum.WEB.name();
        }

        String accessToken = JwtUtils.generateJwtToken(userDetails.getUsername(), userDetails.getPlatform(), channel);

        // 性能测试模式：避免每次登录都写token表，减少DB写压力
        if (bytedeskProperties.isDisableIpFilter()) {
            return AuthResponse.builder()
                .accessToken(accessToken)
                .user(userResponse)
                .build();
        }
        
        String device = authRequest.getDevice();
        if (!StringUtils.hasText(device)) {
            // 如果没有设备信息，使用一个描述性值
            device = "Unknown Device";
        }
        
        // 使用create接口创建保存token
        TokenRequest tokenRequest = TokenRequest.builder()
            .name("Login Token")
            .description("login token")
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
     * 通用的密码验证逻辑
     */
    private Authentication authenticateWithPassword(UserDetailsImpl userDetails, String password, String username) {
        try {
            // 验证密码与数据库中存储的BCrypt密码
            String dbPassword = userDetails.getPassword(); // 数据库存储的BCrypt加密密码
            boolean isValid = passwordEncoder.matches(password, dbPassword);
            
            if (!isValid) {
                log.warn("Password verification failed for user: {}", username);
                return null;
            }

            // 密码验证成功，构造认证对象
            log.info("Password authentication successful for user: {}", username);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
        } catch (Exception e) {
            log.error("Password authentication error for user: {}: {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * 明文密码登录 - 直接验证明文密码
     * 参考 authenticateWithPasswordHash 逻辑，去掉AES解密过程
     */
    public Authentication authenticateWithPlainPassword(AuthRequest authRequest) {
        // 1. 查询用户，传入平台信息
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsernameAndPlatform(authRequest.getUsername(), authRequest.getPlatform());
        if (userDetails == null) {
            log.warn("User not found: {}", authRequest.getUsername());
            return null;
        }

        // 2. 直接使用明文密码验证（无需AES解密）
        String plainPassword = authRequest.getPassword();
        log.debug("Using plain password authentication for user: {}", authRequest.getUsername());

        return authenticateWithPassword(userDetails, plainPassword, authRequest.getUsername());
    }

    /**
     * 密码哈希登录 - 支持AES解密
     * 前端使用AES加密密码，后端解密后验证
     */
    public Authentication authenticateWithPasswordHash(AuthRequest authRequest) {
        // 1. 查询用户
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsernameAndPlatform(authRequest.getUsername(), authRequest.getPlatform());
        if (userDetails == null) {
            log.warn("User not found: {}", authRequest.getUsername());
            return null;
        }
        
        try {
            // 2. 使用AES解密前端发送的加密密码
            String decryptedPassword = PasswordCryptoUtils.decryptPassword(
                authRequest.getPasswordHash(),
                authRequest.getPasswordSalt()
            );
            log.debug("Password decrypted successfully for user: {}, length: {}, first 3 chars: {}",
                     authRequest.getUsername(),
                     decryptedPassword.length(),
                     decryptedPassword.length() > 3 ? decryptedPassword.substring(0, 3) + "***" : decryptedPassword);

            return authenticateWithPassword(userDetails, decryptedPassword, authRequest.getUsername());
            
        } catch (Exception e) {
            // Do not log credential material; keep enough context for troubleshooting
            log.warn("Password hash authentication failed (decrypt/verify): username={}, platform={}, channel={}",
                    authRequest.getUsername(), authRequest.getPlatform(), authRequest.getChannel(), e);
            return null;
        }
    }


}
