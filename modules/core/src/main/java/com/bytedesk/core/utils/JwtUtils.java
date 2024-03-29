/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-07 22:42:26
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

// import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// @AllArgsConstructor
public class JwtUtils {

  @Value("${bytedesk.jwt-secret-key}")
  private String jwtSecret;

  @Value("${bytedesk.jwt-expiration}")
  private long jwtExpirationMs;

  // @Autowired
  // private BytedeskProperties bytedeskProperties;

  @SuppressWarnings("deprecation")
  public String generateJwtToken(String username) {
    // log.info("generateJwtToken: {}, key {}, expiration {}",
    //     username, 
    //     bytedeskProperties.getJwtSecretKey(),
    //     bytedeskProperties.getJwtExpiration()
    //     );

    return Jwts.builder()
        .subject((username))
        .issuedAt(new Date())
        .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(),
            SignatureAlgorithm.HS256)
        .compact();
  }

  // public String generateJwtToken(Authentication authentication) {
  // UserDetailsImpl userPrincipal = (UserDetailsImpl)
  // authentication.getPrincipal();
  // return Jwts.builder()
  // .subject((userPrincipal.getUsername()))
  // .issuedAt(new Date())
  // .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
  // .signWith(key(),
  // SignatureAlgorithm.HS256) // SignatureAlgorithm.HS256
  // .compact();
  // }

  // private SecretKey key() {
  // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  // }

  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  @SuppressWarnings("deprecation")
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(key()).build()
        .parseSignedClaims(token).getPayload().getSubject();
  }

  @SuppressWarnings("deprecation")
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(key()).build().parse(authToken);
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
}
