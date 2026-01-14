/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:17:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 16:30:09
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.UserDisabledException;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.token.TokenEntity;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JwtUtils;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

  @Autowired
  private AuthService authService;

  @Autowired
  private TokenRestService tokenRestService;

  @Autowired
  private BytedeskProperties bytedeskProperties;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String accessToken = JwtUtils.parseAccessToken(request);
      // log.debug("accessToken {}", accessToken);
      if (accessToken != null && JwtUtils.validateJwtToken(accessToken)) {
        // 性能测试模式：AuthService.formatResponse 会跳过 token 表落库。
        // 为避免认证链路依赖 token 表导致 401，这里允许仅校验 JWT。
        if (bytedeskProperties.isDisableIpFilter()) {
          String subject = JwtUtils.getSubjectFromJwtToken(accessToken);
          UsernamePasswordAuthenticationToken authentication = authService.getAuthentication(request, subject);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          // 从数据库验证token是否有效（未被撤销且未过期）
          Optional<TokenEntity> tokenOpt = tokenRestService.findByAccessToken(accessToken);
          if (tokenOpt.isPresent() && tokenOpt.get().isValid()) {
            // 记录最近活跃时间（节流更新）
            tokenRestService.touchLastActiveAtIfNeeded(tokenOpt.get());
            String subject = JwtUtils.getSubjectFromJwtToken(accessToken);
            UsernamePasswordAuthenticationToken authentication = authService.getAuthentication(request, subject);
            SecurityContextHolder.getContext().setAuthentication(authentication);
          } else {
            log.debug("Token is invalid or revoked");
          }
        }
      }
    } catch (UsernameNotFoundException e) {
      SecurityContextHolder.clearContext();
      // token 对应用户不存在：给前端一个明确 401 文案
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(
          JSON.toJSONString(
              JsonResult.error(I18Consts.I18N_USER_SIGNUP_FIRST, HttpServletResponse.SC_UNAUTHORIZED)));
      return;
    } catch (UserDisabledException e) {
      SecurityContextHolder.clearContext();
      // token 对应用户被禁用：403
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(
          JSON.toJSONString(
              JsonResult.error(I18Consts.I18N_USER_DISABLED, HttpServletResponse.SC_FORBIDDEN)));
      return;
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e);
    }
    filterChain.doFilter(request, response);
  }

  
}
