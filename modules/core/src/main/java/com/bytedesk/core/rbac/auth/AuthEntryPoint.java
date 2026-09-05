/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 12:45:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 12:24:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.token.TokenEntity;
import com.bytedesk.core.rbac.token.TokenRepository;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

  private final TokenRepository tokenRepository;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    // log.error("Unauthorized error: {}", authException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    //
    JsonResult<?> body = JsonResult.error(resolveUnauthorizedMessage(request, authException),
        HttpServletResponse.SC_UNAUTHORIZED, request.getServletPath());
    // 
    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }

  /**
   * 成员被禁用（forceLogout）时后端会撤销该成员在本组织的全部 token，
   * 此时前端仍携带已被撤销的 bearer token 访问，默认只能得到
   * "Full authentication is required to access this resource" 这类技术性报错。
   * 这里根据 token 的撤销原因识别该场景，返回明确的禁用提示（i18n key），
   * 前端据此展示"账号已被管理员禁用，请联系管理员启用后再登录"并引导重新登录。
   */
  private String resolveUnauthorizedMessage(HttpServletRequest request, AuthenticationException authException) {
    try {
      String accessToken = JwtUtils.parseAccessToken(request);
      if (!StringUtils.hasText(accessToken)) {
        return authException.getMessage();
      }
      Optional<TokenEntity> tokenOpt = tokenRepository.findFirstByAccessTokenAndDeletedFalse(accessToken);
      if (tokenOpt.isEmpty()) {
        return authException.getMessage();
      }
      TokenEntity token = tokenOpt.get();
      if (Boolean.TRUE.equals(token.getRevoked())
          && TokenRestService.REVOKE_REASON_MEMBER_FORCE_LOGOUT.equals(token.getRevokeReason())) {
        return I18Consts.I18N_FORCE_LOGOUT_REASON;
      }
    } catch (Exception e) {
      log.debug("AuthEntryPoint resolve forceLogout revoke reason failed: {}", e.getMessage());
    }
    return authException.getMessage();
  }

}
