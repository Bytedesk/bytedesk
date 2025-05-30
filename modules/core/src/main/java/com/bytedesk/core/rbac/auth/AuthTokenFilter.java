/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 09:52:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bytedesk.core.rbac.token.TokenEntity;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.utils.JwtUtils;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

  @Autowired
  private AuthService authService;

  @Autowired
  private TokenRestService tokenRestService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String accessToken = parseAccessToken(request);
      // log.debug("accessToken {}", accessToken);
      if (accessToken != null && JwtUtils.validateJwtToken(accessToken)) {
        // 从数据库验证token是否有效（未被撤销且未过期）
        Optional<TokenEntity> tokenOpt = tokenRestService.findByAccessToken(accessToken);
        if (tokenOpt.isPresent() && tokenOpt.get().isValid()) {
          String subject = JwtUtils.getSubjectFromJwtToken(accessToken);
          // log.debug("subject {}", subject);
          //
          UsernamePasswordAuthenticationToken authentication = authService.getAuthentication(request, subject);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          log.debug("Token is invalid or revoked");
        }
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e);
    }
    filterChain.doFilter(request, response);
  }

  private String parseAccessToken(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");
    // read post header
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }
    // read url get parameter
    headerAuth = request.getParameter("accessToken");
    if (StringUtils.hasText(headerAuth)) {
      // log.info("accessToken from request param: {}", headerAuth);
      return headerAuth;
    }
    return null;
  }
}
