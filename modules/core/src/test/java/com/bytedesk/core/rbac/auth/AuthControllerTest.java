package com.bytedesk.core.rbac.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.kaptcha.KaptchaRedisService;
import com.bytedesk.core.push.PushService;
import com.bytedesk.core.push.service.PushSendResult;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private PushService pushService;

    @Mock
    private KaptchaRedisService kaptchaRedisService;

    @Mock
    private TokenRestService tokenRestService;

    @Mock
    private AuthLoginRetryHelper authLoginRetryHelper;

    @Mock
    private BytedeskProperties bytedeskProperties;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldReturnErrorResponseWhenSendMobileCodeFails() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setMobile("13311156272");
        authRequest.setCountry("86");
        authRequest.setType(AuthTypeEnum.MOBILE_LOGIN.name());
        authRequest.setPlatform("BYTEDESK");
        authRequest.setCaptchaUid("captcha-uid");
        authRequest.setCaptchaCode("1234");

        when(kaptchaRedisService.checkKaptcha("captcha-uid", "1234", null)).thenReturn(true);
        when(pushService.sendCode(authRequest, httpServletRequest))
                .thenReturn(PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "短信服务不可用"));

        ResponseEntity<?> response = authController.sendMobileCode(authRequest, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        JsonResult<?> body = assertInstanceOf(JsonResult.class, response.getBody());
        assertEquals(-2, body.getCode());
        assertEquals("短信服务不可用", body.getMessage());
        assertFalse((Boolean) body.getData());
    }

    @Test
    void shouldRejectLoginWhenPreLoginCheckReturnsLockedResponse() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("locked-user");
        authRequest.setPassword("correct-password");
        authRequest.setPlatform("BYTEDESK");
        authRequest.setCaptchaUid("captcha-uid");
        authRequest.setCaptchaCode("1234");

        ResponseEntity<?> lockedResponse = ResponseEntity.ok()
                .body(JsonResult.error(I18Consts.I18N_AUTH_ACCOUNT_LOCKED_MINUTES, -1, false));

        when(bytedeskProperties.isDisableIpFilter()).thenReturn(false);
        when(kaptchaRedisService.checkKaptcha("captcha-uid", "1234", null)).thenReturn(true);
        doReturn(lockedResponse).when(authLoginRetryHelper).performPreLoginChecks("locked-user");

        ResponseEntity<?> response = authController.loginWithUsernamePassword(authRequest, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        JsonResult<?> body = assertInstanceOf(JsonResult.class, response.getBody());
        assertEquals(-1, body.getCode());
        // 即使密码正确，锁定期间也不得进入密码验证流程
        verify(authService, never()).authenticateWithPlainPassword(authRequest);
        verify(authService, never()).authenticateWithPasswordHash(authRequest);
    }

    @Test
    void shouldProceedToAuthenticationWhenPreLoginChecksPass() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("normal-user");
        authRequest.setPassword("password");
        authRequest.setPlatform("BYTEDESK");
        authRequest.setCaptchaUid("captcha-uid");
        authRequest.setCaptchaCode("1234");

        when(bytedeskProperties.isDisableIpFilter()).thenReturn(false);
        when(kaptchaRedisService.checkKaptcha("captcha-uid", "1234", null)).thenReturn(true);
        when(authLoginRetryHelper.performPreLoginChecks("normal-user")).thenReturn(null);
        // 认证失败返回null，但重点是验证流程进入了密码验证
        when(authService.authenticateWithPlainPassword(authRequest)).thenReturn(null);
        doReturn(ResponseEntity.ok().body(JsonResult.error(I18Consts.I18N_USERNAME_OR_PASSWORD_INCORRECT, -1, false)))
                .when(authLoginRetryHelper).handleLoginFailure("normal-user", I18Consts.I18N_USERNAME_OR_PASSWORD_INCORRECT);

        ResponseEntity<?> response = authController.loginWithUsernamePassword(authRequest, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).authenticateWithPlainPassword(authRequest);
    }
}