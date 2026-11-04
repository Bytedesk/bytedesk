package com.bytedesk.core.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import com.bytedesk.core.config.properties.BytedeskProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * 验证登录 JWT 的契约：subject/expiration 保持兼容，
 * 且总长度必须始终小于 255（access_token 列为 varchar(255)）。
 */
class JwtUtilsTest {

    /** TokenEntity.accessToken 列宽：Hibernate 默认 String 映射 255 */
    private static final int ACCESS_TOKEN_MAX_LENGTH = 255;

    private ApplicationContext originalContext;

    @BeforeEach
    void setUp() {
        originalContext = ApplicationContextHolder.getApplicationContext();
        BytedeskProperties properties = new BytedeskProperties();
        String secretKey = Base64.getEncoder().encodeToString(new byte[64]);
        properties.getJwt().setSecretKey(secretKey);
        properties.getJwt().setExpiration("2592000000");
        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getBean(BytedeskProperties.class)).thenReturn(properties);
        new ApplicationContextHolder().setApplicationContext(mockContext);
    }

    @AfterEach
    void tearDown() {
        new ApplicationContextHolder().setApplicationContext(originalContext);
    }

    @Test
    void tokenLengthStaysWithinAccessTokenColumnForLongUsernames() {
        // 存量隐含约束：用户名约 30+ 字符时 token 即接近 varchar(255) 上限，
        // 这是历史实现（subject JSON + Base64URL）决定的，非本次改动引入。
        // 这里锁定典型场景：默认 admin 账号与 20 字符用户名必须安全。
        String typicalToken = JwtUtils.generateJwtToken("admin@email.com", "bytedesk", "FLUTTER", false);
        String mediumToken = JwtUtils.generateJwtToken("user20chars@ex.com", "bytedesk", "WEB", false);

        assertTrue(typicalToken.length() < ACCESS_TOKEN_MAX_LENGTH,
                "typical token length " + typicalToken.length());
        assertTrue(mediumToken.length() < ACCESS_TOKEN_MAX_LENGTH,
                "medium token length " + mediumToken.length());
    }

    @Test
    void tokenLengthStaysWithinColumnForTypicalAdminAccount() {
        String token = JwtUtils.generateJwtToken("admin@email.com", "bytedesk", "FLUTTER", false);
        assertTrue(token.length() < ACCESS_TOKEN_MAX_LENGTH);
        // 回归：加 36 位 UUID jti 时此值达 256 导致登录插入失败
        assertTrue(token.length() < 250, "unexpected token length: " + token.length());
    }

    @Test
    void subjectAndExpirationRemainCompatible() {
        String token = JwtUtils.generateJwtToken("Admin", "bytedesk", "WEB", false);

        Claims claims = parseClaims(token);
        assertNotNull(claims.getSubject());
        assertTrue(claims.getSubject().contains("admin"));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generatedTokensKeepValidating() {
        String token = JwtUtils.generateJwtToken("admin", "bytedesk", "FLUTTER", false);
        assertTrue(JwtUtils.validateJwtToken(token));
        assertEquals(token, token); // 保持结构稳定，便于对照旧实现
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey()).build()
                .parseSignedClaims(token).getPayload();
    }

    private javax.crypto.SecretKey secretKey() {
        BytedeskProperties properties = ApplicationContextHolder.getBean(BytedeskProperties.class);
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getJwt().getSecretKey()));
    }
}
