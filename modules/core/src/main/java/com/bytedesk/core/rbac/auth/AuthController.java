/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 10:29:20
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
import org.springframework.context.annotation.Description;

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
import com.bytedesk.core.utils.PasswordHashUtils;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

@Slf4j
@RestController
@RequestMapping("/auth/v1")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
@Description("Authentication Controller - User authentication and authorization management")
public class AuthController {

    private UserService userService;

    private AuthService authService;

    private PushRestService pushRestService;

    private KaptchaCacheService kaptchaCacheService;

    private AuthenticationManager authenticationManager;

    private UserDetailsServiceImpl userDetailsService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest, HttpServletRequest request) {

        if (!kaptchaCacheService.checkKaptcha(userRequest.getCaptchaUid(), userRequest.getCaptchaCode(), userRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // validate sms code
        // 验证手机验证码
        if (!pushRestService.validateCode(userRequest.getMobile(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -1, false));
        }

        UserResponse userResponse = userService.register(userRequest);

        return ResponseEntity.ok(JsonResult.success("register success", userResponse));
    }

    @PostMapping("/login")
    @ActionAnnotation(title = "用户", action = BytedeskConsts.ACTION_LOGIN_USERNAME, description = "Login With Username & Password")
    public ResponseEntity<?> loginWithUsernamePassword(@RequestBody AuthRequest authRequest) {
        log.debug("login {}", authRequest.toString());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        Authentication authentication;
        // 判断是否使用密码哈希登录
        if (StringUtils.hasText(authRequest.getPassword())) {
            // 使用现有登录逻辑（明文密码）
            log.debug("Using plain password authentication");
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } else if (StringUtils.hasText(authRequest.getPasswordHash()) && StringUtils.hasText(authRequest.getPasswordSalt())) {
            // 使用密码哈希登录
            log.debug("Using password hash authentication");
            authentication = authenticateWithPasswordHash(authRequest);
            if (authentication == null) {
                return ResponseEntity.ok().body(JsonResult.error("用户名或密码错误", -1, false));
            }
        } else {
            return ResponseEntity.ok().body(JsonResult.error("Password or password hash is required", -1, false));
        }
        //
        AuthResponse authResponse = authService.formatResponse(authentication);
        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    /**
     * 密码哈希登录
     */
    private Authentication authenticateWithPasswordHash(AuthRequest authRequest) {
        // 1. 查询用户
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails == null) {
            log.warn("User not found: {}", authRequest.getUsername());
            return null;
        }
        // 2. 验证哈希
        // String dbPassword = userDetails.getPassword(); // 数据库存储的BCrypt加密密码
        // 由于数据库存储的是BCrypt加密后的密码，无法直接反向获取明文
        // 这里假设数据库还存储了明文密码或有其他方式校验（实际生产环境不推荐明文存储）
        // 这里演示：如果数据库存储明文密码，则直接用PasswordHashUtils校验
        // 如果存储的是BCrypt，则无法支持前端hash+salt方式，建议前端直接传明文密码
        //
        // 这里假设数据库存储明文密码（仅演示，实际应存BCrypt）
        // String plainPassword = dbPassword;
        // boolean valid = PasswordHashUtils.verifyPasswordHash(plainPassword, authRequest.getPasswordSalt(), authRequest.getPasswordHash());
        //
        // 实际情况：如果数据库存储BCrypt，则无法支持前端hash+salt方式
        // 这里直接返回null，提示不支持
        log.warn("当前系统仅支持明文密码登录，BCrypt加密无法支持前端hash+salt方式");
        return null;

        // 3. 用 passwordHash 作为“明文”去匹配
        // org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        // boolean valid = encoder.matches(authRequest.getPasswordHash(), dbPassword);
        // if (!valid) {
        //     log.warn("Password hash verification failed for user: {}", authRequest.getUsername());
        //     return null;
        // }
        // 4. 构造认证对象
        // return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // @ActionAnnotation(title = "auth", action = "sendMobileCode", description = "Send mobile code")
    @PostMapping("/send/mobile")
    public ResponseEntity<?> sendMobileCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("send mobile code {}, client {}, type {}", authRequest.toString(), authRequest.getChannel(),
                authRequest.getType());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // send mobile code
        Boolean result = pushRestService.sendCode(authRequest, request);
        if (!result) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ALREADY_SEND, -1, false));
        }

        return ResponseEntity.ok().body(JsonResult.success(I18Consts.I18N_AUTH_CAPTCHA_SEND_SUCCESS));
    }

    // @ActionAnnotation(title = "auth", action = BytedeskConsts.ACTION_LOGIN_MOBILE, description = "Login With mobile & code")
    @PostMapping("/login/mobile")
    public ResponseEntity<?> loginWithMobileCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("login mobile {}", authRequest.toString());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }
        // validate mobile & code
        // 验证手机验证码
        if (!pushRestService.validateCode(authRequest.getMobile(), authRequest.getCode(), request)) {
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
            userRequest.setEmailVerified(false);
            // 默认注册时，仅验证手机号，无需验证邮箱
            userRequest.setMobileVerified(true);
            userService.register(userRequest);
        }

        Authentication authentication = authService.authenticationWithMobileAndPlatform(
                authRequest.getMobile(),
                authRequest.getPlatform(),
                authRequest.getChannel(),
                authRequest.getDevice());
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    // @ActionAnnotation(title = "auth", action = "sendEmailCode", description = "Send email code")
    @PostMapping("/send/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("send email code {}", authRequest.toString());

        if (!kaptchaCacheService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(), authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }
        // send email code
        Boolean result = pushRestService.sendCode(authRequest, request);
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
        if (!pushRestService.validateCode(authRequest.getEmail(), authRequest.getCode(), request)) {
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
            userRequest.setEmailVerified(true);
            userRequest.setMobileVerified(false);
            userService.register(userRequest);
        }

        Authentication authentication = authService.authenticationWithEmailAndPlatform(
                authRequest.getEmail(),
                authRequest.getPlatform(),
                authRequest.getChannel(),
                authRequest.getDevice());
        //
        AuthResponse authResponse = authService.formatResponse(authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

}
