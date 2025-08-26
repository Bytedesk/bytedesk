/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-20 15:52:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.time.ZonedDateTime;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.config.properties.BytedeskProperties;

/**
 * https://github.com/jwtk/jjwt#jws-create-key
 */
@Slf4j
@UtilityClass
public class JwtUtils {

    private BytedeskProperties getBytedeskProperties() {
        return ApplicationContextHolder.getBean(BytedeskProperties.class);
    }

    /**
     * https://github.com/jwtk/jjwt?tab=readme-ov-file#creating-a-jwt
     * 根据渠道类型生成JWT token，支持不同的过期时间
     * 
     * @param username 用户名
     * @param platform 平台
     * @param channel 渠道类型（web/mobile等）
     * @return JWT token字符串
     */
    public String generateJwtToken(String username, String platform, String channel) {
        JwtSubject jwtSubject = new JwtSubject(username.toLowerCase(), platform.toLowerCase());
        
        // 根据渠道类型计算过期时间
        long expirationMs = calculateExpirationMs(channel);
        
        return Jwts.builder()
            .subject(jwtSubject.toJson())
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + expirationMs))
            .signWith(secretKey())
            .compact();
    }

    /**
     * 根据渠道类型计算过期时间（毫秒）
     * 
     * @param channel 渠道类型
     * @return 过期时间（毫秒）
     */
    private long calculateExpirationMs(String channel) {
        // 获取JWT配置的过期时间（毫秒）
        long jwtExpirationMs = Long.parseLong(getBytedeskProperties().getJwt().getExpiration());
        
        // 根据渠道类型调整过期时间
        if (channel != null &amp;amp;& channel.toLowerCase().contains("web")) {
            // Web端使用JWT配置的过期时间（默认30天）
            return jwtExpirationMs;
        } else {
            // 移动端等其他客户端使用更长的过期时间（365天）
            return 365L * 24 * 60 * 60 * 1000; // 365天转换为毫秒
        }
    }

    /**
     * 根据渠道类型计算过期时间（ZonedDateTime）
     * 统一JWT和数据库token的过期时间处理
     * 
     * @param channel 渠道类型
     * @return ZonedDateTime 过期时间
     */
    public static ZonedDateTime calculateExpirationTime(String channel) {
        // 获取JWT配置的过期时间（毫秒）
        long jwtExpirationMs = Long.parseLong(getBytedeskProperties().getJwt().getExpiration());
        
        // 根据渠道类型调整过期时间
        if (channel != null && channel.toLowerCase().contains("web")) {
            // Web端使用JWT配置的过期时间（默认30天）
            return BdDateUtils.now().plusSeconds(jwtExpirationMs / 1000);
        } else {
            // 移动端等其他客户端使用更长的过期时间（365天）
            return BdDateUtils.now().plusDays(365);
        }
    }

    public Boolean validateJwtToken(String authToken) {
        try {
            // 这一步会验证token的签名和过期时间，如果token已过期会抛出ExpiredJwtException
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

    public String getSubjectFromJwtToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(getBytedeskProperties().getJwt().getSecretKey()));
    }
}
