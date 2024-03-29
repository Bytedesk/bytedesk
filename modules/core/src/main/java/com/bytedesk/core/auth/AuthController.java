/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 12:01:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.redis.RedisLoginService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import com.bytedesk.core.token.RefreshToken;
// import com.bytedesk.core.token.RefreshTokenService;
import com.bytedesk.core.utils.JsonResult;

/**
 * 
 */
@Slf4j
@RestController
@RequestMapping("/auth/v1")
@AllArgsConstructor
@Tag(name = "Auth Management Interface")
public class AuthController {

    private UserService userService;

    private AuthService authService;

    private RedisLoginService redisLoginService;

    private AuthenticationManager authenticationManager;

    //
    @PostMapping(value = "/register")
    public JsonResult<UserResponse> register(@RequestBody UserRequest userRequest) {
        try {
            UserResponse userResponse = userService.register(userRequest);
            // return ResponseEntity.ok(userResponse);
            return new JsonResult<UserResponse>("message", 200, userResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginWithUsernamePassword(@RequestBody AuthRequest authRequest) {
        log.debug("login {}", authRequest.toString());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        //
        return authService.formatResponse(authentication);
    }

    @PostMapping("/send/mobile")
    public ResponseEntity<?> sendMobileCode(@RequestBody AuthRequest authRequest) {
        log.debug("send mobile code {}", authRequest.toString());

        // check if has already send validate code within 15min
        if (redisLoginService.hasValidateCode(authRequest.getMobile())) {
            return ResponseEntity.ok()
                    .body(new JsonResult<>("validate code already sent", -1, false));
        }

        int code = authService.getRandomCode(authRequest.getMobile());
        // TODO: send mobile code

        return ResponseEntity.ok().body(new JsonResult<>("validate code success", 200, code));
    }

    @PostMapping("/login/mobile")
    public ResponseEntity<?> loginWithMobileCode(@RequestBody AuthRequest authRequest) {
        log.debug("login mobile {}", authRequest.toString());

        // validate mobile & code
        if (!redisLoginService.validateCode(authRequest.getMobile(), authRequest.getCode())) {
            return ResponseEntity.ok()
                    .body(new JsonResult<>("validate code failed", -1, false));

        }
        Authentication authentication = authService.authenticationWithMobile(authRequest.getMobile());
        //
        return authService.formatResponse(authentication);
    }

    @PostMapping("/send/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody AuthRequest authRequest) {
        log.debug("send email code {}", authRequest.toString());

        // TODO: send email code

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginWithEmailCode(@RequestBody AuthRequest authRequest) {
        log.debug("login email {}", authRequest.toString());

        // TODO: validate email & code

        Authentication authentication = authService.authenticationWithEmail(authRequest.getEmail());
        //
        return authService.formatResponse(authentication);
    }

    // @PostMapping("/refreshToken")
    // public JwtResponse refreshToken(@RequestBody RefreshTokenRequest
    // refreshTokenRequestDTO) {
    // return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
    // .map(refreshTokenService::verifyExpiration)
    // .map(RefreshToken::getUser)
    // .map(userInfo -> {
    // String accessToken = jwtService.generateToken(userInfo.getUsername());
    // // String accessToken = jwtUtils.generateJwtToken(userInfo.getUsername());
    // return JwtResponse.builder()
    // .access_token(accessToken)
    // .refresh_token(refreshTokenRequestDTO.getToken()).build();
    // }).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    // }

}
