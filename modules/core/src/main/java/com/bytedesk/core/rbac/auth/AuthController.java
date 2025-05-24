/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 16:05:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com
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

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.kaptcha.KaptchaCacheService;
import com.bytedesk.core.push.PushRestService;
import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.utils.JsonResult;

@Slf4j
@RestController
@RequestMapping("/auth/v1")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private UserService userService;

    private AuthService authService;

    private PushRestService pushService;

    private KaptchaCacheService kaptchaCacheService;

    private AuthenticationManager authenticationManager;

    // @ActionAnnotation(title = "auth", action = "register", description = "register")
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest, HttpServletRequest request) {

        if (!kaptchaCacheService.checkKaptcha(userRequest.getCaptchaUid(), userRequest.getCaptchaCode(), userRequest.getClient())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // validate sms code
        // 验证手机验证码
        if (!pushService.validateCode(userRequest.getMobile(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -1, false));
        }

        UserResponse userResponse = userService.register(userRequest);

        return ResponseEntity.ok(JsonResult.success("register success", userResponse));
    }

    @PostMapping("/login")
    @ActionAnnotation(title = "用户", action = BytedeskConsts.ACTION_LOGIN_USERNAME, description = "Login With Username & Password")
    public ResponseEntity<?> loginWithUsernamePassword(@RequestBody AuthRequest authRequest) {
        log.debug("login {}", authRequest.toString());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getClient())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    // @ActionAnnotation(title = "auth", action = "sendMobileCode", description = "Send mobile code")
    @PostMapping("/send/mobile")
    public ResponseEntity<?> sendMobileCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("send mobile code {}, client {}, type {}", authRequest.toString(), authRequest.getClient(),
                authRequest.getType());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getClient())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // send mobile code
        Boolean result = pushService.sendCode(authRequest, request);
        if (!result) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ALREADY_SEND, -1, false));
        }

        return ResponseEntity.ok().body(JsonResult.success(I18Consts.I18N_AUTH_CAPTCHA_SEND_SUCCESS));
    }

    // @ActionAnnotation(title = "auth", action = BytedeskConsts.ACTION_LOGIN_MOBILE, description = "Login With mobile & code")
    @PostMapping("/login/mobile")
    public ResponseEntity<?> loginWithMobileCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("login mobile {}", authRequest.toString());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getClient())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }
        // validate mobile & code
        // 验证手机验证码
        if (!pushService.validateCode(authRequest.getMobile(), authRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -1, false));
        }

        // if mobile already exists, if none, then register
        // 手机号是否已经注册，如果没有，则自动注册
        if (!userService.existsByMobileAndPlatform(authRequest.getMobile(), authRequest.getPlatform())) {
            UserRequest userRequest = new UserRequest();
            userRequest.setUsername(authRequest.getMobile());
            userRequest.setNum(authRequest.getMobile());
            userRequest.setMobile(authRequest.getMobile());
            userRequest.setPlatform(authRequest.getPlatform());
            userService.register(userRequest);
        }

        Authentication authentication = authService.authenticationWithMobileAndPlatform(
                authRequest.getMobile(),
                authRequest.getPlatform(),
                authRequest.getClient(),
                authRequest.getDevice());
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    // @ActionAnnotation(title = "auth", action = "sendEmailCode", description = "Send email code")
    @PostMapping("/send/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("send email code {}", authRequest.toString());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getClient())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }
        // send email code
        Boolean result = pushService.sendCode(authRequest, request);
        if (!result) {
            return ResponseEntity.ok(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ALREADY_SEND, -1, false));
        }

        return ResponseEntity.ok(JsonResult.success(I18Consts.I18N_AUTH_CAPTCHA_SEND_SUCCESS));
    }

    @ActionAnnotation(title = "用户", action = BytedeskConsts.ACTION_LOGIN_EMAIL, description = "Login With email & code")
    @PostMapping("/login/email")
    public ResponseEntity<?> loginWithEmailCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("login email {}", authRequest.toString());

        // validate email & code
        if (!pushService.validateCode(authRequest.getEmail(), authRequest.getCode(), request)) {
            return ResponseEntity.ok(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -1, false));
        }

        // 邮箱是否已经注册，如果没有，则自动注册
        if (!userService.existsByEmailAndPlatform(authRequest.getEmail(),
                authRequest.getPlatform())) {
            UserRequest userRequest = new UserRequest();
            userRequest.setUsername(authRequest.getEmail());
            userRequest.setNum(authRequest.getEmail());
            userRequest.setEmail(authRequest.getEmail());
            userRequest.setPlatform(authRequest.getPlatform());
            userService.register(userRequest);
        }

        Authentication authentication = authService.authenticationWithEmailAndPlatform(
                authRequest.getEmail(),
                authRequest.getPlatform(),
                authRequest.getClient(),
                authRequest.getDevice());
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    // TODO: 刷新token
    // @PostMapping("/refreshToken")
    // public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequestDTO) {
    // }

}
