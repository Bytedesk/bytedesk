/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-27 10:40:14
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

import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserDetailsServiceImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * https://www.baeldung.com/get-user-in-spring-security
 */
// @Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private JwtUtils jwtUtils;

    private UserDetailsServiceImpl userDetailsService;

    private ModelMapper modelMapper;

    public User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return modelMapper.map(userDetails, User.class);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(@NonNull HttpServletRequest request, String username) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //
        return authentication;
    }

    public AuthToken authenticationWithMobile(String mobile) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByMobile(mobile);
        return new AuthToken(userDetails);
    }

    public AuthToken authenticationWithEmail(String email) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByEmail(email);
        return new AuthToken(userDetails);
    }

    public AuthResponse formatResponse(Authentication authentication) {

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UserResponse userResponse = convertToUserResponse(userDetails);

        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());

        return AuthResponse.builder()
                .access_token(jwt)
                .user(userResponse)
                .build();
    }
    

    public UserResponse convertToUserResponse(UserDetailsImpl userDetails) {
        UserResponse userResponse = modelMapper.map(userDetails, UserResponse.class);

        return userResponse;
    }

    
}
