/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-28 14:14:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.action.ActionTypeEnum;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.kaptcha.KaptchaRedisService;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.push.service.PushSendResult;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.config.properties.BytedeskProperties;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JwtUtils;
import com.bytedesk.core.rbac.token.TokenRestService;
import org.springframework.util.StringUtils;

@Slf4j
@RestController
@RequestMapping("/auth/v1")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
@Description("Authentication Controller - User authentication and authorization management")
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    private final PushService pushService;

    private final KaptchaRedisService kaptchaRedisService;

    private final TokenRestService tokenRestService;

    private final AuthLoginRetryHelper authLoginRetryHelper;

    private final BytedeskProperties bytedeskProperties;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest, HttpServletRequest request) {

        if (!kaptchaRedisService.checkKaptcha(userRequest.getCaptchaUid(), userRequest.getCaptchaCode(),
                userRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // validate sms code
        if (!pushService.validateCode(userRequest.getMobile(), userRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -1, false));
        }

        UserResponse userResponse = userService.register(userRequest);

        return ResponseEntity.ok(JsonResult.success("register success", userResponse));
    }

    @PostMapping("/login")
    @ActionAnnotation(title = "auth", action = BytedeskConsts.ACTION_LOGIN_USERNAME, description = "Login With Username & Password", type = ActionTypeEnum.LOGIN)
    public ResponseEntity<?> loginWithUsernamePassword(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("login {}", authRequest.toString());

        // 性能测试模式：参考 IpAccessInterceptor，开启 disableIpFilter 后仅保留最基本登录验证流程
        boolean performanceTestingEnabled = bytedeskProperties.isDisableIpFilter();

        // 非性能测试模式才校验验证码；是否启用验证码由 checkKaptcha 内部统一控制
        if (!performanceTestingEnabled) {
            if (!kaptchaRedisService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(),
                    authRequest.getChannel())) {
                return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
            }
        }

        // validate two-factor code if enabled 双重验证
        if (!performanceTestingEnabled && authRequest.getTwoFactorEnabled() != null && authRequest.getTwoFactorEnabled()) {
            log.debug("Two-factor authentication is enabled for user: {}", authRequest.getUsername());
            if (!pushService.validateCode(authRequest.getMobile(), authRequest.getCode(), request)) {
                return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -2, false));
            }
            // 验证用户名和手机号是否为同一个用户
            if (StringUtils.hasText(authRequest.getUsername()) && StringUtils.hasText(authRequest.getMobile())) {
                Boolean userMatch = userService.existsByUsernameAndMobileAndPlatform(
                        authRequest.getUsername(),
                        authRequest.getMobile(),
                        authRequest.getPlatform());
                if (!userMatch) {
                    return ResponseEntity.ok().body(JsonResult.error("用户名和手机号不匹配，请检查后重新输入", -3, false));
                }
            }
        }

        try {
            // 非性能测试模式才做预登录检查（失败次数/锁定等）
            if (!performanceTestingEnabled) {
                authLoginRetryHelper.performPreLoginChecks(authRequest.getUsername());
            }

            Authentication authentication;
            // 判断是否使用密码哈希登录
            if (StringUtils.hasText(authRequest.getPassword())) {
                // 使用自定义明文密码授权逻辑
                log.debug("Using plain password authentication");
                authentication = authService.authenticateWithPlainPassword(authRequest);
                if (authentication == null) {
                    return authLoginRetryHelper.handleLoginFailure(authRequest.getUsername(), "用户名或密码错误");
                }
            } else if (StringUtils.hasText(authRequest.getPasswordHash())
                    && StringUtils.hasText(authRequest.getPasswordSalt())) {
                // 使用密码哈希登录（AES解密）
                log.debug("Using password hash authentication with AES decryption for user: {}", authRequest.getUsername());
                authentication = authService.authenticateWithPasswordHash(authRequest);
                if (authentication == null) {
                    return authLoginRetryHelper.handleLoginFailure(authRequest.getUsername(), "用户名或密码错误");
                }
            } else {
                return ResponseEntity.ok().body(JsonResult.error("Password or password hash is required", -1, false));
            }
         
            // 登录成功，重置失败次数（性能测试模式跳过）
            if (!performanceTestingEnabled) {
                authLoginRetryHelper.resetLoginFailedCount(authRequest.getUsername());
            }
            
            // 格式化并返回成功响应
            AuthResponse authResponse = authService.formatResponse(authRequest, authentication);
            return ResponseEntity.ok(JsonResult.success(authResponse));
            
        } catch (Exception e) {
            log.error("Authentication failed for user: {}: {}", authRequest.getUsername(), e.getMessage());
            if (StringUtils.hasText(authRequest.getPasswordHash())) {
                return ResponseEntity.ok().body(JsonResult.error("密码解密失败，请检查密码格式", -1, false));
            }
            // 性能测试模式下，避免额外的失败次数写入/锁定逻辑
            if (performanceTestingEnabled) {
                return ResponseEntity.ok().body(JsonResult.error("用户名或密码错误", -1, false));
            }
            return authLoginRetryHelper.handleLoginFailure(authRequest.getUsername(), "用户名或密码错误");
        }
    }

    @PostMapping("/send/mobile")
    public ResponseEntity<?> sendMobileCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("send mobile code {}, channel {}, type {}", authRequest.toString(), authRequest.getChannel(),
                authRequest.getType());

        if (!kaptchaRedisService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(),
                authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // send mobile code
        PushSendResult result = pushService.sendCode(authRequest, request);
        if (!result.isSuccess()) {
            String errorMessage = result.getErrorMessage(); //Utils.getErrorMessage(result.getErrorType());
            return ResponseEntity.ok().body(JsonResult.error(errorMessage, -2, false));
        }

        return ResponseEntity.ok().body(JsonResult.success(I18Consts.I18N_AUTH_CAPTCHA_SEND_SUCCESS));
    }

    @ActionAnnotation(title = "auth", action = BytedeskConsts.ACTION_LOGIN_MOBILE, description = "Login With mobile & code", type = ActionTypeEnum.LOGIN)
    @PostMapping("/login/mobile")
    public ResponseEntity<?> loginWithMobileCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("login mobile {}", authRequest.toString());

        if (!kaptchaRedisService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(),
                authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }

        // validate mobile & code
        if (!pushService.validateCode(authRequest.getMobile(), authRequest.getCode(), request)) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_VALIDATE_FAILED, -2, false));
        }

        // if mobile already exists, if none, then register
        // 手机号是否已经注册，如果没有，则自动注册
        if (!userService.existsByMobileAndPlatform(authRequest.getMobile(), authRequest.getPlatform())) {
            UserRequest userRequest = new UserRequest();
            userRequest.setUsername(authRequest.getMobile());
            userRequest.setCountry(authRequest.getCountry());
            userRequest.setNum(authRequest.getMobile());
            userRequest.setMobile(authRequest.getMobile());
            userRequest.setPlatform(authRequest.getPlatform());
            userRequest.setEmailVerified(false);
            // 默认注册时，仅验证手机号，无需验证邮箱
            userRequest.setMobileVerified(true);
            // 注册来源：手机号验证码登录自动注册
            userRequest.setRegisterSource(UserEntity.RegisterSource.MOBILE.name());
            userService.register(userRequest);
        } else {
            // 如果用户已存在，检查并更新手机验证状态
            userService.findByMobileAndPlatform(authRequest.getMobile(), authRequest.getPlatform())
                .ifPresent(user -> {
                    if (!user.isMobileVerified()) {
                        user.setMobileVerified(true);
                        userService.save(user);
                        log.info("Updated mobile verification status for user: {}", user.getUid());
                    }
                });
        }

        Authentication authentication = authService.authenticationWithMobileAndPlatform(
                authRequest.getMobile(),
                authRequest.getPlatform(),
                authRequest.getChannel(),
                authRequest.getDevice());
        //
        AuthResponse authResponse = authService.formatResponse(authRequest, authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    @PostMapping("/send/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("send email code {}", authRequest.toString());

        if (!kaptchaRedisService.checkKaptcha(authRequest.getCaptchaUid(), authRequest.getCaptchaCode(),
                authRequest.getChannel())) {
            return ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_AUTH_CAPTCHA_ERROR, -1, false));
        }
        // send email code
        PushSendResult result = pushService.sendCode(authRequest, request);
        if (!result.isSuccess()) {
            String errorMessage = result.getErrorMessage(); //Utils.getErrorMessage(result.getErrorType());
            return ResponseEntity.ok().body(JsonResult.error(errorMessage, -2, false));
        }

        return ResponseEntity.ok(JsonResult.success(I18Consts.I18N_AUTH_CAPTCHA_SEND_SUCCESS));
    }

    @ActionAnnotation(title = "auth", action = BytedeskConsts.ACTION_LOGIN_EMAIL, description = "Login With email & code", type = ActionTypeEnum.LOGIN)
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
            userRequest.setEmailVerified(true);
            userRequest.setMobileVerified(false);
            // 注册来源：邮箱验证码登录自动注册
            userRequest.setRegisterSource(UserEntity.RegisterSource.EMAIL.name());
            userService.register(userRequest);
        } else {
            // 如果用户已存在，检查并更新邮箱验证状态
            userService.findByEmailAndPlatform(authRequest.getEmail(), authRequest.getPlatform())
                .ifPresent(user -> {
                    if (!user.isEmailVerified()) {
                        user.setEmailVerified(true);
                        userService.save(user);
                        log.info("Updated email verification status for user: {}", user.getUid());
                    }
                });
        }

        Authentication authentication = authService.authenticationWithEmailAndPlatform(
                authRequest.getEmail(),
                authRequest.getPlatform(),
                authRequest.getChannel(),
                authRequest.getDevice());
        //
        AuthResponse authResponse = authService.formatResponse(authRequest, authentication);

        return ResponseEntity.ok(JsonResult.success(authResponse));
    }

    @PostMapping("/login/accessToken")
    @ActionAnnotation(title = "auth", action = "login_accessToken", description = "Login With Access Token", type = ActionTypeEnum.LOGIN)
    public ResponseEntity<?> loginWithAccessToken(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        log.debug("validate accessToken {}", authRequest.getAccessToken());

        boolean isValid = tokenRestService.validateAccessToken(authRequest.getAccessToken());
        if (!isValid) {
            return ResponseEntity.ok(JsonResult.error("accessToken is invalid", -1, false));
        }

        // Extract subject (username) from the access token
        String subject = JwtUtils.getSubjectFromJwtToken(authRequest.getAccessToken());
        
        // Get authentication using the subject
        Authentication authentication = authService.getAuthentication(request, subject);
        
        // Format response similar to other login methods
        AuthResponse authResponse = authService.formatResponse(authRequest, authentication);
        
        return ResponseEntity.ok(JsonResult.success(authResponse));
    }


}
