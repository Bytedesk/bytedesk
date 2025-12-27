/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-30 16:00:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.mqtt.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.token.TokenEntity;
import com.bytedesk.core.rbac.token.TokenRestService;

@Slf4j
@Service
@AllArgsConstructor
public class MqttAuthService {

    private final TokenRestService tokenRestService;

    private final BytedeskProperties bytedeskProperties;

    public Boolean checkValid(String username, String password) {
        // 客户端使用accessToken作为password传递，避免客户端存储密码
        String accessToken = password;
        // log.debug("mqtt auth username {}, accessToken {}", username, accessToken);
        // return true;
        if (accessToken != null && validateJwtToken(accessToken)) {
            // 性能测试模式：AuthService.formatResponse 会跳过 token 表落库。
            // MQTT 鉴权链路不能再强依赖 token 表，否则会导致连接失败。
            if (bytedeskProperties.isDisableIpFilter()) {
                return true;
            }
            // 从数据库验证token是否有效（未被撤销且未过期）
            Optional<TokenEntity> tokenOpt = tokenRestService.findByAccessToken(accessToken);
            if (tokenOpt.isPresent() && tokenOpt.get().isValid()) {
                return true;
            } else {
                // log.debug("mqtt auth Token is invalid or revoked");
                return false;
            }
        }
        log.debug("mqtt auth Token is invalid or expired");
        return false;
    }

    private boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(bytedeskProperties.getJwt().getSecretKey()));
    }

}
