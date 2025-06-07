/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-10 13:02:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 17:52:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth.oauth2;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        
        // 获取认证提供商（github、google等）
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        // 获取用户信息
        Map<String, Object> attributes = user.getAttributes();
        String email = null;
        String name = null;
        
        // 根据不同提供商解析用户信息
        switch (provider) {
            case "github":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                break;
            case "google":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                break;
            case "wecom":
                // String userId = (String) attributes.get("userid");
                // TODO: 调用企业微信API获取用户详细信息
                break;
            default:
                log.warn("Unsupported OAuth2 provider: {}", provider);
        }
        
        log.info("OAuth2 login success: provider={}, email={}, name={}", provider, email, name);
        
        return user;
    }
} 