package com.bytedesk.core.push.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.push.PushRequest;
import com.bytedesk.core.push.PushRestService;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.push.PushFilterService;
import com.bytedesk.core.push.strategy.AuthValidationStrategyFactory;
import com.bytedesk.core.rbac.auth.AuthRequest;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import com.bytedesk.core.sms_push.SmsPushSendService;
import com.bytedesk.core.sms_push.SmsSendResult;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class PushSendServiceTest {

    @Mock
    private AuthValidationStrategyFactory strategyFactory;

    @Mock
    private com.bytedesk.core.email_provider.EmailSendService emailSendService;

    @Mock
    private SmsPushSendService smsPushSendService;

    @Mock
    private BytedeskProperties bytedeskProperties;

    @Mock
    private IpService ipService;

    @Mock
    private PushFilterService pushFilterService;

    @Mock
    private PushRestService pushRestService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private PushSendService pushSendService;

    @Test
    void shouldReturnFailureAndPersistErrorWhenSmsSendFails() {
        AuthRequest authRequest = buildMobileLoginRequest();
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(pushFilterService.canSendCode("127.0.0.1")).thenReturn(true);
        when(pushRestService.existsByStatusAndTypeAndReceiver(PushStatusEnum.PENDING.name(), AuthTypeEnum.MOBILE_LOGIN.name(), "13311156272"))
                .thenReturn(false);
        when(ipService.getIpLocation("127.0.0.1")).thenReturn("local");
        when(smsPushSendService.sendSmsWithResult(eq("13311156272"), eq("86"), any(String.class), eq(httpServletRequest)))
                .thenReturn(SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, "短信服务不可用"));

        PushSendResult result = pushSendService.sendCode(authRequest, httpServletRequest);

        assertFalse(result.isSuccess());
        assertEquals("短信服务不可用", result.getErrorMessage());

        ArgumentCaptor<PushRequest> pushRequestCaptor = ArgumentCaptor.forClass(PushRequest.class);
        verify(pushRestService).create(pushRequestCaptor.capture());
        PushRequest savedRequest = pushRequestCaptor.getValue();
        assertEquals(Boolean.FALSE, savedRequest.getSendSuccess());
        assertEquals("短信服务不可用", savedRequest.getSendMessage());
        assertEquals(PushStatusEnum.ERROR.name(), savedRequest.getStatus());
        assertNotNull(savedRequest.getContent());
        verify(pushFilterService, never()).updateIpLastSentTime("127.0.0.1");
    }

    @Test
    void shouldUpdateIpLimitOnlyWhenSmsSendSucceeds() {
        AuthRequest authRequest = buildMobileLoginRequest();
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(pushFilterService.canSendCode("127.0.0.1")).thenReturn(true);
        when(pushRestService.existsByStatusAndTypeAndReceiver(PushStatusEnum.PENDING.name(), AuthTypeEnum.MOBILE_LOGIN.name(), "13311156272"))
                .thenReturn(false);
        when(ipService.getIpLocation("127.0.0.1")).thenReturn("local");
        when(smsPushSendService.sendSmsWithResult(eq("13311156272"), eq("86"), any(String.class), eq(httpServletRequest)))
                .thenReturn(SmsSendResult.success());

        PushSendResult result = pushSendService.sendCode(authRequest, httpServletRequest);

        assertTrue(result.isSuccess());
        verify(pushFilterService).updateIpLastSentTime("127.0.0.1");
    }

    private AuthRequest buildMobileLoginRequest() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setType(AuthTypeEnum.MOBILE_LOGIN.name());
        authRequest.setPlatform("BYTEDESK");
        authRequest.setMobile("13311156272");
        authRequest.setCountry("86");
        return authRequest;
    }
}