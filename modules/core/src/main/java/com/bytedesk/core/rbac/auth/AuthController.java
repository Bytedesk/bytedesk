/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-17 17:24:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionLogAnnotation;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    private AuthenticationManager authenticationManager;

    private PushService pushService;

    /**
     * 
     * @param userRequest
     * @return
     */
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {

        // validate sms code
        // 验证手机验证码
        if (!pushService.validateSmsCode(userRequest.getMobile(), userRequest.getCode())) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, false));
        }

        UserResponse userResponse = userService.register(userRequest);

        return ResponseEntity.ok(JsonResult.success("register success", userResponse));
    }

    @ActionLogAnnotation(title = "Auth", action = "loginWithUsernamePassword", description = "Login With Username & Password")
    @PostMapping("/login")
    public ResponseEntity<?> loginWithUsernamePassword(@RequestBody AuthRequest authRequest) {
        log.debug("login {}", authRequest.toString());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    @PostMapping("/send/mobile")
    public ResponseEntity<?> sendMobileCode(@RequestBody AuthRequest authRequest) {
        log.debug("send mobile code {}, client {}, type {}", authRequest.toString(), authRequest.getClient(), authRequest.getType());

        // send mobile code
        Boolean result = pushService.sendSmsCode(authRequest.getMobile(), authRequest.getClient(), authRequest.getType(), authRequest.getPlatform());
        if (!result) {
            return ResponseEntity.ok().body(JsonResult.error("already send, dont repeat", -1, false));
        }

        return ResponseEntity.ok().body(JsonResult.success("send mobile code success"));
    }

    @ActionLogAnnotation(title = "Auth", action = "loginWithMobileCode", description = "Login With mobile & code")
    @PostMapping("/login/mobile")
    public ResponseEntity<?> loginWithMobileCode(@RequestBody AuthRequest authRequest) {
        log.debug("login mobile {}", authRequest.toString());

        // validate mobile & code
        // 验证手机验证码
        if (!pushService.validateSmsCode(authRequest.getMobile(), authRequest.getCode())) {
            return ResponseEntity.ok().body(JsonResult.error("validate code failed", -1, new AuthResponse()));
        }

        // if mobile already exists, if none, then registe
        // 手机号是否已经注册，如果没有，则自动注册
        if (!userService.existsByMobileAndPlatform(authRequest.getMobile(), authRequest.getPlatform())) {
            UserRequest userRequest = new UserRequest();
            userRequest.setUsername(authRequest.getMobile());
            userRequest.setMobile(authRequest.getMobile());
            userRequest.setPlatform(authRequest.getPlatform());
            userService.register(userRequest);
        }
        
        Authentication authentication = authService.authenticationWithMobileAndPlatform(authRequest.getMobile(), authRequest.getPlatform());
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    @PostMapping("/send/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody AuthRequest authRequest) {
        log.debug("send email code {}", authRequest.toString());

        // send email code
        Boolean result = pushService.sendEmailCode(authRequest.getEmail(), authRequest.getClient(), authRequest.getType(), authRequest.getPlatform());
        if (!result) {
            return ResponseEntity.ok(JsonResult.error("already send, dont repeat", -1, false));
        }

        return ResponseEntity.ok(JsonResult.success("send email code success"));
    }

    @ActionLogAnnotation(title = "Auth", action = "loginWithEmailCode", description = "Login With email & code")
    @PostMapping("/login/email")
    public ResponseEntity<?> loginWithEmailCode(@RequestBody AuthRequest authRequest) {
        log.debug("login email {}", authRequest.toString());

        // validate email & code
        if (!pushService.validateEmailCode(authRequest.getEmail(), authRequest.getCode())) {
            return ResponseEntity.ok(JsonResult.error("validate code failed", -1, new AuthResponse()));
        }

        // 邮箱是否已经注册，如果没有，则自动注册
        if (!userService.existsByEmailAndPlatform(authRequest.getEmail(), authRequest.getPlatform())) {
            UserRequest userRequest = new UserRequest();
            userRequest.setUsername(authRequest.getEmail());
            userRequest.setEmail(authRequest.getEmail());
            userRequest.setPlatform(authRequest.getPlatform());
            userService.register(userRequest);
        }

        Authentication authentication = authService.authenticationWithEmailAndPlatform(authRequest.getEmail(), authRequest.getPlatform());
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
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
